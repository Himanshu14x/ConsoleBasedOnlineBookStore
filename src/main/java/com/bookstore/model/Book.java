package com.bookstore.model;

public class Book {
    private String id, title, author, genre;
    private double rating, price;

    public Book() {

    }

    public Book(String id, String title, String author, String genre, double rating, double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.rating = rating;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    public double getPrice() {
        return price;
    }
}
