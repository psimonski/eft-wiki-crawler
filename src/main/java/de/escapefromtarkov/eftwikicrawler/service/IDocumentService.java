package de.escapefromtarkov.eftwikicrawler.service;

import org.jsoup.nodes.Document;

import java.util.List;

public interface IDocumentService {

    List<String> getListOfLinksFromDom(Document document, List<String> linkList);

    boolean isBarterItemPage(Document document);

    void createPageSummary(Document document);

    String getPageTitleFromDom(Document document);

}
