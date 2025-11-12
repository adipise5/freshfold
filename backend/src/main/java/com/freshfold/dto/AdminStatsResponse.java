package com.freshfold.dto;

import java.util.List;

public class AdminStatsResponse {
    private Long totalOrders;
    private Long completedOrders;
    private Long pendingOrders;
    private Double totalRevenue;
    private List<HostelStats> hostelStats;
    private List<PersonnelStats> personnelStats;

    public AdminStatsResponse() {}

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(Long completedOrders) { this.completedOrders = completedOrders; }

    public Long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(Long pendingOrders) { this.pendingOrders = pendingOrders; }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public List<HostelStats> getHostelStats() { return hostelStats; }
    public void setHostelStats(List<HostelStats> hostelStats) { this.hostelStats = hostelStats; }

    public List<PersonnelStats> getPersonnelStats() { return personnelStats; }
    public void setPersonnelStats(List<PersonnelStats> personnelStats) { this.personnelStats = personnelStats; }

    public static class HostelStats {
        private String hostelName;
        private Long orderCount;
        private Double revenue;

        public HostelStats() {}

        public HostelStats(String hostelName, Long orderCount, Double revenue) {
            this.hostelName = hostelName;
            this.orderCount = orderCount;
            this.revenue = revenue;
        }

        public String getHostelName() { return hostelName; }
        public void setHostelName(String hostelName) { this.hostelName = hostelName; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public Double getRevenue() { return revenue; }
        public void setRevenue(Double revenue) { this.revenue = revenue; }
    }

    public static class PersonnelStats {
        private String name;
        private Long ordersCompleted;
        private Double earnings;
        private Double rating;

        public PersonnelStats() {}

        public PersonnelStats(String name, Long ordersCompleted, Double earnings, Double rating) {
            this.name = name;
            this.ordersCompleted = ordersCompleted;
            this.earnings = earnings;
            this.rating = rating;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Long getOrdersCompleted() { return ordersCompleted; }
        public void setOrdersCompleted(Long ordersCompleted) { this.ordersCompleted = ordersCompleted; }

        public Double getEarnings() { return earnings; }
        public void setEarnings(Double earnings) { this.earnings = earnings; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }
}