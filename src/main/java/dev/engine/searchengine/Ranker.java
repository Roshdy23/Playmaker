package dev.engine.searchengine;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.print.Doc;
import java.time.LocalTime;
import java.util.*;

import static dev.engine.searchengine.LinkRepository.pageLinksList;
import static dev.engine.searchengine.LinkRepository.indexesList;

public class Ranker {

    public static void main(String[] argv) throws InterruptedException {

//        QueryProcessor q = new QueryProcessor();
//        List<Document> allURLs = q.Search("today matches");
//        List<Document> phraseURLs = q.Search("\"today matches\"");
//        for(Document res: phraseURLs) {
//            System.out.println(res.getString("url"));
//        }
//        System.out.println("Start Time Of Online Ranking: "+ LocalTime.now());
//        List<Document> results = rankPages(allURLs, phraseURLs, "today matches");
//        System.out.println("Finish Time Of Online Ranking: "+ LocalTime.now());
//        for(Document res: results) {
//            System.out.println(res.getString("url"));
//            System.out.println(res.getDouble("score"));
//        }
    }
    public static List<Document> rankPages(List<Document> urls, List<Document> phraseURLs, String query) {
        // 1- NORMAL SCORE ACCORDING TO TF-IDF SUMMATION
        List<Document> scoredURLs = AddScoreForPages(urls, query);
        // 2 - EXTRA POINTS FOR PHRASE_URLs
        for(Document url: scoredURLs) {
            for(Document phURL: phraseURLs) {
                if(phURL.getString("url").equals(url.getString("url"))) {
                    // Add 1 to the original score
                    double score = url.getDouble("score") + 1;
                    url.append("score", score);
                }
            }
        }
//         3 - ADD POPULARITY TO THE SCORE OF THE PAGE
//        for(Document doc: scoredURLs) {
//            for(Document page: pageLinksList) {
//                if(page.getString("url").equals(doc.getString("url"))) {
//                    double tmp = doc.getDouble("score");
//                    tmp = tmp + page.getDouble("popularity");
//                    doc.append("score", tmp);
//                }
//            }
//        }
        // 4- ADD URL SCORE
        List<Document> ret = searchUrls(scoredURLs, query.split("\\s+"));
        Comparator<Document> comparator = (doc1, doc2) -> {
            double score1 = doc1.getDouble("score");
            double score2 = doc2.getDouble("score");
            return Double.compare(score2, score1);
        };
        ret.sort(comparator);
        return ret.subList(0, Math.min(ret.size(), 200));
    }
    public static List<Document> AddScoreForPages(List<Document> urls, String query) {
//        MongoCollection<Document> indexes = (new MongoDatabase()).getCollection("Indexes");
//        List<Document> words = indexes.find().into(new ArrayList<>());
        String[] queryWords = query.split(" ");
        for(Document url: urls) {
            url.append("score", 0.0);
        }
        for(Document word: indexesList) {
            boolean flag = false;
            for(String s: queryWords) {
                if(s.equals(word.getString("word"))) {
                    flag = true;
                    break;
                }
            }
            if(!flag) continue;
            for(Document url: urls) {
                if(word.getString("url").equals(url.getString("url"))) {
                    double tmp = url.getDouble("score") + word.getDouble("score");
                    url.put("score", tmp);
                }
            }
        }
        return urls;
    }
    public static List<Document> searchUrls(List<Document> urlDocuments, String[] queryWords) {

        // Iterate through URL documents and calculate scores
        for (Document document : urlDocuments) {
            String url = document.getString("url");
            if (url != null) {
                double score = calculateScore(url, queryWords) + document.getDouble("score");
                document.append("score", score);
            }
        }

        return urlDocuments;
    }

    // Function to calculate the score for a URL based on query words
    private static double calculateScore(String url, String[] queryWords) {
        double score = 0;
        String query = Arrays.toString(queryWords).toLowerCase();
        for (String queryWord : queryWords) {
            if (url.toLowerCase().contains(queryWord.toLowerCase())) {
                score += 1; // Add one point for each query word found in the URL
            }
        }
        return score;
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
        int c=0;
        for (Document doc : documents) {
            ++c;
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
            System.out.println("Done number " + c );
        }
    }
    public static void calculatePopularityOfPages(int numberOfIterations) {
        // give initial popularity of 1 to all pages
        // increasing the number of iterations leads to more accuracy
        MongoCollection<Document> collection = (new MongoDatabase()).getCollection("PageLinks");
        List<Document> documents = collection.find().into(new ArrayList<>());
        for(Document doc : documents) {
            doc.put("popularity", 1.0);
        }
        while (numberOfIterations!=0) {
            for(Document currDoc: documents) {
                double popularity = 0;
                for(Document doc: documents) {
                    if(currDoc.getString("url").equals(doc.getString("url")))
                        continue;
                    if(doc.getList("links",String.class).contains(currDoc.getString("url"))) {
                        popularity = currDoc.getDouble("popularity") + (doc.getDouble("popularity")/doc.getList("links",String.class).size());
                    }
                }
                popularity = (1-0.85) + (0.85 * popularity);
                currDoc.put("popularity", popularity);
            }
            numberOfIterations--;
            System.out.println("Remindar iterations : " + numberOfIterations);
        }
        int c=0;
        for(Document doc: documents) {
            collection.updateOne(
                    Filters.eq("_id",  doc.getObjectId("_id")),
                    new Document("$set", new Document("popularity", doc.getDouble("popularity"))),
                    new UpdateOptions().upsert(true)
            );
            System.out.println("Done doc number: "+ ++c);
        }
    }

}
