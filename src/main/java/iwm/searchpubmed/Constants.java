/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed;

/**
 *
 * @author eobard
 */
public class Constants {
    public static final String INDEX_PATH = "/tmp/lucene-index";
    public static final String SNAPSHOTS_PATH = "snapshots";
    public static final String[] FIELDS = {"articletitle", "abstracttext", "keyword"};
    public static final String STOPWORDS_PATH = "stopwords.txt";
    public static final int MAX_DOCS = 100;
}
