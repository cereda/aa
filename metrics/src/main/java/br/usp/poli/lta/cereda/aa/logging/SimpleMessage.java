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
package br.usp.poli.lta.cereda.aa.logging;

import org.apache.logging.log4j.message.Message;

/**
 * Define uma mensagem de auditoria simples.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SimpleMessage implements Message {

    // atributos da classe, uma mensagem e
    // seus possíveis parâmetros
    private final String message;
    private final Object[] parameters;

    /**
     * Construtor. Toma apenas uma mensagem.
     * @param m Mensagem a ser auditada.
     */
    public SimpleMessage(String m) {
        this.message = m;
        this.parameters = null;
    }

    /**
     * Construtor. Toma uma mensagem e seus parâmetros.
     * @param m Mensagem a ser auditada.
     * @param pr Parâmetros da mensagem.
     */
    public SimpleMessage(String m, Object... pr) {
        this.message = m;
        this.parameters = pr;
    }

    /**
     * Obtém a mensagem formatada. Se esta possui parâmetros, eles serão
     * devidamente expandidos.
     * @return A mensagem formatada para ser auditada.
     */
    @Override
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mensagem: ");
        if (parameters != null) {
            sb.append(String.format(message, parameters));
        }
        else {
            sb.append(message);
        }
        return sb.toString();
    }

    /**
     * Obtém o formato.
     * @return String vazia.
     */
    @Override
    public String getFormat() {
        return "";
    }

    /**
     * Obtém os parâmetros (não são os atributos da classe).
     * @return Uma referência nula.
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    /**
     * Obtém o objeto de lançamento de exceção.
     * @return Uma referência nula.
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }
    
}
