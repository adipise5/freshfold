package com.freshfold.dto;

public class OrderItemDTO {
    private String itemType;
    private Integer quantity;
    private Double pricePerItem;

    public OrderItemDTO() {}

    public OrderItemDTO(String itemType, Integer quantity, Double pricePerItem) {
        this.itemType = itemType;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
    }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPricePerItem() { return pricePerItem; }
    public void setPricePerItem(Double pricePerItem) { this.pricePerItem = pricePerItem; }
}