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

import br.usp.poli.lta.cereda.aa.utils.IdentifierUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe que representa uma transição da relação de mapeamento do autômato
 * adaptativo. Uma transição pode ser de quatro tipos distintos: consumo de
 * símbolo, transição em vazio, chamada de submáquina e retorno de submáquina.
 * Cada transição pode ter duas ações associadas: uma antes e outra depois da
 * execução da transição propriamente dita.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Transition {

    // variáveis que definem a classe de transição, contendo os estados de
    // origem e destino, o símbolo a ser consumido, a submáquina a ser chamada,
    // um valor lógico representando uma operação de retorno de submáquina,
    // as ações que podem ser associadas, seus respectivos parâmetros, e um
    // identificador unívoco.
    private int identifier;
    private State sourceState;
    private Symbol symbol;
    private State targetState;
    private String submachineCall;
    private String priorActionCall;
    private Object[] priorActionArguments;
    private String postActionCall;
    private Object[] postActionArguments;
    private boolean submachineReturn;

    /**
     * Construtor. Obtém o identificador unívoco da transição corrente e
     * inicializa todas as variáveis apontando para nulo.
     */
    public Transition() {
        identifier = IdentifierUtils.getTransitionIdentifier();
        sourceState = null;
        symbol = null;
        targetState = null;
        submachineCall = null;
        priorActionCall = null;
        priorActionArguments = null;
        postActionCall = null;
        postActionArguments = null;
        submachineReturn = false;
    }

    /**
     * Obtém o identificador da transição corrente.
     * @return Valor inteiro representando o identificador unívoco da transição
     * corrente.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Define o identificador da transição corrente.
     * @param identifier Valor inteiro representando o identificador unívoco da
     * transição corrente.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Obtém o estado de origem.
     * @return Estado de origem da transição.
     */
    public State getSourceState() {
        return sourceState;
    }

    /**
     * Define o estado de origem.
     * @param sourceState Estado de origem.
     */
    public void setSourceState(State sourceState) {
        this.sourceState = sourceState;
    }

    /**
     * Obtém o símbolo a ser consumido na transição.
     * @return Símbolo a ser consumido na transição.
     */
    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Define o símbolo a ser consumido na transição.
     * @param symbol Símbolo a ser consumido na transição.
     */
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Obtém o estado de destino.
     * @return Estado de destino.
     */
    public State getTargetState() {
        return targetState;
    }

    /**
     * Define o estado de destino.
     * @param targetState Estado de destino.
     */
    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    /**
     * Obtém o nome da submáquina a ser chamada.
     * @return Nome da submáquina a ser chamada.
     */
    public String getSubmachineCall() {
        return submachineCall;
    }

    /**
     * Define o nome da submáquina a ser chamada.
     * @param submachineCall Nome da submáquina a ser chamada.
     */
    public void setSubmachineCall(String submachineCall) {
        this.submachineCall = submachineCall;
    }

    /**
     * Obtém o nome da ação anterior.
     * @return Nome da ação anterior.
     */
    public String getPriorActionCall() {
        return priorActionCall;
    }

    /**
     * Define o nome da ação anterior.
     * @param priorActionCall Nome da ação anterior.
     */
    public void setPriorActionCall(String priorActionCall) {
        this.priorActionCall = priorActionCall;
    }

    /**
     * Obtém os parâmetros da ação anterior.
     * @return Vetor de objetos contendo os parâmetros da ação anterior.
     */
    public Object[] getPriorActionArguments() {
        return priorActionArguments;
    }

    /**
     * Define os parâmetros da ação anterior.
     * @param priorActionArguments Vetor de objetos contendo os parâmetros da
     * ação anterior.
     */
    public void setPriorActionArguments(Object[] priorActionArguments) {
        this.priorActionArguments = priorActionArguments;
    }

    /**
     * Obtém o nome da ação posterior.
     * @return Nome da ação posterior.
     */
    public String getPostActionCall() {
        return postActionCall;
    }

    /**
     * Define o nome da ação posterior.
     * @param postActionCall Nome da ação posterior.
     */
    public void setPostActionCall(String postActionCall) {
        this.postActionCall = postActionCall;
    }

    /**
     * Obtém os parâmetros da ação posterior.
     * @return Vetor de objetos contendo os parâmetros da ação posterior.
     */
    public Object[] getPostActionArguments() {
        return postActionArguments;
    }

    /**
     * Define os parâmetros da ação posterior.
     * @param postActionArguments Vetor de objetos contendo os parâmetros da
     * ação posterior.
     */
    public void setPostActionArguments(Object[] postActionArguments) {
        this.postActionArguments = postActionArguments;
    }

    /**
     * Define uma transição convencional com consumo de símbolo ou em vazio.
     * @param sourceState Estado de origem.
     * @param symbol Símbolo a ser consumido, ou nulo representando transição
     * em vazio.
     * @param targetState Estado de destino.
     */
    public void setTransition(State sourceState, Symbol symbol,
            State targetState) {
        this.sourceState = sourceState;
        this.symbol = symbol;
        this.targetState = targetState;
        this.submachineCall = null;
    }

    /**
     * Define uma transição de chamada de submáquina.
     * @param sourceState Estado de origem.
     * @param submachineCall Nome da submáquina a ser chamada.
     * @param targetState Estado de retorno quando a submáquina que foi chamada
     * encerrar-se.
     */
    public void setSubmachineCall(State sourceState, String submachineCall,
            State targetState) {
        this.sourceState = sourceState;
        this.symbol = null;
        this.targetState = targetState;
        this.submachineCall = submachineCall;
    }

    /**
     * Informa se a transição corrente é uma chamada de submáquina.
     * @return Valor lógico informando se a transição corrente é uma chamada de
     * submáquina.
     */
    public boolean isSubmachineCall() {
        return (submachineCall != null);
    }

    /**
     * Informa se a transição corrente é uma transição comum, isto é, que não
     * é uma chamada de de submáquina.
     * @return Valor lógico informando se a transição corrente é comum.
     */
    public boolean isOrdinaryTransition() {
        return !isSubmachineCall();
    }

    /**
     * Informa se a transição corrente é em vazio.
     * @return Valor lógico informando se a transição corrente é em vazio.
     */
    public boolean isEpsilonTransition() {
        return ((!isSubmachineCall()) && (symbol == null));
    }

    /**
     * Informa se a transição corrente é de consumo de símbolo.
     * @return Valor lógico informando se a transição é de consumo de símbolo.
     */
    public boolean isSymbolConsumptionTransition() {
        return ((!isSubmachineCall()) && (symbol != null));
    }

    /**
     * Informa se a transição corrente tem ação anterior.
     * @return Valor lógico informando se a transição tem ação anterior.
     */
    public boolean hasPriorActionCall() {
        return (priorActionCall != null);
    }

    /**
     * Informa se a transição corrente tem ação posterior.
     * @return Valor lógico informando se a transição tem ação posterior.
     */
    public boolean hasPostActionCall() {
        return (postActionCall != null);
    }
    
    /**
     * Informa se a transição corrente é um retorno de submáquina. Este tipo
     * de transição é usado internamente pelo modelo.
     * @return Valor lógico informando se a transição corrente é um retorno de
     * submáquina.
     */
    public boolean isSubmachineReturn() {
        return submachineReturn;
    }

    /**
     * Define se a transição corrente é um retorno de submáquina.
     * @param submachineReturn Valor lógico informando se a transição corrente é
     * um retorno de submáquina.
     */
    public void setSubmachineReturn(boolean submachineReturn) {
        this.submachineReturn = submachineReturn;
    }

    /**
     * Fornece uma representação textual da transição corrente.
     * @return Representação textual da transição corrente.
     */
    @Override
    public String toString() {
        String div = " :: ";
        StringBuilder sb = new StringBuilder();
        sb.append("Transição: { ");
        sb.append("Identificador: ").append(identifier).append(div);
        sb.append("Estado de origem: ").append(sourceState != null ?
                sourceState : "não definido").append(div);
        sb.append("Consumo de símbolo? ").
                append(printAnswer(isSymbolConsumptionTransition())).
                append(div);
        if (isSymbolConsumptionTransition()) {
            sb.append("Símbolo: ").append(symbol).append(div);
        }
        sb.append("Transição em vazio? ").
                append(printAnswer(isEpsilonTransition())).append(div);
        sb.append("Chamada de submáquina? ").
                append(printAnswer(isSubmachineCall())).append(div);
        if (isSubmachineCall()) {
            sb.append("Submáquina: ").append(submachineCall).append(div);
        }
        sb.append("Retorno de submáquina? ")
                .append(printAnswer(isSubmachineReturn())).append(div);
        sb.append("Estado de destino: ").append(targetState != null ?
                targetState : "não definido").append(div);
        sb.append("Ação anterior? ").
                append(printAnswer(hasPriorActionCall())).append(div);
        if (hasPriorActionCall()) {
            sb.append("Nome da ação anterior: ").
                    append(priorActionCall).append(div);
            sb.append("Parâmetros da ação anterior? ").
                    append(printAnswer(priorActionArguments != null)).
                    append(div);
            if (priorActionArguments != null) {
                sb.append("Parâmetros da ação anterior: ").
                        append(printArguments(priorActionArguments, div)).
                        append(div);
            }
        }
        sb.append("Ação posterior? ").append(printAnswer(hasPostActionCall()));
        if (hasPostActionCall()) {
            sb.append(div).append("Nome da ação posterior: ").
                    append(postActionCall);
            sb.append(div).append("Parâmetros da ação posterior? ").
                    append(printAnswer(postActionArguments != null));
            if (postActionArguments != null) {
                sb.append(div).append("Parâmetros da ação posterior: ").
                        append(printArguments(postActionArguments, div));
            }
        }
        sb.append(" }"); 
        return sb.toString();
    }
    
    /**
     * Retorna uma representação textual do vetor de objetos referente aos
     * parâmetros de uma ação.
     * @param objects Vetor de objetos.
     * @param separator Separator dos elementos.
     * @return Representação textual do vetor de objetos.
     */
    private String printArguments(Object[] objects, String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(StringUtils.join(objects, separator));
        sb.append(")");
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
     * Verifica se a ação anterior tem argumentos. Este método verifica se a
     * transição tem ação anterior antes de verificar seus argumentos.
     * @return Valor lógico que denota se a ação anterior tem argumentos.
     */
    public boolean hasPriorActionArguments() {
        return (hasPriorActionCall() ? priorActionArguments != null : false);
    }
    
    /**
     * Verifica se a ação posterior tem argumentos. Este método verifica se a
     * transição tem ação posterior antes de verificar seus argumentos.
     * @return Valor lógico que denota se a ação posterior tem argumentos.
     */
    public boolean hasPostActionArguments() {
        return (hasPostActionCall() ? postActionArguments != null : false);
    }
    
    /**
     * Retorna o número de argumentos da ação anterior.
     * @return Um valor inteiro denotando o número de argumentos da ação
     * anterior. Um valor negativo indica que não há argumentos.
     */
    public int countPriorActionArguments() {
        return (hasPriorActionArguments() ? priorActionArguments.length : -1);
    }
    
    /**
     * Retorna o número de argumentos da ação posterior.
     * @return Um valor inteiro denotando o número de argumentos da ação
     * posterior. Um valor negativo indica que não há argumentos.
     */
    public int countPostActionArguments() {
        return (hasPostActionArguments() ? postActionArguments.length : -1);
    }
    
}
