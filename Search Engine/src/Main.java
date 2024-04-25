import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Main {
    public static void main(String[] args) {
        MongoDatabase mongoDatabase = new MongoDatabase();
        Crawler c=new Crawler();
        Set<String> sites = c.crawl();
        Indexer indexer = new Indexer(sites,mongoDatabase.getCollection("Indexes"),mongoDatabase.getCollection("Content"));
        indexer.index();
    }
}