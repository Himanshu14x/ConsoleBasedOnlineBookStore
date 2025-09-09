package com.bookstore.dao.inmemory;

import com.bookstore.dao.OrderDao;
import com.bookstore.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InMemoryOrderDao implements OrderDao {
    private final Map<String, List<Order>> byUser = new ConcurrentHashMap<>();

    @Override
    public void saveOrder(Order order) {
        byUser.computeIfAbsent(order.getUserId(), k -> new ArrayList<>()).add(order);
    }

    @Override
    public List<Order> getOrdersForUser(String userId) {
        return byUser.getOrDefault(userId, new ArrayList<>());
    }
}
