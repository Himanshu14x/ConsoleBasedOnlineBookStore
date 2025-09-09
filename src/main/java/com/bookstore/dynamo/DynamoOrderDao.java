package com.bookstore.dynamo;

import com.bookstore.dao.OrderDao;
import com.bookstore.model.Order;
import com.bookstore.model.Book;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class DynamoOrderDao implements OrderDao {
    private final DynamoDbClient client;
    private final String tableName = "Orders";

    public DynamoOrderDao(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public void saveOrder(Order order) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("orderId", AttributeValue.builder().s(order.getOrderId()).build());
            item.put("userId", AttributeValue.builder().s(order.getUserId()).build());
            // create list of maps for items
            List<AttributeValue> itemsList = new ArrayList<>();
            for (Book b : order.getItems()) {
                Map<String, AttributeValue> m = new HashMap<>();
                m.put("id", AttributeValue.builder().s(b.getId()).build());
                m.put("title", AttributeValue.builder().s(b.getTitle()).build());
                m.put("price", AttributeValue.builder().n(String.valueOf(b.getPrice())).build());
                m.put("author", AttributeValue.builder().s(b.getAuthor()).build());
                m.put("genre", AttributeValue.builder().s(b.getGenre()).build());
                m.put("rating", AttributeValue.builder().n(String.valueOf(b.getRating())).build());
                itemsList.add(AttributeValue.builder().m(m).build());
            }
            item.put("items", AttributeValue.builder().l(itemsList).build());
            item.put("totalPrice", AttributeValue.builder().n(String.valueOf(order.getTotalPrice())).build());
            PutItemRequest req = PutItemRequest.builder().tableName(tableName).item(item).build();
            client.putItem(req);
            System.out.println("DynamoOrderDao.saveOrder: saved " + order.getOrderId());
        } catch (Exception e) {
            System.out.println("DynamoOrderDao.saveOrder error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public List<Order> getOrdersForUser(String userId) {
        try {
            var req = software.amazon.awssdk.services.dynamodb.model.QueryRequest.builder()
                    .tableName(tableName)
                    .indexName("userId-index")
                    .keyConditionExpression("userId = :uid")
                    .expressionAttributeValues(Map.of(":uid", AttributeValue.builder().s(userId).build()))
                    .build();
            var res = client.query(req);
            List<Order> out = new ArrayList<>();
            for (var item : res.items()) {
                String orderId = item.getOrDefault("orderId", AttributeValue.builder().s("").build()).s();
                double total = item.containsKey("totalPrice") ? Double.parseDouble(item.get("totalPrice").n()) : 0.0;
                List<Book> books = new ArrayList<>();
                if (item.containsKey("items")) {
                    List<AttributeValue> avList = item.get("items").l();
                    for (AttributeValue av : avList) {
                        Map<String, AttributeValue> m = av.m();
                        String id = m.getOrDefault("id", AttributeValue.builder().s("").build()).s();
                        String title = m.getOrDefault("title", AttributeValue.builder().s("").build()).s();
                        String author = m.getOrDefault("author", AttributeValue.builder().s("").build()).s();
                        String genre = m.getOrDefault("genre", AttributeValue.builder().s("").build()).s();
                        double rating = m.containsKey("rating") ? Double.parseDouble(m.get("rating").n()) : 0.0;
                        double price = m.containsKey("price") ? Double.parseDouble(m.get("price").n()) : 0.0;
                        books.add(new Book(id, title, author, genre, rating, price));
                    }
                }
                out.add(new Order(orderId, userId, books, total));
            }
            return out;
        } catch (Exception e) {
            System.out.println("DynamoOrderDao.getOrdersForUser error: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
