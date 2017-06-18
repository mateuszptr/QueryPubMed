/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.indexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author eobard
 */
public class CustomContentHandler extends DefaultHandler {

    private IndexWriter indexWriter;
    private Document doc;
    String currentElement;
    State state;

    enum State {
        EMPTY, JOURNAL
    }

    public CustomContentHandler(IndexWriter indexWriter) {
        super();
        this.indexWriter = indexWriter;
        currentElement = "";
        state = State.EMPTY;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
        if (qName.equalsIgnoreCase("Journal")) {
            state = State.JOURNAL;
        }
        if (qName.equalsIgnoreCase("PubmedArticle")) {
            doc = new Document();
        }
        currentElement = qName.toLowerCase();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("Journal")) {
            state = State.EMPTY;
        }
        if (qName.equalsIgnoreCase("PubmedArticle")) {
            try {
                //docs.add(doc);
                indexWriter.updateDocument(new Term("pmid", doc.get("pmid")), doc);
            } catch (IOException ex) {
                Logger.getLogger(CustomContentHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        currentElement = "";
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        if (state == State.JOURNAL) {
            switch (currentElement) {
                case "issn": {
                    Field field = new StringField(currentElement, new String(chars, start, length), Field.Store.YES);
                    doc.add(field);
                    break;
                }
                case "title": {
                    Field field = new StringField("journaltitle", new String(chars, start, length), Field.Store.YES);
                    doc.add(field);
                    break;
                }
                case "isoabbreviation": {
                    Field field = new StringField(currentElement, new String(chars, start, length), Field.Store.YES);
                    doc.add(field);
                    break;
                }
            }
        }

        switch (currentElement) {
            case "pmid": {
                Field field = new StringField(currentElement, new String(chars, start, length), Field.Store.YES);
                doc.add(field);
                break;
            }
            case "abstracttext": {
                Field field = new TextField(currentElement, new String(chars, start, length), Field.Store.YES);
                doc.add(field);
                break;
            }
            case "keyword": {
                Field field = new TextField(currentElement, new String(chars, start, length), Field.Store.YES);
                doc.add(field);
                break;
            }
            case "articletitle": {
                Field field = new TextField(currentElement, new String(chars, start, length), Field.Store.YES);
                doc.add(field);
                break;

            }
            default:
                break;
        }
    }

}
