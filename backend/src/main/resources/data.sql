package com.freshfold.model;

public class Admin extends User {
    private String adminId;

    public Admin() {
        super();
        this.userType = "ADMIN";
    }

    public Admin(String email, String password, String fullName, String phoneNumber, String adminId) {
        super(email, password, fullName, phoneNumber, "ADMIN");
        this.adminId = adminId;
    }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
}