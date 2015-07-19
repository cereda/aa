/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.lta.cereda.aa.examples;

import br.usp.poli.lta.cereda.aa.model.Symbol;
import java.util.ArrayList;

/**
 *
 * @author paulo
 */
public class ExampleUtils {

    /**
     * Converte uma string em uma lista de símbolos.
     * @param text String de entrada.
     * @return Uma lista de símbolos.
     */
    public static ArrayList<Symbol> convert(final String text) {
        ArrayList<Symbol> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            result.add(new ExampleSymbol(text.substring(i, i + 1)));
        }
        return result;
    }
    
    public static String getAnswer(boolean value) {
        return value ? "sim" : "não";
    }
    
}
