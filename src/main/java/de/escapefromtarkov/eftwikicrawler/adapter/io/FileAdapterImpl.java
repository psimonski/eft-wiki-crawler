package de.escapefromtarkov.eftwikicrawler.adapter.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class FileAdapterImpl implements IFileAdapter {

    private static final Logger LOG = LogManager.getLogger(FileAdapterImpl.class);

    public String getFileContent(String fileName) {
        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return fileContent;
    }

    public Document loadFile(String fileName) throws IOException {
        return Jsoup.parse(new File(fileName), "utf-8");
    }

    public void appendToFile(String string, String fileName) {
        string += "\\\n";
        try {
            Files.write(Paths.get(fileName), string.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void saveFile(Document document, String fileName) {
        try {
            Files.write(Paths.get(fileName), document.outerHtml().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
