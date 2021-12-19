/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.lta.cereda.aa.examples;

import br.usp.poli.lta.cereda.aa.metrics.TimeAnalysis;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author paulo
 */
public class Calculator {
    
    public static double getScore(List<TimeAnalysis> analysis) {
        double sum = 0;
        for (TimeAnalysis time : analysis) {
            switch (time.getType()) {
                case SYMBOL_CONSUMPTION:
                case SYMBOL_CONSUMPTION_WITH_POST_ACTION:
                    sum += omega(time, "transitions");
                    break;
                case QUERY_ACTION:
                    sum += omega(time, "transitions_size");
                    break;
                case ADD_ACTION:
                    sum = sum + (product(time) * 
                            omega(time, "transitions_size")) + product(time);
                    break;
                case REMOVE_ACTION:
                    sum = sum + (product(time) * 
                            omega(time, "transitions_size")) - product(time);
                    break;
            }
        }
        return sum;
    }
    
    private static double omega(TimeAnalysis time, String key) {
        return time.getAttributes().getOrDefault(key, 0.0) / 2.0;
    }
    
    private static double product(TimeAnalysis time) {
        double result = 1;
        for (Map.Entry<String, Double> entry : time.getAttributes().entrySet()) {
            if (!entry.getKey().startsWith("transitions_size")) {
                result *= entry.getValue();
            }
        }
        return result;
    }
    
}
