package dev.engine.searchengine;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.ext.PorterStemmer;
import java.io.IOException;
import java.util.*;
import java.lang.Object;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {
    private static Set<String> URLs;
    private static MongoCollection<org.bson.Document> IndexesCollection;
    private static MongoCollection<org.bson.Document> ContentCollection;
    private static HashMap<String,Integer> tf0=new HashMap<>();
    private static HashMap<String,Integer> tf1=new HashMap<>();
    private static HashMap<String,Integer> tf2=new HashMap<>();
    private static HashMap<String,Integer> tf3=new HashMap<>();
    private static HashMap<String,Integer> tf4=new HashMap<>();
    private static HashMap<String,Integer> tf5=new HashMap<>();
    private static Set<String> siteWords= new HashSet<>();
    private static int siteSize = 0;
    private static boolean spam=false;
    public Indexer(Set<String> urls,MongoCollection<org.bson.Document> icoll,MongoCollection<org.bson.Document> ccoll){
        URLs = urls; IndexesCollection=icoll; ContentCollection = ccoll;
    }
    public void index()
    {
        for(String url : URLs)
        {
            Document site;
            site = request(url);
            spam=false;
            process(site);
            if(!checkSpam())
            {
                saveWords(url,site);
            }
            System.out.println("successfully indexed words for "+url);
            siteWords.clear();
            siteSize=0;
            tf0.clear();
            tf1.clear();
            tf2.clear();
            tf3.clear();
            tf4.clear();
            tf5.clear();
        }
    }
    private void saveWords(String url,Document site){
        for(String word: siteWords)
        {
            int TF=0;
            TF+= tf0.get(word)==null?0:tf0.get(word);
            TF+= tf1.get(word)==null?0:tf1.get(word);
            TF+= tf2.get(word)==null?0:tf2.get(word);
            TF+= tf3.get(word)==null?0:tf3.get(word);
            TF+= tf4.get(word)==null?0:tf4.get(word);
            TF+= tf5.get(word)==null?0:tf5.get(word);
            double normalTF = ((double) TF)/siteSize;
            org.bson.Document query = new org.bson.Document("word",word);
            query.append("url",url);
            org.bson.Document ientry = new org.bson.Document("word",word);
            ientry.append("url",url);
            ientry.append("TF",TF);
            ientry.append("normalTF",normalTF);
            ientry.append("tf0",tf0.get(word)==null?0:tf0.get(word));
            ientry.append("tf1",tf1.get(word)==null?0:tf1.get(word));
            ientry.append("tf2",tf2.get(word)==null?0:tf2.get(word));
            ientry.append("tf3",tf3.get(word)==null?0:tf3.get(word));
            ientry.append("tf4",tf4.get(word)==null?0:tf4.get(word));
            ientry.append("tf5",tf5.get(word)==null?0:tf5.get(word));
            if(IndexesCollection.find(query).first()!=null)
            {
                IndexesCollection.findOneAndReplace(query,ientry);
            }
            else {
                IndexesCollection.insertOne(ientry);
            }
        }
        StringBuilder content = new StringBuilder();
        content.append(site.select("title, h1, h2, h3, h4, h5, h6 p, code, b, a, strong, i, em, blockquote, span").text()).append(" ");
        String title = site.select("title").text();
        String Description = site.select("meta[name=description]").attr("content");
        content.append(Description);
        String Content = content.toString();
        org.bson.Document centry = new org.bson.Document("content",Content);
        centry.append("url",url);
        centry.append("description",Description);
        centry.append("title",title);
        org.bson.Document query = new org.bson.Document("url",url);
        if(ContentCollection.find(query).first()!=null)
        {
            ContentCollection.findOneAndReplace(query,centry);
        }
        else {
            ContentCollection.insertOne(centry);
        }
    }
    private boolean checkSpam(){
        if(siteSize<20) return true;
        for(String word : siteWords){
            int tf=0;
            tf+= tf0.get(word)==null?0:tf0.get(word);
            tf+= tf1.get(word)==null?0:tf1.get(word);
            tf+= tf2.get(word)==null?0:tf2.get(word);
            tf+= tf3.get(word)==null?0:tf3.get(word);
            tf+= tf4.get(word)==null?0:tf4.get(word);
            tf+= tf5.get(word)==null?0:tf5.get(word);
            if(((double) tf)/siteSize>=0.5)
                return true;
        }
        return false;
    }
    private void process(Document site){
        // get all words in text tags <p> <h1-h6> <b> <span> <a>
        String title = site.select("title").text();
        String h13 = site.select("h1, h2, h3").text();
        String description = site.select("meta[name=description]").attr("content");
        String highlights = site.select("b, strong, i, em, blockquote, span, a").text();
        String content = site.select("p, code").text();
        String h46 = site.select("h4, h5, h6").text();
        title = RemoveStopWords.removeStopWords(title);
        h13 = RemoveStopWords.removeStopWords(h13);
        description = RemoveStopWords.removeStopWords(description);
        highlights = RemoveStopWords.removeStopWords(highlights);
        content = RemoveStopWords.removeStopWords(content);
        h46 = RemoveStopWords.removeStopWords(h46);
        lexify(title,0);
        lexify(h13,1);
        lexify(description,2);
        lexify(highlights,3);
        lexify(content,4);
        lexify(h46,5);
    }
    private void lexify(String wordBulk,int priority)
    {
        String[] words = wordBulk.split("\\s+");
        for(String word : words){
            if(word.length()<=1)
                continue;
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]"); //any character not between a-z A-Z 0-9
            Matcher matcher = pattern.matcher(word);
            if(matcher.find())
                continue; //if it contains any special characters dont register it
            PorterStemmer stemmer = new PorterStemmer();
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();
            word.toLowerCase();
            siteSize ++;
            if(!siteWords.contains(word))
                siteWords.add(word);
            switch (priority){
                case 0:
                    tf0.put(word, tf0.get(word)==null?1:tf0.get(word)+1);
                    break;
                case 1:
                    tf1.put(word, tf1.get(word)==null?1:tf1.get(word)+1);
                    break;
                case 2:
                    tf2.put(word, tf2.get(word)==null?1:tf2.get(word)+1);
                    break;
                case 3:
                    tf3.put(word, tf3.get(word)==null?1:tf3.get(word)+1);
                    break;
                case 4:
                    tf4.put(word, tf4.get(word)==null?1:tf4.get(word)+1);
                    break;
                case 5:
                    tf5.put(word, tf5.get(word)==null?1:tf5.get(word)+1);
                    break;
                default: break;
            }
        }
    }
    private Document request(String url)
    {
        try{
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if(con.response().statusCode()==200)
                return doc;
            else
                return null;
        } catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("couldn't establish connection to the url");
            return null;
        }
    }
}