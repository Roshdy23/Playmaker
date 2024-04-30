import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.model.Aggregates;

public class Ranker {

    public static void main(String[] argv) {
        //
        calculateScoreForEachWord();
    }
    public static void calculateIDF() {
        MongoCollection<Document> indexes = (new MongoDatabase()).getCollection("Indexes");
        MongoCollection<Document> content = (new MongoDatabase()).getCollection("Content");

        int totalNumberOfDocuments = (int) content.countDocuments();
        List<String> distinctWords = indexes.distinct("word", String.class).into(new ArrayList<>());
        for (String word : distinctWords) {
            long numberOfDocumentsContainingWord = indexes.countDocuments(Filters.eq("word", word));
            double idf = Math.log((double) totalNumberOfDocuments / (double) numberOfDocumentsContainingWord) / Math.log(10);
            indexes.updateMany(
                    Filters.eq("word", word),
                    new Document("$set", new Document("IDF", idf)),
                    new UpdateOptions().upsert(false)
            );
        }
    }
    public static void calculateScoreForEachWord() {
        // FOR EACH WORD (document)
        // score = (0.0001*tf0+0.00005*tf1+0.000025*tf2+0.0125*tf3+0.00625*tf4+0.003125*tf5) * IDF + (normalTF * IDF)
        // Retrieve all documents from the collection
        MongoCollection<Document> collection = (new MongoDatabase()).getCollection("Indexes");
        List<Document> documents = collection.find().into(new ArrayList<>());
        for (Document doc : documents) {
            double tf0 = doc.getInteger("tf0") * 0.001;
            double tf1 = doc.getInteger("tf1") * 0.0005;
            double tf2 = doc.getInteger("tf2") * 0.00025 ;
            double tf3 = doc.getInteger("tf3") * 0.000125;
            double tf4 = doc.getInteger("tf4") * 0.0000625;
            double tf5 = doc.getInteger("tf5") * 0.00003125;
            double normalTF = doc.getDouble("normalTF");
            double idf = doc.getDouble("IDF");
            double score = (tf0 + tf1 + tf2 + tf3 + tf4 + tf5) * idf + (normalTF * idf);
            collection.updateOne(
                    Filters.eq("_id",  doc.getObjectId("_id")),
                    new Document("$set", new Document("score", score)),
                    new UpdateOptions().upsert(true)
            );
        }
    }
}
