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
package br.usp.poli.lta.cereda.aa.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.Validate;

/**
 * Classe utilitária que fornece algumas operações entre conjuntos.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SetOperations {

    /**
     * Obtém, de forma recursiva, o produto cartesiano de um vetor de conjuntos
     * de acordo com o índice fornecido, em ordem reversa.
     * @param index Índice fornecido.
     * @param sets Vetor de conjuntos.
     * @return Produto cartesiano do vetor de conjuntos de acordo com o índice
     * fornecido, em ordem reversa.
     */
    private static Set<List<Object>> fetchElements(int index, Set<?>... sets) {
        
        Set<List<Object>> result = new HashSet<>();
        if (index == sets.length) {
            result.add(new ArrayList<>());
        } else {
            for (Object obj : sets[index]) {
                for (List<Object> set : fetchElements(index + 1, sets)) {
                    set.add(obj);
                    result.add(set);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Calcula o produto cartesiano de um vetor de conjuntos.
     * @param sets Vetor de conjuntos.
     * @return Produto cartesiano do vetor de conjuntos.
     */
    public static Set<List<Object>> cartesianProduct(Set<?>... sets) {
        
        // deve haver mais do que um conjunto
        Validate.isTrue(
                sets.length > 1,
                "Não é possível calcular o produto cartesiano "
              + "de apenas um conjunto."
        );
        
        // nenhum conjunto pode ser vazio
        for (Set<?> set : sets) {
            Validate.isTrue(
                    !set.isEmpty(),
                    "Não é possível calcular o produto cartesiano "
                  + "quando um dos conjuntos é vazio."
            );
        }
        
        // calcula o produto cartesiano
        Set<List<Object>> elements = fetchElements(0, sets);
        
        // o retorno da função gera listas com elementos em ordem
        // reversa, portanto, é necessário inverter os elementos das
        // listas obtidas
        Set<List<Object>> result = new HashSet<>();
        for (List<Object> list : elements) {
            Collections.reverse(list);
            result.add(list);
        }
        
        return result;    
    }

}
