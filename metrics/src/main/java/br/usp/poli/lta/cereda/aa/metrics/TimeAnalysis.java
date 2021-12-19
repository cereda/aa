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
package br.usp.poli.lta.cereda.aa.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Análise de tempo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class TimeAnalysis {

    // variáveis da classe
    private Type type;
    private Map<String, Double> attributes;

    /**
     * Construtor.
     */
    public TimeAnalysis() {
        attributes = new HashMap<>();
    }

    /**
     * Obtém o tipo da análise.
     * @return Tipo da análise.
     */
    public Type getType() {
        return type;
    }

    /**
     * Define o tipo da análise.
     * @param type Tipo da análise.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Obtém os atributos.
     * @return Atributos.
     */
    public Map<String, Double> getAttributes() {
        return attributes;
    }

    /**
     * Define os atributos.
     * @param attributes Atributos.
     */
    public void setAttributes(Map<String, Double> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adiciona um atributo.
     * @param key Chave.
     * @param value Valor.
     */
    public void addAttribute(String key, double value) {
        this.attributes.put(key, value);
    }

    /**
     * Representação textual do objeto.
     * @return Objeto em formato textual.
     */
    @Override
    public String toString() {
        return String.format("Entrada: [ tipo: %s, atributos: %s ]",
                type, attributes.toString());
    }

    /**
     * Código hash do objeto.
     * @return Valor inteiro representando o código hash do objeto.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Objects.hashCode(this.attributes);
        return hash;
    }

    /**
     * Igualdade do objeto.
     * @param object Objeto.
     * @return Valor lógico.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final TimeAnalysis other = (TimeAnalysis) object;
        if (this.type != other.type) {
            return false;
        }
        return Objects.equals(this.attributes, other.attributes);
    }

}
