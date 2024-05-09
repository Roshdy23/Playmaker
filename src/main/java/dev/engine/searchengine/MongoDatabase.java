package dev.engine.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MongoDatabase {
    private MongoClientURI clientURI;
    private MongoClient mongoClient;
    private com.mongodb.client.MongoDatabase mongoDatabase;

    public MongoDatabase() {
        clientURI = new MongoClientURI("mongodb://localhost:27017/");
        mongoClient = new MongoClient(clientURI);
        mongoDatabase = mongoClient.getDatabase("Playmaker");
        System.out.println("Database connected");
    }

    public com.mongodb.client.MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    public MongoCollection<Document> getCollection(String collection) {
        return mongoDatabase.getCollection(collection);
    }


    // Method to fetch URLs of documents containing a specific word
    public List<String> findUrlsWithWord(String collectionName, String word, String elementName) {
        List<String> urls = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        // Compile the regular expression pattern for the word
        Pattern wordPattern;
        try {
            wordPattern = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            System.err.println("Invalid regular expression: " + e.getMessage());
            return urls; // Return empty list if the pattern is invalid
        }

        // Specify the projection to include only the "url" field
        Document projection = new Document("url", 1);

        // Create the query to match documents containing the word in the specified element
        Document elementFilter = new Document(elementName, wordPattern);
        FindIterable<Document> result = collection.find(elementFilter).projection(projection);

        // Iterate over the documents returned by the query
        for (Document document : result) {
            String url = document.getString("url");
            if (url != null) {
                urls.add(url);
            }
        }

        return urls;
    }

    public List<Document> findDocumentsByUrl(String collectionName, String url) {
        List<Document> relatedDocuments = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        Document urlFilter = new Document("url", url);

        FindIterable<Document> result = collection.find(urlFilter);

        for (Document document : result) {
            relatedDocuments.add(document);
        }

        return relatedDocuments;
    }
    
}
