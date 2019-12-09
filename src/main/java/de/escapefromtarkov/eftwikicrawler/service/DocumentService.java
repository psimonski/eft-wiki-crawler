package de.escapefromtarkov.eftwikicrawler.service;

import de.escapefromtarkov.eftwikicrawler.adapter.http.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DocumentService {

    private static final Logger LOG = LogManager.getLogger(DocumentService.class);

    @Value("${eftwikicrawler.urlToStart}")
    protected String domainToSearch;

    @PostConstruct
    public void init() {
        domainToSearch = "https://" + HttpUtil.getDomainName(domainToSearch);
    }

    public List<String> getListOfLinksFromDom(Document document, List<String> linkList) {
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String hrefValue = link.attr("href");
//            if (!linkList.contains(hrefValue) && hrefValue.startsWith(domainToSearch)) {
//                linkList.add(hrefValue);
//            } else
            if (!linkList.contains(domainToSearch + hrefValue) && hrefValue.startsWith("/")
                    && !hrefValue.contains("?") && !hrefValue.contains(":") && !hrefValue.contains("#")) {
                linkList.add(domainToSearch + hrefValue);
            }
        }
        //LOG.info(String.format("Links found [%s]", linkList.size()));
        return linkList;
    }

    public String getPageTitleFromDom(Document document) {
        return document.title();
    }

    public boolean isBarterItemPage(Document document) {
        Elements element = document.getElementsByClass("va-navbox-titletext");
        return element.hasText() && element.html().contains("Barter Items");
    }

    public void createPageSummary(Document document) {
        LOG.info("=== " + document.title().replace(" - The Official Escape from Tarkov Wiki", "") + " ===");
        // preparing the images has to be done before the anchors can be processed
        // because images are most often surrounded by a href tags.
        replaceImagesOnDom(document);
        removeAnchorsFromDom(document);
        if (isElementPresent(document, "Quests")) {
            getElementsFromListArea(document, "Quests");
        }
        if (isElementPresent(document, "Hideout")) {
            getElementsFromListArea(document, "Hideout");
        }
        if (isElementPresent(document, "Trading")) {
            getElementsFromPictureArea(document, "Trading");
        }
        if (isElementPresent(document, "Crafting")) {
            getElementsFromPictureArea(document, "Crafting");
        }
    }

    public boolean isElementPresent(Document document, String elementId) {
        Element element = document.getElementById(elementId);
        return element != null;
    }

    private void getElementsFromListArea(Document document, String elementId) {
        Element element = document.getElementById(elementId);
        Element ulElement = element.parent().nextElementSibling();
        for (Element childInList : ulElement.children()) {
            LOG.info("    " + elementId + ": " + childInList.html());
        }
    }

    private void getElementsFromPictureArea(Document document, String elementId) {
        Element element = document.getElementById(elementId);
        Element tBodyElement = element.parent().nextElementSibling();
        for (Element tableRow : tBodyElement.children()) {
            Element firstColumn = tableRow.child(0).child(0);
            Element lastColumn = tableRow.child(0).child(0).nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling();
            String optimizedText = beautifyString(lastColumn.html().trim() + " <==> " + firstColumn.html().trim());
            LOG.info("    " + elementId + ": " + optimizedText);
        }
    }

    private void removeAnchorsFromDom(Document document) {
        Elements links = document.select("a");
        String baseUri = links.get(0).baseUri();
        for (Element link : links) {
            Node linkText = new TextNode(link.html(), baseUri);
            link.replaceWith(linkText);
        }
    }

    private void replaceImagesOnDom(Document document) {
        Elements images = document.select("img");
        String baseUri = images.get(0).baseUri();
        for (Element element : images) {
            // use title attribute value of the surrounding a href tag instead of the image tags.
            String aHrefTagTitleValue = element.parent().attr("title");
            element.replaceWith(new TextNode(aHrefTagTitleValue, baseUri));
        }
    }

    private String beautifyString(String string) {
        return string.replaceAll("<br>", " ").replaceAll("<br/>", " ").
                replaceAll("<p>", "").replaceAll("</p>", "").
                replaceAll("&gt;", "");
    }

}
