package dev.engine.searchengine;

import com.mongodb.client.MongoCollection;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.io.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class Crawler implements Runnable {

    private final int mxDepth= 100;
    private static MongoCollection<org.bson.Document> PageLinksCollection;
    private Set<String> visitedwebpages = new ConcurrentSkipListSet<>();
    private Queue<String> allwebpages = new ConcurrentLinkedQueue<>();


    RobotObj robotObject = new RobotObj();

    public Crawler() {

    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " has started..");
        while (true) {
            synchronized (visitedwebpages) {
                if (visitedwebpages.size() >= mxDepth) {
                    break;
                }
            }

            String url;
            synchronized (allwebpages) {
                url = allwebpages.poll();
            }

            try {

                if (!isValid(url)) {
                    System.out.println(Thread.currentThread().getName() + ":");
                    System.out.println("Invalid URL: " + url);
                    continue;
                }
                if (url.startsWith("javascript:")) {
                    System.out.println(Thread.currentThread().getName() + ":");
                    System.out.println("skipping : " + url);
                    continue;
                }


                URI uri = new URI(url);
                String normalizedUrl = uri.normalize().toString();

                if(visitedwebpages.contains(normalizedUrl)) {
                    System.out.println(normalizedUrl + " exists");
                    continue;
                }


                Connection con = Jsoup.connect(normalizedUrl);
                Document doc = con.get();
                if (con.response().statusCode() == 200) { // 200 is the HTTP OK status code

                    if (!robotObject.isURLAllowed(normalizedUrl)) {
                        System.out.println(Thread.currentThread().getName() + ":");
                        System.out.println("URL is not allowed: " + normalizedUrl);
                        continue;
                    }

                    synchronized (visitedwebpages) {
                        if (visitedwebpages.size() >= mxDepth) {
                            break;
                        }

                        visitedwebpages.add(normalizedUrl);
                        System.out.println("Visited " + normalizedUrl);


                        System.out.println("now iam crawling " + normalizedUrl+ "and iam " + Thread.currentThread().getName());




                        System.out.println("Title: " + doc.title());

                        if (visitedwebpages.size() >= mxDepth) {
                            break;
                        }
                    }
                    Vector<String> doclinks=new Vector<>();

                    for (Element link : doc.select("a[href]")) {

                        String nextUrl = link.attr("abs:href");

                        try {

                            URI nextUri = new URI(nextUrl);
                            String normalizedNextUrl = nextUri.normalize().toString();

                            synchronized (visitedwebpages) {
                                if (visitedwebpages.contains(normalizedNextUrl)) {
                                    continue;
                                }

                                doclinks.add(normalizedNextUrl);



                                synchronized (allwebpages) {
                                    allwebpages.add(normalizedNextUrl);

                                    File file = new File("C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\tobeCrawled.txt");

                                    try {
                                        FileWriter fw = new FileWriter(file, true);

                                        BufferedWriter bufferedWriter = new BufferedWriter(fw);
                                        bufferedWriter.write(normalizedNextUrl);
                                        bufferedWriter.newLine();

                                        bufferedWriter.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            }
                        } catch (URISyntaxException e) {
                            System.out.println(Thread.currentThread().getName() + ":");
                            System.out.println("Invalid URL: " + nextUrl);
                        }
                    }

                    org.bson.Document centry = new org.bson.Document("url",normalizedUrl);
                    centry.append("links", doclinks);

                    org.bson.Document query = new org.bson.Document("url",normalizedUrl);
                    if(PageLinksCollection.find(query).first()!=null)
                    {
                        PageLinksCollection.findOneAndReplace(query,centry);
                    }
                    else {
                        PageLinksCollection.insertOne(centry);
                    }

                    File file = new File("C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\crawled.txt");

                    try {
                        FileWriter fw = new FileWriter(file, true);

                        BufferedWriter bufferedWriter = new BufferedWriter(fw);
                        bufferedWriter.write(normalizedUrl);
                        bufferedWriter.newLine();
                        bufferedWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException | URISyntaxException e) {
                System.out.println(Thread.currentThread().getName() + ":");
                System.out.println("Error while crawling URL: " + url);
                System.out.println("Error message: " + e.getMessage());
            }



        }
        System.out.println(Thread.currentThread().getName() + " has finished..");
    }

    public Set<String> crawl() {
        init();
        MongoDatabase db = new MongoDatabase();
        PageLinksCollection = db.getCollection("PageLinks");

        if (allwebpages.size() == 0) {
            startSeed();
        }

        System.out.println("Please enter num of threads");
        Scanner scanner = new Scanner(System.in);
        int numOfThreads = scanner.nextInt();
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);


        for (int i = 0; i < numOfThreads; i++) {

            executor.execute(this);
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }
        System.out.println("Crawling ended successfully and crawled " + visitedwebpages.size() + "\n");
        return visitedwebpages;
    }

    private void init() {
        String filename = "C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\crawled.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                visitedwebpages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        filename = "C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\tobeCrawled.txt";
        int cnt = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null && cnt < mxDepth&& !visitedwebpages.contains(line)&& robotObject.isURLAllowed(line)) {
                allwebpages.add(line);
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void startSeed() {
        String filename = "C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\Seed.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allwebpages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isValid(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }




}




