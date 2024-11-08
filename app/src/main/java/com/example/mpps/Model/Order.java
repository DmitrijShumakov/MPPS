package com.example.mpps.Model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private double totalAmount;
    private String date;
    private String deliveryType;
    private String address;
    private String storeName;
    private String status;
    private List<OrderItem> items;

    // Constructor
    public Order(int orderId, int userId, double totalAmount, String date, String deliveryType, String address, String storeName, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.date = date;
        this.deliveryType = deliveryType;
        this.address = address;
        this.storeName = storeName;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getDate() {
        return date;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public String getAddress() {
        return address;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Inner class for order items
    public static class OrderItem {
        private String name;
        private int quantity;
        private double price;

        public OrderItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }
    }
}
