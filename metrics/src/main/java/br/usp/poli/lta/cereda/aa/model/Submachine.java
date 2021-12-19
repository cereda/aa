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
package br.usp.poli.lta.cereda.aa.model;

import br.usp.poli.lta.cereda.aa.utils.IdentifierUtils;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Classe que representa a definição formal de uma submáquina. A intenção é
 * tentar representar a teoria da forma mais precisa possível.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Submachine {
    
    // atributos da classe, incluindo um identificador unívoco que representa
    // a submáquina corrente, um nome no qual a submáquina é referenciada
    // posteriormente, um estado inicial ou de entrada, um conjunto de todos os
    // estados da submáquina, e um conjunto de estados finais ou de aceitação.
    private int identifier;
    private String name;
    private State initialState;
    private Set<State> acceptingStates;
    private Set<State> states;
    
    /**
     * Construtor. Define a estrutura da submáquina como um todo.
     * @param name Nome da submáquina.
     * @param states Conjunto de todos os estados da submáquina.
     * @param initialState Estado inicial ou de entrada da submáquina.
     * @param acceptingStates Conjunto de estados finais ou de aceitação.
     */
    public Submachine(String name, Set<State> states,
            State initialState, Set<State> acceptingStates) {
        
        this.identifier = IdentifierUtils.getSubmachineIdentifier();
        this.name = name;
        this.initialState = initialState;
        this.acceptingStates = acceptingStates;
        this.states = states;

        // valida se todos os estados definidos estão de acordo com a teoria
        Validate.isTrue(
                states.contains(initialState) &&
                states.containsAll(acceptingStates),
                "Existem estados indefinidos na submáquina '"
                        .concat(String.valueOf(name).concat("'."))
        );
    }
    
    /**
     * Obtém o identificador unívoco da submáquina.
     * @return Um valor inteiro que representa o identificador da submáquina.
     */
    public int getIdentifier() {
        return identifier;
    }
    
    /**
     * Define o identificador unívoco da submáquina.
     * @param identifier Um valor inteiro que representa o identificador da
     * submáquina.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Obtém o nome da submáquina.
     * @return Nome da submáquina.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Define o nome da submáquina.
     * @param name Nome da submáquina.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Obtém o estado inicial ou de entrada da submáquina.
     * @return Estado inicial ou de entrada da submáquina.
     */
    public State getInitialState() {
        return initialState;
    }
    
    /**
     * Define o estado inicial ou de entrada da submáquina.
     * @param initialState Estado inicial ou de entrada da submáquina.
     */
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }
    
    /**
     * Obtém todos os estados de aceitação ou de saída da submáquina.
     * @return Um conjunto contendo os estados de aceitação ou de saída da
     * submáquina.
     */
    public Set<State> getAcceptingStates() {
        return acceptingStates;
    }
    
    /**
     * Define o conjunto de estados finais ou de aceitação da submáquina.
     * @param acceptingStates Conjunto de estados finais da submáquina.
     */
    public void setAcceptingStates(Set<State> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }
    
    /**
     * Obtém todos os estados da submáquina.
     * @return Conjunto de todos os estados da submáquina.
     */
    public Set<State> getStates() {
        return states;
    }
    
    /**
     * Define o conjunto de todos os estados da submáquina.
     * @param states Conjunto de todos os estados da submáquina.
     */
    public void setStates(Set<State> states) {
        this.states = states;
    }
    
    /**
     * Retorna uma representação textual do conjunto de estados.
     * @param set Conjunto de estados.
     * @param separator Separador dos elementos.
     * @return Representação textual do conjunto de estados.
     */
    private String printElements(Set<State> set, String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(StringUtils.join(set, separator));
        sb.append(")");
        return sb.toString();
    }

    /**
     * Retorna uma representação textual da submáquina corrente.
     * @return Representação textual da submáquina corrente.
     */
    @Override
    public String toString() {
        String separator = ", ";
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Submáquina: { ");
        sb.append("Identificador: ").append(identifier).append(div);
        sb.append("Nome: ").append(name).append(div);
        sb.append("Estados: ").append(printElements(states, separator)).
                append(div);
        sb.append("Estado inicial: ").append(initialState).append(div);
        sb.append("Estados de aceitação: ").
                append(printElements(acceptingStates, separator));
        sb.append(" }");
        return sb.toString();
    }
    
}