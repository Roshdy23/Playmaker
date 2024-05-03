
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public class RobotObj {
    private HashMap<String, Set<String>> allDisallowedLinks = new HashMap<>();

    public RobotObj() {

    }

    private Set<String> getDisallowedLinks(URL url) {
        if (allDisallowedLinks.containsKey(url.toString())) {
            System.out.println(url.toString() + " is already in the allDisallowedLinks HashMap.");
            return allDisallowedLinks.get(url.toString());
        }

        Set<String> disallowedLinks = new HashSet<>();

        try {
            URL robotsTxtUrl = new URL(url.getProtocol() + "://" + url.getHost() + "/robots.txt");

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(robotsTxtUrl.openStream()));

            while ((line = br.readLine()) != null) {
                line = line.trim();
                Boolean userAgentStatus = line.startsWith("User-agent:") && line.contains("*");

                if (userAgentStatus && line.startsWith("Disallow:")) {
                    String disallowedPath = line.substring(10).trim();
                    String disallowedUrl = url.getProtocol() + "://" + url.getHost() + disallowedPath;

                    disallowedLinks.add(disallowedUrl);
                }
            }
            br.close();
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + url.toString());
            return null;
        } catch (IOException e) {
            System.out.println("Error occurred while reading robots.txt for URL: " + url.toString());
            return null;
        }

        allDisallowedLinks.put(url.toString(), disallowedLinks);
        return disallowedLinks;
    }

    private Boolean isAllowed(URL url) {
        Set<String> disallowedLinks = getDisallowedLinks(url);

        if (disallowedLinks == null) {
            return false;
        }

        for (String disallowedLink : disallowedLinks) {
            if (url.toString().startsWith(disallowedLink)) {
                return false;
            }
        }
        return true;
    }

    public Boolean isURLAllowed(String url) {
        try {
            URL urlObject = new URL(url);
            return isAllowed(urlObject);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + url);
            return false;
        }
    }
}





