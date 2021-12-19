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

/**
 * Gera identificadores unívocos para representar submáquinas, caminhos de
 * reconhecimento, ações, transições e threads de execução. Todos os métodos
 * dessa classe são estáticos, portanto a classe não deve ser instanciada na
 * forma de um objeto.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class IdentifierUtils {

    // identificadores, iniciando seus valores em 1
    private static int SUBMACHINE_IDENTIFIER = 1;
    private static int RECOGNITION_PATH_IDENTIFIER = 1;
    private static int ACTION_IDENTIFIER = 1;
    private static int TRANSITION_IDENTIFIER = 1;
    private static int KERNEL_IDENTIFIER = 1;

    /**
     * Obtém um novo identificador de submáquina.
     * @return Um valor inteiro representando um novo identificador unívoco de
     * uma submáquina.
     */
    public static int getSubmachineIdentifier() {
        int result = SUBMACHINE_IDENTIFIER;
        SUBMACHINE_IDENTIFIER++;
        return result;
    }
    
    /**
     * Obtém um novo identificador de caminho de reconhecimento.
     * @return Um valor inteiro representando um novo identificador unívoco de
     * um caminho de reconhecimento.
     */
    public static int getRecognitionPathIdentifier() {
        int result = RECOGNITION_PATH_IDENTIFIER;
        RECOGNITION_PATH_IDENTIFIER++;
        return result;
    }

    /**
     * Obtém um novo identificador de ação.
     * @return Um valor inteiro representando um novo identificador unívoco de
     * uma determinada ação.
     */
    public static int getActionIdentifier() {
        int result = ACTION_IDENTIFIER;
        ACTION_IDENTIFIER++;
        return result;
    }
    
    /**
     * Obtém um novo identificador de transição.
     * @return Um valor inteiro representando um novo identificador unívoco de
     * uma transição.
     */
    public static int getTransitionIdentifier() {
        int result = TRANSITION_IDENTIFIER;
        TRANSITION_IDENTIFIER++;
        return result;
    }
    
    /**
     * Obtém um novo identificador de thread de reconhecimento.
     * @return Um valor inteiro representando um novo identificador unívoco de
     * uma thread de reconhecimento.
     */
    public static int getKernelIdentifier() {
        int result = KERNEL_IDENTIFIER;
        KERNEL_IDENTIFIER++;
        return result;
    }
    
}
