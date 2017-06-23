/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.gui;

import iwm.searchpubmed.query.Searcher;
import iwm.searchpubmed.query.Sorter;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author eobard
 */
public class HTMLGenerator {
    StringBuilder htmlText;
    Searcher searcher;
    String queryString;
    TopDocs hits;

    public HTMLGenerator(Searcher searcher, String queryString, TopDocs hits) {
        htmlText = new StringBuilder();
        this.searcher = searcher;
        this.queryString = queryString;
        this.hits = hits;
    }
    
    
    
    void query() {
        htmlText.append("<h2>Processed Query: ");
        htmlText.append(searcher.termList(queryString));
        htmlText.append("</h2>\n");
    }
    
    void article(Document doc) {
        String title = doc.getField("articletitle").stringValue();
        htmlText.append("<a href=\"https://www.ncbi.nlm.nih.gov/pubmed/").append(doc.getField("pmid")).append("\">");
        htmlText.append("<h3>");
        htmlText.append(title);
        htmlText.append("</h3></a>");
//        for(IndexableField f : doc.getFields("abstracttext")) {
//            htmlText.append("<p>");
//            htmlText.append(f.stringValue());
//            htmlText.append("</p>");
//        }
        htmlText.append("<p><b>Keywords: </b>");
        for(IndexableField f : doc.getFields("keyword")) {
            htmlText.append(f.stringValue());
            htmlText.append(" ");
        }
        htmlText.append("</p>");
        htmlText.append("<p><b>PMID:</b> ").append(doc.getField("pmid").stringValue()).append("</p>");
        
    }
    
    public String generateHTML() throws IOException {
        
        query();
        for(ScoreDoc sd : hits.scoreDocs) {
            article(searcher.getIndexSearcher().doc(sd.doc));
        }
        
        return htmlText.toString();
    }
}
