package com.allendowney.thinkdast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class PageFetcher {
	private static final String PARAGRAPH = "p";
	private static final String SITEMAP_TAG = "sitemap";
	private static final String URL_TAG = "url";
	private static long lastRequestTime = -1;
	private static long minInterval = 1000;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Elements fetchPageParagraphs(String url) throws IOException {
		sleepIfNeeded();

		// download and parse the document
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();

		return doc.select(PARAGRAPH);
	}

	public static Elements fetchSitemapElements(String url) throws IOException {
		Elements content = new Elements();
	    if (!url.contains(".gz")) {
			sleepIfNeeded();

			Connection conn = Jsoup.connect(url);
			Document doc = conn.get();

			content = doc.getElementsByTag(SITEMAP_TAG);
			content.addAll(doc.getElementsByTag(URL_TAG));
		}
		return content;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private static void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchPageParagraphs.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}