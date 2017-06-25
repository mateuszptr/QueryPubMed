/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.query;

import iwm.searchpubmed.Constants;
import iwm.searchpubmed.algorithm.ImpactFactorDatabase;
import iwm.searchpubmed.algorithm.TFIDF;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author eobard
 */
public class Sorter {

    Searcher searcher;
    ImpactFactorDatabase impactFactorDatabase;
    public Map<String, Map<String, Double>> docMaps;

    public Sorter(Searcher searcher) {
        this.searcher = searcher;
        try {
            this.impactFactorDatabase = new ImpactFactorDatabase(Constants.IF_DB_PATH);
        } catch (IOException ex) {
            Logger.getLogger(Sorter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TopDocs sortedDocuments(String queryString, boolean idf, boolean impactFactor, boolean eigenfactor, Map<String, Double> termMap) throws IOException, ParseException {
        TopDocs hits = searcher.search(queryString);

        TFIDF tfidf = new TFIDF(queryString, searcher, hits, termMap);

        scores = new HashMap<>();

        for (ScoreDoc doc : hits.scoreDocs) {
            Document d = searcher.getIndexSearcher().doc(doc.doc);
            double score = tfidf.score(d, idf);
            if (impactFactor) {
                score *= impactFactorDatabase.getImpactFactor(d);
            }
            if (eigenfactor) {
                score *= impactFactorDatabase.getEigenfactor(d);
            }
            getScores().put(doc, score);
        }
        docMaps = tfidf.docMaps;
        

        Arrays.sort(hits.scoreDocs, (ScoreDoc t, ScoreDoc t1) -> {
            return -Double.compare(scores.get(t), scores.get(t1));
        });
        

        return hits;
    }
    
    public TopDocs sortedDocuments(String queryString, boolean idf, boolean impactFactor, boolean eigenfactor) throws IOException, ParseException {
        TopDocs hits = searcher.search(queryString);

        TFIDF tfidf = new TFIDF(queryString, searcher, hits);

        scores = new HashMap<>();

        for (ScoreDoc doc : hits.scoreDocs) {
            Document d = searcher.getIndexSearcher().doc(doc.doc);
            double score = tfidf.score(d, idf);
            if (impactFactor) {
                score *= impactFactorDatabase.getImpactFactor(d);
            }
            if (eigenfactor) {
                score *= impactFactorDatabase.getEigenfactor(d);
            }
            getScores().put(doc, score);
        }
        docMaps = tfidf.docMaps;

        Arrays.sort(hits.scoreDocs, (ScoreDoc t, ScoreDoc t1) -> {
            return -Double.compare(scores.get(t), scores.get(t1));
        });
        

        return hits;
    }
    
    private Map<ScoreDoc, Double> scores;

    /**
     * @return the scores
     */
    public Map<ScoreDoc, Double> getScores() {
        return scores;
    }

}
