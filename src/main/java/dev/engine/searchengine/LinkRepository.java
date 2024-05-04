package dev.engine.searchengine;

import com.mongodb.client.MongoCollection;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
        for(Document doc: contentList) {
            ret.add(new Link(doc.getString("url"),
                    doc.getString("content"),doc.getString("description"),
                    doc.getString("title")));
        }
        return ret;
    }
}
