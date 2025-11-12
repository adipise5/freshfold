package com.freshfold.service;

import com.freshfold.dto.*;
import com.freshfold.model.*;
import com.freshfold.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AdminService - Handles admin operations
 * 
 * FEATURES:
 * 1. View campus-wide statistics
 * 2. Generate reports (hostel-wise, personnel-wise)
 * 3. View recent orders
 * 4. Track total revenue
 */
@Service
public class AdminService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Get complete admin dashboard statistics
     */
    public AdminStatsResponse getAdminStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        
        // Total orders
        stats.setTotalOrders(orderRepository.count());
        
        // Completed orders
        stats.setCompletedOrders(orderRepository.countByStatus(OrderStatus.DONE));
        
        // Pending orders
        stats.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));
        
        // Total revenue
        stats.setTotalRevenue(orderRepository.calculateTotalRevenue());
        
        // Hostel-wise statistics
        stats.setHostelStats(getHostelWiseStats());
        
        // Personnel performance
        stats.setPersonnelStats(getPersonnelPerformance());
        
        return stats;
    }
    
    /**
     * Get hostel-wise order and revenue statistics
     */
    private List<AdminStatsResponse.HostelStats> getHostelWiseStats() {
        List<Object[]> orderCounts = orderRepository.getHostelWiseOrderCount();
        List<Object[]> revenues = orderRepository.getHostelWiseRevenue();
        
        // Create map of hostel → revenue
        Map<String, Double> revenueMap = revenues.stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (Double) arr[1]
            ));
        
        // Combine order count and revenue
        return orderCounts.stream()
            .map(arr -> {
                String hostel = (String) arr[0];
                Long count = (Long) arr[1];
                Double revenue = revenueMap.getOrDefault(hostel, 0.0);
                return new AdminStatsResponse.HostelStats(hostel, count, revenue);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get personnel performance statistics
     */
    private List<AdminStatsResponse.PersonnelStats> getPersonnelPerformance() {
        List<Object[]> performance = orderRepository.getPersonnelPerformance();
        
        return performance.stream()
            .map(arr -> new AdminStatsResponse.PersonnelStats(
                (String) arr[0],  // name
                (Long) arr[1],     // orders completed
                (Double) arr[2],   // earnings
                (Double) arr[3]    // rating
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Get last 10 recent orders
     */
    public List<OrderResponse> getRecentOrders() {
        List<LaundryOrder> orders = orderRepository.findTop10ByOrderByCreatedAtDesc();
        
        return orders.stream()
            .map(order -> studentService.getOrderDetails(order.getId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all orders (for report generation)
     */
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(order -> studentService.getOrderDetails(order.getId()))
            .collect(Collectors.toList());
    }
}