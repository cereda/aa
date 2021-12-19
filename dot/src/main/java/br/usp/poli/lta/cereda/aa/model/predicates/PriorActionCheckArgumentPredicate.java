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
 * Filtra transições que tenham um determinado argumento em uma posição
 * específica da ação anterior.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class PriorActionCheckArgumentPredicate implements
        Predicate<Transition> {
    
    // atributos da classe, contendo a posição do argumento e o valor
    private final int index;
    private final Object value;

    /**
     * Construtor.
     * @param index Índice do argumento.
     * @param value Valor do argumento.
     */
    public PriorActionCheckArgumentPredicate(int index, Object value) {
        this.index = index;
        this.value = value;
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
        if (t.hasPriorActionCall()) {
            if (t.hasPriorActionArguments()) {
                if (index < t.countPriorActionArguments()) {
                    return t.getPriorActionArguments()[index].equals(value);
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
}
