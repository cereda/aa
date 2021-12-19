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

import br.usp.poli.lta.cereda.aa.model.Transition;
import org.apache.logging.log4j.message.Message;

/**
 * Define uma mensagem com a expansão de transição para ser auditada.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class TransitionMessage implements Message {

    // atributos da classe, contendo a transição, uma mensagem e seus
    // possíveis parâmetros
    private final Transition transition;
    private final String message;
    private final Object[] parameters;

    /**
     * Construtor. Toma uma transição e a mensagem.
     * @param t Transição.
     * @param m Mensagem a ser auditada.
     */
    public TransitionMessage(Transition t, String m) {
        this.transition = t;
        this.message = m;
        this.parameters = null;
    }

    /**
     * Construtor. Toma apenas a transição a ser auditada.
     * @param t Transição a ser auditada.
     */
    public TransitionMessage(Transition t) {
        this.transition = t;
        this.message = null;
        this.parameters = null;
    }

    /**
     * Construtor. Toma a transição, uma mensagem e seus parâmetros.
     * @param t Transição.
     * @param m Mensagem.
     * @param pr Parâmetros da mensagem. 
     */
    public TransitionMessage(Transition t, String m, Object... pr) {
        this.transition = t;
        this.message = m;
        this.parameters = pr;
    }

    /**
     * Obtém a mensagem formatada. Se esta possui parâmetros, eles serão
     * devidamente expandidos.
     * @return A mensagem formatada para ser auditada, com referência à
     * transição.
     */
    @Override
    public String getFormattedMessage() {
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        if (message != null) {
            sb.append("Mensagem: ");
            if (parameters == null) {
                sb.append(message);
            } else {
                sb.append(String.format(message, parameters));
            }
            sb.append(div);
        }
        sb.append(transition);
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
