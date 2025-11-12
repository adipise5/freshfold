package com.freshfold.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "laundry_orders")
public class LaundryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private Personnel personnel;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false)
    private String serviceType;

    @Column(nullable = false)
    private Integer urgencyDays;

    @Column(nullable = false)
    private String photoUrl;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(length = 500)
    private String rejectionReason;

    @Column
    private Integer studentRating; // New field: Rating (1–5)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime acceptedAt;
    private LocalDateTime collectionAt;
    private LocalDateTime washingAt;
    private LocalDateTime ironingAt;
    private LocalDateTime completedAt;

    public LaundryOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Personnel getPersonnel() { return personnel; }
    public void setPersonnel(Personnel personnel) { this.personnel = personnel; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

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

    public Integer getStudentRating() { return studentRating; }
    public void setStudentRating(Integer studentRating) { this.studentRating = studentRating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }

    public LocalDateTime getCollectionAt() { return collectionAt; }
    public void setCollectionAt(LocalDateTime collectionAt) { this.collectionAt = collectionAt; }

    public LocalDateTime getWashingAt() { return washingAt; }
    public void setWashingAt(LocalDateTime washingAt) { this.washingAt = washingAt; }

    public LocalDateTime getIroningAt() { return ironingAt; }
    public void setIroningAt(LocalDateTime ironingAt) { this.ironingAt = ironingAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
