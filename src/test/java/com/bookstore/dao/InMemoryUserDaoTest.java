package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.dao.inmemory.InMemoryUserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserDaoTest {
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new InMemoryUserDao();
    }

    @Test
    void testSaveAndGetUser() {
        User u = new User("u1", "Alice", "pass");
        userDao.createUser(u);

        User fetched = userDao.getById("u1");
        assertNotNull(fetched);
        assertEquals("Alice", fetched.getName());
    }

    @Test
    void testInvalidUserReturnsNull() {
        assertNull(userDao.getById("doesNotExist"));
    }
}
