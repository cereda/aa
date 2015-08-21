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
import br.usp.poli.lta.cereda.aa.utils.RecognitionPath;
import java.util.HashSet;
import java.util.Set;

/**
 * Exemplo de reconhecimento de uma linguagem regular. A linguagem regular,
 * neste caso, é '(ab)+'.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class RegularLanguageExample {
    
    /**
     * Método principal.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        
        AdaptiveAutomaton aa = new AdaptiveAutomaton() {

            @Override
            public void setup() {
                
                State q0 = new ExampleState("q0");
                State q1 = new ExampleState("q1");
                State q2 = new ExampleState("q2");
                
                Set<State> states = new HashSet<>();
                states.add(q0);
                states.add(q1);
                states.add(q2);
                
                Set<State> accept = new HashSet<>();
                accept.add(q2);
                
                Submachine M = new Submachine("M", states, q0, accept);
                
                Action withA = new Action("A") {
                    
                    @Override
                    public void execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        System.out.println("Consumi A");
                    }
                };
                
                Action withB = new Action("B") {
                    
                    @Override
                    public void execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        System.out.println("Consumi B");
                    }
                };
                
                actions.add(withA);
                actions.add(withB);
                
                Symbol a = new ExampleSymbol("a");
                Symbol b = new ExampleSymbol("b");
                
                Transition t1 = new Transition();
                t1.setTransition(q0, a, q1);
                t1.setPostActionCall("A");
                
                Transition t2 = new Transition();
                t2.setTransition(q1, b, q2);
                t2.setPostActionCall("B");
                
                Transition t3 = new Transition();
                t3.setTransition(q2, a, q1);
                t3.setPostActionCall("A");
                
                submachines.add(M);
                transitions.add(t1);
                transitions.add(t2);
                transitions.add(t3);
                
                setMainSubmachine("M");
                
            }
            
        };
        
        boolean resultado = aa.recognize(ExampleUtils.convert("ab"));
        
        System.out.println("Resultado: cadeia "
                .concat(resultado == true ? "aceita" : "rejeitada"));
        System.out.print("Reconhecimento determinístico? ");
        System.out.println(
                ExampleUtils.getAnswer(aa.getRecognitionPaths().size() == 1)
        );
        for (RecognitionPath rp : aa.getRecognitionPaths()) {
            System.out.println(rp);
        }
    }
    
}
