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
import br.usp.poli.lta.cereda.aa.metrics.TimeAnalysis;
import br.usp.poli.lta.cereda.aa.model.Action;
import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Submachine;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.actions.ActionQuery;
import br.usp.poli.lta.cereda.aa.model.actions.ElementaryActions;
import br.usp.poli.lta.cereda.aa.model.actions.Variable;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exemplo de reconhecimento de uma linguagem dependente de contexto. A
 * linguagem dependente de contexto, neste caso, é 'a^n b^n c^n', com 'n' maior
 * que zero.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class CostExampleTwo {
    
    // variável auxiliar para marcar os novos
    // estados
    private static int COUNTER = 20;
    
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
                State q3 = new ExampleState("q3");
                
                Set<State> states = new HashSet<>();
                states.add(q0);
                states.add(q1);
                states.add(q2);
                states.add(q3);
                
                Set<State> accept = new HashSet<>();
                accept.add(q3);
                
                Submachine M = new Submachine("M", states, q0, accept);

                Symbol a = new ExampleSymbol("a");
                Symbol b = new ExampleSymbol("b");
                Symbol c = new ExampleSymbol("c");
                
                Transition t1 = new Transition();
                t1.setTransition(q0, a, q1);
                
                Transition t2 = new Transition();
                t2.setTransition(q1, b, q2);

                Transition t3 = new Transition();
                t3.setTransition(q2, c, q3);

                Transition t4 = new Transition();
                t4.setTransition(q1, a, q1);
                t4.setPostActionCall("A");
                
                Action adapt = new Action("A") {
                    
                    @Override
                    public List<TimeAnalysis> execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        
                        Symbol a = new ExampleSymbol("a");
                        Symbol b = new ExampleSymbol("b");
                        Symbol c = new ExampleSymbol("c");
                        
                        State q1 = new ExampleState("q1");
                        
                        ElementaryActions ea = new ElementaryActions(transitions);
                        
                        Variable g1 = new Variable(
                                CostExampleTwo.generateState()
                        );
                        Variable g2 = new Variable(
                                CostExampleTwo.generateState()
                        );

                        Variable x = new Variable();
                        Variable y = new Variable();
                        
                        State q2 = new ExampleState("q2");
                        State q3 = new ExampleState("q3");
                        
                        Variable w = new Variable(q2);
                        Variable z = new Variable(q3);
                        
                        ea.query(x, new Variable(b), w);
                        ea.remove(x, new Variable(b), w);
                        
                        ea.query(y, new Variable(c), z);
                        ea.remove(y, new Variable(c), z);
                       
                        ea.remove(new Variable(q1), new Variable(a),
                                new Variable(q1),
                                new ActionQuery(new Variable("A"))
                        );
                       
                        ea.add(x, new Variable(b), g1);
                        ea.add(g1, new Variable(b), w);
                        ea.add(y, new Variable(c), g2);
                        ea.add(g2, new Variable(c), z);
  
                        ea.add(new Variable(q1), new Variable(a),
                                new Variable(q1), 
                                new ActionQuery(new Variable("A"))
                        );
                        
                        return ea.getTimes();
                    }
                };
                
                actions.add(adapt);
                
                submachines.add(M);
                
                transitions.add(t1);
                transitions.add(t2);
                transitions.add(t3);
                transitions.add(t4);
                
                setMainSubmachine("M");
                
            }
            
        };
        
        // realiza a medição para cadeias de
        // comprimento 3 a 150
        
        int times = 50;
        
        aa.recognize(ExampleUtils.convert(""));
        File file = new File("cost2.dat");
        Report r = new Report(file);
        
        for (int i = 1; i <= times; i++) {
            aa.recognize(ExampleUtils.convert(ExampleUtils.generate(i)));
            r.add("%d\t%24f", i, Calculator.getScore(
                    aa.getRecognitionPaths().get(0).getTimes()
            ));
        }
        r.write();
        
    }
    
    /**
     * Gera um novo estado.
     * @return Um novo estado.
     */
    private static State generateState() {
        ExampleState result =
                new ExampleState("q".concat(String.valueOf(COUNTER)));
        COUNTER++;
        return result;
    }
    
}
