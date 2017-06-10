/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.query;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
public class Searcher {
    IndexSearcher indexSearcher;
    IndexReader indexReader;
    QueryParser queryParser;
    Query query;
    
    public Searcher(String path) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(new File(path).toPath());
        queryParser = new QueryParser("abstracttext", new StandardAnalyzer());
        indexReader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(indexReader);
    }
    
    public TopDocs search(String queryString, int maxHits) throws ParseException, IOException {
        query = queryParser.parse(queryString);
        TopDocs hits = indexSearcher.search(query, maxHits);
        return hits;
    }
    
    public Document getDocument(ScoreDoc sd) throws IOException {
        return indexSearcher.doc(sd.doc);
    }
    
    public static void main(String[] args) throws IOException, ParseException {
        Searcher searcher = new Searcher("index");
        
        TopDocs hits = searcher.search("dissociative identity", 10);
        
        for(ScoreDoc sd : hits.scoreDocs) {
            Document d = searcher.getDocument(sd);
            System.out.println(d.getField("pmid").stringValue()+": "+d.getField("articletitle").stringValue()+" ("+sd.score+")\n");
        }
    }
}
