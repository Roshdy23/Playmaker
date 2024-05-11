package dev.engine.searchengine;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import javax.print.Doc;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.engine.searchengine.Ranker.rankPages;

@Repository
public class LinkRepository {

    public static List<Document> indexesList;
    public static List<Document> contentList;
    public static List<Document> pageLinksList;
    public static List<Document> previousQueriesList;
    public static MongoCollection<Document> prvQ;

    @PostConstruct
    public void init() {
        MongoDatabase mongodb = new MongoDatabase();
        MongoCollection<Document> indexes = mongodb.getCollection("Indexes");
        indexesList = indexes.find().into(new ArrayList<>());
        MongoCollection<Document> content = mongodb.getCollection("Content");
        contentList = content.find().into(new ArrayList<>());
        MongoCollection<Document> pageLinks = mongodb.getCollection("PageLinks");
        pageLinksList = pageLinks.find().into(new ArrayList<>());
        prvQ = mongodb.getCollection("previousQueries");
        previousQueriesList = prvQ.find().into(new ArrayList<>());
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
    void addQueryForSuggestions(String query) {
        boolean check = false;
        for(Document doc: previousQueriesList) {
            if(doc.getString("query").equals(query)) {
                int newCount = doc.getInteger("count")+1;
                prvQ.updateOne(
                        Filters.eq("_id",  doc.getObjectId("_id")),
                        new Document("$set", new Document("count", newCount)),
                        new UpdateOptions().upsert(true)
                );
                check = true;
                break;
            }
        }
        if(!check) {
            Document newDocument = new Document("query", query)
                    .append("count", 1);
            prvQ.insertOne(newDocument);
        }
        MongoDatabase mongodb = new MongoDatabase();
        prvQ = mongodb.getCollection("previousQueries");
        previousQueriesList = prvQ.find().into(new ArrayList<>());
    }
    List<String> prevMatchedQueries(String query) {
        List<Document> ret = new ArrayList<>();
        for(Document doc: previousQueriesList) {
            if(doc.getString("query").startsWith(query)) {
                ret.add(doc);
            }
        }
        Comparator<Document> comparator = (doc1, doc2) -> {
            int score1 = doc1.getInteger("count");
            int score2 = doc2.getInteger("count");
            return Integer.compare(score2, score1);
        };
        ret.sort(comparator);
        List<String> res = new ArrayList<>();
        int c=0;
        for(Document doc: ret) {
            res.add(doc.getString("query"));
            if(++c == 6) break;
        }
        return res;
    }
}
