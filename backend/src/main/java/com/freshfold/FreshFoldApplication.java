package com.freshfold;

import com.freshfold.config.FileStorageProperties;   // ✅ ADD THIS
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;   // ✅ ADD THIS
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Spring Boot Application for FreshFold Laundry Management System
 *
 * This is the entry point of the application.
 */
@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)   // ✅ ADD THIS
public class FreshFoldApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshFoldApplication.class, args);

        System.out.println("\n========================================");
        System.out.println("🧺 FreshFold Backend Started Successfully!");
        System.out.println("========================================");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("🗄️ Connected Database: MySQL (freshfold)");
        System.out.println("========================================\n");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
