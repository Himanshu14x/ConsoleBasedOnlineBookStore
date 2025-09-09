package com.bookstore.dynamo;

import com.bookstore.dao.UserDao;
import com.bookstore.model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class DynamoUserDao implements UserDao {
    private final DynamoDbClient client;
    private final String tableName = "Users";

    public DynamoUserDao(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public User getById(String userId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("userId", AttributeValue.builder().s(userId).build());
            GetItemRequest req = GetItemRequest.builder().tableName(tableName).key(key).build();
            var res = client.getItem(req);
            if (!res.hasItem() || res.item().isEmpty()) return null;
            Map<String, AttributeValue> item = res.item();
            String name = item.getOrDefault("name", AttributeValue.builder().s("").build()).s();
            String pwd = item.getOrDefault("password", AttributeValue.builder().s("").build()).s();

            return new User(userId, name, pwd);
        } catch (Exception e) {
            System.out.println("DynamoUserDao.getById error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public boolean createUser(User user) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("userId", AttributeValue.builder().s(user.getUserId()).build());
            item.put("name", AttributeValue.builder().s(user.getName() == null ? "" : user.getName()).build());
            item.put("password", AttributeValue.builder().s(user.getPassword() == null ? "" : user.getPassword()).build());
            item.put("recentBooks", AttributeValue.builder().l(new ArrayList<>()).build());
            PutItemRequest req = PutItemRequest.builder().tableName(tableName).item(item).build();
            client.putItem(req);

            return true;
        } catch (Exception e) {
            System.out.println("DynamoUserDao.createUser error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




    @Override
    public boolean checkCredentials(String userId, String password) {
        User u = getById(userId);
        return u != null && u.getPassword().equals(password);
    }

    @Override
    public java.util.List<String> getRecentBooks(String userId) {
        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("userId", AttributeValue.builder().s(userId).build());
            GetItemRequest req = GetItemRequest.builder().tableName(tableName).key(key).projectionExpression("recentBooks").build();
            var res = client.getItem(req);
            if (!res.hasItem() || res.item().isEmpty()) return new ArrayList<>();
            var item = res.item();
            var attr = item.get("recentBooks");
            if (attr == null) return new ArrayList<>();
            List<AttributeValue> list = attr.l();
            List<String> out = new ArrayList<>();
            for (AttributeValue a : list) out.add(a.s());
            return out;
        } catch (Exception e) {
            System.out.println("DynamoUserDao.getRecentBooks error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void addRecentBook(String userId, String bookId) {
        try {
            // get current list
            List<String> cur = getRecentBooks(userId);
            cur.add(bookId);
            if (cur.size() > 10) cur = cur.subList(cur.size()-10, cur.size());

            // build Dynamo list attribute
            List<AttributeValue> avList = new ArrayList<>();
            for (String s : cur) avList.add(AttributeValue.builder().s(s).build());

            // use UpdateItem to set only the recentBooks attribute
            Map<String, AttributeValue> key = Map.of("userId", AttributeValue.builder().s(userId).build());
            Map<String, AttributeValue> exprValues = Map.of(":r", AttributeValue.builder().l(avList).build());
            software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest req =
                    software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest.builder()
                            .tableName(tableName)
                            .key(key)
                            .updateExpression("SET recentBooks = :r")
                            .expressionAttributeValues(exprValues)
                            .build();
            client.updateItem(req);
        } catch (Exception e) {
            System.out.println("DynamoUserDao.addRecentBook error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
