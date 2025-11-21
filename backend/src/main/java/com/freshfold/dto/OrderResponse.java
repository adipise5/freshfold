package com.freshfold.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    private Long id;
    private StudentInfo student;
    private PersonnelInfo personnel;
    private List<OrderItemDTO> items;
    private String status;
    private String serviceType;
    private Integer urgencyDays;
    private String photoUrl;
    private Double totalPrice;
    private String pickupLocation;
    private String rejectionReason;
    private String createdAt;
    private String updatedAt;

    private Integer studentRating; // rating given by student

    // NEW: status history for timeline
    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();

    public OrderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }

    public PersonnelInfo getPersonnel() { return personnel; }
    public void setPersonnel(PersonnelInfo personnel) { this.personnel = personnel; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Integer getUrgencyDays() { return urgencyDays; }
    public void setUrgencyDays(Integer urgencyDays) { this.urgencyDays = urgencyDays; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Integer getStudentRating() { return studentRating; }
    public void setStudentRating(Integer studentRating) { this.studentRating = studentRating; }

    public List<StatusHistoryEntry> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistoryEntry> statusHistory) {
        this.statusHistory = statusHistory;
    }

    // ================= Inner Classes ================= //

    public static class StudentInfo {
        private Long id;
        private String fullName;
        private String studentId;
        private String hostel;
        private String roomNumber;
        private String phoneNumber;

        public StudentInfo() {}

        public StudentInfo(Long id,
                           String fullName,
                           String studentId,
                           String hostel,
                           String roomNumber,
                           String phoneNumber) {
            this.id = id;
            this.fullName = fullName;
            this.studentId = studentId;
            this.hostel = hostel;
            this.roomNumber = roomNumber;
            this.phoneNumber = phoneNumber;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getHostel() { return hostel; }
        public void setHostel(String hostel) { this.hostel = hostel; }

        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    public static class PersonnelInfo {
        private Long id;
        private String fullName;
        private String employeeId;
        private String phoneNumber;
        private Double rating;

        public PersonnelInfo() {}

        public PersonnelInfo(Long id,
                             String fullName,
                             String employeeId,
                             String phoneNumber,
                             Double rating) {
            this.id = id;
            this.fullName = fullName;
            this.employeeId = employeeId;
            this.phoneNumber = phoneNumber;
            this.rating = rating;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }

    // NEW: Timeline entry used by frontend as order.statusHistory[]
    public static class StatusHistoryEntry {
        private String status;
        private String timestamp;

        public StatusHistoryEntry() {}

        public StatusHistoryEntry(String status, String timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
