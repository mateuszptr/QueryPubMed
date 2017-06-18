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
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

        for (CSVRecord rec : records) {
            String issn = rec.get("ISSN");
            double impact_factor = Double.valueOf(rec.get("IMPACT_FACTOR"));
            double eigenfactor = Double.valueOf(rec.get("EIGENFACTOR"));
            
            impactFactor.put(issn, impact_factor);
            eigenFactor.put(issn, eigenfactor);
        }

    }

}
