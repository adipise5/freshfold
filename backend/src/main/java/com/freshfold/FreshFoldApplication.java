package com.freshfold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main Spring Boot Application for FreshFold Laundry Management System
 *
 * This is the entry point of the application.
 * @SpringBootApplication annotation enables:
 * - Component scanning
 * - Auto-configuration
 * - Additional configuration
 */
@SpringBootApplication
public class FreshFoldApplication {

    public static void main(String[] args) {
        // ✅ This line actually starts Spring Boot + Tomcat
        SpringApplication.run(FreshFoldApplication.class, args);

        System.out.println("\n========================================");
        System.out.println("🧺 FreshFold Backend Started Successfully!");
        System.out.println("========================================");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("🗄️ Connected Database: MySQL (freshfold)");
        System.out.println("========================================\n");

        // Keeps backend running even if something triggers auto-exit
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Configure CORS to allow frontend (React) to communicate with backend
     * Allows requests from http://localhost:3000
     */
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
