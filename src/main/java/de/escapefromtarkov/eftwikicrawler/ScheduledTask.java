package de.escapefromtarkov.eftwikicrawler;

import de.escapefromtarkov.eftwikicrawler.adapter.http.HttpAdapterImpl;
import de.escapefromtarkov.eftwikicrawler.adapter.http.IHttpAdapter;
import de.escapefromtarkov.eftwikicrawler.adapter.io.FileAdapterImpl;
import de.escapefromtarkov.eftwikicrawler.adapter.io.IFileAdapter;
import de.escapefromtarkov.eftwikicrawler.service.DocumentService;
import de.escapefromtarkov.eftwikicrawler.service.IDocumentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTask {

    private static final Logger LOG = LogManager.getLogger(ScheduledTask.class);

    @Value("${eftwikicrawler.urlToStart}")
    private String urlToStart;

    @Value("${eftwikicrawler.folderToWriteFiles}")
    private String folderToWriteFiles;

    @Value("${eftwikicrawler.createFiles}")
    private boolean createFiles;

    private IHttpAdapter httpAdapter;

    private IFileAdapter fileAdapter;

    private IDocumentService documentService;

    private List<String> linkListToDo = new LinkedList<>();
    private List<String> linkListProcessed = new LinkedList<>();

    public ScheduledTask(HttpAdapterImpl httpAdapter, FileAdapterImpl fileAdapter, DocumentService documentService) {
        this.httpAdapter = httpAdapter;
        this.fileAdapter = fileAdapter;
        this.documentService = documentService;
    }

    @PostConstruct
    public void init() {
    }

    @Bean
    public TaskScheduler taskExecutor() {
        return new ConcurrentTaskScheduler(
                Executors.newScheduledThreadPool(3));
    }

    // 3600000 = each hour
    // 21600000 = each 6 hours
    // 86400000 = once a day
    @Scheduled(fixedRate = 1000, initialDelay = 0)
    public void periodicallyTriggeredCrawler() throws InterruptedException {
        if (linkListToDo.isEmpty()) {
            linkListToDo.add(urlToStart);
        }
        List<String> linkListOfLastCrawledPage = startCrawling();
        linkListOfLastCrawledPage.removeAll(linkListToDo);
        linkListOfLastCrawledPage.removeAll(linkListProcessed);
        linkListToDo.addAll(linkListOfLastCrawledPage);
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Processed [%s/%s], from last page [%s]", linkListProcessed.size(),
                    linkListToDo.size(), linkListOfLastCrawledPage.size()));
        }
    }

    private List<String> startCrawling() throws InterruptedException {
        List<String> linkList = new LinkedList<>();
        String linkToRemove = "";
        for (String link : linkListToDo) {
            if (linkListProcessed.contains(link)) {
                continue;
            }
            linkList = processPage(link, linkList);
            linkListProcessed.add(link);
            linkToRemove = link;
            break;
        }
        linkListToDo.remove(linkToRemove);
        return linkList;
    }

    private List<String> processPage(String url, List<String> linkList) throws InterruptedException {
        try {
            //String fileName = "C:\\temp\\test\\A pack of screws - The Official Escape from Tarkov Wiki.html";
            //Document document = documentService.loadPageFromFile(fileName);
            Document document = httpAdapter.surf(url);
            linkList = documentService.getListOfLinksFromDom(document, linkList);
            if (documentService.isBarterItemPage(document)) {
                documentService.createPageSummary(document);
                if (createFiles) {
                    fileAdapter.saveFile(document,folderToWriteFiles + documentService.getPageTitleFromDom(document) + ".html");
                }
            }
        } catch (IOException e) {
            LOG.error("Error: " + e.getMessage() + " (but will try to continue in 300 seconds...)");
            TimeUnit.SECONDS.sleep(300);
            processPage(url, linkList);
        }
        if (createFiles) {
            fileAdapter.appendToFile(url, folderToWriteFiles + "traffic.log");
        }
        return linkList;
    }

}