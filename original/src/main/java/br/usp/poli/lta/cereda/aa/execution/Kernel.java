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
package br.usp.poli.lta.cereda.aa.execution;

import br.usp.poli.lta.cereda.aa.logging.SimpleMessage;
import br.usp.poli.lta.cereda.aa.logging.TransitionMessage;
import br.usp.poli.lta.cereda.aa.model.Stack;
import br.usp.poli.lta.cereda.aa.model.Symbol;

import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.predicates.EpsilonPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SubmachineCallPredicate;
import br.usp.poli.lta.cereda.aa.model.sets.ActionsSet;
import br.usp.poli.lta.cereda.aa.model.sets.SubmachinesSet;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.utils.IdentifierUtils;
import br.usp.poli.lta.cereda.aa.utils.RecognitionPath;
import com.rits.cloning.Cloner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Realiza o passo computacional do autômato adaptativo. É importante destacar
 * que uma thread só é executada uma única vez.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Kernel extends Thread {

    // auditoria da execução da thread referente ao passo computacional
    // do autômato adaptativo
    private static final Logger logger = LogManager.getLogger(Kernel.class);

    // identificador unívoco da thread; apesar da thread ter seu identificador
    // já definido, optou-se por definir um identificador de controle interno
    private int identifier;

    // pilha do autômato adaptativo
    private Stack stack;

    // conjuntos de ações, transições e submáquinas, de acordo
    // com o modelo formal
    private ActionsSet actions;
    private Mapping transitions;
    private SubmachinesSet submachines;

    // lista de símbolos, representando a cadeia de entrada,
    // e o cursor que identifica qual a posição do símbolo
    // a ser lido
    private List<Symbol> input;
    private int cursor;

    // variáveis auxiliares para o tratamento da transição corrente,
    // sinalizador indicador de permissão de ação anterior, submáquina
    // corrente e submáquina principal
    private Transition transition;
    private boolean enablePriorAction;
    private String currentSubmachine;
    private String mainSubmachine;

    // objeto de clonagem para auxiliar na cópia das
    // estruturas do autômato adaptativo
    private final Cloner dolly;

    // listas referenciadas externamente, são utilizadas para
    // realizar a comunicação com o método de reconhecimento
    // do autômato adaptativo
    private List<Kernel> threads;
    private List<Integer> removals;
    private Map<Integer, RecognitionPath> paths;

    /**
     * Construtor. Referências externas são atualizadas.
     * @param threads Lista de threads.
     * @param removals Lista de identificadores para remoção.
     * @param paths Mapa de caminhos de reconhecimento.
     */
    public Kernel(List<Kernel> threads, List<Integer> removals, Map<Integer, RecognitionPath> paths) {

        // obtém um identificador unívoco para a thread corrente
        identifier = IdentifierUtils.getKernelIdentifier();

        // inicializa todas as estruturas
        stack = new Stack();
        actions = new ActionsSet();
        transitions = new Mapping();
        submachines = new SubmachinesSet();

        // inicializa as variáveis de leitura
        input = new ArrayList<>();
        cursor = 0;

        // cria um novo objeto de clonagem
        // dentro do escopo da thread
        dolly = new Cloner();

        // atribui as referências externas
        this.threads = threads;
        this.removals = removals;
        this.paths = paths;

        // variáveis de operação são
        // definidas como nulo
        transition = null;
        currentSubmachine = null;
        mainSubmachine = null;

        // sinalizador é definido
        // como verdadeiro, apriori
        enablePriorAction = true;
    }

    /**
     * Obtém o identificador da thread.
     * @return Valor inteiro representando o identificador da thread.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * Obtém a pilha do autômato adaptativo.
     * @return Pilha do autômato adaptativo.
     */
    public Stack getStack() {
        return stack;
    }

    /**
     * Obtém o conjunto de ações do autômato adaptativo.
     * @return Conjunto de ações do autômato adaptativo.
     */
    public ActionsSet getActions() {
        return actions;
    }

    /**
     * Obtém o conjunto de transições do autômato adaptativo.
     * @return Conjunto de transições do autômato adaptativo.
     */
    public Mapping getTransitions() {
        return transitions;
    }

    /**
     * Obtém o conjunto de submáquinas do autômato adaptativo.
     * @return Conjunto de submáquinas do autômato adaptativo.
     */
    public SubmachinesSet getSubmachines() {
        return submachines;
    }

    /**
     * Obtém a lista de símbolos.
     * @return Lista de símbolos.
     */
    public List<Symbol> getInput() {
        return input;
    }

    /**
     * Obtém o valor do cursor.
     * @return Valor inteiro da posição do cursor.
     */
    public int getCursor() {
        return cursor;
    }

    /**
     * Obtém a transição corrente.
     * @return Transição corrente.
     */
    public Transition getTransition() {
        return transition;
    }

    /**
     * Verifica se a ação anterior está habilitada.
     * @return Valor lógico indicando se a ação anterior está habilitada.
     */
    public boolean isEnablePriorAction() {
        return enablePriorAction;
    }

    /**
     * Obtém a lista de threads.
     * @return Lista de threads.
     */
    public List<Kernel> getThreads() {
        return threads;
    }

    /**
     * Obtém a lista de identificadores para remoção de threads.
     * @return Lista de identificadores para remoção de threads.
     */
    public List<Integer> getRemovals() {
        return removals;
    }

    /**
     * Obtém o mapa de caminhos de reconhecimento.
     * @return Mapa de caminhos de reconhecimento.
     */
    public Map<Integer, RecognitionPath> getPaths() {
        return paths;
    }

    /**
     * Define o identificador da thread.
     * @param identifier Valor inteiro representando o identificador da thread.
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    /**
     * Define a pilha do autômato adaptativo.
     * @param stack Pilha do autômato adaptativo.
     */
    public void setStack(Stack stack) {
        this.stack = dolly.deepClone(stack);
    }

    /**
     * Define o conjunto de ações do autômato adaptativo.
     * @param actions Conjunto de ações do autômato adaptativo.
     */
    public void setActions(ActionsSet actions) {
        this.actions = actions;
    }

    /**
     * Define o conjunto de transições do autômato adaptativo.
     * @param transitions Conjunto de transições do autômato adaptativo.
     */
    public void setTransitions(Mapping transitions) {
        this.transitions = transitions;
    }

    /**
     * Define o conjunto de submáquinas do autômato adaptativo.
     * @param submachines Conjunto de submáquinas do autômato adaptativo.
     */
    public void setSubmachines(SubmachinesSet submachines) {
        this.submachines = dolly.deepClone(submachines);
    }

    /**
     * Define a lista de símbolos representando a cadeia de entrada.
     * @param input Lista de símbolos representando a cadeia de entrada.
     */
    public void setInput(List<Symbol> input) {
        this.input = dolly.deepClone(input);
    }

    /**
     * Define o valor do cursor.
     * @param cursor Valor inteiro representando a posição do cursor.
     */
    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * Define a transição corrente.
     * @param transition Transição.
     */
    public void setTransition(Transition transition) {
        this.transition = dolly.deepClone(transition);
    }

    /**
     * Define se a ação anterior está habilitada.
     * @param enablePriorAction Valor lógico indicando se a ação anterior está
     * habilitada.
     */
    public void setEnablePriorAction(boolean enablePriorAction) {
        this.enablePriorAction = enablePriorAction;
    }

    /**
     * Define a lista de threads.
     * @param threads Lista de threads.
     */
    public void setThreads(List<Kernel> threads) {
        this.threads = threads;
    }

    /**
     * Define a lista de identificadores para remoção de threads.
     * @param removals Lista de identificadores para remoção de threads.
     */
    public void setRemovals(List<Integer> removals) {
        this.removals = removals;
    }

    /**
     * Define o mapa de caminhos de reconhecimento.
     * @param paths Mapa de caminhos de reconhecimento.
     */
    public void setPaths(Map<Integer, RecognitionPath> paths) {
        this.paths = paths;
    }

    /**
     * Obtém o nome da submáquina corrente.
     * @return Nome da submáquina corrente.
     */
    public String getCurrentSubmachine() {
        return currentSubmachine;
    }

    /**
     * Define a submáquina corrente.
     * @param currentSubmachine Nome da submáquina corrente.
     */
    public void setCurrentSubmachine(String currentSubmachine) {
        this.currentSubmachine = currentSubmachine;
    }

    /**
     * Obtém o nome da submáquina principal.
     * @return Nome da submáquina principal.
     */
    public String getMainSubmachine() {
        return mainSubmachine;
    }

    /**
     * Define a submáquina principal.
     * @param mainSubmachine Nome da submáquina principal.
     */
    public void setMainSubmachine(String mainSubmachine) {
        this.mainSubmachine = mainSubmachine;
    }

    /**
     * Executa a ação da thread, que representa um passo computacional do
     * autômato adaptativo. Após a execução deste método, a thread encerra-se.
     */
    @Override
    public void run() {

        // mensagem de log
        logger.debug(new TransitionMessage(transition, "A thread %d iniciou #run() com a transição %d.", identifier, transition.getIdentifier()));

        // lista auxiliar de transições para armazenar os resultados
        // das consultas ao conjunto de transições do modelo
        List<Transition> query;

        // a transição corrente não pode ser nula, caso contrário
        // uma exceção é lançada
        Validate.notNull(transition, "A transição não pode ser nula.");

        // a transição corrente tem uma ação anterior?
        if (transition.hasPriorActionCall()) {

            // mensagem de log
            logger.debug(new SimpleMessage("[Thread %d] A transição %d possui uma ação anterior.", identifier, transition.getIdentifier()));

            // sim, tem uma ação; o passo computacional permite
            // a execução dessa ação anterior?
            if (enablePriorAction) {

                // sim, permite
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] A thread está autorizada a executar a ação anterior.", identifier));

                // executa a ação antes da efetivação da transição,
                // passando os eventuais parâmetros de acordo com
                // o mapeamento
                actions.fromName(transition.getPriorActionCall()).execute(transitions, transition, transition.getPriorActionArguments());

                // desabilita o passo computacional para ações anteriores,
                // caso não seja possível prosseguir com a transição corrente
                enablePriorAction = false;

                // é importante verificar se a ação anterior removeu ou
                // alterou a transição corrente; ela não existe mais?
                if (!transitions.hasIdentifier(transition.getIdentifier())) {

                    // sim, ela não existe mais
                    
                    // a ação removeu ou alterou a transição corrente, então
                    // é necessário recalcular a nova transição corrente
                    
                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A transição %d foi alterada ou não existe mais. É necessário recalcular a nova transição.", identifier, transition.getIdentifier()));

                    // o estado de origem passa a ser o estado corrente
                    // para recalcular a nova transição
                    br.usp.poli.lta.cereda.aa.model.State currentState = transition.getSourceState();

                    // o valor do cursor é igual ao tamanho da cadeia?
                    if (cursor == input.size()) {

                        // sim, os valores são iguais, o que significa que
                        // não tenho mais nada para consumir
                        
                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] Não há mais símbolos para consumir.", identifier));

                        // consulta quais transições partem do estado corrente
                        // transitando em vazio ou fazendo chamadas de submáquinas
                        query = transitions.withEpsilonOrSubmachineFromSourceState(currentState);

                        // a consulta está vazia?
                        if (query.isEmpty()) {

                            // sim, a consulta está vazia, portanto não
                            // tenho para onde ir
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Não existem transições em vazio ou chamadas de submáquinas.", identifier));

                            // o estado corrente é final na submáquina corrente?
                            if (submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {

                                // sim, o estado corrente é final na submáquina corrente
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final na submáquina corrente.", identifier));

                                // a pilha está vazia?
                                if (stack.isEmpty()) {

                                    // sim, pilha vazia
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));

                                    // a submáquina corrente é a principal?
                                    if (currentSubmachine.equals(mainSubmachine)) {

                                        // sim, indica que a cadeia foi aceita
                                        
                                        // mensagens de log
                                        logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente é a principal.", identifier));
                                        logger.debug(new SimpleMessage("[Thread %d] A cadeia foi aceita.", identifier));

                                        // define o resultado do caminho corrente
                                        // como verdadeiro, isto é, a cadeia foi
                                        // aceita!
                                        paths.get(identifier).setResult(true);
                                        
                                        // define a posição final do cursor da
                                        // cadeia de entrada
                                        paths.get(identifier).setCursor(cursor);
                                        
                                        // define o estado final do processo
                                        // de reconhecimento
                                        paths.get(identifier).setState(dolly.deepClone(currentState));

                                        // informa que a thread pode ser removida
                                        removals.add(identifier);

                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                        // retorna, e a thread é encerrada
                                        return;

                                    } else {

                                        // a submáquina corrente não é principal,
                                        // portanto a cadeia deve ser rejeitada
                                        
                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente não é a principal.", identifier));

                                        // define o resultado do caminho corrente
                                        // como falso, isto é, a cadeia foi rejeitada
                                        paths.get(identifier).setResult(false);
                                        
                                        // define a posição final do cursor da
                                        // cadeia de entrada
                                        paths.get(identifier).setCursor(cursor);
                                        
                                        // define o estado final do processo
                                        // de reconhecimento
                                        paths.get(identifier).setState(dolly.deepClone(currentState));

                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                        // informa que a thread pode ser removida
                                        removals.add(identifier);

                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                        // retorna, encerrando a execução da thread
                                        return;

                                    }
                                } else {

                                    // a pilha não está vazia, portanto a transição
                                    // corrente torna-se uma operação de desempilhamento,
                                    // isto é, de retorno de submáquina
                                    transition = new Transition();
                                    transition.setSubmachineReturn(true);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(transition, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, transition.getIdentifier()));

                                }
                            } else {

                                // não estou no estado final da submáquina corrente,
                                // portanto a cadeia foi rejeitada
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final na submáquina corrente.", identifier));

                                // define o resultado do caminho corrente
                                // como falso, isto é, a cadeia foi rejeitada
                                paths.get(identifier).setResult(false);
                                
                                // define a posição final do cursor da
                                // cadeia de entrada
                                paths.get(identifier).setCursor(cursor);
                                
                                // define o estado final do processo
                                // de reconhecimento
                                paths.get(identifier).setState(dolly.deepClone(currentState));

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                // informa que a thread pode ser removida
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // retorna e encerra a execução da thread corrente
                                return;

                            }

                        } else {

                            // a consulta não está vazia!
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] A consulta não está vazia.", identifier));
                            
                            // a consulta tem transições em vazio e chamadas
                            // de submáquinas?
                            if (hasEpsilonTransitions(query) && hasSubmachineCalls(query)) {
                                
                                // sim, ambas

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Existem transições em vazio e chamadas de submáquina.", identifier));

                                // situação de não-determinismo, com transições em
                                // vazio e chamadas de submáquinas
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(false);
                                    k.setTransition(t);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                    // adiciona a nova transição ao caminho
                                    // de reconhecimento de cada thread
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                }
                                
                                // remove a thread corrente
                                removals.add(identifier);

                                // mensagem
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando a execução da thread
                                return;

                            } else {
                                
                                // temos transições em vazio ou chamadas
                                // de submáquinas

                                // primeira verificação: existem chamadas de
                                // submáquinas na consulta?
                                if (hasSubmachineCalls(query)) {

                                    // sim, existem chamadas de submáquina
                                    // (e, por tabela, não existem transições
                                    // em vazio)
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] Existem chamadas de submáquina.", identifier));
                                    
                                    // é uma só chamada de submáquina?
                                    if (query.size() == 1) {

                                        // sim, situação determinística
                                        
                                        // define a transição corrente
                                        // como o resultado da consulta
                                        transition = dolly.deepClone(query.get(0));

                                        // mensagem de log
                                        logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma chamada de submáquina.", identifier));

                                    } else {

                                        // são várias chamadas de submáquinas
                                        
                                        // mensagem
                                        logger.debug(new SimpleMessage("[Thread %d] São várias chamadas de submáquinas.", identifier));

                                        
                                        // situação de não-determinismo, com várias
                                        // chamadas de submáquinas
                                        for (Transition t : query) {

                                            // para cada transição da consulta, criar uma nova thread
                                            Kernel k = new Kernel(threads, removals, paths);
                                            k.setStack(stack);
                                            k.setActions(actions);
                                            k.setTransitions(transitions);
                                            k.setSubmachines(submachines);
                                            k.setCurrentSubmachine(currentSubmachine);
                                            k.setMainSubmachine(mainSubmachine);
                                            k.setInput(input);
                                            k.setCursor(cursor);
                                            k.setEnablePriorAction(false);
                                            k.setTransition(t);

                                            // adiciona a transição no caminho de reconhecimento
                                            // para cada nova thread
                                            paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                            paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                            threads.add(k);

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                        }
                                        
                                        // adiciona a thread para remoção
                                        removals.add(identifier);

                                        // e retorna, encerrando a execução da thread
                                        return;

                                    }
                                } else {

                                    // a consulta tem transições em vazio
                                    // (e, por tabela, não tem chamadas de
                                    // submáquinas!)
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A consulta tem transições em vazio.", identifier));
                                    
                                    // o estado corrente não é final no contexto
                                    // da submáquina corrente?
                                    if (!submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {

                                        // sim, o estado é comum, não final
                                        
                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final no contexto da submáquina corrente.", identifier));

                                        // é uma transição só?
                                        if (query.size() == 1) {

                                            // sim, portanto a transição
                                            // é determinística
                                            
                                            // define a transição corrente
                                            // como resultado da consulta
                                            transition = dolly.deepClone(query.get(0));

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição em vazio.", identifier));

                                        } else {
                                            
                                            // são várias transições em vazio

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(transition, "[Thread %d] São várias transições em vazio.", identifier));

                                            // situação de não-determinismo, com várias
                                            // transições em vazio
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a execução
                                            // da thread corrente
                                            return;

                                        }
                                    } else {

                                        // o estado corrente é final e existem
                                        // transições em vazio
                                        
                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final no contexto da submáquina corrente.", identifier));

                                        // a pilha está vazia?
                                        if (stack.isEmpty()) {
                                            
                                            // sim, pilha vazia

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));

                                            // a submáquina corrente é a principal?
                                            if (currentSubmachine.equals(mainSubmachine)) {

                                                // sim, é a submáquina principal
                                                
                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente é a principal.", identifier));
                                                
                                                // situação de não-determinismo, pois o autômato
                                                // pode prosseguir o reconhecimento ou encerrar
                                                for (Transition t : query) {

                                                    // para cada transição da consulta, criar uma nova thread
                                                    Kernel k = new Kernel(threads, removals, paths);
                                                    k.setStack(stack);
                                                    k.setActions(actions);
                                                    k.setTransitions(transitions);
                                                    k.setSubmachines(submachines);
                                                    k.setCurrentSubmachine(currentSubmachine);
                                                    k.setMainSubmachine(mainSubmachine);
                                                    k.setInput(input);
                                                    k.setCursor(cursor);
                                                    k.setEnablePriorAction(false);
                                                    k.setTransition(t);

                                                    // adiciona o caminho de reconhecimento
                                                    // para as novas threads
                                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                    threads.add(k);

                                                    // mensagem de log
                                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                                }

                                                // adiciona um caminho adicional informando
                                                // que o autômato reconheceu a cadeia de
                                                // entrada
                                                paths.get(identifier).setResult(true);
                                                
                                                // define a posição final do cursor da
                                                // cadeia de entrada
                                                paths.get(identifier).setCursor(cursor);
                                                
                                                // define o estado final do processo
                                                // de reconhecimento
                                                paths.get(identifier).setState(dolly.deepClone(currentState));

                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] A cadeia foi aceita.", identifier));

                                                // adiciona a thread corrente
                                                // na lista de remoções
                                                removals.add(identifier);

                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                                // e retorna, encerrando
                                                // a thread corrente
                                                return;

                                            } else {

                                                // a submáquina corrente não
                                                // é a submáquina principal
                                                
                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente não é a principal.", identifier));
                                                
                                                // situação de não-determinismo, pois a submáquina não é
                                                // a principal, a pilha está vazia e não é possível
                                                // retornar (cadeia rejeitada), mas o autômato pode
                                                // seguir as transições em vazio
                                                for (Transition t : query) {

                                                    // para cada transição da consulta, criar uma nova thread
                                                    Kernel k = new Kernel(threads, removals, paths);
                                                    k.setStack(stack);
                                                    k.setActions(actions);
                                                    k.setTransitions(transitions);
                                                    k.setSubmachines(submachines);
                                                    k.setCurrentSubmachine(currentSubmachine);
                                                    k.setMainSubmachine(mainSubmachine);
                                                    k.setInput(input);
                                                    k.setCursor(cursor);
                                                    k.setEnablePriorAction(false);
                                                    k.setTransition(t);

                                                    // adiciona o caminho de reconhecimento
                                                    // para as novas threads
                                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                    threads.add(k);

                                                    // mensagem de log
                                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                                }

                                                // informa que o caminho de reconhecimento
                                                // corrente resultou na rejeição da cadeia
                                                // de entrada
                                                paths.get(identifier).setResult(false);
                                                
                                                // define a posição final do cursor da
                                                // cadeia de entrada
                                                paths.get(identifier).setCursor(cursor);
                                                
                                                // define o estado final do processo
                                                // de reconhecimento
                                                paths.get(identifier).setState(dolly.deepClone(currentState));

                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                                // adiciona thread corrente
                                                // na lista de remoções
                                                removals.add(identifier);

                                                // mensagem de log
                                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                                // e retorna!
                                                return;

                                            }
                                        } else {

                                            // a pilha não está vazia, então
                                            // existem operações de desempilhamento,
                                            // isto é, de retorno de submáquinas
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] A pilha não está vazia.", identifier));

                                            // situação de não-determinismo, com
                                            // retorno de submáquinas e transições
                                            // em vazio
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }

                                            // cria uma thread adicional que fará
                                            // o desempilhamento, isto é, retorno
                                            // de submáquina
                                            Kernel k = new Kernel(threads, removals, paths);
                                            k.setStack(stack);
                                            k.setActions(actions);
                                            k.setTransitions(transitions);
                                            k.setSubmachines(submachines);
                                            k.setCurrentSubmachine(currentSubmachine);
                                            k.setMainSubmachine(mainSubmachine);
                                            k.setInput(input);
                                            k.setCursor(cursor);
                                            k.setEnablePriorAction(false);
                                            Transition t = new Transition();
                                            t.setSubmachineReturn(true);
                                            k.setTransition(t);

                                            // adiciona o caminho de reconhecimento
                                            // para a nova thread
                                            paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                            paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                            threads.add(k);

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(t, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, t.getIdentifier()));

                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a thread corrente
                                            return;

                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        // ainda existem símbolos a serem lidos
                        // na cadeia de entrada
                        
                        // lê símbolo apontado pelo cursor
                        Symbol currentSymbol = input.get(cursor);

                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] Ainda existem símbolos para consumir.", identifier));

                        // todas as transições que, partindo do estado corrente, consomem
                        // o símbolo corrente, transitam em vazio ou chamam submáquinas
                        query = transitions.withSymbolEpsilonOrSubmachineFromSourceState(currentState, currentSymbol);

                        // a consulta está vazia?
                        if (query.isEmpty()) {

                            // sim, vazia
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Não existem transições com consumo de símbolo, em vazio ou chamadas de submáquinas.", identifier));

                            // a pilha está vazia?
                            if (stack.isEmpty()) {
                                
                                // sim, a pilha está vazia

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));

                                // não há para onde ir, a cadeia
                                // está rejeitada
                                paths.get(identifier).setResult(false);
                                
                                // define a posição final do cursor da
                                // cadeia de entrada
                                paths.get(identifier).setCursor(cursor);
                                
                                // define o estado final do processo
                                // de reconhecimento
                                paths.get(identifier).setState(dolly.deepClone(currentState));

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                // informa que a thread pode ser removida
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // retorna, encerrando a execução
                                // da thread corrente
                                return;

                            } else {

                                // a pilha não está vazia
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] A pilha não está vazia.", identifier));

                                // o estado corrente é final no contexto da submáquina corrente?
                                if (submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {
                                    
                                    // sim, o estado é final no contexto
                                    // da submáquina corrente

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final na submáquina corrente.", identifier));

                                    // define a transição corrente como uma operação
                                    // de desempilhamento, isto é, retorno de submáquina
                                    transition = new Transition();
                                    transition.setSubmachineReturn(true);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(transition, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, transition.getIdentifier()));

                                } else {

                                    // o estado não é final no contexto da
                                    // submáquina corrente
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final na submáquina corrente.", identifier));

                                    // não há para onde ir e a cadeia é,
                                    // portanto, rejeitada
                                    paths.get(identifier).setResult(false);
                                    
                                    // define a posição final do cursor da
                                    // cadeia de entrada
                                    paths.get(identifier).setCursor(cursor);
                                    
                                    // define o estado final do processo
                                    // de reconhecimento
                                    paths.get(identifier).setState(dolly.deepClone(currentState));

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                    // informa que a thread pode ser removida
                                    removals.add(identifier);

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                    // retorna, encerrando a
                                    // thread corrente
                                    return;

                                }
                            }
                        } else {

                            // a consulta não está vazia, existem transições
                            
                            // a consulta tem transições com
                            // consumo de símbolos?
                            if (hasSymbolTransitions(query)) {

                                // sim, existem transições com
                                // consumo de símbolos
                                
                                // é uma só transição?
                                if (getSymbolTransitions(query).size() == 1) {

                                    // a consulta tem transições em vazio?
                                    if (hasEpsilonTransitions(query)) {

                                        // sim, tem transições em vazio
                                        
                                        // a consulta tem chamadas de submáquinas?
                                        if (hasSubmachineCalls(query)) {

                                            // sim, tem chamadas de submáquinas
                                            
                                            // não-determinismo com um consumo de símbolo,
                                            // transições em vazio e chamadas de submáquinas
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo, transições em vazio e chamadas de submáquinas.", identifier));

                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a
                                            // execução da thread corrente
                                            return;

                                        } else {

                                            // não há chamadas de submáquinas,
                                            // apenas consumo de símbolo e
                                            // transições em vazio
                                            
                                            // não-determinismo, com uma transição
                                            // de consumo de símbolo e transições
                                            // em vazio
                                            
                                            // mensagem
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo e transições em vazio.", identifier));

                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);
                                                
                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a execução
                                            // da thread corrente
                                            return;

                                        }
                                    } else {

                                        // a consulta não tem transições em vazio
                                        
                                        // a consulta tem chamadas de submáquinas?
                                        if (hasSubmachineCalls(query)) {

                                            // sim, tem chamadas de submáquinas
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo e chamadas de submáquinas.", identifier));

                                            // não-determinismo com chamadas de
                                            // submáquinas e uma transição de
                                            // consumo de símbolo
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);
                                                
                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de erro
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a
                                            // execução da thread
                                            return;

                                        } else {

                                            // existe apenas uma transição de
                                            // consumo de símbolo, portanto,
                                            // determinística
                                            transition = dolly.deepClone(query.get(0));

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição de consumo de símbolo.", identifier));

                                        }
                                    }
                                } else {

                                    // existem várias transições de consumo de
                                    // símbolo, portanto, já é não-determinístico
                                    
                                    // a consulta tem transições em vazio?
                                    if (hasEpsilonTransitions(query)) {

                                        // sim, tem transições em vazio
                                        
                                        // a consulta tem chamadas de submáquinas?
                                        if (hasSubmachineCalls(query)) {

                                            // sim, tem chamadas de submáquinas
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo, transições em vazio e chamadas de submáquinas.", identifier));

                                            // não-determinismo com transições com
                                            // consumo de símbolos, transições em
                                            // vazio e chamadas de submáquinas
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a thread corrente
                                            return;
                                            
                                        } else {

                                            // não há chamadas de submáquinas,
                                            // apenas transições com consumo
                                            // de símbolo e transições em vazio
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo e transições em vazio.", identifier));

                                            // não-determinismo com transições de consumo
                                            // de símbolo e transição em vazio
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);
                                                
                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a thread corrente
                                            return;

                                        }
                                    } else {

                                        // não há transições em vazio
                                        
                                        // a consulta tem chamadas de submáquinas?
                                        if (hasSubmachineCalls(query)) {

                                            // sim, tem chamadas de submáquinas
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo e chamadas de submáquinas.", identifier));

                                            // não-determinismo com transições de
                                            // consumo de símbolo e chamadas de
                                            // submáquinas
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);
                                                
                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a
                                            // thread corrente
                                            return;

                                        } else {

                                            // não há chamadas de submáquinas,
                                            // portanto, apenas transições com
                                            // consumo de símbolo

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo.", identifier));

                                            // não-determinismo com transições
                                            // com consumo de símbolo
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente na
                                            // lista de remoções
                                            removals.add(identifier);

                                            // mensagem de erro
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a thread corrente
                                            return;

                                        }
                                    }
                                }
                            } else {

                                // a consulta não tem transições com consumo
                                // de símbolo, apenas transições em vazio ou
                                // chamadas de submáquinas
                                
                                // a consulta tem chamadas de submáquinas?
                                if (hasSubmachineCalls(query)) {

                                    // sim, tem chamadas de submáquina
                                    
                                    // é uma só?
                                    if (getSubmachineCalls(query).size() == 1) {

                                        // sim, é uma só chamada
                                        
                                        // a consulta tem transições em vazio?
                                        if (hasEpsilonTransitions(query)) {

                                            // sim, existem transições em vazio
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma chamada de submáquina e transições em vazio.", identifier));

                                            // não-determinismo com uma chamada de
                                            // submáquina e transições em vazio
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona thread na lista
                                            // de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a execução da thread
                                            return;

                                        } else {

                                            // só existe uma chamada de submáquina, portanto,
                                            // o passo é determinístico
                                            
                                            // a transição corrente passa a ser
                                            // a chamada de submáquina
                                            transition = dolly.deepClone(query.get(0));

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma chamada de submáquina.", identifier));

                                        }
                                    } else {

                                        // são várias chamadas de submáquinas,
                                        // portanto, o passo será não-determinístico
                                        
                                        // a consulta tem transições em vazio?
                                        if (hasEpsilonTransitions(query)) {

                                            // sim, tem transições em vazio
                                            
                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com chamadas de submáquinas e transições em vazio.", identifier));

                                            // não-determinismo com chamadas de submáquinas
                                            // e transições em vazio
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread corrente
                                            // na lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando
                                            // a thread corrente
                                            return;

                                        } else {
                                            // não tem transições em vazio,
                                            // apenas várias chamadas de
                                            // submáquinas

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com chamadas de submáquinas.", identifier));

                                            // não-determinismo, com chamadas
                                            // de submáquinas
                                            
                                            // situação de não-determinismo
                                            for (Transition t : query) {

                                                // para cada transição da consulta, criar uma nova thread
                                                Kernel k = new Kernel(threads, removals, paths);
                                                k.setStack(stack);
                                                k.setActions(actions);
                                                k.setTransitions(transitions);
                                                k.setSubmachines(submachines);
                                                k.setCurrentSubmachine(currentSubmachine);
                                                k.setMainSubmachine(mainSubmachine);
                                                k.setInput(input);
                                                k.setCursor(cursor);
                                                k.setEnablePriorAction(false);
                                                k.setTransition(t);

                                                // adiciona o caminho de reconhecimento
                                                // para as novas threads
                                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                                threads.add(k);

                                                // mensagem de log
                                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                            }
                                            
                                            // adiciona a thread na
                                            // lista de remoções
                                            removals.add(identifier);

                                            // mensagem de log
                                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                            // e retorna, encerrando a
                                            // execução da thread corrente
                                            return;

                                        }
                                    }
                                } else {

                                    // existem apenas transições em vazio
                                    
                                    // só tem uma transição?
                                    if (query.size() == 1) {

                                        // sim, uma única transição em
                                        // vazio, portanto, o passo é
                                        // determinístico
                                        transition = dolly.deepClone(query.get(0));

                                        // mensagem de log
                                        logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição em vazio.", identifier));

                                    } else {
                                        
                                        // existem várias transições em vazio

                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições em vazio.", identifier));

                                        // situação de não-determinismo
                                        for (Transition t : query) {

                                            // para cada transição da consulta, criar uma nova thread
                                            Kernel k = new Kernel(threads, removals, paths);
                                            k.setStack(stack);
                                            k.setActions(actions);
                                            k.setTransitions(transitions);
                                            k.setSubmachines(submachines);
                                            k.setCurrentSubmachine(currentSubmachine);
                                            k.setMainSubmachine(mainSubmachine);
                                            k.setInput(input);
                                            k.setCursor(cursor);
                                            k.setEnablePriorAction(false);
                                            k.setTransition(t);

                                            // adiciona o caminho de reconhecimento
                                            // para as novas threads
                                            paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                            paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                            threads.add(k);

                                            // mensagem de log
                                            logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                        }
                                        
                                        // adiciona a thread na
                                        // lista de remoções
                                        removals.add(identifier);

                                        // mensagem de log
                                        logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                        // e retorna, encerrando a
                                        // thread corrente
                                        return;

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // fim do tratamento da ação anterior
        
        // início da execução da transição
        // propriamente dita
        
        // o estado corrente passa
        // a ser nulo
        br.usp.poli.lta.cereda.aa.model.State currentState = null;

        // a transição corrente é de desempilhamento,
        // ou seja, é de retorno de submáquina?
        if (transition.isSubmachineReturn()) {

            // sim, é uma transição de retorno
            // de submáquina

            // mensagem de log
            logger.debug(new SimpleMessage("[Thread %d] A transição é de retorno de submáquina. Conteúdo da pilha: %s", identifier, stack));

            // a pilha não está vazia?
            if (!stack.isEmpty()) {
                
                // sim, a pilha não está vazia

                // o estado corrente recebe o topo da pilha
                // e a submáquina corrente é atualizada
                currentState = stack.pop();
                currentSubmachine = submachines.getFromState(currentState).getName();

                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] Submáquina corrente: %s", identifier, submachines.getFromState(currentState)));

            } else {

                // retorno de submáquina com a pilha vazia,
                // de alguma forma, ocorreu algum erro grave
                // de conceito

                // lança exceção
                Validate.isTrue(false, "Não posso retornar com uma pilha vazia.");
            }
        } else {

            // não é uma operação de desempilhamento, isto é, de retorno
            // de submáquina, portanto, a transição pode ser de consumo
            // de símbolo, transição em vazio ou chamada de submáquina
            
            // a transição é de consumo de símbolo?
            if (transition.isSymbolConsumptionTransition()) {

                // sim, consumo de símbolo
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] A transição é de consumo do símbolo '%s'.", identifier, String.valueOf(transition.getSymbol())));
                
                // o estado corrente recebe estado de destino da
                // transição e cursor é incrementado
                currentState = transition.getTargetState();
                cursor++;
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] O novo estado é '%s' (cursor na posição %d).", identifier, String.valueOf(currentState), cursor));

            } else {

                // a transição pode ser uma chamada de submáquina
                // ou transição em vazio
                
                // a transição é uma chamada de submáquina?
                if (transition.isSubmachineCall()) {

                    // sim, chamada de submáquina
                    
                    // empilha o estado de retorno, define estado o corrente
                    // de acordo com o estado inicial da submáquina chamada e
                    // atualiza a referência à submáquina corrente
                    stack.push(transition.getTargetState());
                    currentSubmachine = transition.getSubmachineCall();
                    currentState = submachines.getFromName(currentSubmachine).getInitialState();

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A transição é de chamada da submáquina '%s'. Novo conteúdo da pilha: %s", identifier, String.valueOf(transition.getSubmachineCall()), stack));
                    logger.debug(new SimpleMessage("[Thread %d] Submáquina corrente: %s", identifier, submachines.getFromName(currentSubmachine)));

                } else {

                    // só nos resta transição em vazio
                    
                    // o estado corrente passa a ser o estado
                    // de destino da transição, e não há consumo
                    // de símbolo
                    currentState = transition.getTargetState();

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A transição é em vazio.", identifier));

                }
            }
        }

        // existe ação posterior associada à transição corrente?
        if (transition.hasPostActionCall()) {
            
            // sim, existe

            // mensagem de log
            logger.debug(new TransitionMessage(transition, "[Thread %d] Executando a ação posterior da transição %d.", identifier, transition.getIdentifier()));

            // executa a ação posterior após a efetivação da transição
            actions.fromName(transition.getPostActionCall()).execute(transitions, transition, transition.getPostActionArguments());
        }

        // define o passo computacional como verdadeiro, uma vez que
        // a transição pôde ser completada com sucesso
        enablePriorAction = true;

        // fim do tratamento da aplicação da transição corrente e
        // eventual execução da ação posterior
        
        // início do cálculo da nova transição
        
        // o valor do cursor é igual ao tamanho da cadeia?
        if (cursor == input.size()) {

            // sim, são iguais, portanto não
            // há mais nada para consumir
            
            // mensagem de log
            logger.debug(new SimpleMessage("[Thread %d] Não há mais símbolos para consumir.", identifier));
            
            // consulta quais transições partem do estado corrente
            // transitando em vazio ou fazendo chamadas de submáquinas
            query = transitions.withEpsilonOrSubmachineFromSourceState(currentState);

            // a consulta está vazia?
            if (query.isEmpty()) {

                // sim, a consulta está vazia,
                // não temos para onde ir
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] Não existem transições em vazio ou chamadas de submáquinas.", identifier));
                
                // o estado corrente é final no contexto da submáquina corrente?
                if (submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {
                    
                    // sim, o estado corrente é final no
                    // contexto da submáquina corrente

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final na submáquina corrente.", identifier));

                    // a pilha está vazia?
                    if (stack.isEmpty()) {

                        // sim, pilha vazia
                        
                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));
                        
                        // a submáquina corrente é a principal?
                        if (currentSubmachine.equals(mainSubmachine)) {                          

                            // sim, indicando que a cadeia foi aceita
                            // pelo autômato adaptativo
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente é a principal.", identifier));
                            
                            // adiciona o resultado do reconhecimento
                            // no caminho de reconhecimento
                            paths.get(identifier).setResult(true);
                            
                            // define a posição final do cursor da
                            // cadeia de entrada
                            paths.get(identifier).setCursor(cursor);
                            
                            // define o estado final do processo
                            // de reconhecimento
                            paths.get(identifier).setState(dolly.deepClone(currentState));

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] A cadeia foi aceita.", identifier));

                            // informa que a thread pode ser removida,
                            // uma vez que o reconhecimento acabou
                            removals.add(identifier);

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                            // retorna, encerrando a thread corrente,
                            // uma vez que a cadeia já foi aceita
                            return;

                        } else {

                            // submáquina corrente não é a principal,
                            // portanto a cadeia deve ser rejeitada
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente não é a principal.", identifier));

                            // adiciona o resultado do reconhecimento
                            // no caminho de reconhecimento
                            paths.get(identifier).setResult(false);
                            
                            // define a posição final do cursor da
                            // cadeia de entrada
                            paths.get(identifier).setCursor(cursor);
                            
                            // define o estado final do processo
                            // de reconhecimento
                            paths.get(identifier).setState(dolly.deepClone(currentState));

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                            // informa que a thread
                            // corrente pode ser removida
                            removals.add(identifier);

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                            // retorna, encerrando a thread corrente,
                            // uma vez que a cadeia já foi rejeitada
                            return;

                        }
                    } else {

                        // a pilha não está vazia, portanto, define a transição
                        // corrente como uma operação de desempilhamento, isto é,
                        // uma operação de retorno de submáquina
                        transition = new Transition();
                        transition.setSubmachineReturn(true);

                        // mensagem de log
                        logger.debug(new TransitionMessage(transition, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, transition.getIdentifier()));

                    }
                } else {
                    
                    // o estado corrente não é final no contexto da
                    // submáquina corrente, portanto, a cadeia deve
                    // ser rejeitada

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final na submáquina corrente.", identifier));

                    // adiciona o resultado de reconhecimento
                    // no caminho de reconhecimento
                    paths.get(identifier).setResult(false);
                    
                    // define a posição final do cursor da
                    // cadeia de entrada
                    paths.get(identifier).setCursor(cursor);
                    
                    // define o estado final do processo
                    // de reconhecimento
                    paths.get(identifier).setState(dolly.deepClone(currentState));

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                    // informa que a thread pode ser removida
                    removals.add(identifier);

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                    // retorna, encerrando a thread, uma vez
                    // que a cadeia já foi rejeitada
                    return;

                }

            } else {

                // a consulta não está vazia, existem transições
                // em vazio ou chamadas de submáquinas
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] A consulta não está vazia.", identifier));

                // a consulta tem transições em vazio e
                // chamadas de submáquinas?
                if (hasEpsilonTransitions(query) && hasSubmachineCalls(query)) {
                    
                    // sim, a consulta tem transições em
                    // vazio e chamadas de submáquinas

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] Existem transições em vazio e chamadas de submáquina.", identifier));

                    // situação de não-determinismo
                    for (Transition t : query) {

                        // para cada transição da consulta, criar uma nova thread
                        Kernel k = new Kernel(threads, removals, paths);
                        k.setStack(stack);
                        k.setActions(actions);
                        k.setTransitions(transitions);
                        k.setSubmachines(submachines);
                        k.setCurrentSubmachine(currentSubmachine);
                        k.setMainSubmachine(mainSubmachine);
                        k.setInput(input);
                        k.setCursor(cursor);
                        k.setEnablePriorAction(true);
                        k.setTransition(t);

                        // adiciona o caminho de reconhecimento
                        // para as novas threads
                        paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                        paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                        threads.add(k);

                        // mensagem de log
                        logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                    }
                    
                    // adiciona a thread corrente
                    // na lista de remoções
                    removals.add(identifier);

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                    // e retorna, encerrando a
                    // thread corrente
                    return;

                } else {
                    
                    // existem chamadas de submáquinas ou
                    // transições em vazio (de forma
                    // exclusiva)

                    // a consulta tem chamadas de
                    // submáquinas?
                    if (hasSubmachineCalls(query)) {

                        // sim, tem chamadas de submáquinas
                        
                        // é uma transição só?
                        if (query.size() == 1) {

                            // sim, define a transição corrente
                            // como resultado da consulta
                            transition = dolly.deepClone(query.get(0));

                            // mensagem de log
                            logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma chamada de submáquina.", identifier));

                        } else {

                            // são várias chamadas de submáquinas
                            
                            // mensagem de log
                            logger.debug(new TransitionMessage(transition, "[Thread %d] São várias chamadas de submáquina.", identifier));

                            // situação de não-determinismo
                            for (Transition t : query) {

                                // para cada transição da consulta, criar uma nova thread
                                Kernel k = new Kernel(threads, removals, paths);
                                k.setStack(stack);
                                k.setActions(actions);
                                k.setTransitions(transitions);
                                k.setSubmachines(submachines);
                                k.setCurrentSubmachine(currentSubmachine);
                                k.setMainSubmachine(mainSubmachine);
                                k.setInput(input);
                                k.setCursor(cursor);
                                k.setEnablePriorAction(true);
                                k.setTransition(t);

                                // adiciona o caminho de reconhecimento
                                // para as novas threads
                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                threads.add(k);

                                // mensagem de log
                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                            }
                            
                            // adiciona a thread corrente
                            // na lista de remoção
                            removals.add(identifier);

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                            // e retorna, encerrando a
                            // execução da thread corrente
                            return;

                        }
                    } else {

                        // a consulta tem transições em vazio
                        // (mas não tem chamadas de submáquinas)
                        
                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] A consulta tem transições em vazio.", identifier));

                        // o estado corrente não é final no contexto da
                        // submáquina corrente?
                        if (!submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {

                            // sim, o estado é comum, não final, no
                            // contexto da submáquina corrente
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final no contexto da submáquina corrente.", identifier));

                            // é uma transição só?
                            if (query.size() == 1) {

                                // sim, portanto a transição é determinística
                                
                                // define o transição corrente como
                                // o resultado da consulta
                                transition = dolly.deepClone(query.get(0));

                                // mensagem de log
                                logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição em vazio.", identifier));

                            } else {

                                // várias transições em vazio, passo
                                // não-determinístico
                                logger.debug(new TransitionMessage(transition, "[Thread %d] São várias transições em vazio.", identifier));

                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            }
                        } else {

                            // o estado corrente é final no contexto da
                            // submáquina corrente e existem transições em vazio
                            
                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final na submáquina corrente.", identifier));

                            // a pilha está vazia?
                            if (stack.isEmpty()) {

                                // sim, pilha vazia
                                logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));

                                // a submáquina corrente é a principal?
                                if (currentSubmachine.equals(mainSubmachine)) {

                                    // sim, é a submáquina principal
                                    
                                    // o passo é não-determinístico, pois o autômato
                                    // pode encerrar o reconhecimento aceitando a
                                    // cadeia ou continuar na transição em vazio
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente é a principal.", identifier));
                                    
                                    // situação de não-determinismo
                                    for (Transition t : query) {

                                        // para cada transição da consulta, criar uma nova thread
                                        Kernel k = new Kernel(threads, removals, paths);
                                        k.setStack(stack);
                                        k.setActions(actions);
                                        k.setTransitions(transitions);
                                        k.setSubmachines(submachines);
                                        k.setCurrentSubmachine(currentSubmachine);
                                        k.setMainSubmachine(mainSubmachine);
                                        k.setInput(input);
                                        k.setCursor(cursor);
                                        k.setEnablePriorAction(true);
                                        k.setTransition(t);

                                        // adiciona o caminho de reconhecimento
                                        // para as novas threads
                                        paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                        paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                        threads.add(k);

                                        // mensagem de log
                                        logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                    }

                                    // adiciona um caminho adicional informando
                                    // que a cadeia foi aceita pelo autômato
                                    paths.get(identifier).setResult(true);
                                    
                                    // define a posição final do cursor da
                                    // cadeia de entrada
                                    paths.get(identifier).setCursor(cursor);
                                    
                                    // define o estado final do processo
                                    // de reconhecimento
                                    paths.get(identifier).setState(dolly.deepClone(currentState));

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A cadeia foi aceita.", identifier));

                                    // adiciona a thread na
                                    // lista de remoção
                                    removals.add(identifier);

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                    // e retorna, encerrando a thread
                                    return;

                                } else {

                                    // a submáquina corrente não
                                    // é a principal
                                    
                                    // não-determinismo, pois o autômato
                                    // pode prosseguir com o reconhecimento
                                    // ou rejeitar a cadeia
                                    
                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A submáquina corrente não é a principal.", identifier));

                                    // situação de não-determinismo
                                    for (Transition t : query) {

                                        // para cada transição da consulta, criar uma nova thread
                                        Kernel k = new Kernel(threads, removals, paths);
                                        k.setStack(stack);
                                        k.setActions(actions);
                                        k.setTransitions(transitions);
                                        k.setSubmachines(submachines);
                                        k.setCurrentSubmachine(currentSubmachine);
                                        k.setMainSubmachine(mainSubmachine);
                                        k.setInput(input);
                                        k.setCursor(cursor);
                                        k.setEnablePriorAction(true);
                                        k.setTransition(t);

                                        // adiciona o caminho de reconhecimento
                                        // para as novas threads
                                        paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                        paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                        threads.add(k);

                                        // mensagem de log
                                        logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                    }

                                    // informa que a cadeia foi
                                    // rejeitada
                                    paths.get(identifier).setResult(false);
                                    
                                    // define a posição final do cursor da
                                    // cadeia de entrada
                                    paths.get(identifier).setCursor(cursor);
                                    
                                    // define o estado final do processo
                                    // de reconhecimento
                                    paths.get(identifier).setState(dolly.deepClone(currentState));

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                                    // adiciona a thread corrente
                                    // na lista de remoção
                                    removals.add(identifier);

                                    // mensagem de log
                                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                    // e retorna, encerrando
                                    // a thread corrente
                                    return;

                                }
                            } else {

                                // a pilha não está vazia, portanto,
                                // existe uma operação de desempilhamento,
                                // isto é, de retorno de submáquina
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] A pilha não está vazia.", identifier));

                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }

                                // cria uma thread adicional para tratar
                                // do retorno de submáquina (operação de
                                // desempilhamento)
                                Kernel k = new Kernel(threads, removals, paths);
                                k.setStack(stack);
                                k.setActions(actions);
                                k.setTransitions(transitions);
                                k.setSubmachines(submachines);
                                k.setCurrentSubmachine(currentSubmachine);
                                k.setMainSubmachine(mainSubmachine);
                                k.setInput(input);
                                k.setCursor(cursor);
                                k.setEnablePriorAction(true);
                                Transition t = new Transition();
                                t.setSubmachineReturn(true);
                                k.setTransition(t);

                                // adiciona o caminho de reconhecimento
                                // para a nova thread
                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                threads.add(k);

                                // mensagem de log
                                logger.debug(new TransitionMessage(t, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, t.getIdentifier()));

                                // adiciona thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            }
                        }
                    }
                }
            }
        } else {

            // ainda existem símbolos a serem lidos
            
            // lê símbolo apontado pelo cursor
            Symbol currentSymbol = input.get(cursor);

            // todas as transições que, partindo do estado corrente, consomem
            // o símbolo corrente, transitam em vazio ou chamam submáquinas
            query = transitions.withSymbolEpsilonOrSubmachineFromSourceState(currentState, currentSymbol);

            // a consulta está vazia?
            if (query.isEmpty()) {
                
                // sim, consulta vazia

                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] Não existem transições com consumo de símbolo, em vazio ou chamadas de submáquinas.", identifier));

                // a pilha está vazia?
                if (stack.isEmpty()) {
                    
                    // sim, pilha vazia

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A pilha está vazia.", identifier));
                   
                    // não há para onde ir, a cadeia
                    // foi rejeitada
                    paths.get(identifier).setResult(false);
                    
                    // define a posição final do cursor da
                    // cadeia de entrada
                    paths.get(identifier).setCursor(cursor);
                    
                    // define o estado final do processo
                    // de reconhecimento
                    paths.get(identifier).setState(dolly.deepClone(currentState));
                    
                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                    // informa que a thread
                    // corrente pode ser removida
                    removals.add(identifier);

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                    // retorna, encerrando
                    // a thread corrente
                    return;

                } else {

                    // a pilha não está vazia
                    
                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A pilha não está vazia.", identifier));

                    // o estado corrente é final no contexto da submáquina corrente?
                    if (submachines.getFromName(currentSubmachine).getAcceptingStates().contains(currentState)) {
                        
                        // sim, o estado é final no contexto da
                        // submáquina corrente

                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] O estado corrente é final na submáquina corrente.", identifier));

                        // sim, o estado é final no contexto
                        // da submáquina corrente                        
                        
                        // define a transição corrente como uma operação
                        // de desempilhamento, isto é, de retorno de
                        // submáquina
                        transition = new Transition();
                        transition.setSubmachineReturn(true);

                        // mensagem de log
                        logger.debug(new TransitionMessage(transition, "[Thread %d] A pilha não está vazia. A nova transição %d é um retorno de submáquina.", identifier, transition.getIdentifier()));

                    } else {

                        // o estado corrente não é final no contexto
                        // da submáquina corrente
                        
                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] O estado corrente não é final na submáquina corrente.", identifier));

                        // não há para onde ir, a cadeia é
                        // então rejeitada
                        paths.get(identifier).setResult(false);
                        
                        // define a posição final do cursor da
                        // cadeia de entrada
                        paths.get(identifier).setCursor(cursor);
                        
                        // define o estado final do processo
                        // de reconhecimento
                        paths.get(identifier).setState(dolly.deepClone(currentState));

                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] A cadeia foi rejeitada.", identifier));

                        // informa que a thread pode ser removida
                        removals.add(identifier);

                        // mensagem de log
                        logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                        // retorna, encerrando a operação
                        // da thread corrente
                        return;

                    }
                }
            } else {

                // a consulta não está vazia, portanto
                // existem transições
                
                // mensagem de log
                logger.debug(new SimpleMessage("[Thread %d] A consulta não está vazia.", identifier));

                // a consulta tem consumo de símbolos?
                if (hasSymbolTransitions(query)) {
                    
                    // sim, a consulta tem consumo de símbolos

                    // mensagem de log
                    logger.debug(new SimpleMessage("[Thread %d] A consulta possui transições com consumo de símbolo.", identifier));

                    // apenas uma transição de consumo de símbolos?
                    if (getSymbolTransitions(query).size() == 1) {
                        
                        // sim, apenas uma transição de consumo
                        // de símbolo

                        // a consulta tem transições em vazio?
                        if (hasEpsilonTransitions(query)) {

                            // sim, tem transições em vazio
                            
                            // a consulta tem chamadas de submáquinas?
                            if (hasSubmachineCalls(query)) {

                                // sim, tem chamadas de submáquinas
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo, transições em vazio e chamadas de submáquinas.", identifier));

                                // não-determinismo, com uma transição de consumo
                                // de símbolo, transições em vazio e chamadas de
                                // submáquinas
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            } else {

                                // não há chamadas de submáquinas
                                
                                // não-determinismo, com uma transição de consumo
                                // de símbolo e transições em vazio
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo e transições em vazio.", identifier));

                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            }
                        } else {

                            // a consulta tem uma transição de consumo de
                            // símbolo, mas não tem transições em vazio
                            
                            // a consulta tem chamadas de submáquinas?
                            if (hasSubmachineCalls(query)) {

                                // sim, tem chamadas de submáquinas
                                
                                // mensagem
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma transição de consumo de símbolo e chamadas de submáquinas.", identifier));

                                // não-determinismo, com uma transição de consumo
                                // de símbolo e chamadas de submáquinas
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            } else {
                                
                                // não existem transições com chamadas
                                // de submáquinas, existe apenas uma
                                // transição com consumo de símbolo,
                                // portanto, é determinístico
                                transition = dolly.deepClone(query.get(0));

                                // mensagem de log
                                logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição de consumo de símbolo.", identifier));

                            }
                        }
                    } else {

                        // existem várias transições de consumo de símbolo,
                        // portanto o passo já será não-determinístico
                        
                        // a consulta tem transições em vazio?
                        if (hasEpsilonTransitions(query)) {

                            // sim, tem transições em vazio
                            
                            // a consulta tem chamadas de submáquinas?
                            if (hasSubmachineCalls(query)) {

                                // sim, a consulta tem
                                // chamadas de submáquinas
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo, transições em vazio e chamadas de submáquinas.", identifier));

                                // não-determinismo com transições de consumo
                                // de símbolo, transições em vazio e chamadas
                                // de submáquinas
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;
                                
                            } else {

                                // não existem chamadas de submáquina,
                                // apenas transições em vazio e com
                                // consumo de símbolo
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo e transições em vazio.", identifier));

                                // não-determinismo com transições de consumo
                                // de símbolo e transições em vazio
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            }
                        } else {

                            // não há transições em vazio
                            
                            // a consulta tem chamadas de submáquinas?
                            if (hasSubmachineCalls(query)) {

                                // sim, tem chamadas de submáquinas
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo e chamadas de submáquinas.", identifier));

                                // não-determinismo com transições com consumo
                                // de símbolo e chamadas de submáquinas
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando a
                                // thread corrente
                                return;

                            } else {

                                // não há chamadas de submáquinas,
                                // apenas transições com consumo
                                // de símbolo
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com transições de consumo de símbolo.", identifier));

                                // não determinismo com transições com
                                // consumo de símbolo
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando a
                                // thread corrente
                                return;

                            }
                        }
                    }
                } else {

                    // a consulta não tem transições com consumo
                    // de símbolos, apenas transições em vazio ou
                    // chamadas de submáquinas
                    
                    // a consulta tem chamadas de submáquinas?
                    if (hasSubmachineCalls(query)) {

                        // sim, tem chamadas de submáquinas
                        
                        // é uma transição só?
                        if (getSubmachineCalls(query).size() == 1) {

                            // sim é uma só chamada de submáquina
                            
                            // a consulta tem transições em vazio?
                            if (hasEpsilonTransitions(query)) {

                                // sim, tem transições em vazio
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com uma chamada de submáquina e transições em vazio.", identifier));

                                // não-determinismo com uma chamada de
                                // submáquina e transições em vazio
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoção
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            } else {
                                
                                // não há transições em vazio

                                // só existe uma chamada de submáquina, portanto
                                // o passo é determinístico
                                transition = dolly.deepClone(query.get(0));

                                // mensagem de log
                                logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma chamada de submáquina.", identifier));

                            }
                        } else {

                            // são várias chamadas de submáquinas, portanto o
                            // passo já será não-determinístico
                            
                            // a consulta tem transições em vazio?
                            if (hasEpsilonTransitions(query)) {

                                // sim, tem transições em vazio
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Situação de não-determinismo, com chamadas de submáquinas e transições em vazio.", identifier));

                                // não-determinismo com chamadas de submáquinas
                                // e transições em vazio
                                
                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando
                                // a thread corrente
                                return;

                            } else {
                                
                                // não tem transições em vazio

                                // não-determinismo, com chamadas de submáquinas
                                
                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] São várias chamadas de submáquinas.", identifier));

                                // situação de não-determinismo
                                for (Transition t : query) {

                                    // para cada transição da consulta, criar uma nova thread
                                    Kernel k = new Kernel(threads, removals, paths);
                                    k.setStack(stack);
                                    k.setActions(actions);
                                    k.setTransitions(transitions);
                                    k.setSubmachines(submachines);
                                    k.setCurrentSubmachine(currentSubmachine);
                                    k.setMainSubmachine(mainSubmachine);
                                    k.setInput(input);
                                    k.setCursor(cursor);
                                    k.setEnablePriorAction(true);
                                    k.setTransition(t);

                                    // adiciona o caminho de reconhecimento
                                    // para as novas threads
                                    paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                    paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                    threads.add(k);

                                    // mensagem de log
                                    logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                                }
                                
                                // adiciona a thread corrente
                                // na lista de remoções
                                removals.add(identifier);

                                // mensagem de log
                                logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                                // e retorna, encerrando a
                                // thread corrente
                                return;

                            }
                        }
                    } else {

                        // existem apenas transições em vazio
                        
                        // só tem uma transição?
                        if (query.size() == 1) {

                            // sim, uma única transição em vazio, portanto o
                            // passo é determinístico
                            transition = dolly.deepClone(query.get(0));

                            // mensagem de log
                            logger.debug(new TransitionMessage(transition, "[Thread %d] É apenas uma transição em vazio.", identifier));

                        } else {

                            // existem várias transições em vazio
                            
                            // mensagem de log
                            logger.debug(new TransitionMessage(transition, "[Thread %d] São várias transições em vazio.", identifier));

                            // situação de não-determinismo
                            for (Transition t : query) {

                                // para cada transição da consulta, criar uma nova thread
                                Kernel k = new Kernel(threads, removals, paths);
                                k.setStack(stack);
                                k.setActions(actions);
                                k.setTransitions(transitions);
                                k.setSubmachines(submachines);
                                k.setCurrentSubmachine(currentSubmachine);
                                k.setMainSubmachine(mainSubmachine);
                                k.setInput(input);
                                k.setCursor(cursor);
                                k.setEnablePriorAction(true);
                                k.setTransition(t);

                                // adiciona o caminho de reconhecimento
                                // para as novas threads
                                paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
                                paths.get(k.getIdentifier()).addPath(String.valueOf(t));
                                threads.add(k);

                                // mensagem de log
                                logger.debug(new TransitionMessage(t, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), t.getIdentifier()));

                            }
                            
                            // adiciona a thread corrente
                            // na lista de remoções
                            removals.add(identifier);

                            // mensagem de log
                            logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));

                            // e retorna, encerrando
                            // a thread corrente
                            return;

                        }
                    }
                }
            }
        }

        // fim do cálculo da nova transição
        
        // uma vez que a thread corrente chegou ao fim e esta será encerrada,
        // é necessário criar uma nova thread para tratar do próximo passo
        // computacional
        
        // cria uma nova thread
        Kernel k = new Kernel(threads, removals, paths);
        k.setStack(stack);
        k.setActions(actions);
        k.setTransitions(transitions);
        k.setSubmachines(submachines);
        k.setCurrentSubmachine(currentSubmachine);
        k.setMainSubmachine(mainSubmachine);
        k.setInput(input);
        k.setCursor(cursor);
        k.setEnablePriorAction(true);
        k.setTransition(transition);

        // adiciona o caminho de reconhecimento para a nova thread
        paths.put(k.getIdentifier(), dolly.deepClone(paths.get(identifier)));
        paths.get(k.getIdentifier()).addPath(String.valueOf(transition));
        threads.add(k);

        // mensagem de log
        logger.debug(new TransitionMessage(transition, "[Thread %d] Adicionando uma nova thread %d para tratar da transição %d.", identifier, k.getIdentifier(), transition.getIdentifier()));

        // adiciona a thread corrente
        // na lista de remoções
        removals.add(identifier);

        // mensagem de log
        logger.debug(new SimpleMessage("[Thread %d] Esta thread foi marcada para remoção.", identifier));
        
        // término da execução do passo computacional
    }

    /**
     * Verifica se a consulta tem chamadas de submáquina.
     * @param query Consulta.
     * @return Valor lógico indicando se a consulta tem chamadas de submáquina.
     */
    private boolean hasSubmachineCalls(List<Transition> query) {
        return !getSubmachineCalls(query).isEmpty();
    }

    /**
     * Obtém as transições que possuem chamadas de submáquina a partir da
     * consulta corrente.
     * @param query Consulta.
     * @return Lista de transições.
     */
    private List<Transition> getSubmachineCalls(List<Transition> query) {
        Collection<Transition> result = CollectionUtils.select(query, new SubmachineCallPredicate());
        return new ArrayList<>(result);
    }

    /**
     * Verifica se a consulta tem transições de consumo de símbolo.
     * @param query Consulta.
     * @return Valor lógico indicando se a consulta tem transições de consumo de
     * símbolo.
     */
    private boolean hasSymbolTransitions(List<Transition> query) {
        return !getSymbolTransitions(query).isEmpty();
    }

    /**
     * Obtém as transições com consumo de símbolo a partir da consulta corrente.
     * @param query Consulta.
     * @return Lista de transições.
     */
    private List<Transition> getSymbolTransitions(List<Transition> query) {
        Predicate[] chain = new Predicate[]{new EpsilonPredicate(), new SubmachineCallPredicate()};
        Collection<Transition> result = CollectionUtils.select(query, PredicateUtils.nonePredicate(chain));
        return new ArrayList<>(result);
    }

    /**
     * Verifica se a consulta tem transições em vazio.
     * @param query Consulta.
     * @return Valor lógico indicando se a consulta tem transições em vazio.
     */
    private boolean hasEpsilonTransitions(List<Transition> query) {
        return !getEpsilonTransitions(query).isEmpty();
    }

    /**
     * Obtém as transições em vazio a partir da consulta corrente.
     * @param query Consulta.
     * @return Lista de transições.
     */
    private List<Transition> getEpsilonTransitions(List<Transition> query) {
        Collection<Transition> result = CollectionUtils.select(query, new EpsilonPredicate());
        return new ArrayList<>(result);
    }

}
