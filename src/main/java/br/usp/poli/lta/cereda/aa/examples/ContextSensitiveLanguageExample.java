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
import br.usp.poli.lta.cereda.aa.model.actions.ActionQuery;
import br.usp.poli.lta.cereda.aa.model.actions.ElementaryActions;
import br.usp.poli.lta.cereda.aa.model.actions.Variable;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.utils.RecognitionPath;
import java.util.HashSet;
import java.util.Set;

/**
 * Exemplo de reconhecimento de uma linguagem dependente de contexto. A
 * linguagem dependente de contexto, neste caso, é 'a^n b^n c^n', com 'n' maior
 * que zero.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class ContextSensitiveLanguageExample {
    
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
                t4.setPostActionArguments(Variable.values(q2, q3));
                
                Action adapt = new Action("A") {
                    
                    @Override
                    public void execute(Mapping transitions,
                            Transition transition, Object... parameters) {
                        
                        Symbol a = new ExampleSymbol("a");
                        Symbol b = new ExampleSymbol("b");
                        Symbol c = new ExampleSymbol("c");
                        
                        State q1 = new ExampleState("q1");
                        
                        ElementaryActions ea = new ElementaryActions(transitions);
                        
                        Variable p1 = new Variable(parameters[0]);
                        Variable p2 = new Variable(parameters[1]);
                        
                        Variable g1 = new Variable(
                                ContextSensitiveLanguageExample.generateState()
                        );
                        Variable g2 = new Variable(
                                ContextSensitiveLanguageExample.generateState()
                        );

                        Variable x = new Variable();
                        Variable y = new Variable();
                        
                        ea.query(x, new Variable(b), p1);
                        ea.remove(x, new Variable(b), p1);
                        
                        ea.query(y, new Variable(c), p2);
                        ea.remove(y, new Variable(c), p2);
                       
                        ea.remove(new Variable(q1), new Variable(a),
                                new Variable(q1),
                                new ActionQuery(new Variable("A"), p1, p2)
                        );
                       
                        ea.add(x, new Variable(b), g1);
                        ea.add(g1, new Variable(b), p1);
                        ea.add(y, new Variable(c), g2);
                        ea.add(g2, new Variable(c), p2);
  
                        ea.add(new Variable(q1), new Variable(a),
                                new Variable(q1), 
                                new ActionQuery(new Variable("A"), g1, g2)
                        );
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
        
        boolean resultado = aa.recognize(ExampleUtils.convert("aaabbbccc"));
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
