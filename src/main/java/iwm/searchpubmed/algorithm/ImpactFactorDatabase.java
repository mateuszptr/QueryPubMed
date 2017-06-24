/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.algorithm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.document.Document;

/**
 *
 * @author eobard
 */
public class ImpactFactorDatabase {

    Map<String, Double> impactFactor;
    Map<String, Double> eigenFactor;

    public ImpactFactorDatabase(String file) throws FileNotFoundException, IOException {
        impactFactor = new HashMap<>();
        eigenFactor = new HashMap<>();

        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

        for (CSVRecord rec : records) {
            //System.out.println(rec.get("ISSN") + " " + rec.get("IMPACT_FACTOR"));
            String issn = rec.get("ISSN");

            try {
                double impact_factor = Double.valueOf(rec.get("IMPACT_FACTOR"));
                double eigenfactor = Double.valueOf(rec.get("EIGENFACTOR"));

                impactFactor.put(issn, impact_factor);
                eigenFactor.put(issn, eigenfactor);
                
            } catch (NumberFormatException e) {

            }
        }

    }

    public double getImpactFactor(Document doc) {
        String issn;
        if (doc.getField("issn") != null) {
            issn = doc.getField("issn").stringValue();
            if(impactFactor.get(issn) != null)
                return impactFactor.get(issn);
            else 
                return 0.0;
        }
        else {
            return 0.0;
        }
    }

    public double getEigenfactor(Document doc) {
        String issn;
        if (doc.getField("issn") != null) {
            issn = doc.getField("issn").stringValue();
            if(eigenFactor.get(issn) != null)
                return eigenFactor.get(issn);
            else 
                return 0.0;
        }
        else {
            return 0.0;
        }
    }

}
