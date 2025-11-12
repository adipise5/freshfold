package com.freshfold.service;

import com.freshfold.dto.*;
import com.freshfold.model.*;
import com.freshfold.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AuthService - Handles user authentication and registration
 * 
 * WORKFLOW:
 * 1. Login: Validate credentials → Return user data
 * 2. Signup: Validate input → Create user → Save to database
 * 
 * SECURITY NOTE: In production, passwords should be hashed using BCrypt
 * For prototype, we're storing plain text passwords
 */
@Service
public class AuthService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    /**
     * Authenticate user based on role
     * 
     * @param request Contains email, password, and role
     * @return LoginResponse with user details or null if auth fails
     */
    public LoginResponse login(LoginRequest request) {
        String role = request.getRole().toUpperCase();
        
        switch (role) {
            case "STUDENT":
                return loginStudent(request.getEmail(), request.getPassword());
            case "PERSONNEL":
                return loginPersonnel(request.getEmail(), request.getPassword());
            case "ADMIN":
                return loginAdmin(request.getEmail(), request.getPassword());
            default:
                return null;
        }
    }
    
    /**
     * Student login
     */
    private LoginResponse loginStudent(String email, String password) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        
        if (studentOpt.isEmpty()) {
            return null;
        }
        
        Student student = studentOpt.get();
        
        // Verify password (in production, use BCrypt.matches())
        if (!student.getPassword().equals(password)) {
            return null;
        }
        
        // Prepare user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("studentId", student.getStudentId());
        userData.put("hostel", student.getHostel());
        userData.put("roomNumber", student.getRoomNumber());
        userData.put("phoneNumber", student.getPhoneNumber());
        
        return new LoginResponse(
            student.getId(),
            student.getFullName(),
            student.getEmail(),
            "STUDENT",
            "dummy-token", // For future JWT implementation
            userData
        );
    }
    
    /**
     * Personnel login
     */
    private LoginResponse loginPersonnel(String email, String password) {
        Optional<Personnel> personnelOpt = personnelRepository.findByEmail(email);
        
        if (personnelOpt.isEmpty()) {
            return null;
        }
        
        Personnel personnel = personnelOpt.get();
        
        if (!personnel.getPassword().equals(password)) {
            return null;
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("employeeId", personnel.getEmployeeId());
        userData.put("phoneNumber", personnel.getPhoneNumber());
        userData.put("yearsExperience", personnel.getYearsExperience());
        userData.put("rating", personnel.getRating());
        
        return new LoginResponse(
            personnel.getId(),
            personnel.getFullName(),
            personnel.getEmail(),
            "PERSONNEL",
            "dummy-token",
            userData
        );
    }
    
    /**
     * Admin login
     */
    private LoginResponse loginAdmin(String email, String password) {
        // Try to find by email or adminId
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        
        if (adminOpt.isEmpty()) {
            // Try with adminId
            adminOpt = adminRepository.findByAdminId(email);
        }
        
        if (adminOpt.isEmpty()) {
            return null;
        }
        
        Admin admin = adminOpt.get();
        
        if (!admin.getPassword().equals(password)) {
            return null;
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("adminId", admin.getAdminId());
        
        return new LoginResponse(
            admin.getId(),
            admin.getFullName(),
            admin.getEmail(),
            "ADMIN",
            "dummy-token",
            userData
        );
    }
    
    /**
     * Register new student
     */
    public ApiResponse registerStudent(StudentSignupRequest request) {
        // Check if email already exists
        if (studentRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already registered");
        }
        
        // Check if student ID already exists
        if (studentRepository.existsByStudentId(request.getStudentId())) {
            return new ApiResponse(false, "Student ID already registered");
        }
        
        // Validate BITS email (should end with @pilani.bits-pilani.ac.in)
        if (!request.getEmail().endsWith("@pilani.bits-pilani.ac.in")) {
            return new ApiResponse(false, "Please use BITS Pilani email ID");
        }
        
        // Create new student
        Student student = new Student();
        student.setFullName(request.getFullName());
        student.setStudentId(request.getStudentId());
        student.setEmail(request.getEmail());
        student.setHostel(request.getHostel());
        student.setRoomNumber(request.getRoomNumber());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setPassword(request.getPassword()); // In production, hash this!
        
        studentRepository.save(student);
        
        return new ApiResponse(true, "Student registered successfully", student.getId());
    }
    
    /**
     * Register new personnel
     */
    public ApiResponse registerPersonnel(PersonnelSignupRequest request) {
        if (personnelRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already registered");
        }
        
        if (personnelRepository.existsByEmployeeId(request.getEmployeeId())) {
            return new ApiResponse(false, "Employee ID already registered");
        }
        
        Personnel personnel = new Personnel();
        personnel.setFullName(request.getFullName());
        personnel.setEmployeeId(request.getEmployeeId());
        personnel.setEmail(request.getEmail());
        personnel.setPhoneNumber(request.getPhoneNumber());
        personnel.setYearsExperience(request.getYearsExperience());
        personnel.setRating(request.getRating() != null ? request.getRating() : 3.0);
        personnel.setPassword(request.getPassword());
        
        personnelRepository.save(personnel);
        
        return new ApiResponse(true, "Personnel registered successfully", personnel.getId());
    }
}