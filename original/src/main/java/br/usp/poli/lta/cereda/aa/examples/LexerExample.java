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

import br.usp.poli.lta.cereda.aa.execution.AdaptiveAutomaton;
import br.usp.poli.lta.cereda.aa.model.Action;
import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Submachine;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Exemplo de implementação de um analisador léxico simples, que reconhece
 * palavras e números.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class LexerExample {
    
    final static List<ExampleToken> tokens = new ArrayList<>();
    
    /**
     * Método principal.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        
        AdaptiveAutomaton aa = new AdaptiveAutomaton() {

            @Override
            public void setup() {
                
                ExampleState q0 = new ExampleState("q0");
                ExampleState q1 = new ExampleState("q1");
                ExampleState q2 = new ExampleState("q2");
                ExampleState q3 = new ExampleState("q3");
                ExampleState q4 = new ExampleState("q4");
                
                HashSet<State> states = new HashSet<>();
                states.add(q0);
                states.add(q1);
                states.add(q2);
                states.add(q3);
                states.add(q4);
                
                HashSet<State> accept = new HashSet<>();
                accept.add(q0);
                accept.add(q1);
                accept.add(q2);
                accept.add(q3);
                accept.add(q4);
                
                Submachine M = new Submachine("M", states, q0, accept);
                
                Action withA = new Action("A") {
                    
                    @Override
                    public void execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        ExampleToken token = new ExampleToken();
                        token.setId(String.valueOf(parameters[0]));
                        token.setValue(String.valueOf(parameters[1]));
                        tokens.add(token);
                    }
                };
                
                Action withB = new Action("B") {
                    
                    @Override
                    public void execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        tokens.get(tokens.size() - 1).setValue(
                                tokens.get(tokens.size() - 1).getValue().
                                        concat(String.valueOf(parameters[0])));
                    }
                };
               
                actions.add(withA);
                actions.add(withB);
                
                submachines.add(M);
                
                List<Transition> ts;
                
                ts = ExampleUtils.
                        create("0123456789", q0, q1, "A", "number");
                for (Transition t : ts) {
                    transitions.add(t);
                }
                
                ts = ExampleUtils.
                        create("abcdefghijklmnopqrstuvwxyz", q0, q2, "A", "word");
                for (Transition t : ts) {
                    transitions.add(t);
                }
                
                ts = ExampleUtils.
                        create("0123456789", q1, q1, "B");
                for (Transition t : ts) {
                    transitions.add(t);
                }
                
                ts = ExampleUtils.
                        create("abcdefghijklmnopqrstuvwxyz", q2, q2, "B");
                for (Transition t : ts) {
                    transitions.add(t);
                }
                
                setMainSubmachine("M");
                
            }
            
        };
        
        List<Symbol> symbols = ExampleUtils.convert("hello world 123");
        
        int size = symbols.size();
        int compare = 0;
        
        do {
            symbols = symbols.subList(compare, size);
            aa.recognize(symbols);
            size = symbols.size();
            compare = aa.getRecognitionPaths().get(0).getCursor() == compare ?
                    compare + 1 : aa.getRecognitionPaths().get(0).getCursor();
        } while (size > compare);
        
        System.out.println(tokens);

    }
   
}
