package com.freshfold.service;

import com.freshfold.dto.*;
import com.freshfold.model.*;
import com.freshfold.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @Transactional
    public ApiResponse createOrder(OrderRequest request) {

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Personnel personnel = personnelRepository.findById(request.getPersonnelId())
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        double basePrice = request.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * OrderItem.getPriceForItem(item.getItemType()))
                .sum();

        double totalPrice = calculateTotalPrice(basePrice, request.getUrgencyDays());

        LaundryOrder order = new LaundryOrder();
        order.setStudent(student);
        order.setPersonnel(personnel);
        order.setServiceType(request.getServiceType());
        order.setUrgencyDays(request.getUrgencyDays());
        order.setPhotoUrl(request.getPhotoUrl());
        order.setTotalPrice(totalPrice);
        order.setPickupLocation(request.getPickupLocation());
        order.setStatus(OrderStatus.PENDING);

        for (OrderItemDTO itemDTO : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setItemType(itemDTO.getItemType());
            item.setQuantity(itemDTO.getQuantity());
            item.setPricePerItem(OrderItem.getPriceForItem(itemDTO.getItemType()));
            order.addItem(item);
        }

        LaundryOrder savedOrder = orderRepository.save(order);

        return new ApiResponse(true, "Order created successfully",
                convertToOrderResponse(savedOrder));
    }

    private double calculateTotalPrice(double basePrice, int urgencyDays) {
        return switch (urgencyDays) {
            case 1 -> basePrice * 1.5;
            case 2 -> basePrice * 1.25;
            default -> basePrice;
        };
    }

    public List<OrderResponse> getStudentOrders(Long studentId) {
        List<LaundryOrder> orders = orderRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId);

        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetails(Long orderId) {
        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToOrderResponse(order);
    }

    public List<Personnel> getAllPersonnel() {
        return personnelRepository.findAllByOrderByRatingDesc();
    }

    private OrderResponse convertToOrderResponse(LaundryOrder order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());

        // ===== Student info =====
        Student student = order.getStudent();
        OrderResponse.StudentInfo studentInfo = new OrderResponse.StudentInfo(
                student.getId(),
                student.getFullName(),
                student.getStudentId(),
                student.getHostel(),
                student.getRoomNumber(),
                student.getPhoneNumber()
        );
        response.setStudent(studentInfo);

        // ===== Personnel info =====
        if (order.getPersonnel() != null) {
            Personnel personnel = order.getPersonnel();
            OrderResponse.PersonnelInfo personnelInfo = new OrderResponse.PersonnelInfo(
                    personnel.getId(),
                    personnel.getFullName(),
                    personnel.getEmployeeId(),
                    personnel.getPhoneNumber(),
                    personnel.getRating()
            );
            response.setPersonnel(personnelInfo);
        }

        // ===== Items =====
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getItemType(),
                        item.getQuantity(),
                        item.getPricePerItem()
                ))
                .collect(Collectors.toList());
        response.setItems(itemDTOs);

        // ===== Basic fields =====
        response.setStatus(order.getStatus().toString());
        response.setServiceType(order.getServiceType());
        response.setUrgencyDays(order.getUrgencyDays());
        response.setPhotoUrl(order.getPhotoUrl());
        response.setTotalPrice(order.getTotalPrice());
        response.setPickupLocation(order.getPickupLocation());
        response.setRejectionReason(order.getRejectionReason());
        response.setCreatedAt(order.getCreatedAt().format(formatter));
        response.setUpdatedAt(order.getUpdatedAt().format(formatter));
        response.setStudentRating(order.getStudentRating());

        // ===== NEW: status history for timeline =====
        response.setStatusHistory(buildStatusHistory(order));

        return response;
    }

    /**
     * Build a chronological status history from the timestamp fields
     * in LaundryOrder. This is what the React UI displays as:
     *   order.statusHistory.map(h => h.status + " -> " + h.timestamp)
     */
    private List<OrderResponse.StatusHistoryEntry> buildStatusHistory(LaundryOrder order) {
        List<OrderResponse.StatusHistoryEntry> history = new ArrayList<>();

        addIfNotNull(history, "PENDING", order.getCreatedAt());
        addIfNotNull(history, "ACCEPTED", order.getAcceptedAt());
        addIfNotNull(history, "PENDING_COLLECTION", order.getCollectionAt());
        addIfNotNull(history, "WASHING", order.getWashingAt());
        addIfNotNull(history, "IRONING", order.getIroningAt());
        addIfNotNull(history, "DONE", order.getCompletedAt());

        return history;
    }

    private void addIfNotNull(List<OrderResponse.StatusHistoryEntry> history,
                              String status,
                              LocalDateTime time) {
        if (time != null) {
            history.add(
                    new OrderResponse.StatusHistoryEntry(
                            status,
                            time.format(formatter)
                    )
            );
        }
    }

    /**
     * Return sorted accessible URLs for all photos attached to an order.
     */
    public List<String> getOrderPhotos(Long orderId) {
        List<String> filenames = fileStorageService.listFileNamesForOrder(orderId);
        return filenames.stream()
                .map(fileStorageService::buildFileUrl)
                .collect(Collectors.toList());
    }

    /**
     * Submit rating for a completed order
     */
    @Transactional
    public ApiResponse submitRating(Long orderId, Integer rating) {

        if (rating == null || rating < 1 || rating > 5) {
            return new ApiResponse(false, "Rating must be between 1 and 5");
        }

        LaundryOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DONE) {
            return new ApiResponse(false, "You can only rate completed orders");
        }

        if (order.getStudentRating() != null) {
            return new ApiResponse(false, "Order already rated");
        }

        order.setStudentRating(rating);
        orderRepository.save(order);

        updatePersonnelRating(order.getPersonnel().getId());

        return new ApiResponse(true, "Rating submitted successfully");
    }

    /**
     * Recalculate personnel average rating
     */
    private void updatePersonnelRating(Long personnelId) {
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new RuntimeException("Personnel not found"));

        List<LaundryOrder> ratedOrders = orderRepository
                .findByPersonnelIdAndStatus(personnelId, OrderStatus.DONE)
                .stream()
                .filter(order -> order.getStudentRating() != null)
                .collect(Collectors.toList());

        if (!ratedOrders.isEmpty()) {
            double averageRating = ratedOrders.stream()
                    .mapToInt(LaundryOrder::getStudentRating)
                    .average()
                    .orElse(personnel.getRating());

            personnel.setRating(averageRating);
            personnelRepository.save(personnel);
        }
    }
}
