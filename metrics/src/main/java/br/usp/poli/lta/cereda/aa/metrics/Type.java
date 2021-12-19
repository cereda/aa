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
package br.usp.poli.lta.cereda.aa.metrics;

/**
 * Enumeração do tipo de análise de tempo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public enum Type {
    EMPTY_TRANSITION,
    SYMBOL_CONSUMPTION,
    SUBMACHINE_CALL,
    SUBMACHINE_RETURN,
    EMPTY_TRANSITION_WITH_POST_ACTION,
    SYMBOL_CONSUMPTION_WITH_POST_ACTION,
    SUBMACHINE_CALL_WITH_POST_ACTION,
    SUBMACHINE_RETURN_WITH_POST_ACTION,
    EMPTY_TRANSITION_WITH_PRE_ACTION,
    SYMBOL_CONSUMPTION_WITH_PRE_ACTION,
    SUBMACHINE_CALL_WITH_PRE_ACTION,
    SUBMACHINE_RETURN_WITH_PRE_ACTION,
    EMPTY_TRANSITION_WITH_PRE_AND_POST_ACTIONS,
    SYMBOL_CONSUMPTION_WITH_PRE_AND_POST_ACTIONS,
    SUBMACHINE_CALL_WITH_PRE_AND_POST_ACTIONS,
    SUBMACHINE_RETURN_WITH_PRE_AND_POST_ACTIONS,
    QUERY_ACTION,
    REMOVE_ACTION,
    ADD_ACTION,
}
