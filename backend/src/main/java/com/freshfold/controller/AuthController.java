package com.freshfold.controller;

import com.freshfold.dto.*;
import com.freshfold.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Handles authentication endpoints
 * 
 * API ENDPOINTS:
 * POST /api/auth/login - Login for all user types
 * POST /api/auth/signup/student - Student registration
 * POST /api/auth/signup/personnel - Personnel registration
 * 
 * WORKFLOW:
 * Frontend → HTTP Request → Controller → Service → Repository → Database
 *                                    ↓
 *                            Response ← DTO
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Login endpoint for all user types
     * 
     * REQUEST BODY:
     * {
     *   "email": "user@example.com",
     *   "password": "password123",
     *   "role": "STUDENT" | "PERSONNEL" | "ADMIN"
     * }
     * 
     * RESPONSE:
     * {
     *   "id": 1,
     *   "fullName": "John Doe",
     *   "email": "john@pilani.bits-pilani.ac.in",
     *   "role": "STUDENT",
     *   "token": "dummy-token",
     *   "userData": { ... }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            
            if (response == null) {
                return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Invalid credentials"));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }
    
    /**
     * Student signup endpoint
     * 
     * REQUEST BODY:
     * {
     *   "fullName": "John Doe",
     *   "studentId": "2021A7PS0001P",
     *   "email": "john@pilani.bits-pilani.ac.in",
     *   "hostel": "Ram Bhawan",
     *   "roomNumber": "101",
     *   "phoneNumber": "9876543210",
     *   "password": "password123"
     * }
     */
    @PostMapping("/signup/student")
    public ResponseEntity<ApiResponse> signupStudent(
            @RequestBody StudentSignupRequest request) {
        try {
            ApiResponse response = authService.registerStudent(request);
            
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Signup failed: " + e.getMessage()));
        }
    }
    
    /**
     * Personnel signup endpoint
     * 
     * REQUEST BODY:
     * {
     *   "fullName": "Rahul Kumar",
     *   "employeeId": "EMP001",
     *   "email": "rahul@freshfold.com",
     *   "phoneNumber": "9876543210",
     *   "yearsExperience": 5,
     *   "rating": 4.0,
     *   "password": "password123"
     * }
     */
    @PostMapping("/signup/personnel")
    public ResponseEntity<ApiResponse> signupPersonnel(
            @RequestBody PersonnelSignupRequest request) {
        try {
            ApiResponse response = authService.registerPersonnel(request);
            
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Signup failed: " + e.getMessage()));
        }
    }
}