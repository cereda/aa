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

import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.commons.lang3.StringUtils;

/**
 * Define a estrutura de dados de pilha para ser utilizada pelo autômato
 * adaptativo. A pilha, neste caso, permite apenas a manipulação de elementos
 * que estão no topo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Stack {

    // atributo da classe, que é a pilha
    private final Deque<State> stack;

    /**
     * Construtor. Inicializa a pilha.
     */
    public Stack() {
        stack = new ArrayDeque<>();
    }

    /**
     * Adiciona o estado informado no topo da pilha.
     * @param entry Estado a ser adicionado no topo da pilha.
     */
    public void push(State entry) {
        stack.addFirst(entry);
    }

    /**
     * Remove o estado do topo da pilha e o retorna.
     * @return Estado que estava no topo da pilha.
     */
    public State pop() {
        State result = stack.getFirst();
        stack.removeFirst();
        return result;
    }

    /**
     * Retorna o estado que está no topo da pilha, sem, entretanto, removê-lo.
     * @return Estado que está no topo da pilha.
     */
    public State top() {
        return stack.getFirst();
    }

    /**
     * Verifica se a pilha está vazia.
     * @return Valor lógico que informa se a pilha está vazia.
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Limpa a pilha, removendo todos os elementos existentes.
     */
    public void clear() {
        stack.clear();
    }
    
    /**
     * Retorna uma representação textual da pilha.
     * @param separator Separador dos elementos.
     * @return Representação textual da pilha.
     */
    private String printElements(String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(StringUtils.join(stack, separator));
        sb.append("]");
        return sb.toString();
    }

    /**
     * Retorna uma representação textual da pilha.
     * @return Representação textual da pilha.
     */
    @Override
    public String toString() {
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Pilha: { ");
        sb.append("A pilha está vazia? ").append(printAnswer(isEmpty()));
        if (!isEmpty()) {
            sb.append(div).append("Elementos (topo à esquerda): ");
            sb.append(printElements(div));
        }
        sb.append(" }");
        return sb.toString();
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
    
}
