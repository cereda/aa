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

/**
 * Classe que apresenta uma implementação simplificada de um token léxico.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class ExampleToken {
        
    // identificação e valor do token
    private String id;
    private String value;

    /**
     * Obtém a identificação do token.
     * @return Identificação do token.
     */
    public String getId() {
        return id;
    }

    /**
     * Define a identificação do token.
     * @param id Identificação do token.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtém o valor do token.
     * @return Valor do token.
     */
    public String getValue() {
        return value;
    }

    /**
     * Define o valor do token.
     * @param value Valor do token.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Define uma representação textual para o token.
     * @return Representação textual do token.
     */
    @Override
    public String toString() {
        return "(".concat(id).concat(", ").concat(value).concat(")");
    }

}