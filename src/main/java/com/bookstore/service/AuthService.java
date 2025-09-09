package com.bookstore.service;

import com.bookstore.dao.UserDao;
import com.bookstore.model.User;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean login(String userId, String password) {
        return userDao.checkCredentials(userId, password);
    }

    public boolean register(String name, String userId, String password) {
        User u = new User(userId, name, password);
        return userDao.createUser(u);
    }

    public User getUser(String userId) {
        return userDao.getById(userId);
    }
}
