package com.bookstore.util;

import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine() {
        return scanner.nextLine().trim();
    }

    public static int readIntSafe() {
        String s = readLine();
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return Integer.MIN_VALUE;
        }
    }
}
