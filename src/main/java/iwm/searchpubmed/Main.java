/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed;

import iwm.searchpubmed.indexer.Indexer;
import iwm.searchpubmed.query.TestSearcher;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

/**
 *
 * @author monday
 */
public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        TokenStream ts = new EnglishAnalyzer().tokenStream("", new StringReader("cancers"));
        CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
        
        ts.reset();
        do {
            System.out.println(cta.toString());
        } while(ts.incrementToken());
        ts.end();
        ts.close();
    }
}
