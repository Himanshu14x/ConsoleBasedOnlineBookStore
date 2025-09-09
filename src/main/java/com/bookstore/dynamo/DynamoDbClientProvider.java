package com.bookstore.dynamo;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class DynamoDbClientProvider {
    private static DynamoDbClient client;

    public static synchronized DynamoDbClient getClient() {
        if (client != null) return client;
        try {
            Properties p = new Properties();
            p.load(Files.newInputStream(Paths.get("src/main/resources/config.properties")));
            String use = p.getProperty("useDynamo", "false");
            if (!"true".equalsIgnoreCase(use)) return null;

            String accessKey = p.getProperty("aws.accessKeyId");
            String secretKey = p.getProperty("aws.secretKey");
            String region = p.getProperty("aws.region", "us-east-1");
            String endpoint = p.getProperty("aws.endpoint", "");

            AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);


            DynamoDbClientBuilder b = DynamoDbClient.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(creds))
                    .region(Region.of(region));

            if (endpoint != null && !endpoint.isBlank()) {
                b.endpointOverride(URI.create(endpoint));
            }

            client = b.build();
            System.out.println("DynamoDb client created (useDynamo=true)");
            return client;
        } catch (Exception e) {
            System.out.println("DynamoDb client not created: " + e.getMessage());
            return null;
        }
    }
}
