package com.bookstore.dynamo;

import com.bookstore.dao.UserDao;
import com.bookstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test that requires DynamoDB Local to be running on port 8000.
 * Disabled by default so unit test runs are fast and independent.
 */
@Disabled("Integration test - enable only when DynamoDB Local is running")
class DynamoUserDaoIntegrationTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        DynamoDbClient client = DynamoDbClientProvider.getClient();
        assertNotNull(client, "DynamoDbClientProvider returned null. Ensure config is set.");
        userDao = new DynamoUserDao(client);
    }

    @Test
    void testSaveAndGetUser() {
        User u = new User("intTestUser", "ITUser", "secret");
        userDao.createUser(u);

        User fetched = userDao.getById("intTestUser");
        assertNotNull(fetched);
        assertEquals("ITUser", fetched.getName());
    }
}
