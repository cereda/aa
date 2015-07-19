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
package br.usp.poli.lta.cereda.aa.model.predicates;

import br.usp.poli.lta.cereda.aa.model.Transition;
import org.apache.commons.collections4.Predicate;

/**
 * Filtra transições que fazem chamada à submáquina informada.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SubmachinePredicate implements Predicate<Transition> {

    // atributo de classe
    private final String submachine;

    /**
     * Construtor.
     * @param submachine Nome da submáquina.
     */
    public SubmachinePredicate(String submachine) {
        this.submachine = submachine;
    }
    
    /**
     * Verifica se a transição corrente atende as condições do predicado
     * informado.
     * @param t Transição a ser verifica.
     * @return Um valor lógico denotando se a transição corrente atende as
     * condições.
     */
    @Override
    public boolean evaluate(Transition t) {
        if (t.isSubmachineCall()) {
            return t.getSubmachineCall().equals(submachine);
        }
        else {
            return false;
        }
    }
    
}
