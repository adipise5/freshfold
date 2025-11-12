// Save as: src/main/java/com/freshfold/service/PersonnelService.java
package com.freshfold.service;

import com.freshfold.dto.*;
import com.freshfold.model.*;
import com.freshfold.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PersonnelService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private StudentService studentService;

    public List<OrderResponse> getPendingRequests() {
        List<LaundryOrder> orders = orderRepository
                .findByStatusOrderByCreatedAtDesc(OrderStatus.PENDING);

        return orders.stream()
                .map(order -> studentService.getOrderDetails(order.getId()))
                .toList();
    }

    public List<OrderResponse> getInProgressOrders(Long personnelId) {
        List<OrderStatus> inProgressStatuses = Arrays.asList(
                OrderStatus.ACCEPTED,
                OrderStatus.PENDING_COLLECTION,
                OrderStatus.WASHING,
                OrderStatus.IRONING
        );

        List<LaundryOrder> orders = orderRepository
                .findByPersonnelIdAndStatusIn(personnelId, inProgressStatuses);

        return orders.stream()
                .map(order -> studentService.getOrderDetails(order.getId()))
                .toList();
    }

    public List<OrderResponse> getCompletedOrders(Long personnelId) {
        List<LaundryOrder> orders = orderRepository
                .findByPersonnelIdAndStatus(personnelId, OrderStatus.DONE);

        return orders.stream()
                .map(order -> studentService.getOrderDetails(order.getId()))
                .toList();
    }

    @Transactional
    public ApiResponse acceptOrder(Long orderId, Long personnelId) {
        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            return new ApiResponse(false, "Order is not in pending status");
        }

        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedAt(LocalDateTime.now());
        order.setPersonnel(personnel);

        orderRepository.save(order);

        return new ApiResponse(true, "Order accepted successfully");
    }

    @Transactional
    public ApiResponse rejectOrder(Long orderId, String reason) {
        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            return new ApiResponse(false, "Order is not in pending status");
        }

        order.setStatus(OrderStatus.REJECTED);
        order.setRejectionReason(reason);

        orderRepository.save(order);

        return new ApiResponse(false, "Order rejected", reason);
    }

    @Transactional
    public ApiResponse updateOrderStatus(Long orderId, String newStatus) {
        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus targetStatus = OrderStatus.valueOf(newStatus);

        if (!isValidStatusTransition(currentStatus, targetStatus)) {
            return new ApiResponse(false, "Invalid status transition");
        }

        order.setStatus(targetStatus);
        switch (targetStatus) {
            case PENDING_COLLECTION -> order.setCollectionAt(LocalDateTime.now());
            case WASHING -> order.setWashingAt(LocalDateTime.now());
            case IRONING -> order.setIroningAt(LocalDateTime.now());
            case DONE -> order.setCompletedAt(LocalDateTime.now());
        }

        orderRepository.save(order);

        return new ApiResponse(true, "Order status updated to " + targetStatus);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus target) {
        return switch (current) {
            case ACCEPTED -> target == OrderStatus.PENDING_COLLECTION;
            case PENDING_COLLECTION -> target == OrderStatus.WASHING;
            case WASHING -> target == OrderStatus.IRONING;
            case IRONING -> target == OrderStatus.DONE;
            default -> false;
        };
    }

    public Map<String, Object> getPersonnelStats(Long personnelId) {
        Long completedOrders = orderRepository
                .countByPersonnelIdAndStatus(personnelId, OrderStatus.DONE);

        Double totalEarnings = orderRepository
                .calculatePersonnelRevenue(personnelId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("completedOrders", completedOrders);
        stats.put("totalEarnings", totalEarnings);

        return stats;
    }
}