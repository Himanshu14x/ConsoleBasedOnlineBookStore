package com.bookstore.dynamo;

import com.bookstore.model.Book;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

public class BookSeeder {
    public static void main(String[] args) {
        DynamoDbClient client = DynamoDbClientProvider.getClient();
        if (client == null) {
            System.out.println("Dynamo client not created. Set useDynamo=true in config.properties and provide credentials.");
            return;
        }
        DynamoBookDao dao = new DynamoBookDao(client);

        List<Book> books = List.of(
                new Book("SF001","Dune","Frank Herbert","Science Fiction",4.5,499.0),
                new Book("SF002","Neuromancer","William Gibson","Science Fiction",4.2,399.0),
                new Book("SF003","Foundation","Isaac Asimov","Science Fiction",4.3,349.0),
                new Book("SF004","Snow Crash","Neal Stephenson","Science Fiction",4.1,299.0),
                new Book("SF005","The Martian","Andy Weir","Science Fiction",4.4,349.0),
                new Book("SF006","Hyperion","Dan Simmons","Science Fiction",3.9,299.0),
                new Book("SF007","The Left Hand of Darkness","Ursula K. Le Guin","Science Fiction",3.8,279.0),
                new Book("SF008","Old Man's War","John Scalzi","Science Fiction",4.0,259.0),
                new Book("SF009","Red Mars","Kim Stanley Robinson","Science Fiction",3.7,399.0),
                new Book("SF010","Leviathan Wakes","James S. A. Corey","Science Fiction",4.1,329.0),

                new Book("PR001","Clean Code","Robert C. Martin","Programming",4.5,399.0),
                new Book("PR002","Effective Java","Joshua Bloch","Programming",4.8,499.0),
                new Book("PR003","The Pragmatic Programmer","Andrew Hunt","Programming",4.6,450.0),
                new Book("PR004","Design Patterns","Erich Gamma","Programming",4.2,299.0),
                new Book("PR005","Refactoring","Martin Fowler","Programming",4.3,449.0),
                new Book("PR006","Introduction to Algorithms","Cormen et al.","Programming",3.9,599.0),
                new Book("PR007","Head First Java","Kathy Sierra","Programming",3.8,299.0),
                new Book("PR008","Java Concurrency in Practice","Brian Goetz","Programming",4.1,399.0),
                new Book("PR009","You Don't Know JS","Kyle Simpson","Programming",4.0,199.0),
                new Book("PR010","Cracking the Coding Interview","Gayle Laakmann McDowell","Programming",4.0,349.0),

                new Book("DS001","Hands-On Machine Learning","Aurelien Geron","Data Science",4.5,599.0),
                new Book("DS002","Pattern Recognition and Machine Learning","Christopher Bishop","Data Science",4.1,549.0),
                new Book("DS003","Data Science from Scratch","Joel Grus","Data Science",3.8,299.0),
                new Book("DS004","Python for Data Analysis","Wes McKinney","Data Science",4.0,399.0),
                new Book("DS005","Deep Learning","Ian Goodfellow","Data Science",4.2,699.0),
                new Book("DS006","An Introduction to Statistical Learning","Gareth James","Data Science",3.9,449.0),
                new Book("DS007","R for Data Science","Hadley Wickham","Data Science",3.7,299.0),
                new Book("DS008","Practical Statistics for Data Scientists","Peter Bruce","Data Science",4.0,329.0),
                new Book("DS009","Machine Learning Yearning","Andrew Ng","Data Science",4.3,0.0),
                new Book("DS010","Bayesian Reasoning and Machine Learning","David Barber","Data Science",3.6,499.0),

                new Book("HR001","The Shining","Stephen King","Horror",4.4,299.0),
                new Book("HR002","It","Stephen King","Horror",4.2,399.0),
                new Book("HR003","Bird Box","Josh Malerman","Horror",3.7,199.0),
                new Book("HR004","The Haunting of Hill House","Shirley Jackson","Horror",4.0,249.0),
                new Book("HR005","House of Leaves","Mark Z. Danielewski","Horror",3.9,279.0),
                new Book("HR006","Dracula","Bram Stoker","Horror",4.1,199.0),
                new Book("HR007","The Exorcist","William Peter Blatty","Horror",3.8,229.0),
                new Book("HR008","The Tell-Tale Heart and Other Stories","Edgar Allan Poe","Horror",3.6,99.0),
                new Book("HR009","The Silence of the Lambs","Thomas Harris","Horror",4.0,349.0),
                new Book("HR010","Mexican Gothic","Silvia Moreno-Garcia","Horror",4.1,299.0),

                new Book("CK001","Salt, Fat, Acid, Heat","Samin Nosrat","Cooking",4.5,799.0),
                new Book("CK002","The Joy of Cooking","Irma S. Rombauer","Cooking",4.0,499.0),
                new Book("CK003","Essentials of Classic Italian Cooking","Marcella Hazan","Cooking",4.2,599.0),
                new Book("CK004","Mastering the Art of French Cooking","Julia Child","Cooking",4.3,699.0),
                new Book("CK005","How to Cook Everything","Mark Bittman","Cooking",3.9,349.0),
                new Book("CK006","Jerusalem","Yotam Ottolenghi","Cooking",3.8,399.0),
                new Book("CK007","Plenty","Yotam Ottolenghi","Cooking",4.1,429.0),
                new Book("CK008","The Food Lab","J. Kenji Lopez-Alt","Cooking",4.4,599.0),
                new Book("CK009","Indian-ish","Priya Krishna","Cooking",3.7,349.0),
                new Book("CK010","Baking: From My Home to Yours","Dorie Greenspan","Cooking",4.0,299.0)
        );

        int success = 0;
        for (Book b : books) {
            boolean ok = dao.putBook(b);
            if (ok) success++;
        }
        System.out.println("Seeded " + success + " books.");
    }
}
