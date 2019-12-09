package de.escapefromtarkov.eftwikicrawler.adapter.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpAdapterImpl {

    private static final Logger LOG = LogManager.getLogger(HttpAdapterImpl.class);

    @Value("${proxy.host}")
    protected String proxyHost;

    @Value("${proxy.port}")
    protected String proxyPort;

    @Value("${proxy.exceptions}")
    protected String proxyExceptions;

    /**
     * Fetch a web page.
     */
    public Document surf(String url) throws IOException {
        Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        Document document = connection.timeout(5000).ignoreHttpErrors(true).followRedirects(true).get();
        LOG.debug(String.format("Loaded page title [%s] from url [%s]", document.title(), url));
        return document;
    }

}
