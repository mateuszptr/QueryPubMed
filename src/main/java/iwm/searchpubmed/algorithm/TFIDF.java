/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.algorithm;

import iwm.searchpubmed.Constants;
import iwm.searchpubmed.query.Searcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author eobard
 */
public class TFIDF {

    Map<String, Double> idfs;
    String[] termList;

    public TFIDF(String queryString, IndexSearcher searcher, TopDocs hits) throws IOException, ParseException {
        idfs = new HashMap<>();

        CharArraySet arraySet = new CharArraySet(Files.readAllLines(Paths.get(Constants.STOPWORDS_PATH)), true);
        QueryParser dummy = new QueryParser("", new EnglishAnalyzer(arraySet));
        String parsedString = dummy.parse(queryString).toString();

        termList = parsedString.split("\\s+");
        idf(searcher, hits);

    }

    public double score(Document doc, boolean idf) {

        Map<String, Double> scores = new HashMap<>();

        for (String term : termList) {
            int wordCount = 0, termCount = 0;
            for (IndexableField f : doc.getFields()) {
                String words[] = f.stringValue().split("\\s+");
                for (String s : words) {
                    wordCount++;
                    if (s.equalsIgnoreCase(term)) {
                        termCount++;
                    }
                }
                double score = termCount * 1.0 / wordCount;
                if (idf) {
                    score *= idfs.get(term);
                }
                scores.put(term, score);
            }
        }

        return scores.values().stream().mapToDouble(t -> t).average().getAsDouble();
    }

    public double score(Document doc) {
        return score(doc, true);
    }

    private boolean contains(Document doc, String term) {
        for (IndexableField f : doc.getFields()) {
            String words[] = f.stringValue().split("\\s+");
            for (String s : words) {
                if (s.equalsIgnoreCase(term)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void idf(IndexSearcher searcher, TopDocs hits) throws IOException {
        int docsCount = hits.totalHits;
        for (String term : termList) {
            int count = 0;
            for (ScoreDoc sd : hits.scoreDocs) {
                Document d = searcher.doc(sd.doc);
                if (contains(d, term)) {
                    count++;
                }
            }
            double score = Math.log(docsCount / (count + 1.0));
            idfs.put(term, score);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String queryString = "cancers and their symptoms";

        Searcher searcher = new Searcher();
        TopDocs hits = searcher.search(queryString);
        TFIDF tfidf = new TFIDF(queryString, searcher.getIndexSearcher(), hits);

        Map<ScoreDoc, Double> scores = new HashMap<>();

        for (ScoreDoc sd : hits.scoreDocs) {
            Document d = searcher.getIndexSearcher().doc(sd.doc);
            double score = tfidf.score(d);
            scores.put(sd, score);
        }

        Arrays.sort(hits.scoreDocs, (ScoreDoc t, ScoreDoc t1) -> {
            return -Double.compare(scores.get(t), scores.get(t1));
        });

        for (ScoreDoc t : hits.scoreDocs) {
            try {
                Document d = searcher.getIndexSearcher().doc(t.doc);
                System.out.println(String.format("%s (%f)", d.getField("articletitle").stringValue(), scores.get(t)));
            } catch (IOException ex) {
                Logger.getLogger(TFIDF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
