package dev.engine.searchengine;

import com.mongodb.client.MongoCollection;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static dev.engine.searchengine.Ranker.rankPages;

@Repository
public class LinkRepository {

    public static List<Document> indexesList;
    public static List<Document> contentList;
    public static List<Document> pageLinksList;

    @PostConstruct
    public void init() {
        MongoDatabase mongodb = new MongoDatabase();
        MongoCollection<Document> indexes = mongodb.getCollection("Indexes");
        indexesList = indexes.find().into(new ArrayList<>());
        MongoCollection<Document> content = mongodb.getCollection("Content");
        contentList = content.find().into(new ArrayList<>());
        MongoCollection<Document> pageLinks = mongodb.getCollection("PageLinks");
        pageLinksList = pageLinks.find().into(new ArrayList<>());
    }
    List<Link> search(String query) {
        List<Link> ret = new ArrayList<>();
        QueryProcessor q = new QueryProcessor();
        List<Document> allURLs = q.Search(query);
        List<Document> phraseURLs = q.Search("\"" + query +"\"");
        System.out.println("Start Time Of Online Ranking: "+ LocalTime.now());
        List<Document> results = rankPages(allURLs, phraseURLs, query);
        System.out.println("Finish Time Of Online Ranking: "+ LocalTime.now());
        for(Document res: results) {
            ret.add(new Link(res.getString("url"),res.getString("title"),res.getString("description"),"this is the  content"));
        }
        return ret;
    }
}
