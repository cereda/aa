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
package br.usp.poli.lta.cereda.aa.model.actions;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Define uma consulta de ação.
 * @author paulo
 */
public class ActionQuery {
    
    // atributos da classe, contendo o nome da ação a ser consultada e seus
    // argumentos
    private final Variable name;
    private final List<Variable> arguments;

    /**
     * Construtor.
     * @param name Variável que denota o nome da ação. 
     */
    public ActionQuery(Variable name) {
        this.name = name;
        this.arguments = new ArrayList<>();
    }

    /**
     * Construtor.
     * @param name Variável que denota o nome da ação.
     * @param parameters Lista de variáveis representando os parâmetros da ação
     * consultada.
     */
    public ActionQuery(Variable name, Variable... parameters) {
        this.name = name;
        this.arguments = new ArrayList<>();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                this.arguments.add(parameters[i]);
            }
        }
    }

    /**
     * Obtém a variável que denota o nome da ação.
     * @return Variável que denota o nome da ação.
     */
    public Variable getName() {
        return name;
    }

    /**
     * Obtém uma lista de variáveis que denota a lista de argumentos da ação.
     * @return Lista de variáveis que denota a lista de argumentos da ação.
     */
    public List<Variable> getArguments() {
        return arguments;
    }
    
    /**
     * Verifica se a consulta da ação tem argumentos definidos.
     * @return Valor lógico denotando se a consulta da ação tem argumentos
     * definidos.
     */
    public boolean hasArguments() {
        return (!arguments.isEmpty());
    }
    
    /**
     * Retorna uma representação textual do valor lógico informado.
     * @param value Valor lógico.
     * @return Representação textual do valor lógico.
     */
    private String printAnswer(boolean value) {
        String yes = "sim";
        String no = "não";
        return (value ? yes : no);
    }

    /**
     * Fornece uma representação textual da consulta de ação.
     * @return Representação textual da consulta de ação.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String div = " :: ";
        String separator = ", ";
        sb.append("Consulta de ação: { ");
        sb.append("Nome da ação: ").append(name);
        sb.append("Tem argumentos? ").append(printAnswer(hasArguments()));
        if (hasArguments()) {
            sb.append(div).append("Argumentos: [ ");
            sb.append(StringUtils.join(arguments, separator)).append(" ]");
        }
        sb.append(" }");
        return sb.toString();
    }

}
