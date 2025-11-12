package com.freshfold.config;

import com.freshfold.model.Admin;
import com.freshfold.model.Personnel;
import com.freshfold.repository.AdminRepository;
import com.freshfold.repository.PersonnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Seeds database with initial data
 * 
 * SEEDED DATA:
 * 1. Default Admin account
 * 2. Predefined Personnel (5 laundry workers)
 * 
 * This runs automatically when the application starts
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Override
    public void run(String... args) {
        System.out.println("\n🔄 Initializing database with seed data...");
        
        // Seed admin if not exists
        if (!adminRepository.existsByAdminId("admin")) {
            Admin admin = new Admin();
            admin.setAdminId("admin");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@freshfold.com");
            admin.setPassword("admin123"); // In production, hash this!
            adminRepository.save(admin);
            System.out.println("✅ Admin account created (ID: admin, Password: admin123)");
        }
        
        // Seed personnel if not exists
        if (personnelRepository.count() == 0) {
            createPersonnel("Rahul Kumar", "EMP001", "9876543210", 5, 4.0);
            createPersonnel("Sanjeev Sharma", "EMP002", "9876543211", 3, 3.0);
            createPersonnel("Arjun Patel", "EMP003", "9876543212", 4, 4.0);
            createPersonnel("Rajkumar Sinha", "EMP004", "9876543213", 2, 3.0);
            createPersonnel("Tejkumar", "EMP005", "9876543214", 6, 5.0);
            System.out.println("✅ 5 Personnel accounts created");
        }
        
        System.out.println("✅ Database initialization complete!\n");
        System.out.println("==========================================");
        System.out.println("🔑 DEFAULT LOGIN CREDENTIALS");
        System.out.println("==========================================");
        System.out.println("Admin:");
        System.out.println("  ID: admin");
        System.out.println("  Password: admin123");
        System.out.println("\nPersonnel (any of these):");
        System.out.println("  Email: rahul@freshfold.com, Password: password");
        System.out.println("  Email: sanjeev@freshfold.com, Password: password");
        System.out.println("  Email: arjun@freshfold.com, Password: password");
        System.out.println("  Email: rajkumar@freshfold.com, Password: password");
        System.out.println("  Email: tej@freshfold.com, Password: password");
        System.out.println("==========================================\n");
    }
    
    private void createPersonnel(String name, String empId, String phone, 
                                  int experience, double rating) {
        Personnel personnel = new Personnel();
        personnel.setFullName(name);
        personnel.setEmployeeId(empId);
        
        // Generate email from name
        String email = name.toLowerCase()
            .split(" ")[0] + "@freshfold.com";
        personnel.setEmail(email);
        
        personnel.setPhoneNumber(phone);
        personnel.setYearsExperience(experience);
        personnel.setRating(rating);
        personnel.setPassword("password"); // Default password for all personnel
        
        personnelRepository.save(personnel);
    }
}