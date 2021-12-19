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
package br.usp.poli.lta.cereda.aa.examples;

import br.usp.poli.lta.cereda.aa.model.State;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Exemplo de implementação da classe abstrata que representa um estado do
 * conjunto de estados do autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 */
public class ExampleState extends State {

    // nome do estado
    private String value;

    /**
     * Construtor.
     * @param value Nome do estado.
     */
    public ExampleState(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        else {
            if (!(object.getClass().equals(ExampleState.class))) {
                return false;
            }
            else {
                return new EqualsBuilder().append(this.getValue(),
                        ((ExampleState) object).getValue()).isEquals();
            }
        }
    }

    /**
     * Obtém o nome do estado.
     * @return Nome do estado.
     */
    public String getValue() {
        return value;
    }

    /**
     * Define o nome do estado.
     * @param value Nome do estado.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.getValue()).hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
    
}
