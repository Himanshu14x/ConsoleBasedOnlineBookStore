package com.bookstore.service;

import com.bookstore.dao.BookDao;
import com.bookstore.model.Book;

import java.util.List;

public class RecommendationService {
    private final BookDao bookDao;

    public RecommendationService(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public List<Book> recommend(String lastGenre) {
        if (lastGenre == null) return java.util.Collections.emptyList();
        return bookDao.findByGenreAndMinRating(lastGenre, 4.0, 5);
    }
}
