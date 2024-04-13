

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Crawler implements Runnable{


    private int id =-1;

    private Set<String> visitedWebPages =new ConcurrentSkipListSet<>(); // for the visisted pages


    private Queue<String> allWebPages  = new ConcurrentLinkedQueue<>();


    public static final int maxDepth=5;

    @Override
    public void run() {
        while(true) {
            synchronized (visitedWebPages) {
                if (visitedWebPages.size() >= maxDepth) {
                    break;
                }
            }
            String url1;
            synchronized (allWebPages) {
                url1 = allWebPages.poll();
            }

            if(url1==null)
            {
                System.out.println("invalid url ");
                continue;
            }
            try {
                URI uri1 = new URI(url1);

                url1 = uri1.normalize().toString();
            }

            catch (URISyntaxException e) {
                System.out.println("invalid URL");
                continue;
            }

            synchronized (visitedWebPages) {

                    if (visitedWebPages.contains(url1) == false) {

                        Document doc=request(url1);

                        if(doc!=null) {
                            System.out.println("now iam crawling " + url1 + "and iam " + Thread.currentThread().getName());
                            crawlPage(doc);
                        }
                        else
                            continue;

                        if(visitedWebPages.size()>=maxDepth)break;
                    } else {
                        continue;
                    }

            }
        }

    }
    private void crawlPage(Document doc)
    {
        for(Element link : doc.select("a[href]"))
        {
            String nextLink= link.absUrl("href");

            try {


                URI nextLinkUri = new URI(nextLink);

                nextLink=nextLinkUri.normalize().toString();

                if(visitedWebPages.contains(nextLink)==false)
                {
                    allWebPages.add(nextLink);
                }

                if(visitedWebPages.size()>=maxDepth)return;

            }
            catch (URISyntaxException e)
            {
                System.out.println("invalid url");
               continue;
            }


        }
    }

    private Document request(String url)
    {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if(con.response().statusCode()==200)
            {
                visitedWebPages.add(url);
            }
            else
                return null;
            return doc;
        }
        catch(IOException exception) {
            System.out.println("error in con");
            return null;
        }

    }

    public void crawl()
    {
        if(allWebPages.size()==0)
        {
            startSeed();
        }

        System.out.println("Please enter num of threads");
        Scanner scanner = new Scanner(System.in);
        int numOfThreads =scanner.nextInt();
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);


        for (int i = 0; i < numOfThreads; i++) {

            executor.execute(this);
        }
        executor.shutdown();

        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }
        System.out.println("Crawling ended successfully and crawled " + visitedWebPages.size() +"\n");
    }

    private void startSeed()
    {
        String filename = "src/Seed.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                allWebPages.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
