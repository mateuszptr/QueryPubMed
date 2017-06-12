/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.indexer;

import iwm.searchpubmed.Constants;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

/**
 *
 * @author eobard
 */
public class Indexer {
    private IndexWriter writer;
    
    public Indexer(String path) throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(path).toPath());
        CharArraySet arraySet = new CharArraySet(Files.readAllLines(Paths.get(Constants.STOPWORDS_PATH)), true);

        writer = new IndexWriter(indexDirectory, new IndexWriterConfig(new EnglishAnalyzer(arraySet)));
    }
    
    public void indexDocuments(File file) throws SAXException, IOException, ParserConfigurationException {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        CustomContentHandler handler = new CustomContentHandler(writer);
        GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
        
        parser.parse(is, handler);        
        
    }
    
    public void close() throws IOException {
        writer.close();
    }
    
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        Indexer indexer = new Indexer("/tmp/lucene-index/");
        
        File dir = new File("snapshots");
        File[] files = dir.listFiles();
        
        for (File file : files) {
            indexer.indexDocuments(file);
        }
        indexer.close();
    }
}
