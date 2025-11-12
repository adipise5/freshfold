package com.freshfold.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private LaundryOrder order;

    @Column(nullable = false)
    private String itemType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double pricePerItem;

    public OrderItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LaundryOrder getOrder() { return order; }
    public void setOrder(LaundryOrder order) { this.order = order; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPricePerItem() { return pricePerItem; }
    public void setPricePerItem(Double pricePerItem) { this.pricePerItem = pricePerItem; }

    public Double getItemTotal() {
        return quantity * pricePerItem;
    }

    // Static Utility Pricing Map
    public static Double getPriceForItem(String itemType) {
        return switch (itemType) {
            case "Shirt" -> 15.0;
            case "T-Shirt" -> 12.0;
            case "Pants" -> 20.0;
            case "Jeans" -> 25.0;
            case "Undergarments" -> 8.0;
            case "Socks" -> 5.0;
            case "Bed Sheets" -> 30.0;
            case "Towels" -> 15.0;
            default -> 10.0;
        };
    }
}
