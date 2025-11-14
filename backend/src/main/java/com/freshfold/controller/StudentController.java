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
     * Upload clothing photo and save it under the order's naming convention.
     *
     * Requires orderId param. Example:
     * POST /api/student/upload-photo?orderId=123  (form-data: file=...)
     *
     * Response contains stored filename and accessible URL.
     */
    @PostMapping("/upload-photo")
    public ResponseEntity<ApiResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") Long orderId) {
        try {
            fileStorageService.init(); // safe to call; idempotent
            String storedFilename = fileStorageService.storeFileForOrder(file, orderId);
            String fileUrl = fileStorageService.buildFileUrl(storedFilename);

            // Optionally: you might want to update the LaundryOrder.photoUrl to the first image here.
            // This code doesn't save the order entity automatically; client/service should call createOrder or update as appropriate.

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Photo uploaded successfully",
                    Map.of(
                            "filename", storedFilename,
                            "url", fileUrl
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Upload failed: " + e.getMessage()));
        }
    }

    /**
     * Get all image URLs for an order, sorted by naming (orderId.jpg first, then orderId_1.jpg, orderId_2.jpg...)
     * GET /api/student/orders/{orderId}/photos
     */
    @GetMapping("/orders/{orderId}/photos")
    public ResponseEntity<?> getOrderPhotos(@PathVariable Long orderId) {
        try {
            List<String> urls = studentService.getOrderPhotos(orderId);
            return ResponseEntity.ok(urls);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch photos: " + e.getMessage()));
        }
    }

    // ---------------- existing endpoints ----------------

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
