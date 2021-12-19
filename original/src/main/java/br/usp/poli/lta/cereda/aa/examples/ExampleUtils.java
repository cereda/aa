/**
* ------------------------------------------------------
*    Laboratório de Linguagens e Técnicas Adaptativas
*       Escola Politécnica, Universidade São Paulo
* ------------------------------------------------------
* 
* This program is free software: you can redistribute it
* and/or modify  it under the  terms of the  GNU General
* Public  License  as  published by  the  Free  Software
* Foundation, either  version 3  of the License,  or (at
* your option) any later version.
* 
* This program is  distributed in the hope  that it will
* be useful, but WITHOUT  ANY WARRANTY; without even the
* implied warranty  of MERCHANTABILITY or FITNESS  FOR A
* PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
* 
**/
package br.usp.poli.lta.cereda.aa.examples;

import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para a definição dos exemplos.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class ExampleUtils {

    /**
     * Converte uma string em uma lista de símbolos.
     * @param text String de entrada.
     * @return Uma lista de símbolos.
     */
    public static List<Symbol> convert(final String text) {
        ArrayList<Symbol> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            result.add(new ExampleSymbol(text.substring(i, i + 1)));
        }
        return result;
    }
    
    /**
     * Retorna uma resposta de acordo com o valor lógico informado.
     * @param value Valor lógico.
     * @return Resposta positiva ou negativa.
     */
    public static String getAnswer(boolean value) {
        return value ? "sim" : "não";
    }
    
    /**
     * Cria uma lista de transições de acordo com o padrão informado.
     * @param elements Padrão textual.
     * @param from Estado inicial.
     * @param to Estado final.
     * @param name Nome da ação.
     * @return Lista de transições.
     */
    public static List<Transition> create(String elements,
            State from, State to, String name) {
        return create(elements, from, to, name, null);
    }
   
    /**
     * Cria uma lista de transições de acordo com o padrão informado.
     * @param elements Padrão textual.
     * @param from Estado inicial.
     * @param to Estado final.
     * @param name Nome da ação.
     * @param id Identificador do token.
     * @return Lista de transições.
     */
    public static List<Transition> create(String elements, State from,
            State to, String name, String id) {
        List<Transition> transitions = new ArrayList<>();
        for (int i = 0; i < elements.length(); i++) {
            Transition t = new Transition();
            t.setSourceState(from);
            t.setTargetState(to);
            t.setSymbol(new ExampleSymbol(String.valueOf(elements.charAt(i))));
            t.setPostActionCall(name);
            Object[] parameters;
            if (id != null) {
                parameters = new Object[] { id, String.valueOf(elements.charAt(i)) };
            }
            else {
                parameters = new Object[] { String.valueOf(elements.charAt(i)) };
            }
            t.setPostActionArguments(parameters);
            transitions.add(t);
        }
        return transitions;
    }
    
}
