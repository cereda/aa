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

import br.usp.poli.lta.cereda.aa.model.Symbol;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe utilitária para a definição dos exemplos.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class ExampleUtils {

    /**
     * Converte uma string em uma lista de símbolos.
     * @param text String de entrada.
     * @return Uma lista de símbolos.
     */
    public static List<Symbol> convert(final String text) {
        ArrayList<Symbol> result = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            result.add(new ExampleSymbol(text.substring(i, i + 1)));
        }
        return result;
    }
    
    /**
     * Retorna uma resposta de acordo com o valor lógico informado.
     * @param value Valor lógico.
     * @return Resposta positiva ou negativa.
     */
    public static String getAnswer(boolean value) {
        return value ? "sim" : "não";
    }
    
    /**
     * Gera uma cadeia de comprimento n, no formato a^n b^n c^n.
     * @param value Comprimento de cada parcial.
     * @return Retorna a cadeia gerada.
     */
    public static String generate(int value) {
        return StringUtils.repeat("a", value)
                .concat(StringUtils.repeat("b", value))
                .concat(StringUtils.repeat("c", value));
    }
    
}
