package com.bookstore.service;

import com.bookstore.dao.BookDao;
import com.bookstore.dao.UserDao;
import com.bookstore.dao.OrderDao;
import com.bookstore.dao.inmemory.InMemoryBookDao;
import com.bookstore.dao.inmemory.InMemoryUserDao;
import com.bookstore.dao.inmemory.InMemoryOrderDao;
import com.bookstore.dynamo.DynamoDbClientProvider;
import com.bookstore.dynamo.DynamoUserDao;
import com.bookstore.dynamo.DynamoOrderDao;
import com.bookstore.model.Book;
import com.bookstore.model.User;
import com.bookstore.model.Order;
import com.bookstore.util.ConsoleUtil;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

public class MenuService {
    private final UserDao userDao;
    private final BookDao bookDao;
    private final OrderDao orderDao;
    private final AuthService authService;
    private final CartService cartService;
    private final OrderService orderService;
    private final RecommendationService recommendationService;
    private User currentUser = null;
    private boolean running = true;

    public MenuService() {
        DynamoDbClient client = DynamoDbClientProvider.getClient();
        if (client != null) {
            System.out.println("Attempting to use DynamoDB-backed DAOs (make sure tables exist)...");
            this.userDao = new DynamoUserDao(client);
            this.orderDao = new DynamoOrderDao(client);
            this.bookDao = new com.bookstore.dynamo.DynamoBookDao(client);
        } else {
            this.userDao = new InMemoryUserDao();
            this.bookDao = new InMemoryBookDao();
            this.orderDao = new InMemoryOrderDao();
        }
        this.authService = new AuthService(userDao);
        this.cartService = new CartService();
        this.orderService = new OrderService(orderDao);
        this.recommendationService = new RecommendationService(bookDao);
    }

    public void start() {
        while (running) showInitialMenu();
    }

    private void showInitialMenu() {
        System.out.println("\n1. login\n2. register\n3. exit");
        System.out.print("Enter choice: ");
        String input = ConsoleUtil.readLine();
        switch (input) {
            case "1" -> doLogin();
            case "2" -> doRegister();
            case "3" -> doExit();
            default -> System.out.println("invalid input.");
        }
    }

    private void doLogin() {
        System.out.print("enter user ID: ");
        String id = ConsoleUtil.readLine();
        System.out.print("enter password: ");
        String pwd = ConsoleUtil.readLine();
        if (authService.login(id, pwd)) {
            currentUser = authService.getUser(id);
            System.out.println("Login success. Welcome, " + currentUser.getName());
            showHomeMenu();
        } else {
            System.out.println("invalid credentials.");
            System.out.println("1. login\n2. register");
            System.out.print("Enter choice: ");
            String c = ConsoleUtil.readLine();
            if ("1".equals(c)) doLogin();
            else if ("2".equals(c)) doRegister();
        }
    }

    private void doRegister() {
        System.out.print("enter name: ");
        String name = ConsoleUtil.readLine();
        System.out.print("enter user ID: ");
        String id = ConsoleUtil.readLine();
        System.out.print("enter password: ");
        String pwd = ConsoleUtil.readLine();
        boolean ok = authService.register(name, id, pwd);
        if (ok) {
            System.out.println("Registration successful. Logged in as " + name);
            currentUser = authService.getUser(id);
            showHomeMenu();
        } else {
            System.out.println("userId already exists. Try logging in.");
        }
    }

    private void doExit() {
        System.out.println("Thank you for visiting Amazon book store");
        try {
            Thread.sleep(3000);
        } catch (Exception ignored) {
        }
        System.exit(0);
    }

    private void showHomeMenu() {
        boolean inHome = true;
        while (inHome) {
            System.out.println("\nHome Menu:\nHello " + currentUser.getName());
            System.out.println("1. search a book\n2. view cart\n3. checkout\n4. recent books\n5. recommended books\n6. my orders\n7. log out");
            System.out.print("Enter choice: ");
            String c = ConsoleUtil.readLine();
            switch (c) {
                case "1" -> doSearch();
                case "2" -> doViewCart();
                case "3" -> doCheckout();
                case "4" -> doRecentBooks();
                case "5" -> doRecommended();
                case "6" -> doMyOrders();
                case "7" -> {
                    System.out.println("logged out successfully");
                    currentUser = null;
                    inHome = false;
                }
                default -> System.out.println("invalid input.");
            }
        }
    }

