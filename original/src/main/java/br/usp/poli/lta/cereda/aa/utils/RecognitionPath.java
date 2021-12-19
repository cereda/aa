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

import br.usp.poli.lta.cereda.aa.model.State;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe define um caminho de reconhecimento. Em outras palavras, esta
 * classe contém todos os passos de reconhecimento de uma cadeia de entrada
 * submetida ao autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class RecognitionPath {

    // atributos da classe, contendo um identificador unívoco, uma lista de
    // strings contendo todos os passos de reconhecimento, a posição corrente
    // do cursor, o estado final, e uma variável lógica indicando o resultado
    // do processo de reconhecimento representado através do objeto em questão
    private int identifier;
    private List<String> path;
    private int cursor;
    private State state;
    private Boolean result;

    /**
     * Construtor. Obtém o identificador e inicializa as outras variáveis.
     */
    public RecognitionPath() {
        identifier = IdentifierUtils.getRecognitionPathIdentifier();
        path = new ArrayList<>();
        result = null;
        cursor = 0;
        state = null;
    }

    /**
     * Informa se o caminho reconhecimento representado por esse objeto já
     * encerrou-se. O término de um caminho de reconhecimento ocorre quando
     * seu resultado é conhecido, seja ele verdadeiro ou falso.
     * @return Um valor lógico informando se o caminho de reconhecimento já foi
     * encerrado.
     */
    public boolean done() {
        return (result != null);
    }

    /**
     * Obtém o identificador unívoco do caminho de reconhecimento corrente.
     * @return Um valor inteiro representando o identificador do objeto.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Define o valor do identificador do objeto corrente.
     * @param identifier Um valor inteiro representando o identificador.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Obtém a lista de passos computacionais dados pelo autômato adaptativo e
     * contidas no caminho de reconhecimento.
     * @return Uma lista de strings contendo os passos computacionais dados pelo
     * autômato adaptativo.
     */
    public List<String> getPath() {
        return path;
    }

    /**
     * Define a lista de passos computacionais.
     * @param path Uma lista de strings contendo os passos computacionais dados
     * pelo autômato adaptativo.
     */
    public void setPath(List<String> path) {
        this.path = path;
    }

    /**
     * Obtém o resultado do caminho de reconhecimento.
     * @return Um valor lógico informando se o autômato adaptativo reconheceu
     * a cadeia de entrada fornecida.
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Define o valor do resultado do caminho de reconhecimento.
     * @param result Valor lógico representando o resultado do caminho de
     * reconhecimento.
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * Adiciona a string ao caminho de reconhecimento.
     * @param path String a ser adicionada ao caminho de reconhecimento.
     */
    public void addPath(String path) {
        this.path.add(path);
    }

    /**
     * Obtém a posição do cursor da cadeia de entrada.
     * @return Valor inteiro representando a posição do cursor da cadeia de
     * entrada.
     */
    public int getCursor() {
        return cursor;
    }

    /**
     * Define o valor do cursor da cadeia de entrada.
     * @param cursor Valor inteiro do cursor da cadeia de entrada.
     */
    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * Obtém o estado final do processo de reconhecimento da cadeia de entrada.
     * @return Estado final do processo de reconhecimento da cadeia de entrada.
     */
    public State getState() {
        return state;
    }

    /**
     * Define o valor do estado final do processo de reconhecimento da cadeia
     * de entrada.
     * @param state Estado final do processo de reconhecimento da cadeia de
     * entrada.
     */
    public void setState(State state) {
        this.state = state;
    }
    
    /**
     * Fornece uma representação textual do objeto corrente.
     * @return Uma representação textual do caminho de reconhecimento do
     * autômato adaptativo.
     */
    @Override
    public String toString() {
        String tab = "  ";
        String newline = "\n";
        String bullet = ":: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Caminho de reconhecimento: {").append(newline);
        sb.append(tab).append("Identificador: ").
                append(identifier).append(newline);
        for (String s : path) {
            sb.append(tab).append(tab).append(bullet)
                    .append(s).append(newline);
        }
        sb.append(tab).append("Resultado: ").append(result).append(newline);
        sb.append("}");
        
        return sb.toString();
    }
    
}
