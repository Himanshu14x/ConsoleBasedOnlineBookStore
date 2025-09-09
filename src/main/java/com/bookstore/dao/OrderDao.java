package com.bookstore.dao;

import com.bookstore.model.Order;

import java.util.List;

public interface OrderDao {
    void saveOrder(Order order);

    List<Order> getOrdersForUser(String userId);
}