    private void doSearch() {
        while (true) {
            System.out.print("Search a book with title/author: ");
            String q = ConsoleUtil.readLine();
            List<Book> found = bookDao.searchByTitleOrAuthor(q);
            if (found.isEmpty()) {
                System.out.println("no matching books found.");
                System.out.println("1. search again\n2. return to previous menu\n3. log out");
                System.out.print("Enter choice: ");
                String c = ConsoleUtil.readLine();
                if ("1".equals(c)) continue;
                if("2".equals(c)) return;
                if ("3".equals(c)) { System.out.println("logged out successfully"); currentUser = null; return; }
                else { System.out.println("invalid input"); continue; }
            } else {

                boolean inFoundMenu = true;
                while (inFoundMenu) {
                    System.out.println("Found books:");

                    for (int i = 0; i < found.size(); i++) {
                        Book b = found.get(i);
                        System.out.println((i + 1) + ". " + b.getTitle() + " by " + b.getAuthor());
                    }

                    System.out.println();
                    System.out.println("Options:\n1. view details\n2. search again\n3. return to previous menu\n4. log out");
                    System.out.print("Enter option: ");
                    String opt = ConsoleUtil.readLine();

                    if ("1".equals(opt)) {
                        System.out.print("Enter book number to view: ");
                        int num = ConsoleUtil.readIntSafe();
                        if (num == Integer.MIN_VALUE || num < 1 || num > found.size()) {
                            System.out.println("invalid input");

                            continue;
                        }
                        Book sel = found.get(num - 1);

                        if (currentUser != null) {
                            try { userDao.addRecentBook(currentUser.getUserId(), sel.getId()); } catch (Exception ignored) {}
                        }
                        System.out.println("Title: " + sel.getTitle());
                        System.out.println("Author: " + sel.getAuthor());
                        System.out.println("Rating: " + sel.getRating());
                        System.out.println("Genre: " + sel.getGenre());
                        System.out.println("Price: " + sel.getPrice());
                        System.out.println("1. add to cart\n2. return to previous menu");
                        System.out.print("Enter option: ");
                        String v = ConsoleUtil.readLine();
                        if ("1".equals(v)) {
                            cartService.add(sel);
                            System.out.println("book has been added to cart.");
                            return;
                        } else {

                            continue;
                        }
                    } else if ("2".equals(opt)) {

                        inFoundMenu = false;
                    } else if ("3".equals(opt)) {

                        return;
                    } else if ("4".equals(opt)) {
                        System.out.println("logged out successfully");
                        currentUser = null;
                        return;
                    } else {
                        System.out.println("invalid input");

                    }
                }

                continue;
            }
        }
    }


    private void doViewCart() {
        if (cartService.isEmpty()) {
            System.out.println("cart is empty.");
            return;
        }

        boolean inCartMenu = true;
        while (inCartMenu) {
            System.out.println("Your cart:");
            List<Book> items = cartService.list();
            for (int i = 0; i < items.size(); i++) {
                Book b = items.get(i);
                System.out.println((i + 1) + ". " + b.getTitle() + " - Rs. " + b.getPrice());
            }
            System.out.println("Subtotal: Rs. " + cartService.subtotal());
            System.out.println("1. checkout\n2. remove a book\n3. return to previous menu");
            System.out.print("Enter choice: ");
            String c = ConsoleUtil.readLine();

            if ("1".equals(c)) {
                // Reuse the shared checkout UI/logic
                doCheckout();
                // after doCheckout returns:
                // - if user paid, cartService.clear() already happened in doCheckout
                // - if user returned without paying, cart stays as-is
                // If cart is now empty, exit cart menu; otherwise stay in cart menu
                if (cartService.isEmpty()) inCartMenu = false;
            } else if ("2".equals(c)) {
                // Remove a book
                System.out.print("Enter book number to remove: ");
                int idx = ConsoleUtil.readIntSafe();
                if (idx == Integer.MIN_VALUE || idx < 1 || idx > items.size()) {
                    System.out.println("invalid input");
                    // stay in cart menu
                } else {
                    cartService.remove(idx - 1);
                    System.out.println("Book removed from cart.");
                    if (cartService.isEmpty()) {
                        System.out.println("cart is now empty.");
                        inCartMenu = false;
                    }
                }
            } else if ("3".equals(c)) {
                // Return to previous menu (home)
                inCartMenu = false;
            } else {
                System.out.println("invalid input");
                // stay in cart menu and re-display options
            }
        }
    }



