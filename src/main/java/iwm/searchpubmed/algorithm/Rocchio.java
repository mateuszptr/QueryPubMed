/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author eobard
 */
public class Rocchio {
    private static final double A = 1.0;
    private static final double B = 0.8;
    
    public static Map<String, Double> rocchioWeigths(Map<String, Double> queryWeigths, Collection<Map<String, Double>> goodDocumentsWeigths) {
        Map<String, Double> output = new HashMap<>();
        for(Map.Entry<String, Double> entry : queryWeigths.entrySet()) {
            String term = entry.getKey();
            double val = A * entry.getValue();
            
            for(Map<String, Double> map : goodDocumentsWeigths) {
                val += B * map.get(term);
            }
            output.put(term, val);
        }
        return output;
    }
}
