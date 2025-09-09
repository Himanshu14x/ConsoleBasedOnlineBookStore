package com.bookstore.service;

import com.bookstore.dao.OrderDao;
import com.bookstore.model.Book;
import com.bookstore.model.Order;
import com.bookstore.util.IdGenerator;

import java.util.List;

public class OrderService {
    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Order checkout(String userId, List<Book> items) {
        double total = items.stream().mapToDouble(Book::getPrice).sum();
        String orderId = IdGenerator.nextOrderId();
        Order o = new Order(orderId, userId, items, total);
        orderDao.saveOrder(o);
        return o;
    }

    public java.util.List<Order> getOrders(String userId) {
        return orderDao.getOrdersForUser(userId);
    }
}
