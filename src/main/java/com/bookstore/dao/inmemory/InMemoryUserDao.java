package com.bookstore.dao.inmemory;


import com.bookstore.dao.UserDao;
import com.bookstore.model.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class InMemoryUserDao implements UserDao {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, List<String>> recent = new ConcurrentHashMap<>();

    public InMemoryUserDao() {
        User demo = new User("himanshu", "Himanshu", "password");
        users.put(demo.getUserId(), demo);
        recent.put(demo.getUserId(), new ArrayList<>());
    }

    @Override
    public User getById(String userId) {
        return users.get(userId);
    }

    @Override
    public boolean createUser(User user) {
        if (users.containsKey(user.getUserId())) return false;
        users.put(user.getUserId(), user);
        recent.put(user.getUserId(), new ArrayList<>());
        return true;
    }

    @Override
    public boolean checkCredentials(String userId, String password) {
        User u = users.get(userId);
        return u != null && u.getPassword().equals(password);
    }

    @Override
    public java.util.List<String> getRecentBooks(String userId) {
        return new ArrayList<>(recent.getOrDefault(userId, new ArrayList<>()));
    }

    @Override
    public void addRecentBook(String userId, String bookId) {
        recent.computeIfAbsent(userId, k -> new ArrayList<>());
        List<String> lst = recent.get(userId);
        lst.add(bookId);
        if (lst.size() > 10) lst.remove(0);
    }
}
