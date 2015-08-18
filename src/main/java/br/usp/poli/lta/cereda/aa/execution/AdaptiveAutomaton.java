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

import br.usp.poli.lta.cereda.aa.model.Stack;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.sets.ActionsSet;
import br.usp.poli.lta.cereda.aa.model.sets.SubmachinesSet;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.utils.RecognitionPath;
import com.rits.cloning.Cloner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;

/**
 * Implementa o autômato adaptativo propriamente dito. Esta classe é definida
 * como abstrata, forçando a redefinição do método de especificação das
 * transições, submáquinas e ações.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public abstract class AdaptiveAutomaton {
    
    // atributos de classe
    
    // elementos a partir da definição formal: conjunto de transições, conjunto
    // de submáquinas, conjunto de ações e pilha
    protected Mapping transitions;
    protected SubmachinesSet submachines;
    protected ActionsSet actions;
    protected Stack stack;
    
    // referência ao nome da submáquina principal e definição de uma constante
    // que representa a transição em vazio, caso ela seja necessária em uma
    // transição
    private String mainSubmachine;
    public Symbol EPSILON;
    
    // lista contendo os símbolos, representando a cadeia de entrada a ser
    // submetida ao autômato adaptativo
    private List<Symbol> input;

    // lista de threads representando cada passo computacional do
    // autômato, lista de remoção de threads e mapa de caminhos de
    // recomendação
    private final List<Kernel> threads;
    private final List<Integer> removals;
    private final Map<Integer, RecognitionPath> paths;
    
    // variável que determina se o autômato deve parar ao 
    // encerrar, pelo menos, uma execução obtendo resultado
    private boolean stopAtFirstFinishedRecognitionPath;
    
    // objeto que representa uma referência ao autômato
    // adaptativo corrente
    private AdaptiveAutomaton reference;
        
    /**
     * Construtor.
     */
    public AdaptiveAutomaton() {
        
        // define novos conjuntos
        transitions = new Mapping();
        submachines = new SubmachinesSet();
        actions = new ActionsSet();
        stack = new Stack();
        
        // inicializa as variáveis
        mainSubmachine = null;
        EPSILON = null;
        
        // cria nova listas auxiliares
        threads = new ArrayList<>();
        removals = new ArrayList<>();
        paths = new HashMap<>();
        
        // define que, inicialmente, o autômato só interromperá o processo de
        // reconhecimento quando todas as instâncias encerrarem-se
        stopAtFirstFinishedRecognitionPath = false;
        
        // define a referência inicial do autômato adaptativo
        reference = null;
            
    }
    
    /**
     * Define a configuração inicial do autômato adaptativo. Este método é
     * abstrato e deverá ser implementado.
     */
    public abstract void setup();

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
        this.submachines = submachines;
    }

    /**
     * Define o conjunto de ações do autômato adaptativo.
     * @param actions Conjunto de ações do autômato adaptativo.
     */
    public void setActions(ActionsSet actions) {
        this.actions = actions;
    }

    /**
     * Define a submáquina principal.
     * @param mainSubmachine Nome da submáquina principal.
     */
    public void setMainSubmachine(String mainSubmachine) {
        this.mainSubmachine = mainSubmachine;
    }

    /**
     * Obtém o mapa contendo os caminhos de reconhecimento.
     * @return Mapa contendo os caminhos de reconhecimento.
     */
    public Map<Integer, RecognitionPath> getRecognitionMap() {
        return reference.getRecognitionMapOnce();
    }
    
    /**
     * Obtém, apenas uma vez, o mapa contendo os caminhos de reconhecimento.
     * @return Mapa contendo os caminhos de reconhecimento.
     */
    private Map<Integer, RecognitionPath> getRecognitionMapOnce() {
        return paths;
    }
    
    /**
     * Obtém a lista contendo todos os caminhos de reconhecimento obtidos
     * durante a execução do autômato adaptativo.
     * @return Lista de todos os caminhos de reconhecimento.
     */
    public List<RecognitionPath> getRecognitionPaths() {
        return reference.getRecognitionPathsOnce();
    }
    
    /**
     * Obtém, apenas uma vez, a lista contendo todos os caminhos de
     * reconhecimento obtidos
     * durante a execução do autômato adaptativo.
     * @return Lista de todos os caminhos de reconhecimento.
     */
    private List<RecognitionPath> getRecognitionPathsOnce() {
        List<RecognitionPath> result = new ArrayList<>();
        for (int key : paths.keySet()) {
            result.add(paths.get(key));
        }
        return result;  
    }
    
    /**
     * Reconhece uma lista de símbolos representado a cadeia de entrada.
     * @param input Lista de símbolos representando a cadeia de entrada.
     * @return Um valor lógico informando se o autômato adaptativo reconheceu
     * a cadeia de entrada.
     */
    public boolean recognize(List<Symbol> input) {
        
        // cria um objeto de clonagem para realizar
        // a cópia do autômato adaptativo corrente
        Cloner dolly = new Cloner();
        
        // cria um clone do autômato corrente
        reference = dolly.deepClone(this);
        
        // inicia o processo de reconhecimento da lista de
        // símbolos, retornando o resultado
        return reference.recognizeOnce(input);
    }
    
    /**
     * Reconhece, apenas uma vez, uma lista de símbolos representado a cadeia
     * de entrada.
     * @param input Lista de símbolos representando a cadeia de entrada.
     * @return Um valor lógico informando se o autômato adaptativo reconheceu
     * a cadeia de entrada.
     */
    private boolean recognizeOnce(List<Symbol> input) {
        
        // realiza a configuração e verifica se a submáquina
        // principal não é nula
        setup();
        Validate.notNull(
                mainSubmachine,
                "A submáquina principal não pode ser nula."
        );
        
        // cria a thread inicial com os conjuntos
        // do modelo
        Kernel k = new Kernel(threads, removals, paths);
        k.setStack(stack);
        k.setActions(actions);
        k.setTransitions(transitions);
        k.setSubmachines(submachines);
        k.setCurrentSubmachine(mainSubmachine);
        k.setMainSubmachine(mainSubmachine);
        k.setInput(input);
        k.setCursor(0);
        k.setEnablePriorAction(true);
        
        // cria uma transição inicial em vazio que faz o autômato
        // entrar no estado inicial da submáquina principal e ajusta
        // o cursor de leitura no início da cadeia
        Transition t = new Transition();
        t.setTransition(
                null,
                EPSILON,
                submachines.getFromName(mainSubmachine).getInitialState()
        );
        k.setTransition(t);

        // cria um novo caminho de reconhecimento e
        // adiciona a thread inicial na lista de threads
        paths.put(k.getIdentifier(), new RecognitionPath());
        threads.add(k);
        
        // enquanto a lista de threads não estiver vazia e o autômato
        // adaptativo não retornou alguma saída em relação ao processo
        // de reconhecimento, repete o passo computacional
        while (!threads.isEmpty() && !atLeastOneRecognitionPathIsDone()) {
            
            // verifica se existem threads que já terminaram
            // e devem ser removidas
            if (!removals.isEmpty()) {
                
                // para cada identificador das threads a serem removidas,
                // percorre a lista de threads e remove aquelas cujo
                // identificador é igual
                for (int i : removals) {
                    for (int j = 0; j < threads.size(); j++) {
                        if (threads.get(j).getIdentifier() == i) {
                            threads.remove(j);
                            break;
                        }
                    }
                    
                    // repete a operação no mapa de caminhos de
                    // reconhecimento, mas apenas os reconhecimentos
                    // incompletos são removidos
                    if (!paths.get(i).done()) {
                        paths.remove(i);
                    }
                }
                
                // todas as threads marcadas para remoção foram
                // devidamente removidas, portanto, agora a lista
                // de remoção deve ser limpa
                removals.clear();
                
            }
            
            // executa um passo computacional para cada thread
            // da lista de threads
            for (int i = 0; i < threads.size(); i++) {
            
                // tenta executar, talvez a thread dê erro
                try {
                    threads.get(i).start();
                    threads.get(i).join();
                }
                catch (InterruptedException exception) {
                    System.err.println("Thread error: " +
                            exception.getMessage());
                    System.exit(1);
                }
            }
        }

        // o processo de reconhecimento da cadeia já encerrou, mas ainda
        // é necessário fazer uma última limpeza na lista de threads e no
        // mapa dos caminhos de reconhecimento
        if (!removals.isEmpty()) {
            
            // para cada identificador das threads a serem removidas,
            // percorre a lista de threads e remove aquelas cujo
            // identificador é igual
            for (int i : removals) {
                for (int j = 0; j < threads.size(); j++) {
                    if (threads.get(j).getIdentifier() == i) {
                        threads.remove(j);
                        break;
                    }
                }

                // repete a operação no mapa de caminhos de
                // reconhecimento, mas apenas os reconhecimentos
                // incompletos são removidos
                if (!paths.get(i).done()) {
                    paths.remove(i);
                }
            }

            // todas as threads marcadas para remoção foram
            // devidamente removidas, portanto, agora a lista
            // de remoção deve ser limpa
            removals.clear();
        }
        
        // verifica se algum caminho de reconhecimento conduziu à
        // aceitação da cadeia e retorna o resultado
        for (int i : paths.keySet()) {
            if (paths.get(i).done()) {
                if (paths.get(i).getResult() == true) {
                    return true;
                }
            }
        }

        // a cadeia não foi aceita, retorna falso
        return false;
    }
    
    /**
     * Verifica se, pelo menos, um caminho de reconhecimento encerrou-se.
     * @return Um valor lógico que denota se, pelo menos, um caminho de
     * reconhecimento já encerrou-se.
     */
    private boolean atLeastOneRecognitionPathIsDone() {
        
        // se o sinalizador para parar na primeira ocorrência de um
        // caminho de reconhecimento for verdadeiro, 
        if (stopAtFirstFinishedRecognitionPath == false) {
            return false;
        }
        
        // percorre o mapa e verifica se algum caminho já encerrou,
        // retornando o valor da consulta
        for (int i : paths.keySet()) {
            if (paths.get(i).done()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna uma representação textual do autômato adaptativo.
     * @return Representação textual do autômato adaptativo.
     */
    @Override
    public String toString() {
        String newline = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("Autômato adaptativo: {").append(newline);
        sb.append(submachines).append(newline);
        sb.append(transitions).append(newline);
        sb.append(actions).append(newline);
        sb.append(stack).append(newline);
        sb.append("Submáquina principal: ").
                append(mainSubmachine != null ?
                        mainSubmachine : "não definida").append(newline);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Define a condição de parada do autômato adaptativo tal que este não
     * prossiga após o resultado de, pelo menos, um caminho de reconhecimento.
     * @param flag Valor lógico que determina se o autômato deve continuar o
     * reconhecimento mesmo que já tenha um resultado disponível.
     */
    public void setStopAtFirstResult(boolean flag) {
        this.stopAtFirstFinishedRecognitionPath = flag;
    }
    
}
