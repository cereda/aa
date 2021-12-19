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
package br.usp.poli.lta.cereda.aa.model.sets;

import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.predicates.EpsilonPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SourceStatePredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SubmachineCallPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SubmachinePredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SymbolPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.TargetStatePredicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;

/**
 * Classe que define o conjunto de transições do autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 */
public class Mapping {

    // atributo da classe, que é um conjunto contendo todas as transições do
    // autômato adaptativo.
    private Set<Transition> transitions;

    /**
     * Construtor. Cria o novo conjunto de transições.
     */
    public Mapping() {
        transitions = new HashSet<>();
    }

    /**
     * Obtém o conjunto de transições.
     * @return Conjunto de transições.
     */
    public Set<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Define o conjunto de transições.
     * @param transitions Conjunto de transições.
     */
    public void setTransitions(Set<Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * Retorna um valor lógico informando se a transição com o identificador
     * informado existe no conjunto de transições.
     * @param identifier Valor inteiro denotando o identificador unívoco de uma
     * transição.
     * @return Valor lógico informando se a transição com identificador
     * informado existe no conjunto de transições.
     */
    public boolean hasIdentifier(int identifier) {
        for (Transition transition : transitions) {
            if (transition.getIdentifier() == identifier) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtém a cardinalidade do conjunto de transições.
     * @return Valor lógico denotando a cardinalidade do conjunto de transições.
     */
    public int size() {
        return transitions.size();
    }

    /**
     * Consulta quais transições possuem o estado de origem informado.
     * @param state Estado de origem.
     * @return Transições que possuem o estado de origem informado.
     */
    public List<Transition> withSourceState(State state) {
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                new SourceStatePredicate(state)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que consomem o símbolo informado.
     * @param symbol Símbolo a ser consumido.
     * @return Transições que consomem o símbolo informado.
     */
    public List<Transition> withSymbol(Symbol symbol) {
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                new SymbolPredicate(symbol)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que possuem o estado de destino informado.
     * @param state Estado de destino.
     * @return Transições que possuem o estado de destino informado.
     */
    public List<Transition> withTargetState(State state) {
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                new TargetStatePredicate(state)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que fazem chamada à submáquina informada.
     * @param submachine Nome da submáquina.
     * @return Transições que fazem chamada à submáquina informada.
     */
    public List<Transition> withSubmachine(String submachine) {
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                new SubmachinePredicate(submachine)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que consomem o símbolo informado a partir do estado
     * de origem também informado.
     * @param state Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @return Transições que consomem o símbolo informado a partir do estado
     * de origem também informado.
     */
    public List<Transition> withSourceStateAndSymbol(State state,
            Symbol symbol) {
        Predicate[] chain = {
            new SourceStatePredicate(state),
            new SymbolPredicate(symbol)
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que partem do estado de origem informado e seguem até
     * o estado de destino também informado.
     * @param state1 Estado de origem.
     * @param state2 Estado de destino.
     * @return Transições que partem do estado de origem informado e seguem até
     * o estado de destino também informado.
     */
    public List<Transition> withSourceAndTargetStates(State state1,
            State state2) {
        Predicate[] chain = {
            new SourceStatePredicate(state1),
            new TargetStatePredicate(state2)
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta quais transições partem de um estado de origem, consomem o
     * símbolo e chegam até um estado de destino.
     * @param state1 Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param state2 Estado de destino.
     * @return Transições partem de um estado de origem, consomem o símbolo e
     * chegam até um estado de destino.
     */
    public List<Transition> withSourceStateSymbolAndTargetState(State state1,
            Symbol symbol, State state2) {
        Predicate[] chain = {
            new SourceStatePredicate(state1),
            new SymbolPredicate(symbol),
            new TargetStatePredicate(state2)
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições que partem de um estado de origem e chamam uma
     * determinada submáquina.
     * @param state Estado de origem.
     * @param submachine Nome da submáquina.
     * @return Transições que partem de um estado de origem e chamam uma
     * determinada submáquina.
     */
    public List<Transition> withSourceStateAndSumbmachine(State state,
            String submachine) {
        Predicate[] chain = {
            new SourceStatePredicate(state),
            new SubmachinePredicate(submachine)
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições em vazio a partir do estado de origem.
     * @param state Estado de origem.
     * @return Transições em vazio a partir do estado de origem.
     */
    public List<Transition> withEpsilonFromSourceState(State state) {
        Predicate[] chain = {
            new SourceStatePredicate(state),
            new EpsilonPredicate()
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições em vazio.
     * @return Transições em vazio.
     */
    public List<Transition> withEpsilonTransitions() {
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                new EpsilonPredicate()
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições em vazio ou com chamadas de submáquina a partir do
     * estado de origem informado.
     * @param state Estado de origem.
     * @return Transições em vazio ou com chamadas de submáquina a partir do
     * estado de origem informado.
     */
    public List<Transition> withEpsilonOrSubmachineFromSourceState(
            State state) {
        Predicate[] chain = {
            new EpsilonPredicate(),
            new SubmachineCallPredicate()
        };
        Collection<Transition> result = CollectionUtils.select(
                CollectionUtils.select(
                        transitions, new SourceStatePredicate(state)
                ),
                PredicateUtils.anyPredicate(chain)
        );
        return new ArrayList<>(result);
    }

    /**
     * Consulta transições com o consumo de um símbolo específico, em vazio ou
     * com chamadas de submáquina a partir do estado de origem informado.
     * @param state Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @return Transições com o consumo de um símbolo específico, em vazio ou
     * com chamadas de submáquina a partir do estado de origem informado.
     */
    public List<Transition> withSymbolEpsilonOrSubmachineFromSourceState(
            State state, Symbol symbol) {
        Predicate[] chain = {
            new EpsilonPredicate(),
            new SubmachineCallPredicate(),
            new SymbolPredicate(symbol)
        };
        Collection<Transition> result = CollectionUtils.select(
                CollectionUtils.select(
                        transitions,
                        new SourceStatePredicate(state)),
                PredicateUtils.anyPredicate(chain)
        );
        return new ArrayList<>(result);
    }
    
    /**
     * Adiciona a transição no conjunto de transições.
     * @param t Transição a ser adicionada.
     */
    public void add(Transition t) {
        transitions.add(t);
    }
    
    /**
     * Consulta transições que consomem um determinado símbolo e chegam até o
     * estado de destino especificado.
     * @param symbol Símbolo a ser consumido.
     * @param state Estado de destino.
     * @return Transições que consomem um determinado símbolo e chegam até o
     * estado de destino especificado.
     */
    public List<Transition> withSymbolAndTargetState(Symbol symbol,
            State state) {
        Predicate[] chain = {
            new SymbolPredicate(symbol),
            new TargetStatePredicate(state)
        };
        Collection<Transition> result = CollectionUtils.select(
                transitions,
                PredicateUtils.allPredicate(chain)
        );
        return new ArrayList<>(result);
    }
    
    /**
     * Remove a transição contendo o identificador informado do conjunto de
     * transições.
     * @param identifier Valor inteiro contendo o identificador da transição a
     * ser removida.
     */
    public void removeFromIdentifier(int identifier) {
        Transition result = null;
        for (Transition transition : transitions) {
            if (transition.getIdentifier() == identifier) {
                result = transition;
                break;
            }
        }
        if (result != null) {
            transitions.remove(result);
        }
    }
    
    /**
     * Retorna uma representação textual do conjunto de transições.
     * @return Representação textual do conjunto de transições.
     */
    @Override
    public String toString() {
        String newline = "\n";
        String tab = "  ";
        String bullet = ":: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Conjunto de transições: {").append(newline);
        if (transitions.isEmpty()) {
            sb.append(tab).append("O conjunto está vazio.").append(newline);
        }
        else {
            for (Transition t : transitions) {
                sb.append(tab).append(bullet).append(t).append(newline);
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
