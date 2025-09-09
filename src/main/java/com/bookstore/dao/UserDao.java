package com.bookstore.dao;

import com.bookstore.model.User;

public interface UserDao {
    User getById(String userId);

    boolean createUser(User user);

    boolean checkCredentials(String userId, String password);

    java.util.List<String> getRecentBooks(String userId);

    void addRecentBook(String userId, String bookId);
}
