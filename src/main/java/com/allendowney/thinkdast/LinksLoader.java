package com.allendowney.thinkdast;

import com.panforge.robotstxt.RobotsTxt;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class LinksLoader {
    private static final String PROTOCOL_DELIMITER = "://";
    public static final String ROBOTS_TXT_APPENDIX = "/robots.txt";
    private static final String LOC_TAG = "loc";

    /**
     * Takes arbitrary link to the site and returns it's address
     * in form, like http://example.com, with given links protocol.
     *
     * @param link  Arbitrary link to the site
     * @return      Site's common address
     * @throws MalformedURLException if site link is malformed.
     */
    public static String getSiteAddress(String link) throws MalformedURLException {
        URL url = new URL(link);
        return url.getProtocol() + PROTOCOL_DELIMITER + url.getHost();
    }

    public static boolean isSiteAvailable(String address) throws IOException {
        boolean result = false;
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        result = (connection.getResponseCode() == 200);
        connection.disconnect();
        return result;
    }

    public Set<String> getLinksFromRobotsTxt(String robotsTxtLink) throws IOException {
        try (InputStream robotsTxtStream = new URL(robotsTxtLink).openStream()) {
            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtStream);
            return new HashSet<>(robotsTxt.getSitemaps());
        }
    }

    public Set<String> getLinksFromSitemap(String sitemap) throws IOException {
        Set<String> links = new HashSet<>();
        for (Element elt : PageFetcher.fetchSitemapElements(sitemap)) {
            links.add(elt.getElementsByTag(LOC_TAG).text());
        }
        return links;
    }
}
