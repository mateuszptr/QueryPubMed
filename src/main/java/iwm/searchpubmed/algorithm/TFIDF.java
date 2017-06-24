/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.algorithm;

import iwm.searchpubmed.Constants;
import iwm.searchpubmed.query.Searcher;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author eobard
 */
public class TFIDF {

    Map<String, Double> idfs;
    String[] termList;

    public TFIDF(String queryString, Searcher searcher, TopDocs hits) throws IOException, ParseException {
        idfs = new HashMap<>();

        arraySet = new CharArraySet(Files.readAllLines(Paths.get(Constants.STOPWORDS_PATH)), true);

        termList = searcher.termList(queryString);
        idf(searcher, hits);

    }
    CharArraySet arraySet;

    private String[] fieldTerms(IndexableField f) throws IOException {
        if (f.fieldType().tokenized()) {
            TokenStream ts = f.tokenStream(new EnglishAnalyzer(arraySet), null);
            CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
            List<String> terms = new ArrayList<>();
            ts.reset();
            do {
                if (!cta.toString().equals("")) {
                    terms.add(cta.toString());
                }
            } while (ts.incrementToken());
            ts.end();
            ts.close();
            return terms.toArray(new String[terms.size()]);
        } else {
            String words[] = f.stringValue().split("\\s+");
            return words;
        }
    }

    public double score(Document doc, boolean idf) throws IOException {

        Map<String, Double> scores = new HashMap<>();

        for (String term : termList) {
            int wordCount = 0, termCount = 0;
            for (IndexableField f : doc.getFields()) {
                String words[] = fieldTerms(f);

                for (String s : words) {
                    wordCount++;
                    if (s.equalsIgnoreCase(term)) {
                        termCount++;
                    }
                }
                double score;
                if (termCount > 0) {
                    score = 1.0 + Math.log10(termCount);
                } else {
                    score = 0.0;
                }
                if (idf) {
                    score *= idfs.get(term);
                }
                scores.put(term, score);
            }
        }

        return scores.values().stream().mapToDouble(t -> t).average().getAsDouble();
    }

    public double score(Document doc) throws IOException {
        return score(doc, true);
    }

    private boolean contains(Document doc, String term) throws IOException {
        for (IndexableField f : doc.getFields()) {
            String words[] = fieldTerms(f);
            for (String s : words) {
                if (s.equalsIgnoreCase(term)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void idf(Searcher searcher, TopDocs hits) throws IOException, ParseException {
//        int docsCount = hits.scoreDocs.length;
//        for (String term : termList) {
//            int count = 0;
//            for (ScoreDoc sd : hits.scoreDocs) {
//                Document d = searcher.doc(sd.doc);
//                if (contains(d, term)) {
//                    count++;
//                }
//            }
//            double score = Math.log(docsCount / (count + 1.0));
//            idfs.put(term, score);
//        }
        for (String term : termList) {
            Query termQuery = searcher.getParser().parse(term);
            int count = searcher.getIndexSearcher().count(termQuery);
            int docsCount = searcher.getIndexSearcher().getIndexReader().numDocs();
            double score = Math.log10(1.0 + docsCount / (count + 1.0));
            idfs.put(term, score);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String queryString = "cancers and their symptoms";

        Searcher searcher = new Searcher();
        TopDocs hits = searcher.search(queryString);
        TFIDF tfidf = new TFIDF(queryString, searcher, hits);

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
