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

/**
 * Define uma consulta de submáquina.
 * @author Paulo Roberto Massa Cereda
 */
public class SubmachineQuery {
    
    // variável interna
    private Variable variable;

    /**
     * Construtor.
     * @param variable Variável da consulta de submáquina.
     */
    public SubmachineQuery(Variable variable) {
        this.variable = variable;
    }

    /**
     * Obtém a variável.
     * @return Variável.
     */
    public Variable getVariable() {
        return variable;
    }

    /**
     * Define a variável.
     * @param variable Variável.
     */
    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    /**
     * Retorna uma representação textual da consulta de submáquina.
     * @return Representação textual da consulta de submáquina.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Consulta de submáquina: { ");
        sb.append(variable).append(" }");
        return sb.toString();
    }
  
}
