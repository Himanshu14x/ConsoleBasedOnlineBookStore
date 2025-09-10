package com.bookstore.model;

import com.bookstore.service.CartService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testAddBookAndTotalPrice() {
        CartService cart = new CartService();
        Book b1 = new Book("B001", "Clean Code", "Robert C. Martin", "Programming", 4.5, 399.0);
        Book b2 = new Book("B002", "Effective Java", "Joshua Bloch", "Programming", 4.8, 499.0);

        cart.add(b1);
        cart.add(b2);

        assertEquals(2, cart.list().size());
        assertEquals(898.0, cart.subtotal(), 0.001);
    }

    @Test
    void testRemoveBook() {
        CartService cart = new CartService();
        Book b1 = new Book("B001", "Clean Code", "Robert C. Martin", "Programming", 4.5, 399.0);

        cart.add(b1);
        assertEquals(1, cart.list().size());

        cart.remove(0);
        assertTrue(cart.list().isEmpty());
    }

    @Test
    void testClearCart() {
        CartService cart = new CartService();
        cart.add(new Book("B001","A","A", "X", 4.0, 100.0));
        cart.add(new Book("B002","B","B", "Y", 4.0, 200.0));

        cart.clear();
        assertTrue(cart.list().isEmpty());
        assertEquals(0.0, cart.subtotal(), 0.001);
    }
}
