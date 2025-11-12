package com.freshfold.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * FileStorageService - Handles file upload operations
 * 
 * FEATURES:
 * 1. Store uploaded photos in local directory
 * 2. Generate unique filenames to avoid conflicts
 * 3. Validate file type and size
 * 
 * WORKFLOW:
 * Frontend → Upload photo → Backend saves to /uploads → Return file URL
 */
@Service
public class FileStorageService {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    /**
     * Initialize upload directory on service startup
     */
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            System.out.println("📁 Upload directory created: " + uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
    
    /**
     * Store uploaded file and return file path
     * 
     * @param file MultipartFile from HTTP request
     * @return Relative path to stored file
     */
    public String storeFile(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("Cannot upload empty file");
            }
            
            // Validate file type (only images allowed)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            
            // Validate file size (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB in bytes
            if (file.getSize() > maxSize) {
                throw new RuntimeException("File size exceeds 5MB limit");
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Create upload path
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ File uploaded: " + uniqueFilename);
            
            // Return relative path
            return "/uploads/" + uniqueFilename;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a file
     */
    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get file as Path object
     */
    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }
}