    private void doCheckout() {
        if (cartService.isEmpty()) {
            System.out.println("please add an item to the cart first.");
            return;
        }

        // Show cart items, prices and total
        List<Book> items = cartService.list();
        System.out.println("Order summary:");
        double total = 0.0;
        for (int i = 0; i < items.size(); i++) {
            Book b = items.get(i);
            System.out.println((i + 1) + ". " + b.getTitle() + " - Rs. " + b.getPrice());
            total += b.getPrice();
        }
        System.out.println("Order total: Rs. " + total);

        // Prompt for payment or return; keep prompting until valid choice
        while (true) {
            System.out.println("Options:");
            System.out.println("1. Pay");
            System.out.println("2. return to previous menu");
            System.out.print("Enter choice: ");
            String c = ConsoleUtil.readLine();

            if ("1".equals(c)) {
                // perform checkout: create order, persist it
                Order o = orderService.checkout(currentUser.getUserId(), items);
                cartService.clear();
                System.out.println("Order success! Order Id: " + o.getOrderId());
                System.out.println("Paid Rs. " + o.getTotalPrice());
                // return to home menu
                return;
            } else if ("2".equals(c)) {
                // return to previous menu (home), keep cart intact
                return;
            } else {
                System.out.println("invalid input");
                // loop again and re-display the pay/return options
            }
        }
    }


