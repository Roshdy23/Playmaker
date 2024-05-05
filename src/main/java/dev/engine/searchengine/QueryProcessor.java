package dev.engine.searchengine;

import java.util.*;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import org.tartarus.snowball.ext.PorterStemmer;

import javax.print.Doc;

import static dev.engine.searchengine.LinkRepository.contentList;
import static dev.engine.searchengine.LinkRepository.indexesList;

public class QueryProcessor {

    static final String[] stopWords = {"a", "an", "the", "is", "are", "am", "was", "were", "has", "have", "had", "been", "will", "shall", "be", "do", "does", "did", "can", "could", "may", "might", "must", "should", "of", "in", "on", "at", "to", "from", "by", "for", "about", "with", "without", "not", "no", "yes", "or", "and", "but", "if", "else", "then", "than", "else", "when", "where", "what", "who", "how", "which", "whom", "whose", "why", "because", "however", "therefore", "thus", "so", "such", "this", "that", "these", "those", "their", "his", "her", "its", "our", "your", "their", "any", "some", "many", "much", "few", "little", "own", "other", "another", "each", "every", "all", "both", "neither", "either", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth"};
    static final Character[] punctuations = {'.', ',', ':', ';', '?', '!', '\'', '\"', '(', ')', '{', '}', '[', ']', '<', '>', '/', '\\', '|', '-', '_', '+', '=', '*', '&', '^', '%', '$', '#', '@', '`', '~', '“', '”', '‘', '’', '–', '—', '…'};

    String query;
    String[] phrases;

    static final Set<String> stopWordsSet = new HashSet<>(Arrays.asList(stopWords));
    static final Set<Character> punctuationsSet = new HashSet<>(Arrays.asList(punctuations));

    PhraseSearcher phraseSearcher = new PhraseSearcher();

    QueryProcessor() {

    }


    public ArrayList<String> ProcessQuery(String query) {
        return PreProcessQuery(query);
    }

    private ArrayList<String> PreProcessQuery(String query) {
        if (phraseSearcher.HasPhrases(query)) {
            String tmp = "";
            for (int i = 1; i < query.length() - 1; i++)
                tmp += query.charAt(i);
            query = tmp;
        }
        query = query.toLowerCase();
        query = removeStopWords(query);
        query = removePunctuation(query);
        ArrayList<String> ret = stem(query);
        return ret;
    }

    private String removeStopWords(String query) {
        String[] words = query.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (stopWordsSet.contains(word)) {
                continue;
            }
            result.append(word).append(" ");
        }
        return result.toString().trim();
    }

    private String removePunctuation(String query) {
        String[] words = query.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            StringBuilder wordBuilder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (!punctuationsSet.contains(word.charAt(i))) {
                    wordBuilder.append(word.charAt(i));
                }
            }
            word = wordBuilder.toString();
            result.append(word).append(" ");
        }
        return result.toString().trim();
    }

    public static ArrayList<String> stem(String word) {
        ArrayList<String> ret = new ArrayList<>();
        PorterStemmer porterStemmer = new PorterStemmer();
        int i = 0;
        String tmp = "";
        while (i < word.length()) {
            while (i < word.length() && word.charAt(i) != ' ') {
                tmp += word.charAt(i);
                i++;
            }
            porterStemmer.setCurrent(tmp);
            porterStemmer.stem();
            ret.add(porterStemmer.getCurrent());
            tmp = "";
            while (i < word.length() && word.charAt(i) == ' ')
                i++;
        }
        return ret;
    }

    public List<Document> Search(String s) {
        ArrayList<String> res = ProcessQuery(s);
        HashMap<String, Boolean> mp = new HashMap<String, Boolean>();
        HashMap<String, Boolean> mp2 = new HashMap<String, Boolean>();
        for (String t : res) {
            mp2.put(t, true);
        }
        ArrayList<String> ret = new ArrayList<String>();
//        for (int i = 0; i < res.size(); i++) {
//            for (String j : tmp) {
//                if (mp.get(j) != null)
//                    continue;
//                mp.put(j, true);
//                ret.add(j);
//            }
//        }
        for (Document doc : indexesList) {
            if (mp2.get(doc.getString("word")) != null)
            {
                if (mp.get(doc.getString("url")) == null){
                    mp.put(doc.getString("url"), true);
                    ret.add(doc.getString("url"));
                }
            }
        }
        PhraseSearcher P = new PhraseSearcher();
        if (P.HasPhrases(s)) {
            String SS = "";
            for (int i = 1; i < s.length() - 1; i++)
                SS += s.charAt(i);
            s = SS;
            mp.clear();
            mp2.clear();
            for (int i = 0; i < ret.size(); i++) {
                mp2.put(ret.get(i), true);
            }
            ret.clear();
            for(Document doc:contentList){
                if (mp2.get(doc.getString("url")) == null)
                    continue;
                if(doc.getString("content").contains(s)){
                    ret.add(doc.getString("url"));
                }
            }
        }
        mp2.clear();
        for (int i = 0;i < ret.size(); i++)
            mp2.put(ret.get(i),true);
        List<Document>ToReturn = new ArrayList<Document>();
        for(Document doc:contentList){
            if (mp2.get(doc.getString("url")) != null)
                ToReturn.add(doc);
        }
        return ToReturn;
    }

    private class PhraseSearcher {

        PhraseSearcher() {
            // Empty constructor
        }

        public Boolean HasPhrases(String query) {
            return query.contains("\"");
        }

        public String[] GetPhrases(String query) {
            String[] phrases = query.split("\"");
            return phrases;
        }

        public String RemovePhrases(String query) {
            for (String phrase : phrases) {
                query = query.replace("\"" + phrase + "\"", "");
            }
            return query;
        }

        public void ProcessPhrase(String phrase) {
            ;
        }

    }

}
