/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.query;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import iwm.searchpubmed.Constants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author eobard
 */
public class Searcher {

    private IndexSearcher indexSearcher;
    private IndexReader reader;
    private MultiFieldQueryParser parser;

    public Searcher() throws IOException {
        Directory directory = FSDirectory.open(new File(Constants.INDEX_PATH).toPath());
        reader = DirectoryReader.open(directory);
        CharArraySet arraySet = new CharArraySet(Files.readAllLines(Paths.get(Constants.STOPWORDS_PATH)), true);
        parser = new MultiFieldQueryParser(Constants.FIELDS, new EnglishAnalyzer(arraySet));
        indexSearcher = new IndexSearcher(getReader());

    }

    public TopDocs search(String queryString) throws ParseException, IOException {
        Query query = getParser().parse(queryString);

        TopDocs hits = getIndexSearcher().search(query, Constants.MAX_DOCS);
        
        return hits;
    }

    public static void main(String[] args) throws ParseException, IOException {
        Searcher s = new Searcher();
        Query q = s.getParser().parse("cancers and their symptoms");
        
    }

    /**
     * @return the indexSearcher
     */
    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }

    /**
     * @return the reader
     */
    public IndexReader getReader() {
        return reader;
    }

    /**
     * @return the parser
     */
    public MultiFieldQueryParser getParser() {
        return parser;
    }
}
