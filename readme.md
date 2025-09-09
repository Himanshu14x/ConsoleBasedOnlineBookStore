# Bookstore (Console) - Java (Maven)

Starter Java console bookstore with DynamoDB-ready user recent-books persistence and 50 seeded books.

Run:
- mvn -B clean test package
- java -jar target/bookstore-1.0-SNAPSHOT-shaded.jar

Toggle DynamoDB in src/main/resources/config.properties (useDynamo=true) and supply AWS credentials.
