/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.query;

import iwm.searchpubmed.Constants;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author eobard
 */
public class TestSearcher {
    IndexSearcher indexSearcher;
    IndexReader indexReader;
    QueryParser queryParser;
    Query query;
    
    public TestSearcher(String path) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(new File(path).toPath());
        CharArraySet arraySet = new CharArraySet(Files.readAllLines(Paths.get(Constants.STOPWORDS_PATH)), true);
        queryParser = new QueryParser("abstracttext", new EnglishAnalyzer(arraySet));
        indexReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(indexReader);
    }
    
    public TopDocs search(String queryString, int maxHits) throws ParseException, IOException {
        query = queryParser.parse(queryString);
        System.out.println(query.toString("abstracttext"));
        TopDocs hits = indexSearcher.search(query, maxHits);
        
        return hits;
    }
    
    public Document getDocument(ScoreDoc sd) throws IOException {
        return indexSearcher.doc(sd.doc);
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        TestSearcher searcher = new TestSearcher("/tmp/lucene-index");
        
        TopDocs hits = searcher.search("cancers and their symptoms", 10);
        
        for(ScoreDoc sd : hits.scoreDocs) {
            Document d = searcher.getDocument(sd);
            System.out.println(d.getField("pmid").stringValue()+": "+d.getField("articletitle").stringValue()+" ("+sd.score+")\n");
        }
    }
}
