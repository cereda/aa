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

import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.utils.IdentifierUtils;

/**
 * Classe que representa uma ação no autômato adaptativo. Uma ação pode conter
 * ações adaptativas elementares e ações de caráter semântico, de acordo com a
 * implementação do método abstrato da classe.
 * @author Paulo Roberto Massa Cereda
 */
public abstract class Action {

    // atributos de classe, contendo o identificador unívoco da ação e o nome
    // no qual esta ação será referenciada posteriormente, durante o processo
    // de reconhecimento de uma cadeia pelo autômato adaptativo
    private int identifier;
    private String name;

    /**
     * Construtor.
     * @param name Nome da ação.
     */
    public Action(String name) {
        this.identifier = IdentifierUtils.getActionIdentifier();
        this.name = name;
    }

    /**
     * Obtém o identificador unívoco da ação.
     * @return Valor inteiro que representa o identificador unívoco da ação.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Define o identificador unívoco da ação.
     * @param identifier Valor inteiro que representa o identificador unívoco da
     * ação.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Obtém o nome da ação.
     * @return Nome da ação.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome da ação.
     * @param name Nome da ação.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Executa a ação. É possível utilizar ações adaptativas elementares e de
     * caráter semântico no corpo do método. Este método está definido como
     * abstrato.
     * @param transitions Conjunto de transições do autômato adaptativo.
     * @param transition Transição corrente, servindo como referência.
     * @param parameters Parâmetros da ação.
     */
    public abstract void execute(
            Mapping transitions,
            Transition transition,
            Object... parameters
    );

    /**
     * Fornece uma representação textual da ação.
     * @return Representação textual da ação.
     */
    @Override
    public String toString() {
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Ação: { ");
        sb.append("Identificador: ").append(identifier).append(div);
        sb.append("Nome: ").append(name);
        sb.append(" }");
        return sb.toString();
    }
    
}

