package com.freshfold.dto;

public class LoginResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private Object userData;

    public LoginResponse() {}

    public LoginResponse(Long id, String fullName, String email, String role, String token, Object userData) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.userData = userData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Object getUserData() { return userData; }
    public void setUserData(Object userData) { this.userData = userData; }
}