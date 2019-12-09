package de.escapefromtarkov.eftwikicrawler.adapter.http;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IHttpAdapter {

    Document surf(String url) throws IOException;

}
