package com.freshfold.controller;

import com.freshfold.dto.*;
import com.freshfold.model.Personnel;
import com.freshfold.service.FileStorageService;
import com.freshfold.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * StudentController - Handles student-related endpoints
 */
@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload clothing photo
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<ApiResponse> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            fileStorageService.init();
            String filePath = fileStorageService.storeFile(file);

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Photo uploaded successfully",
                    filePath
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Create new laundry order
     */
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse> createOrder(@RequestBody OrderRequest request) {
        try {
            ApiResponse response = studentService.createOrder(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Order creation failed: " + e.getMessage()));
        }
    }

    /**
     * Get all orders for a student
     */
    @GetMapping("/orders/{studentId}")
    public ResponseEntity<?> getStudentOrders(@PathVariable Long studentId) {
        try {
            List<OrderResponse> orders = studentService.getStudentOrders(studentId);
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch orders: " + e.getMessage()));
        }
    }

    /**
     * Get specific order details
     */
    @GetMapping("/orders/detail/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            OrderResponse order = studentService.getOrderDetails(orderId);
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse(false, "Order not found"));
        }
    }

    /**
     * Get personnel list sorted by rating
     */
    @GetMapping("/personnel")
    public ResponseEntity<?> getAllPersonnel() {
        try {
            List<Personnel> personnel = studentService.getAllPersonnel();
            return ResponseEntity.ok(personnel);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch personnel"));
        }
    }

    /**
     * Submit rating for completed order
     * POST /api/student/orders/{orderId}/rating
     * Body: { "rating": 5 }
     */
    @PostMapping("/orders/{orderId}/rating")
    public ResponseEntity<ApiResponse> submitRating(
            @PathVariable Long orderId,
            @RequestBody Map<String, Integer> request) {

        try {
            Integer rating = request.get("rating");
            ApiResponse response = studentService.submitRating(orderId, rating);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to submit rating: " + e.getMessage()));
        }
    }
}
