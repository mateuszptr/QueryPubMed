/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.gui;

import iwm.searchpubmed.query.Searcher;
import iwm.searchpubmed.query.Sorter;
import java.io.IOException;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

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
    
    
    void query(String queryString, Map<String, Double> termMap) {
        htmlText.append("<h1>Processed Query: </h1>");
        
        htmlText.append("<table>");
        for(Map.Entry<String, Double> entry : termMap.entrySet()) {
            String term = entry.getKey();
            double weigth = entry.getValue();
            htmlText.append("<tr><td>").append(term).append("</td>");
            htmlText.append("<td>").append("<input id='").append(term).append("' value='").append(weigth).append("'></td></tr>");
        }
        htmlText.append("</table>");
        
    }
    
    void query(String queryString) {
        query(queryString, searcher.termMap(queryString));
    }
    
    void article(Document doc, double score, int no) {
        String title = doc.getField("articletitle").stringValue();
        htmlText.append("<a href=\"https://www.ncbi.nlm.nih.gov/pubmed/").append(doc.getField("pmid").stringValue()).append("\">");
        htmlText.append("<h2>").append(no).append(". ");
        htmlText.append(escapeHtml4(title));
        htmlText.append("</h2></a>");
        htmlText.append("<p>Score: ").append(score).append("</p>");
        htmlText.append("Useful?").append("<input type='checkbox' id='").append(doc.getField("pmid").stringValue()).append("'><br>");
        for(IndexableField f : doc.getFields("abstracttext")) {
            htmlText.append("<p>");
            htmlText.append(escapeHtml4(f.stringValue()));
            htmlText.append("</p>");
        }
        htmlText.append("<p><b>Keywords: </b>");
        for(IndexableField f : doc.getFields("keyword")) {
            htmlText.append(escapeHtml4(f.stringValue()));
            htmlText.append(" ");
        }
        htmlText.append("</p>");
        htmlText.append("<p><b>PMID:</b> ").append(doc.getField("pmid").stringValue()).append("</p>");
        
        
    }
    
    public String generateHTML(String queryString, TopDocs hits, Map<String, Double> termMap) throws IOException {
        htmlText = new StringBuilder();
        query(queryString, termMap);
        int i=0;
        for(ScoreDoc sd : hits.scoreDocs) {
            i++;
            article(searcher.getIndexSearcher().doc(sd.doc), sorter.getScores().get(sd), i);
        }
        htmlText.append("<hr>");
        
        return htmlText.toString();
    }
    
    public String generateHTML(String queryString, TopDocs hits) throws IOException {
        htmlText = new StringBuilder();
        query(queryString);
        int i=0;
        for(ScoreDoc sd : hits.scoreDocs) {
            i++;
            article(searcher.getIndexSearcher().doc(sd.doc), sorter.getScores().get(sd), i);
        }
        htmlText.append("<hr>");
        
        System.out.println(htmlText.toString());
        return htmlText.toString();
    }
}
