

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Crawler implements Runnable {


    private int id = -1;

    private Set<String> visitedWebPages = new ConcurrentSkipListSet<>(); // for the visisted pages


    private Queue<String> allWebPages = new ConcurrentLinkedQueue<>();

   private  HashMap<String, Boolean> disAllowed = new HashMap<>();


    public static final int maxDepth = 5;

    @Override
    public void run() {
        while (true) {
            synchronized (visitedWebPages) {
                if (visitedWebPages.size() >= maxDepth || allWebPages.size()==0) {
                    break;
                }
            }
            String url1;
            String robotUrl;
            synchronized (allWebPages) {
                url1 = allWebPages.poll();
            }

                if (url1 == null) {
                    System.out.println(" url is null");
                    continue;
                }
                try {
                    URI uri1 = new URI(url1);
                    url1 = uri1.normalize().toString();
                    robotUrl = url1 + "/robots.txt";
                } catch (URISyntaxException e) {
                    System.out.println("invalid URL");
                    continue;
                }


            synchronized (visitedWebPages) {

                if (visitedWebPages.contains(url1) == false &&isAllowedUrl(url1)) {

                    Document doc = request(url1);

                    if (doc != null) {
                        System.out.println("now iam crawling " + url1 + "and iam " + Thread.currentThread().getName());

                        crawlPage(doc);

                        File file = new File("C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\crawled.txt");

                        try {
                            FileWriter fw = new FileWriter(file, true);

                            BufferedWriter bufferedWriter = new BufferedWriter(fw);
                            bufferedWriter.write(url1);
                            bufferedWriter.newLine();
                            bufferedWriter.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    } else
                        continue;

                    if (visitedWebPages.size() >= maxDepth) break;
                } else {
                    continue;
                }

            }
        }

    }

    private void crawlPage(Document doc) {
        for (Element link : doc.select("a[href]")) {
            String nextLink = link.absUrl("href");

            try {


                URI nextLinkUri = new URI(nextLink);

                nextLink = nextLinkUri.normalize().toString();
              String  robotUrl = nextLink + "/robots.txt";
                if (!visitedWebPages.contains(nextLink) && isAllowedUrl(nextLink)&&nextLink!=null) {
                    allWebPages.add(nextLink);
                    File file = new File("C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\tobeCrawled.txt");

                    try {
                        FileWriter fw = new FileWriter(file, true);

                        BufferedWriter bufferedWriter = new BufferedWriter(fw);
                        bufferedWriter.write(nextLink);
                        bufferedWriter.newLine();

                        bufferedWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (visitedWebPages.size() >= maxDepth) return;

            } catch (URISyntaxException e) {
                System.out.println("invalid url");
                continue;
            }


        }
    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                visitedWebPages.add(url);
            } else
                return null;
            return doc;
        } catch (IOException exception) {
            System.out.println("error in con");
            return null;
        }

    }

    public Set<String> crawl() {
        init();


        if (allWebPages.size() == 0) {
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
        System.out.println("Crawling ended successfully and crawled " + visitedWebPages.size() + "\n");
        return visitedWebPages;
    }

    private void init() {
        String filename = "C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\crawled.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                visitedWebPages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        filename = "C:\\Users\\elros\\OneDrive\\Documents\\Search Engine\\Search-Engine\\Search Engine\\src\\tobeCrawled.txt";
        int cnt = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null && cnt < maxDepth && !visitedWebPages.contains(line)&&isAllowedUrl(line)) {
                allWebPages.add(line);
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
                allWebPages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAllowedUrl(String robots)
    {
        try {
            URL url = new URL(robots);
            URL robotsTxtUrl = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt");
            String temp =robotsTxtUrl.toString();
            if(disAllowed.containsKey(temp))
            {
                return disAllowed.get(temp);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(robotsTxtUrl.openStream()));
            String line;
            boolean allowedToCrawl = true;

            // Check if the robots.txt file disallows crawling for this user-agent
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("User-agent: *")) {
                    while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                        if (line.trim().startsWith("Disallow:")) {
                            String disallowedPath = line.substring(10).trim();
                            String disallowedUrl = url.getProtocol() + "://" + url.getHost() + disallowedPath;
                            if(robots.startsWith(disallowedUrl))
                            {
                                allowedToCrawl=false;
                                break;
                            }
                        }
                    }
                    break; // No need to check further
                }
            }

            reader.close();

            if (allowedToCrawl) {
            disAllowed.put(robots,true);
             return true;
            } else {
                disAllowed.put(robots,false);
             return false;
            }
        } catch (IOException e) {
            disAllowed.put(robots,false);
            System.out.println("Error reading robots.txt: " + e.getMessage());
        }
        return false;
    }





}
