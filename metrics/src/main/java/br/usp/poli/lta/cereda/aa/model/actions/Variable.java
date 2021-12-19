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

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.Validate;

/**
 * Representa uma variável de consulta nas ações elementares. Cada variável
 * possui um conjunto de valores.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Variable {
    
    // atributos da classe, contendo um conjunto de valores e um sinalizador
    // indicando se a variável está disponível para preenchimento
    private boolean available;
    private Set<Object> values;

    /**
     * Construtor vazio.
     */
    public Variable() {
        available = true;
        values = new HashSet<>();
    }

    /**
     * Construtor com parâmetro.
     * @param value Valor da variável.
     */
    public Variable(Object value) {
        available = false;
        values = new HashSet<>();
        values.add(value);
    }
    
    /**
     * Retorna a cardinalidade do conjunto.
     * @return Valor inteiro representando a cardinalidade do conjunto de
     * valores.
     */
    public int size() {
        return values.size();
    }
    
    /**
     * Obtém todos os valores do conjunto.
     * @return Conjunto de valores armazenados na variável.
     */
    public Set<Object> getValues() {
        Validate.isTrue(
                !available,
                "Não é possível obter o valor de uma variável não inicializada."
        );
        return values;
    }

    /**
     * Verifica se a variável está disponível para preencimento.
     * @return Valor lógico indicando se a variável está disponível para
     * preenchimento.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Define o conjunto de valores da variável.
     * @param values Conjunto de valores da variável.
     */
    public void setValues(Set<Object> values) {
        this.values = values;
        available = false;
    }

    /**
     * Retorna uma representação textual da variável.
     * @return Representação textual da variável.
     */
    @Override
    public String toString() {
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Variável: { ");
        sb.append("Disponível para preenchimento? ").
                append(printAnswer(available));
        if (!available) {
            sb.append(div).append("Elementos: ");
            if (values.isEmpty()) {
                sb.append("conjunto vazio");
            }
            else {
                sb.append(printElements(values, ", "));
            }
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
    
    /**
     * Retorna uma representação textual do conjunto de estados.
     * @param set Conjunto de estados.
     * @param separator Separador dos elementos.
     * @return Representação textual do conjunto de estados.
     */
    private String printElements(Set<Object> set, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object element : set) {
            if (element == null) {
                sb.append("ε");
            }
            else {
                sb.append(element);
            }
            sb.append(separator.concat(" "));
        }
        return "(".concat(sb.toString().substring(0, sb.length() - 2)).
                concat(")");
    }
    
    /**
     * Retorna um vetor de objetos. Este método estático é utilizado somente
     * para facilitar a especificação de um vetor de objetos.
     * @param objects Lista de objetos.
     * @return Vetor de objetos.
     */
    public static Object[] values(Object... objects) {
        return objects;
    }
    
}
