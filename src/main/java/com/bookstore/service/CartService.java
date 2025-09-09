package com.bookstore.service;

import com.bookstore.model.Book;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final List<Book> cart = new ArrayList<>();

    public void add(Book b) {
        cart.add(b);
    }

    public List<Book> list() {
        return new ArrayList<>(cart);
    }

    public double subtotal() {
        return cart.stream().mapToDouble(Book::getPrice).sum();
    }

    public boolean isEmpty() {
        return cart.isEmpty();
    }

    public void clear() {
        cart.clear();
    }

    public void remove(int index) {
        if (index >= 0 && index < cart.size()) cart.remove(index);
    }
}
