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

import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import org.apache.commons.collections4.Predicate;

/**
 * Filtra transições que consomem o símbolo informado.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SymbolPredicate implements Predicate<Transition> {

    // atributo da classe
    private final Symbol symbol;

    /**
     * Construtor.
     * @param symbol Símbolo a ser consumido.
     */
    public SymbolPredicate(Symbol symbol) {
        this.symbol = symbol;
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
        if (t.isSymbolConsumptionTransition()) {
            return t.getSymbol().equals(symbol);
        }
        else {
            return false;
        }
    }
   
}
