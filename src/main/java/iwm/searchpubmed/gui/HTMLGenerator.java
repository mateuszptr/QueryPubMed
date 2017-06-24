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
    Sorter sorter;

    public HTMLGenerator(Searcher searcher, Sorter sorter) {
        this.searcher = searcher;
        this.sorter = sorter;
    }
    
    
    void query(String queryString) {
        htmlText.append("<h1>Processed Query: ");
        htmlText.append(searcher.termList(queryString));
        htmlText.append("</h1>");
    }
    
    void article(Document doc, double score) {
        String title = doc.getField("articletitle").stringValue();
        htmlText.append("<a href=\"https://www.ncbi.nlm.nih.gov/pubmed/").append(doc.getField("pmid").stringValue()).append("\">");
        htmlText.append("<h2>");
        htmlText.append(title);
        htmlText.append("</h2></a>");
        htmlText.append("<p>Score: ").append(score).append("</p>");
        for(IndexableField f : doc.getFields("abstracttext")) {
            htmlText.append("<p>");
            htmlText.append(f.stringValue());
            htmlText.append("</p>");
        }
        htmlText.append("<p><b>Keywords: </b>");
        for(IndexableField f : doc.getFields("keyword")) {
            htmlText.append(f.stringValue());
            htmlText.append(" ");
        }
        htmlText.append("</p>");
        htmlText.append("<p><b>PMID:</b> ").append(doc.getField("pmid").stringValue()).append("</p>");
        
    }
    
    public String generateHTML(String queryString, TopDocs hits) throws IOException {
        htmlText = new StringBuilder();
        query(queryString);
        for(ScoreDoc sd : hits.scoreDocs) {
            article(searcher.getIndexSearcher().doc(sd.doc), sorter.getScores().get(sd));
        }
        htmlText.append("<hr>");
        
        return htmlText.toString();
    }
}
