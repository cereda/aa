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

import br.usp.poli.lta.cereda.aa.model.Action;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.Validate;

/**
 * Classe que representa o conjunto de ações do autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 */
public class ActionsSet {

    // atributo da classe, que é um conjunto contendo todas as ações definidas
    // no modelo
    private Set<Action> actions;

    /**
     * Construtor. Cria um novo conjunto.
     */
    public ActionsSet() {
        actions = new HashSet<>();
    }

    /**
     * Adiciona uma nova ação no conjunto de ações.
     * @param action Ação a ser adicionada.
     */
    public void add(Action action) {
        actions.add(action);
    }

    /**
     * Obtém o conjunto de ações.
     * @return Conjunto de ações.
     */
    public Set<Action> getActions() {
        return actions;
    }

    /**
     * Define o conjunto de ações.
     * @param actions Conjunto de ações.
     */
    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }

    /**
     * Retorna a cardinalidade do conjunto de ações.
     * @return Valor inteiro representando a cardinalidade do conjunto de ações.
     */
    public int size() {
        return actions.size();
    }

    /**
     * Retorna uma ação de acordo com o nome informado.
     * @param name Nome da ação a ser pesquisada.
     * @return A ação referente ao nome informado.
     */
    public Action fromName(String name) {
        for (Action action : actions) {
            if (action.getName().equals(name)) {
                return action;
            }
        }
        Validate.isTrue(false, "Não existe ação com nome '"
                .concat(String.valueOf(name)).concat("'.")
        );
        return null;
    }

    /**
     * Remove a ação do conjunto de ações de acordo com o nome informado.
     * @param name Nome da ação a ser removida do conjunto.
     */
    public void removeByName(String name) {
        Action result = null;
        for (Action action : actions) {
            if (action.getName().equals(name)) {
                result = action;
                break;
            }
        }
        Validate.notNull(result, "Não existe ação com nome '"
                .concat(String.valueOf(name)).concat("'.")
        );
        actions.remove(result);
    }
    
    /**
     * Retorna uma representação textual do conjunto de ações.
     * @return Representação textual do conjunto de ações.
     */
    @Override
    public String toString() {
        String newline = "\n";
        String tab = "  ";
        String bullet = ":: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Conjunto de ações: {").append(newline);
        if (actions.isEmpty()) {
            sb.append(tab).append("O conjunto está vazio.").append(newline);
        }
        else {
            for (Action a : actions) {
                sb.append(tab).append(bullet).append(a).append(newline);
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
