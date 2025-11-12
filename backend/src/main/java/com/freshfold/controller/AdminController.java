package com.freshfold.controller;

import com.freshfold.dto.*;
import com.freshfold.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * AdminController - Handles admin endpoints
 * 
 * API ENDPOINTS:
 * GET /api/admin/stats - Get complete dashboard statistics
 * GET /api/admin/orders/recent - Get last 10 orders
 * GET /api/admin/orders/all - Get all orders (for report)
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * Get complete admin dashboard statistics
     * 
     * RESPONSE:
     * {
     *   "totalOrders": 150,
     *   "completedOrders": 120,
     *   "pendingOrders": 10,
     *   "totalRevenue": 15000.0,
     *   "hostelStats": [...],
     *   "personnelStats": [...]
     * }
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        try {
            AdminStatsResponse stats = adminService.getAdminStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Failed to fetch statistics"));
        }
    }
    
    /**
     * Get last 10 recent orders
     */
    @GetMapping("/orders/recent")
    public ResponseEntity<?> getRecentOrders() {
        try {
            List<OrderResponse> orders = adminService.getRecentOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Failed to fetch recent orders"));
        }
    }
    
    /**
     * Get all orders (for generating reports)
     */
    @GetMapping("/orders/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderResponse> orders = adminService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Failed to fetch orders"));
        }
    }
}