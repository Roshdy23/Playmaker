package dev.engine.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.print.Doc;
import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class Main {
public static void main(String[] args) throws InterruptedException, IOException {
//        MongoDatabase mongoDatabase = new MongoDatabase();
//        MongoCollection<Document> pageLinks = mongoDatabase.getCollection("PageLinks");
//        List<Document> pageLinksLis = pageLinks.find().into(new ArrayList<>());
//        MongoCollection<Document> content = mongoDatabase.getCollection("Content");
//        List<Document> contentList = content.find().into(new ArrayList<>());
//    String fileName = "/home/abdallah/IdeaProjects/SearchEngine/src/main/resources/crawled.txt"; // Specify your file name here
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//        for(Document doc: pageLinksLis) {
//            int f=0;
//            for(Document doc1: contentList) {
//                if(doc.getString("url").equals(doc1.getString("url"))) {
//                    f=1;
//                    break;
//                }
//            }
//            if(f==0) {
//                writer.write(doc.getString("url"));
//                writer.newLine();
//            }
//        }
//        writer.close();
//        Set<String> sites = new HashSet<>();
//        for(Document doc: pageLinksLis) {
//            sites.add(doc.getString("url"));
//        }
//        System.out.println(sites.size());
//        String fileName = "/home/abdallah/IdeaProjects/SearchEngine/src/main/resources/crawled.txt"; // Specify your file name here
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//        try {
//            for(String s: sites) {
//                writer.write(s);
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//writer.close();
//        Crawler c=new Crawler();
//        Set<String> sites = c.crawl();

//        String fileName = "/home/abdallah/IdeaProjects/SearchEngine/src/main/resources/crawleddd.txt"; // Specify your file name here
//        Set<String> sites = new HashSet<>();
//
//        // Read strings from file and add to set
//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sites.add(line);
//            }
//        } catch (IOException e) {
//        }
//        System.out.println(LocalTime.now());
//        Indexer indexer = new Indexer(sites,mongoDatabase.getCollection("Indexes"),mongoDatabase.getCollection("Content"));
//        indexer.index();
//        System.out.println(LocalTime.now());
//        QueryProcessor q = new QueryProcessor();
//        List<Document> list = q.Search("\"Live Soccer Scores\"");
//        for (Document doc : list) {
//            System.out.println(doc.getString("url"));
//            System.out.println(doc.getString("title"));
//            System.out.println(doc.getString("description"));
//        }
}
}