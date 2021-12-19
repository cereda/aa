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
package br.usp.poli.lta.cereda.aa.model.sets;

import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Submachine;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.Validate;

/**
 * Esta classe define o conjunto de submáquinas do autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SubmachinesSet {

    // atributo da classe, representando o conjunto de todas as submáquinas do
    // autômato adaptativo
    private Set<Submachine> submachines;

    /**
     * Construtor. Cria um novo conjunto de submáquinas.
     */
    public SubmachinesSet() {
        submachines = new HashSet<>();
    }

    /**
     * Adiciona uma nova submáquina ao conjunto de submáquinas.
     * @param submachine Nova submáquina a ser adicionada.
     */
    public void add(Submachine submachine) {
        submachines.add(submachine);
    }

    /**
     * Obtém o conjunto de todas as submáquinas.
     * @return Conjunto de todas as submáquinas.
     */
    public Set<Submachine> getSubmachines() {
        return submachines;
    }

    /**
     * Define o conjunto de todas as submáquinas.
     * @param submachines Conjunto de todas as submáquinas.
     */
    public void setSubmachines(Set<Submachine> submachines) {
        this.submachines = submachines;
    }

    /**
     * Retorna a cardinalidade do conjunto de todas as submáquinas.
     * @return Valor inteiro representando a cardinalidade do conjunto de todas
     * as submáquinas do autômato adaptativo.
     */
    public int size() {
        return submachines.size();
    }

    /**
     * Obtém a submáquina que contém o estado informado. De acordo com a teoria,
     * os conjuntos das submáquinas são disjuntos entre si.
     * @param state Estado a ser pesquisado.
     * @return Submáquina que contém o estado pesquisado.
     */
    public Submachine getFromState(State state) {
        for (Submachine submachine : submachines) {
            if (submachine.getStates().contains(state)) {
                return submachine;
            }
        }
        Validate.isTrue(false, "Não existe submáquina com o estado '"
                .concat(String.valueOf(state)).concat("' em seu conjunto.")
        );
        return null;
    }

    /**
     * Obtém a submáquina de acordo com o nome informado.
     * @param name Nome da submáquina.
     * @return Submáquina que possui o nome pesquisado.
     */
    public Submachine getFromName(String name) {
        for (Submachine submachine : submachines) {
            if (submachine.getName().equals(name)) {
                return submachine;
            }
        }
        Validate.isTrue(false, "Não existe submáquina com o nome '"
                .concat(String.valueOf(name)).concat("'.")
        );
        return null;
    }

    /**
     * Remove a submáquina do conjunto de submáquinas, de acordo com o nome
     * informado.
     * @param name Nome da submáquina a ser removida do conjunto de submáquinas.
     */
    public void removeByName(String name) {
        Submachine result = null;
        for (Submachine submachine : submachines) {
            if (submachine.getName().equals(name)) {
                result = submachine;
                break;
            }
        }
        Validate.notNull(result, "Não existe submáquina com o nome '"
                .concat(String.valueOf(name)).concat("'.")
        );
        submachines.remove(result);
    }

    /**
     * Retorna uma representação textual do conjunto de submáquinas.
     * @return Representação textual do conjunto de submáquinas.
     */
    @Override
    public String toString() {
        String newline = "\n";
        String tab = "  ";
        String bullet = ":: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Conjunto de submáquinas: {").append(newline);
        if (submachines.isEmpty()) {
            sb.append(tab).append("O conjunto está vazio.").append(newline);
        }
        else {
            for (Submachine s : submachines) {
                sb.append(tab).append(bullet).append(s).append(newline);
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
}
