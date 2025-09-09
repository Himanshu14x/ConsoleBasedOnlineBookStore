package com.bookstore.model;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private List<Book> items;
    private double totalPrice;

    public Order() {
    }

    public Order(String orderId, String userId, List<Book> items, double totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public List<Book> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
