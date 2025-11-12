package com.freshfold.dto;

import java.util.List;

public class OrderRequest {
    private Long studentId;
    private Long personnelId;
    private List<OrderItemDTO> items;
    private String serviceType;
    private Integer urgencyDays;
    private String photoUrl;
    private String pickupLocation;

    public OrderRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getPersonnelId() { return personnelId; }
    public void setPersonnelId(Long personnelId) { this.personnelId = personnelId; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Integer getUrgencyDays() { return urgencyDays; }
    public void setUrgencyDays(Integer urgencyDays) { this.urgencyDays = urgencyDays; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
}