    private void doRecentBooks() {
        while (true) {
            System.out.println("Recent books:");
            try {
                var ids = userDao.getRecentBooks(currentUser.getUserId());
                if (ids == null || ids.isEmpty()) {
                    System.out.println("No recent books.");
                    System.out.println("1. return to previous menu");
                    System.out.print("Enter option: ");
                    String in = ConsoleUtil.readLine();
                    if ("1".equals(in)) return;
                    else { System.out.println("invalid input"); continue; }
                } else {
                    // Show resolved book title + author with numbers
                    for (int i = 0; i < ids.size(); i++) {
                        String bookId = ids.get(i);
                        var book = bookDao.getById(bookId);
                        if (book != null) {
                            System.out.println((i + 1) + ". " + book.getTitle() + " — " + book.getAuthor());
                        } else {
                            System.out.println((i + 1) + ". " + bookId + " (details not found)");
                        }
                    }

                    System.out.println("Options:\n1. view details\n2. return to previous menu");
                    System.out.print("Enter option: ");
                    String opt = ConsoleUtil.readLine();

                    if ("1".equals(opt)) {
                        System.out.print("Enter book number to view: ");
                        int num = ConsoleUtil.readIntSafe();
                        if (num == Integer.MIN_VALUE || num < 1 || num > ids.size()) {
                            System.out.println("invalid input");
                            // stay in recent-books menu
                            continue;
                        }
                        String id = ids.get(num - 1);
                        var book = bookDao.getById(id);
                        if (book == null) {
                            System.out.println("Book details not found.");
                            continue;
                        }
                        // show details
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Author: " + book.getAuthor());
                        System.out.println("Rating: " + book.getRating());
                        System.out.println("Genre: " + book.getGenre());
                        System.out.println("Price: " + book.getPrice());
                        System.out.println("1. add to cart\n2. return to previous menu");
                        System.out.print("Enter option: ");
                        String v = ConsoleUtil.readLine();
                        if ("1".equals(v)) {
                            cartService.add(book);
                            System.out.println("book has been added to cart.");
                            return; // after adding, return to Home menu
                        } else if ("2".equals(v)) {
                            // stay in recent-books menu
                            continue;
                        } else {
                            System.out.println("invalid input");
                            // stay in recent-books menu
                            continue;
                        }
                    } else if ("2".equals(opt)) {
                        return; // back to home
                    } else {
                        System.out.println("invalid input");
                        // stay in recent-books menu
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not load recent books: " + e.getMessage());
                System.out.println("1. return to previous menu");
                System.out.print("Enter option: ");
                String in = ConsoleUtil.readLine();
                if ("1".equals(in)) return;
                else { System.out.println("invalid input"); continue; }
            }
        }
    }



    private void doRecommended() {
        while (true) {
            // Determine last genre from recent books if possible
            String lastGenre = null;
            try {
                var ids = userDao.getRecentBooks(currentUser.getUserId());
                if (ids != null && !ids.isEmpty()) {
                    String lastBookId = ids.get(ids.size()-1);
                    var lastBook = bookDao.getById(lastBookId);
                    if (lastBook != null) lastGenre = lastBook.getGenre();
                }
            } catch (Exception ignored) {}

            if (lastGenre == null) {
                // fallback: inform user and allow them to return
                System.out.println("No recent book genre found to base recommendations on.");
                System.out.println("1. return to previous menu");
                System.out.print("Enter option: ");
                String in = ConsoleUtil.readLine();
                if ("1".equals(in)) return;
                else { System.out.println("invalid input"); continue; }
            }

            List<Book> rec = recommendationService.recommend(lastGenre);
            if (rec.isEmpty()) {
                System.out.println("No recommendations available for genre: " + lastGenre);
                System.out.println("1. return to previous menu");
                System.out.print("Enter option: ");
                String in = ConsoleUtil.readLine();
                if ("1".equals(in)) return;
                else { System.out.println("invalid input"); continue; }
            }

            // Show recommendations and options
            System.out.println("Recommended books (based on your last '" + lastGenre + "' read):");
            for (int i = 0; i < rec.size(); i++) {
                Book b = rec.get(i);
                System.out.println((i + 1) + ". " + b.getTitle() + " by " + b.getAuthor() + " (" + b.getRating() + ")");
            }

            System.out.println("Options:\n1. view details\n2. return to previous menu");
            System.out.print("Enter option: ");
            String opt = ConsoleUtil.readLine();

            if ("1".equals(opt)) {
                System.out.print("Enter book number to view: ");
                int num = ConsoleUtil.readIntSafe();
                if (num == Integer.MIN_VALUE || num < 1 || num > rec.size()) {
                    System.out.println("invalid input");
                    // stay in recommended menu
                    continue;
                }
                Book sel = rec.get(num - 1);
                System.out.println("Title: " + sel.getTitle());
                System.out.println("Author: " + sel.getAuthor());
                System.out.println("Rating: " + sel.getRating());
                System.out.println("Genre: " + sel.getGenre());
                System.out.println("Price: " + sel.getPrice());
                System.out.println("1. add to cart\n2. return to previous menu");
                System.out.print("Enter option: ");
                String v = ConsoleUtil.readLine();
                if ("1".equals(v)) {
                    cartService.add(sel);
                    System.out.println("book has been added to cart.");
                    return; // after add -> back to Home
                } else if ("2".equals(v)) {
                    // go back to recommendation list
                    continue;
                } else {
                    System.out.println("invalid input");
                    continue;
                }
            } else if ("2".equals(opt)) {
                return;
            } else {
                System.out.println("invalid input");
                continue;
            }
        }
    }



    private void doMyOrders() {
        List<Order> orders = orderService.getOrders(currentUser.getUserId());
        if (orders.isEmpty()) {
            System.out.println("You have no orders.");
            return;
        }
        System.out.println("Your orders:");
        for (Order o : orders) {
            System.out.println("OrderId: " + o.getOrderId() + " | total: Rs. " + o.getTotalPrice());
            for (Book b : o.getItems()) System.out.println("  - " + b.getTitle() + " (" + b.getPrice() + ")");
        }
    }
}
