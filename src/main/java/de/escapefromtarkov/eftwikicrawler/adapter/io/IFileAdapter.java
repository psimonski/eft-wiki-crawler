package de.escapefromtarkov.eftwikicrawler.adapter.io;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IFileAdapter {

    String getFileContent(String fileName);

    Document loadFile(String fileName) throws IOException;

    void appendToFile(String string, String fileName);

    void saveFile(Document document, String fileName);

}
