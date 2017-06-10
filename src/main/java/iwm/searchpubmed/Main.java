/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed;

import iwm.searchpubmed.indexer.Indexer;
import iwm.searchpubmed.query.Searcher;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

/**
 *
 * @author monday
 */
public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, ParseException {
        System.out.println("Hello World");
     
        Indexer.main(args);
        Searcher.main(args);
    }
}
