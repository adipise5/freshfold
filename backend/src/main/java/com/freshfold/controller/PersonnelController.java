package com.freshfold.controller;

import com.freshfold.dto.*;
import com.freshfold.service.PersonnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnel")
@CrossOrigin(origins = "http://localhost:3000")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    @GetMapping("/orders/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            List<OrderResponse> orders = personnelService.getPendingRequests();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch pending requests"));
        }
    }

    @GetMapping("/orders/inprogress/{personnelId}")
    public ResponseEntity<?> getInProgressOrders(@PathVariable Long personnelId) {
        try {
            List<OrderResponse> orders = personnelService.getInProgressOrders(personnelId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch in-progress orders"));
        }
    }

    @GetMapping("/orders/completed/{personnelId}")
    public ResponseEntity<?> getCompletedOrders(@PathVariable Long personnelId) {
        try {
            List<OrderResponse> orders = personnelService.getCompletedOrders(personnelId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch completed orders"));
        }
    }

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse> acceptOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, Long> request) {
        try {
            Long personnelId = request.get("personnelId");
            ApiResponse response = personnelService.acceptOrder(orderId, personnelId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to accept order: " + e.getMessage()));
        }
    }

    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<ApiResponse> rejectOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("rejectionReason");
            ApiResponse response = personnelService.rejectOrder(orderId, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to reject order: " + e.getMessage()));
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody StatusUpdateRequest request) {
        try {
            ApiResponse response = personnelService.updateOrderStatus(
                    orderId,
                    request.getStatus()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to update status: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/{personnelId}")
    public ResponseEntity<?> getPersonnelStats(@PathVariable Long personnelId) {
        try {
            Map<String, Object> stats = personnelService.getPersonnelStats(personnelId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(false, "Failed to fetch statistics"));
        }
    }
}