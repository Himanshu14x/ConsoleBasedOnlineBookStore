package com.bookstore.util;

import java.util.UUID;

public class IdGenerator {
    public static String nextOrderId() {

        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
