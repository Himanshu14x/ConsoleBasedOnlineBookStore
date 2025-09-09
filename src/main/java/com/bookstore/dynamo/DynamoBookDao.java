package com.bookstore.dynamo;

import com.bookstore.dao.BookDao;
import com.bookstore.model.Book;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoBookDao implements BookDao {
    private final DynamoDbClient client;
    private final String tableName = "Books";

    public DynamoBookDao(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public Book getById(String id) {
        try {
            Map<String, AttributeValue> key = Map.of("bookId", AttributeValue.builder().s(id).build());
            GetItemRequest req = GetItemRequest.builder().tableName(tableName).key(key).build();
            var res = client.getItem(req);
            if (!res.hasItem() || res.item().isEmpty()) return null;
            return fromItem(res.item());
        } catch (Exception e) {
            System.out.println("DynamoBookDao.getById error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Book> searchByTitleOrAuthor(String query) {
        if (query == null || query.isBlank()) return Collections.emptyList();
        String q = query.toLowerCase(Locale.ROOT);
        // For simplicity, perform a Scan and filter client-side (OK for small dataset / demo)
        try {
            ScanRequest req = ScanRequest.builder().tableName(tableName).build();
            ScanResponse res = client.scan(req);
            return res.items().stream()
                    .map(this::fromItem)
                    .filter(Objects::nonNull)
                    .filter(b -> b.getTitle().toLowerCase(Locale.ROOT).contains(q) ||
                            b.getAuthor().toLowerCase(Locale.ROOT).contains(q))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("DynamoBookDao.searchByTitleOrAuthor error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Book> findByGenreAndMinRating(String genre, double minRating, int limit) {
        if (genre == null) return Collections.emptyList();
        try {
            ScanRequest req = ScanRequest.builder().tableName(tableName).build();
            ScanResponse res = client.scan(req);
            return res.items().stream()
                    .map(this::fromItem)
                    .filter(Objects::nonNull)
                    .filter(b -> b.getGenre().equalsIgnoreCase(genre) && b.getRating() >= minRating)
                    .sorted((a,b) -> Double.compare(b.getRating(), a.getRating()))
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("DynamoBookDao.findByGenreAndMinRating error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void seedDemoBooks() {
        // keep this for compatibility but prefer to run the BookSeeder which writes to Dynamo
        // Here we do nothing (or could call BookSeeder programmatically)
        System.out.println("DynamoBookDao.seedDemoBooks: Use BookSeeder to populate the Books table.");
    }

    // helper to convert Dynamo item to Book
    private Book fromItem(Map<String, AttributeValue> item) {
        try {
            String id = item.getOrDefault("bookId", AttributeValue.builder().s("").build()).s();
            String title = item.getOrDefault("title", AttributeValue.builder().s("").build()).s();
            String author = item.getOrDefault("author", AttributeValue.builder().s("").build()).s();
            String genre = item.getOrDefault("genre", AttributeValue.builder().s("").build()).s();
            double rating = item.containsKey("rating") ? Double.parseDouble(item.get("rating").n()) : 0.0;
            double price = item.containsKey("price") ? Double.parseDouble(item.get("price").n()) : 0.0;
            return new Book(id, title, author, genre, rating, price);
        } catch (Exception e) {
            System.out.println("DynamoBookDao.fromItem error: " + e.getMessage());
            return null;
        }
    }

    // helper to write a Book to Dynamo
    public boolean putBook(Book b) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("bookId", AttributeValue.builder().s(b.getId()).build());
            item.put("title", AttributeValue.builder().s(b.getTitle()).build());
            item.put("author", AttributeValue.builder().s(b.getAuthor()).build());
            item.put("genre", AttributeValue.builder().s(b.getGenre()).build());
            item.put("rating", AttributeValue.builder().n(String.valueOf(b.getRating())).build());
            item.put("price", AttributeValue.builder().n(String.valueOf(b.getPrice())).build());
            PutItemRequest req = PutItemRequest.builder().tableName(tableName).item(item).build();
            client.putItem(req);
            return true;
        } catch (Exception e) {
            System.out.println("DynamoBookDao.putBook error: " + e.getMessage());
            return false;
        }
    }
}
