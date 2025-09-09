package com.bookstore.dao.inmemory;
import com.bookstore.dao.BookDao;
import com.bookstore.model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class InMemoryBookDao implements BookDao {
    private final List<Book> books = new ArrayList<>();

    public InMemoryBookDao() { seedDemoBooks(); }

    @Override
    public List<Book> searchByTitleOrAuthor(String query) {
        if (query == null || query.isBlank()) return new ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase(Locale.ROOT).contains(q) ||
                        b.getAuthor().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByGenreAndMinRating(String genre, double minRating, int limit) {
        return books.stream()
                .filter(b -> b.getGenre().equalsIgnoreCase(genre) && b.getRating() >= minRating)
                .sorted((a,b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Book getById(String id) {
        return books.stream()
                .filter(b -> b.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }


    @Override
    public void seedDemoBooks() {
        books.clear();
        // Science Fiction
        books.add(new Book("SF001","Dune","Frank Herbert","Science Fiction",4.5,499.0));
        books.add(new Book("SF002","Neuromancer","William Gibson","Science Fiction",4.2,399.0));
        books.add(new Book("SF003","Foundation","Isaac Asimov","Science Fiction",4.3,349.0));
        books.add(new Book("SF004","Snow Crash","Neal Stephenson","Science Fiction",4.1,299.0));
        books.add(new Book("SF005","The Martian","Andy Weir","Science Fiction",4.4,349.0));
        books.add(new Book("SF006","Hyperion","Dan Simmons","Science Fiction",3.9,299.0));
        books.add(new Book("SF007","The Left Hand of Darkness","Ursula K. Le Guin","Science Fiction",3.8,279.0));
        books.add(new Book("SF008","Old Man's War","John Scalzi","Science Fiction",4.0,259.0));
        books.add(new Book("SF009","Red Mars","Kim Stanley Robinson","Science Fiction",3.7,399.0));
        books.add(new Book("SF010","Leviathan Wakes","James S. A. Corey","Science Fiction",4.1,329.0));

        // Programming
        books.add(new Book("PR001","Clean Code","Robert C. Martin","Programming",4.5,399.0));
        books.add(new Book("PR002","Effective Java","Joshua Bloch","Programming",4.8,499.0));
        books.add(new Book("PR003","The Pragmatic Programmer","Andrew Hunt","Programming",4.6,450.0));
        books.add(new Book("PR004","Design Patterns","Erich Gamma","Programming",4.2,299.0));
        books.add(new Book("PR005","Refactoring","Martin Fowler","Programming",4.3,449.0));
        books.add(new Book("PR006","Introduction to Algorithms","Cormen et al.","Programming",3.9,599.0));
        books.add(new Book("PR007","Head First Java","Kathy Sierra","Programming",3.8,299.0));
        books.add(new Book("PR008","Java Concurrency in Practice","Brian Goetz","Programming",4.1,399.0));
        books.add(new Book("PR009","You Don't Know JS","Kyle Simpson","Programming",4.0,199.0));
        books.add(new Book("PR010","Cracking the Coding Interview","Gayle Laakmann McDowell","Programming",4.0,349.0));

        // Data Science
        books.add(new Book("DS001","Hands-On Machine Learning","Aurelien Geron","Data Science",4.5,599.0));
        books.add(new Book("DS002","Pattern Recognition and Machine Learning","Christopher Bishop","Data Science",4.1,549.0));
        books.add(new Book("DS003","Data Science from Scratch","Joel Grus","Data Science",3.8,299.0));
        books.add(new Book("DS004","Python for Data Analysis","Wes McKinney","Data Science",4.0,399.0));
        books.add(new Book("DS005","Deep Learning","Ian Goodfellow","Data Science",4.2,699.0));
        books.add(new Book("DS006","An Introduction to Statistical Learning","Gareth James","Data Science",3.9,449.0));
        books.add(new Book("DS007","R for Data Science","Hadley Wickham","Data Science",3.7,299.0));
        books.add(new Book("DS008","Practical Statistics for Data Scientists","Peter Bruce","Data Science",4.0,329.0));
        books.add(new Book("DS009","Machine Learning Yearning","Andrew Ng","Data Science",4.3,0.0));
        books.add(new Book("DS010","Bayesian Reasoning and Machine Learning","David Barber","Data Science",3.6,499.0));

        // Horror
        books.add(new Book("HR001","The Shining","Stephen King","Horror",4.4,299.0));
        books.add(new Book("HR002","It","Stephen King","Horror",4.2,399.0));
        books.add(new Book("HR003","Bird Box","Josh Malerman","Horror",3.7,199.0));
        books.add(new Book("HR004","The Haunting of Hill House","Shirley Jackson","Horror",4.0,249.0));
        books.add(new Book("HR005","House of Leaves","Mark Z. Danielewski","Horror",3.9,279.0));
        books.add(new Book("HR006","Dracula","Bram Stoker","Horror",4.1,199.0));
        books.add(new Book("HR007","The Exorcist","William Peter Blatty","Horror",3.8,229.0));
        books.add(new Book("HR008","The Tell-Tale Heart and Other Stories","Edgar Allan Poe","Horror",3.6,99.0));
        books.add(new Book("HR009","The Silence of the Lambs","Thomas Harris","Horror",4.0,349.0));
        books.add(new Book("HR010","Mexican Gothic","Silvia Moreno-Garcia","Horror",4.1,299.0));

        // Cooking
        books.add(new Book("CK001","Salt, Fat, Acid, Heat","Samin Nosrat","Cooking",4.5,799.0));
        books.add(new Book("CK002","The Joy of Cooking","Irma S. Rombauer","Cooking",4.0,499.0));
        books.add(new Book("CK003","Essentials of Classic Italian Cooking","Marcella Hazan","Cooking",4.2,599.0));
        books.add(new Book("CK004","Mastering the Art of French Cooking","Julia Child","Cooking",4.3,699.0));
        books.add(new Book("CK005","How to Cook Everything","Mark Bittman","Cooking",3.9,349.0));
        books.add(new Book("CK006","Jerusalem","Yotam Ottolenghi","Cooking",3.8,399.0));
        books.add(new Book("CK007","Plenty","Yotam Ottolenghi","Cooking",4.1,429.0));
        books.add(new Book("CK008","The Food Lab","J. Kenji Lopez-Alt","Cooking",4.4,599.0));
        books.add(new Book("CK009","Indian-ish","Priya Krishna","Cooking",3.7,349.0));
        books.add(new Book("CK010","Baking: From My Home to Yours","Dorie Greenspan","Cooking",4.0,299.0));
    }
}
