package com.bookstore.dao;

import com.bookstore.model.Book;

import java.util.List;

public interface BookDao {
    List<Book> searchByTitleOrAuthor(String query);

    List<Book> findByGenreAndMinRating(String genre, double minRating, int limit);

    void seedDemoBooks();
    Book getById(String id);

}
