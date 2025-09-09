package com.bookstore;

import com.bookstore.service.MenuService;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello! Welcome to Amazon Bookstore");


        MenuService menu = new MenuService();
        menu.start();
    }
}
