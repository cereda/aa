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

import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Symbol;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.predicates.EpsilonPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PostActionCallPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PostActionCheckArgumentPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PostActionCountArgumentsPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PostActionPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PriorActionCallPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PriorActionCheckArgumentPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PriorActionCountArgumentsPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.PriorActionPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SourceStatePredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SubmachineCallPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SubmachinePredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.SymbolPredicate;
import br.usp.poli.lta.cereda.aa.model.predicates.TargetStatePredicate;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.utils.SetOperations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.lang3.Validate;

/**
 * Implementa as ações elementares de consulta, inserção e remoção de transições
 * de acordo com um determinado padrão de busca.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class ElementaryActions {

    // conjunto de transições a serem analisadas
    private final Mapping transitions;

    /**
     * Construtor.
     * @param transitions Conjunto de transições do modelo do autômato
     * adaptativo.
     */
    public ElementaryActions(Mapping transitions) {
        this.transitions = transitions;
    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void query(Variable source, Variable symbol, Variable target) {
        
        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // predicado do estado de origem
        Predicate sourcePredicate;

        // se o estado de origem não está disponível,
        // significa que existem valores
        if (!source.isAvailable()) {
            
            // para cada valor existente,
            // adiciona um predicado
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            
            // união dos predicados
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            
            // qualquer valor é válido
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado do símbolo a ser consumido
        Predicate symbolPredicate;

        // se o símbolo não está disponível, existem
        // valores associados a ele
        if (!symbol.isAvailable()) {

            // para cada valor existente,
            // adiciona um predicado
            Collection symbolPredicateList = new ArrayList();
            for (Object currentSymbol : symbol.getValues()) {
                
                // temos uma transição convencional?
                if (currentSymbol != null) {
                    
                    // sim, transição convencional
                    symbolPredicateList.add(
                            new SymbolPredicate((Symbol) currentSymbol)
                    );
                    
                } else {
                    
                    // transição em vazio
                    symbolPredicateList.add(new EpsilonPredicate());
                }
            }
            
            // união dos predicados
            symbolPredicate = PredicateUtils.anyPredicate(symbolPredicateList);
        } else {
            
            // qualquer valor é válido
            symbolPredicate = TruePredicate.truePredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;

        // se o estado de destino não está disponível,
        // temos valores a considerar
        if (!target.isAvailable()) {

            // para cada valor existente,
            // adiciona um predicado
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            
            // união dos predicados
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            
            // qualquer valor do estado de
            // destino é válido
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção de todos os predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> symbolResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            symbolResult.add(transition.getSymbol());
            targetResult.add(transition.getTargetState());
        }

        // se o estado de origem está
        // disponível, preenche os valores
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // se o símbolo está disponível,
        // preenche os valores
        if (symbol.isAvailable()) {
            symbol.setValues(symbolResult);
        }

        // se o estado de destino está
        // disponível, preenche os valores
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void query(Variable source, SubmachineQuery submachine,
            Variable target) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // predicado do estado de origem
        Predicate sourcePredicate;

        // o estado de origem não está disponível?
        if (!source.isAvailable()) {

            // para cada valor existente,
            // adiciona um predicado
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            
            // união dos predicados
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            
            // qualquer valor é válido
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado de submáquina
        Predicate submachinePredicate;

        // existem valores de submáquina?
        if (!submachine.getVariable().isAvailable()) {

            // para cada valor existente,
            // adiciona um predicado
            Collection submachinePredicateList = new ArrayList();
            for (Object currentSubmachine : submachine.getVariable().
                    getValues()) {
                submachinePredicateList.add(
                        new SubmachinePredicate((String) currentSubmachine)
                );
            }
            
            // união dos predicados
            submachinePredicate = PredicateUtils.anyPredicate(
                    submachinePredicateList
            );
        } else {
            
            // qualquer valor é válido, contanto
            // que seja uma chamada de submáquina
            submachinePredicate = new SubmachineCallPredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;

        // existem valores para o
        // estado de destino?
        if (!target.isAvailable()) {

            // para cada valor existente,
            // adiciona um predicado
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            
            // união dos predicados
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            
            // qualquer valor é válido
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção de todos
        // os predicados disponíveis
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> submachineResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            submachineResult.add(transition.getSubmachineCall());
            targetResult.add(transition.getTargetState());
        }

        // preenche a variável do
        // estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche a variável da
        // chamada de submáquina
        if (submachine.getVariable().isAvailable()) {
            submachine.getVariable().setValues(submachineResult);
        }

        // preenche a variável do
        // estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void query(Variable source, SubmachineQuery submachine,
            Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");

        // predicado da ação posterior
        Predicate postActionPredicate;

        // tratamento do nome da ação posterior
        Predicate postActionNamePredicate;
        
        // o nome não está disponível, então
        // existem valores
        if (!postAction.getName().isAvailable()) {
            
            // obtém dos os nomes existentes
            Collection postActionNamePredicateList = new ArrayList();
            for (Object currentPostActionName : postAction.getName().
                    getValues()) {
                postActionNamePredicateList.add(
                        new PostActionPredicate((String) currentPostActionName)
                );
            }
            
            // união dos predicados
            postActionNamePredicate = PredicateUtils.anyPredicate(
                    postActionNamePredicateList
            );
        } else {
            
            // qualquer valor é válido, contanto
            // que seja um nome de ação posterior
            postActionNamePredicate = new PostActionCallPredicate();
        }

        // tratamento dos argumentos da ação
        // posterior, se existirem
        if (postAction.hasArguments()) {
            
            // obtém todos os valores de todos
            // os argumentos da ação posterior
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            
            // percorre todos os argumentos
            for (int i = 0; i < postActionArguments.size(); i++) {
                if (!postActionArguments.get(i).isAvailable()) {
                    postActionArgumentPredicateList = new ArrayList();
                    for (Object currentPostActionArgument : 
                            postActionArguments.get(i).getValues()) {
                        postActionArgumentPredicateList.add(
                                new PostActionCheckArgumentPredicate(
                                        i,
                                        currentPostActionArgument
                                )
                        );
                    }
                    postActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    postActionArgumentPredicateList
                            )
                    );
                } else {
                    postActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            
            // adiciona o predicado
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            
            // não existem argumentos
            postActionPredicate = postActionNamePredicate;
        }

        // predicado do estado de origem
        Predicate sourcePredicate;

        // existem valores para o
        // estado de origem
        if (!source.isAvailable()) {

            // obtém todos os valores
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            
            // união dos predicados
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            
            // qualquer valor é válido
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado de chamada de submáquina
        Predicate submachinePredicate;

        // existem valores de chamadas de submáquinas
        if (!submachine.getVariable().isAvailable()) {

            // obtém todos os valores
            Collection submachinePredicateList = new ArrayList();
            for (Object currentSubmachine : submachine.getVariable().
                    getValues()) {
                submachinePredicateList.add(
                        new SubmachinePredicate((String) currentSubmachine)
                );
            }
            
            // união dos predicados
            submachinePredicate = PredicateUtils.anyPredicate(
                    submachinePredicateList
            );
        } else {
            
            // qualquer valor é válido, contanto
            // que seja uma chamada de submáquina
            submachinePredicate = new SubmachineCallPredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;

        // existem valores associados
        if (!target.isAvailable()) {

            // obtém todos os valores
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            
            // união dos predicados
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            
            // qualquer valor é válido
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção de todos
        // os predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            postActionPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> submachineResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> postActionNameResult = new HashSet<>();
        List<HashSet<Object>> postActionArgumentsResult = new ArrayList<>();

        // inicializa a lista de argumentos
        // da ação posterior, se existirem
        if (postAction.hasArguments()) {
            for (Variable argument : postAction.getArguments()) {
                postActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            submachineResult.add(transition.getSubmachineCall());
            targetResult.add(transition.getTargetState());
            postActionNameResult.add(transition.getPostActionCall());
            if (postAction.hasArguments()) {
                for (int i = 0; i < postAction.getArguments().size(); i++) {
                    postActionArgumentsResult.get(i).add(
                            transition.getPostActionArguments()[i]
                    );
                }
            }
        }

        // preenche o estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche a chamada de submáquina
        if (submachine.getVariable().isAvailable()) {
            submachine.getVariable().setValues(submachineResult);
        }

        // preenche o estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // preenche o nome da ação posterior
        if (postAction.getName().isAvailable()) {
            postAction.getName().setValues(postActionNameResult);
        }

        // preenche os argumentos da ação posterior
        if (postAction.hasArguments()) {
            for (int i = 0; i < postAction.getArguments().size(); i++) {
                if (postAction.getArguments().get(i).isAvailable()) {
                    postAction.getArguments().get(i).setValues(
                            postActionArgumentsResult.get(i)
                    );
                }
            }
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void query(ActionQuery priorAction, Variable source,
            SubmachineQuery submachine, Variable target) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // predicado da ação anterior
        Predicate priorActionPredicate;

        // tratamento do nome da ação anterior
        Predicate priorActionNamePredicate;
        if (!priorAction.getName().isAvailable()) {
            Collection priorActionNamePredicateList = new ArrayList();
            for (Object currentPriorActionName : priorAction.getName().
                    getValues()) {
                priorActionNamePredicateList.add(
                        new PriorActionPredicate(
                                (String) currentPriorActionName
                        )
                );
            }
            priorActionNamePredicate = PredicateUtils.anyPredicate(
                    priorActionNamePredicateList
            );
        } else {
            priorActionNamePredicate = new PriorActionCallPredicate();
        }

        // tratamento dos argumentos da ação anterior
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                if (!priorActionArguments.get(i).isAvailable()) {
                    priorActionArgumentPredicateList = new ArrayList();
                    for (Object currentPriorActionArgument : 
                            priorActionArguments.get(i).getValues()) {
                        priorActionArgumentPredicateList.add(
                                new PriorActionCheckArgumentPredicate(
                                        i,
                                        currentPriorActionArgument
                                )
                        );
                    }
                    priorActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    priorActionArgumentPredicateList
                            )
                    );
                } else {
                    priorActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }
        
        // predicado do estado de origem
        Predicate sourcePredicate;
        if (!source.isAvailable()) {
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado da chamada de submáquina
        Predicate submachinePredicate;
        if (!submachine.getVariable().isAvailable()) {
            Collection submachinePredicateList = new ArrayList();
            for (Object currentSubmachine : submachine.getVariable().
                    getValues()) {
                submachinePredicateList.add(
                        new SubmachinePredicate((String) currentSubmachine)
                );
            }
            submachinePredicate = PredicateUtils.anyPredicate(
                    submachinePredicateList
            );
        } else {
            submachinePredicate = new SubmachineCallPredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;
        if (!target.isAvailable()) {
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            priorActionPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> submachineResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> priorActionNameResult = new HashSet<>();
        List<HashSet<Object>> priorActionArgumentsResult = new ArrayList<>();

        // existem argumentos da ação anterior,
        // inicializa cada conjunto
        if (priorAction.hasArguments()) {
            for (Variable argument : priorAction.getArguments()) {
                priorActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos de resultados
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            submachineResult.add(transition.getSubmachineCall());
            targetResult.add(transition.getTargetState());
            priorActionNameResult.add(transition.getPriorActionCall());
            if (priorAction.hasArguments()) {
                for (int i = 0; i < priorAction.getArguments().size(); i++) {
                    priorActionArgumentsResult.get(i).add(
                            transition.getPriorActionArguments()[i]
                    );
                }
            }
        }

        // preenche o estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche a chamada de submáquina
        if (submachine.getVariable().isAvailable()) {
            submachine.getVariable().setValues(submachineResult);
        }

        // preenche o estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // preenche o nome da ação anterior
        if (priorAction.getName().isAvailable()) {
            priorAction.getName().setValues(priorActionNameResult);
        }

        // preenche os argumentos da ação anterior
        if (priorAction.hasArguments()) {
            for (int i = 0; i < priorAction.getArguments().size(); i++) {
                if (priorAction.getArguments().get(i).isAvailable()) {
                    priorAction.getArguments().get(i).setValues(
                            priorActionArgumentsResult.get(i)
                    );
                }
            }
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void query(ActionQuery priorAction, Variable source,
            SubmachineQuery submachine, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // predicado da ação anterior
        Predicate priorActionPredicate;
        
        // tratamento do nome da ação anterior
        Predicate priorActionNamePredicate;
        if (!priorAction.getName().isAvailable()) {
            Collection priorActionNamePredicateList = new ArrayList();
            for (Object currentPriorActionName : priorAction.getName().
                    getValues()) {
                priorActionNamePredicateList.add(
                        new PriorActionPredicate(
                                (String) currentPriorActionName
                        )
                );
            }
            priorActionNamePredicate = PredicateUtils.anyPredicate(
                    priorActionNamePredicateList
            );
        } else {
            priorActionNamePredicate = new PriorActionCallPredicate();
        }

        // tratamento dos argumentos da
        // ação anterior, se existirem
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                if (!priorActionArguments.get(i).isAvailable()) {
                    priorActionArgumentPredicateList = new ArrayList();
                    for (Object currentPriorActionArgument : 
                            priorActionArguments.get(i).getValues()) {
                        priorActionArgumentPredicateList.add(
                                new PriorActionCheckArgumentPredicate(
                                        i,
                                        currentPriorActionArgument
                                )
                        );
                    }
                    priorActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    priorActionArgumentPredicateList
                            )
                    );
                } else {
                    priorActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }
        
        // predicado da ação posterior
        Predicate postActionPredicate;

        // tratamento do nome da ação posterior
        Predicate postActionNamePredicate;
        if (!postAction.getName().isAvailable()) {
            Collection postActionNamePredicateList = new ArrayList();
            for (Object currentPostActionName : postAction.getName().
                    getValues()) {
                postActionNamePredicateList.add(
                        new PostActionPredicate((String) currentPostActionName)
                );
            }
            postActionNamePredicate = PredicateUtils.anyPredicate(
                    postActionNamePredicateList
            );
        } else {
            postActionNamePredicate = new PostActionCallPredicate();
        }

        // tratamento dos argumentos da
        // ação posterior, se existirem
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                if (!postActionArguments.get(i).isAvailable()) {
                    postActionArgumentPredicateList = new ArrayList();
                    for (Object currentPostActionArgument : 
                            postActionArguments.get(i).getValues()) {
                        postActionArgumentPredicateList.add(
                                new PostActionCheckArgumentPredicate(
                                        i,
                                        currentPostActionArgument
                                )
                        );
                    }
                    postActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    postActionArgumentPredicateList
                            )
                    );
                } else {
                    postActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }
        
        // predicado do estado de origem
        Predicate sourcePredicate;
        if (!source.isAvailable()) {
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(new SourceStatePredicate(
                        (State) currentSource)
                );
            }
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado da chamada de submáquina
        Predicate submachinePredicate;
        if (!submachine.getVariable().isAvailable()) {
            Collection submachinePredicateList = new ArrayList();
            for (Object currentSubmachine : 
                    submachine.getVariable().getValues()) {
                submachinePredicateList.add(
                        new SubmachinePredicate((String) currentSubmachine)
                );
            }
            submachinePredicate = PredicateUtils.anyPredicate(
                    submachinePredicateList
            );
        } else {
            submachinePredicate = new SubmachineCallPredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;
        if (!target.isAvailable()) {
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate(
                                (State) currentTarget
                        )
                );
            }
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            priorActionPredicate,
                            postActionPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> submachineResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> priorActionNameResult = new HashSet<>();
        List<HashSet<Object>> priorActionArgumentsResult = new ArrayList<>();
        Set<Object> postActionNameResult = new HashSet<>();
        List<HashSet<Object>> postActionArgumentsResult = new ArrayList<>();

        // inicializa conjunto dos argumentos
        // da ação anterior
        if (priorAction.hasArguments()) {
            for (Variable argument : priorAction.getArguments()) {
                priorActionArgumentsResult.add(new HashSet<>());
            }
        }

        // inicializa conjunto dos argumentos
        // da ação posterior
        if (postAction.hasArguments()) {
            for (Variable argument : postAction.getArguments()) {
                postActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos de resultados
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            submachineResult.add(transition.getSubmachineCall());
            targetResult.add(transition.getTargetState());
            priorActionNameResult.add(transition.getPriorActionCall());
            if (priorAction.hasArguments()) {
                for (int i = 0; i < priorAction.getArguments().size(); i++) {
                    priorActionArgumentsResult.get(i).add(
                            transition.getPriorActionArguments()[i]
                    );
                }
            }
            postActionNameResult.add(transition.getPostActionCall());
            if (postAction.hasArguments()) {
                for (int i = 0; i < postAction.getArguments().size(); i++) {
                    postActionArgumentsResult.get(i).add(
                            transition.getPostActionArguments()[i]
                    );
                }
            }
        }

        // insere os valores do estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // insere os valores das chamadas de submáquinas
        if (submachine.getVariable().isAvailable()) {
            submachine.getVariable().setValues(submachineResult);
        }

        // insere os valores do estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // insere os valores do nome da ação anterior
        if (priorAction.getName().isAvailable()) {
            priorAction.getName().setValues(priorActionNameResult);
        }
        
        // insere os valores dos argumentos da
        // ação anterior, se existirem
        if (priorAction.hasArguments()) {
            for (int i = 0; i < priorAction.getArguments().size(); i++) {
                if (priorAction.getArguments().get(i).isAvailable()) {
                    priorAction.getArguments().get(i).setValues(
                            priorActionArgumentsResult.get(i)
                    );
                }
            }
        }

        // insere os valores do nome da ação posterior
        if (postAction.getName().isAvailable()) {
            postAction.getName().setValues(postActionNameResult);
        }

        // insere os valores dos argumentos da
        // ação posterior, se existirem
        if (postAction.hasArguments()) {
            for (int i = 0; i < postAction.getArguments().size(); i++) {
                if (postAction.getArguments().get(i).isAvailable()) {
                    postAction.getArguments().get(i).setValues(
                            postActionArgumentsResult.get(i)
                    );
                }
            }
        }

    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void remove(Variable source, Variable symbol, Variable target) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // predicado do símbolo
        Collection symbolPredicateList = new ArrayList();
        for (Object currentSymbol : symbol.getValues()) {
            if (currentSymbol != null) {
                symbolPredicateList.add(
                        new SymbolPredicate((Symbol) currentSymbol)
                );
            } else {
                symbolPredicateList.add(new EpsilonPredicate());
            }
        }
        Predicate symbolPredicate = PredicateUtils.anyPredicate(
                symbolPredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate
                        }
                )
        );
        
        // lista de identificadores para remoção
        ArrayList<Integer> removals = new ArrayList<>();
        
        // percorre o resultado da consulta
        // e adiciona os identificadores na
        // lista de remoção
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo
        // com seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void remove(ActionQuery priorAction, Variable source,
            Variable symbol, Variable target) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(priorAction, "A variável não pode ser nula.");
                
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto for vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto for vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação anterior
        Predicate priorActionPredicate;
        Predicate priorActionNamePredicate;
        Collection priorActionNamePredicateList = new ArrayList();
        for (Object currentPriorActionName :
                priorAction.getName().getValues()) {
            priorActionNamePredicateList.add(
                    new PriorActionPredicate((String) currentPriorActionName)
            );
        }
        priorActionNamePredicate = PredicateUtils.anyPredicate(
                priorActionNamePredicateList
        );
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                priorActionArgumentPredicateList = new ArrayList();
                for (Object currentPriorActionArgument :
                        priorActionArguments.get(i).getValues()) {
                    priorActionArgumentPredicateList.add(
                            new PriorActionCheckArgumentPredicate(
                                    i,
                                    currentPriorActionArgument
                            )
                    );
                }
                priorActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                priorActionArgumentPredicateList
                        )
                );
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // predicado do símbolo
        Collection symbolPredicateList = new ArrayList();
        for (Object currentSymbol : symbol.getValues()) {
            if (currentSymbol != null) {
                symbolPredicateList.add(
                        new SymbolPredicate((Symbol) currentSymbol)
                );
            } else {
                symbolPredicateList.add(new EpsilonPredicate());
            }
        }
        Predicate symbolPredicate = PredicateUtils.anyPredicate(
                symbolPredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            priorActionPredicate
                        }
                )
        );
        
        // lista de identificadores
        // para remoção
        ArrayList<Integer> removals = new ArrayList<>();
        
        // percorre as transições da
        // consulta e adiciona os
        // identificadores
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo
        // com seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void remove(Variable source, Variable symbol, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }
        
        // retorna se conjunto for vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se conjunto for vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação posterior
        Predicate postActionPredicate;
        Predicate postActionNamePredicate;
        Collection postActionNamePredicateList = new ArrayList();
        for (Object currentPostActionName :
                postAction.getName().getValues()) {
            postActionNamePredicateList.add(
                    new PostActionPredicate((String) currentPostActionName)
            );
        }
        postActionNamePredicate = PredicateUtils.anyPredicate(
                postActionNamePredicateList
        );
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                postActionArgumentPredicateList = new ArrayList();
                for (Object currentPostActionArgument :
                        postActionArguments.get(i).getValues()) {
                    postActionArgumentPredicateList.add(
                            new PostActionCheckArgumentPredicate(
                                    i,
                                    currentPostActionArgument
                            )
                    );
                }
                postActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                postActionArgumentPredicateList
                        )
                );
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação do conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // predicado do símbolo
        Collection symbolPredicateList = new ArrayList();
        for (Object currentSymbol : symbol.getValues()) {
            if (currentSymbol != null) {
                symbolPredicateList.add(
                        new SymbolPredicate((Symbol) currentSymbol)
                );
            } else {
                symbolPredicateList.add(new EpsilonPredicate());
            }
        }
        Predicate symbolPredicate = PredicateUtils.anyPredicate(
                symbolPredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            postActionPredicate
                        }
                )
        );
        
        // lista de identificadores
        // para remoção
        List<Integer> removals = new ArrayList<>();
        
        // percorre o resultado da
        // consulta e adiciona os
        // identificadores na lista
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo
        // com seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void remove(ActionQuery priorAction, Variable source,
            Variable symbol, Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "Variavel tem que tá inicializada!"
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação anterior
        Predicate priorActionPredicate;
        Predicate priorActionNamePredicate;
        Collection priorActionNamePredicateList = new ArrayList();
        for (Object currentPriorActionName :
                priorAction.getName().getValues()) {
            priorActionNamePredicateList.add(
                    new PriorActionPredicate((String) currentPriorActionName)
            );
        }
        priorActionNamePredicate = PredicateUtils.anyPredicate(
                priorActionNamePredicateList
        );
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                priorActionArgumentPredicateList = new ArrayList();
                for (Object currentPriorActionArgument :
                        priorActionArguments.get(i).getValues()) {
                    priorActionArgumentPredicateList.add(
                            new PriorActionCheckArgumentPredicate(
                                    i,
                                    currentPriorActionArgument
                            )
                    );
                }
                priorActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                priorActionArgumentPredicateList
                        )
                );
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test :
                    postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se conjunto for vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se conjunto for vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação posterior
        Predicate postActionPredicate;
        Predicate postActionNamePredicate;
        Collection postActionNamePredicateList = new ArrayList();
        for (Object currentPostActionName : postAction.getName().getValues()) {
            postActionNamePredicateList.add(
                    new PostActionPredicate((String) currentPostActionName)
            );
        }
        postActionNamePredicate = PredicateUtils.anyPredicate(
                postActionNamePredicateList
        );
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                postActionArgumentPredicateList = new ArrayList();
                for (Object currentPostActionArgument : 
                        postActionArguments.get(i).getValues()) {
                    postActionArgumentPredicateList.add(
                            new PostActionCheckArgumentPredicate(
                                    i,
                                    currentPostActionArgument
                            )
                    );
                }
                postActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                postActionArgumentPredicateList
                        )
                );
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // predicado do símbolo
        Collection symbolPredicateList = new ArrayList();
        for (Object currentSymbol : symbol.getValues()) {
            if (currentSymbol != null) {
                symbolPredicateList.add(
                        new SymbolPredicate((Symbol) currentSymbol)
                );
            } else {
                symbolPredicateList.add(new EpsilonPredicate());
            }
        }
        Predicate symbolPredicate = PredicateUtils.anyPredicate(
                symbolPredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            priorActionPredicate,
                            postActionPredicate
                        }
                )
        );
        
        // lista de identificadores
        // para remoção
        List<Integer> removals = new ArrayList<>();
        
        // percorre o resultado da consulta
        // e adiciona os identificadores na lista
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo
        // com seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void remove(Variable source, SubmachineQuery submachine,
            Variable target) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // predicado de chamada de submáquina
        Collection submachinePredicateList = new ArrayList();
        for (Object currentSubmachine :
                submachine.getVariable().getValues()) {
            submachinePredicateList.add(
                    new SubmachinePredicate((String) currentSubmachine)
            );
        }
        Predicate submachinePredicate = PredicateUtils.anyPredicate(
                submachinePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State )currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate
                        }
                )
        );
        
        // lista de identificadores
        // para remoção
        List<Integer> removals = new ArrayList<>();
        
        // percorre o resultado da consulta,
        // adicionando os identificadores na lista
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo com
        // seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void remove(Variable source, SubmachineQuery submachine,
            Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto for vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto for vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação posterior
        Predicate postActionPredicate;
        Predicate postActionNamePredicate;
        Collection postActionNamePredicateList = new ArrayList();
        for (Object currentPostActionName : postAction.getName().getValues()) {
            postActionNamePredicateList.add(
                    new PostActionPredicate((String) currentPostActionName)
            );
        }
        postActionNamePredicate = PredicateUtils.anyPredicate(
                postActionNamePredicateList
        );
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                postActionArgumentPredicateList = new ArrayList();
                for (Object currentPostActionArgument :
                        postActionArguments.get(i).getValues()) {
                    postActionArgumentPredicateList.add(
                            new PostActionCheckArgumentPredicate(
                                    i,
                                    currentPostActionArgument
                            )
                    );
                }
                postActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                postActionArgumentPredicateList
                        )
                );
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // predicado de chamada de submáquina
        Collection submachinePredicateList = new ArrayList();
        for (Object currentSubmachine : 
                submachine.getVariable().getValues()) {
            submachinePredicateList.add(
                    new SubmachinePredicate((String) currentSubmachine)
            );
        }
        Predicate submachinePredicate = PredicateUtils.anyPredicate(
                submachinePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            postActionPredicate
                        }
                )
        );
        
        // lista de identificadores
        // para remoção
        List<Integer> removals = new ArrayList<>();
        
        // percorre todas as transições
        // da consulta, adicionando os
        // identificadores
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de acordo com
        // seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void remove(ActionQuery priorAction, Variable source,
            SubmachineQuery submachine, Variable target) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação anterior
        Predicate priorActionPredicate;
        Predicate priorActionNamePredicate;
        Collection priorActionNamePredicateList = new ArrayList();
        for (Object currentPriorActionName :
                priorAction.getName().getValues()) {
            priorActionNamePredicateList.add(
                    new PriorActionPredicate((String) currentPriorActionName)
            );
        }
        priorActionNamePredicate = PredicateUtils.anyPredicate(
                priorActionNamePredicateList
        );
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                priorActionArgumentPredicateList = new ArrayList();
                for (Object currentPriorActionArgument :
                        priorActionArguments.get(i).getValues()) {
                    priorActionArgumentPredicateList.add(
                            new PriorActionCheckArgumentPredicate(
                                    i,
                                    currentPriorActionArgument
                            )
                    );
                }
                priorActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                priorActionArgumentPredicateList
                        )
                );
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // predicado da chamada de submáquina
        Collection submachinePredicateList = new ArrayList();
        for (Object currentSubmachine : submachine.getVariable().getValues()) {
            submachinePredicateList.add(
                    new SubmachinePredicate((String) currentSubmachine)
            );
        }
        Predicate submachinePredicate = PredicateUtils.anyPredicate(
                submachinePredicateList
        );

        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            priorActionPredicate
                        }
                )
        );
        
        // lista de identificadore
        // para remoção
        List<Integer> removals = new ArrayList<>();
        
        // para cada transição da consulta,
        // adiciona identificador na lista
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições de
        // acordo com seus identificadores
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Remove transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void remove(ActionQuery priorAction, Variable source,
            SubmachineQuery submachine, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validações em relação ao conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna para valores vazios
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna para valores vazios
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação anterior
        Predicate priorActionPredicate;
        
        // tratamento do nome da ação anterior
        Predicate priorActionNamePredicate;
        Collection priorActionNamePredicateList = new ArrayList();
        for (Object currentPriorActionName :
                priorAction.getName().getValues()) {
            priorActionNamePredicateList.add(
                    new PriorActionPredicate((String) currentPriorActionName)
            );
        }
        priorActionNamePredicate = PredicateUtils.anyPredicate(
                priorActionNamePredicateList
        );

        // tratamento dos argumentos da
        // ação anterior, se existirem
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                priorActionArgumentPredicateList = new ArrayList();
                for (Object currentPriorActionArgument :
                        priorActionArguments.get(i).getValues()) {
                    priorActionArgumentPredicateList.add(
                            new PriorActionCheckArgumentPredicate(
                                    i,
                                    currentPriorActionArgument
                            )
                    );
                }
                priorActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                priorActionArgumentPredicateList
                        )
                );
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }

        // tratamento do conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna com conjunto vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna com conjunto vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // predicado da ação posterior
        Predicate postActionPredicate;
        
        // tratamento do nome da ação posterior
        Predicate postActionNamePredicate;
        Collection postActionNamePredicateList = new ArrayList();
        for (Object currentPostActionName : postAction.getName().getValues()) {
            postActionNamePredicateList.add(
                    new PostActionPredicate((String) currentPostActionName)
            );
        }
        postActionNamePredicate = PredicateUtils.anyPredicate(
                postActionNamePredicateList
        );

        // tratamento dos argumentos da
        // ação anterior, se existirem
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                postActionArgumentPredicateList = new ArrayList();
                for (Object currentPostActionArgument :
                        postActionArguments.get(i).getValues()) {
                    postActionArgumentPredicateList.add(
                            new PostActionCheckArgumentPredicate(
                                    i,
                                    currentPostActionArgument
                            )
                    );
                }
                postActionArgumentsPredicateList.add(
                        PredicateUtils.anyPredicate(
                                postActionArgumentPredicateList
                        )
                );
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // tratamento do conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto está vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // predicado do estado de origem
        Collection sourcePredicateList = new ArrayList();
        for (Object currentSource : source.getValues()) {
            sourcePredicateList.add(
                    new SourceStatePredicate((State) currentSource)
            );
        }
        Predicate sourcePredicate = PredicateUtils.anyPredicate(
                sourcePredicateList
        );

        // tratamento do conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto está vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // predicado da chamada de submáquina
        Collection submachinePredicateList = new ArrayList();
        for (Object currentSubmachine : submachine.getVariable().getValues()) {
            submachinePredicateList.add(
                    new SubmachinePredicate((String) currentSubmachine)
            );
        }
        Predicate submachinePredicate = PredicateUtils.anyPredicate(
                submachinePredicateList
        );
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }
        
        // tratamento do estado de destino
        Collection targetPredicateList = new ArrayList();
        for (Object currentTarget : target.getValues()) {
            targetPredicateList.add(
                    new TargetStatePredicate((State) currentTarget)
            );
        }
        Predicate targetPredicate = PredicateUtils.anyPredicate(
                targetPredicateList
        );

        // a consulta é a união dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            submachinePredicate,
                            targetPredicate,
                            priorActionPredicate,
                            postActionPredicate
                        }
                )
        );
        
        // lista de identificadores de remoção
        List<Integer> removals = new ArrayList<>();
        
        // percorre as transições da consulta
        // e obtém seus identificadores
        for (Transition transition : query) {
            removals.add(transition.getIdentifier());
        }

        // remove as transições marcadas
        // com o identificador
        for (int id : removals) {
            transitions.removeFromIdentifier(id);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void add(Variable source, Variable symbol, Variable target) {
    
        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(
                source.getValues(),
                symbol.getValues(),
                target.getValues()
        );
        
        // percorre o resultado do produto cartesiano
        for (List<Object> elements : product) {

            // cria nova transição
            Transition transition = new Transition();
            transition.setSourceState((State) elements.get(0));
            transition.setSymbol((Symbol) elements.get(1));
            transition.setTargetState((State) elements.get(2));

            // adiciona a nova transição no
            // conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void add(ActionQuery priorAction, Variable source,
            Variable symbol, Variable target) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação do conteúdo dos argumentos
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se algum dos argumentos possuir
        // um conjunto vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula os índices de inserção
        int priorTotal = priorAction.hasArguments() ?
                priorAction.getArguments().size() : 0;
        int total = 4 + priorTotal;
        
        // obtém todos os valores para a
        // montagem dos conjuntos
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = priorAction.getName().getValues();
        sets[priorTotal + 1] = source.getValues();
        sets[priorTotal + 2] = symbol.getValues();
        sets[priorTotal + 3] = target.getValues();

        // conjunto adicional para os argumentos
        // da ação anterior
        if (priorAction.hasArguments()) {
            for (int i = 1; i <= priorTotal; i++) {
                sets[i] = priorAction.getArguments().get(i - 1).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // insere as novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setPriorActionCall((String) elements.get(0));
            if (priorAction.hasArguments()) {
                Object[] priorParameters = new Object[priorTotal];
                for (int i = 1; i <= priorTotal; i++) {                    
                    priorParameters[i - 1] = elements.get(i);
                }
                transition.setPriorActionArguments(priorParameters);
            }
            transition.setSourceState((State) elements.get(priorTotal + 1));
            transition.setSymbol((Symbol) elements.get(priorTotal + 2));
            transition.setTargetState((State) elements.get(priorTotal + 3));

            // adiciona a nova transição
            // no conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void add(Variable source, Variable symbol, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se algum dos argumentos possuir
        // um conjunto vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula os índices
        int postTotal = postAction.hasArguments() ?
                postAction.getArguments().size() : 0;
        int total = 4 + postTotal;
        
        // define os conjuntos para
        // calcular o produto cartesiano
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = source.getValues();
        sets[1] = symbol.getValues();
        sets[2] = target.getValues();
        sets[3] = postAction.getName().getValues();

        // conjuntos adicionais, caso a ação
        // tenha argumentos
        if (postAction.hasArguments()) {
            for (int i = 4; i < total; i++) {
                sets[i] = postAction.getArguments().get(i - 4).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // insere as novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setSourceState((State) elements.get(0));
            transition.setSymbol((Symbol) elements.get(1));
            transition.setTargetState((State) elements.get(2));
            transition.setPostActionCall((String) elements.get(3));
            if (postAction.hasArguments()) {
                Object[] postParameters = new Object[postTotal];
                for (int i = 4; i < total; i++) {
                    postParameters[i - 4] = elements.get(i);
                }
                transition.setPostActionArguments(postParameters);
            }

            // adiciona a nova transição no
            // conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param submachine Chamada de submáquina.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void add(Variable source, SubmachineQuery submachine,
            Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto for vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto for vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula os índices
        int postTotal = postAction.hasArguments() ?
                postAction.getArguments().size() : 0;
        int total = 4 + postTotal;
        
        // define os conjuntos para
        // o cálculo do produto cartesiano
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = source.getValues();
        sets[1] = submachine.getVariable().getValues();
        sets[2] = target.getValues();
        sets[3] = postAction.getName().getValues();

        // conjuntos adicionais no caso
        // da ação posterior ter parâmetros
        if (postAction.hasArguments()) {
            for (int i = 4; i < total; i++) {
                sets[i] = postAction.getArguments().get(i - 4).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // insere novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setSourceState((State) elements.get(0));
            transition.setSubmachineCall((String) elements.get(1));
            transition.setTargetState((State) elements.get(2));
            transition.setPostActionCall((String) elements.get(3));
            if (postAction.hasArguments()) {
                Object[] postParameters = new Object[postTotal];
                for (int i = 4; i < total; i++) {
                    postParameters[i - 4] = elements.get(i);
                }
                transition.setPostActionArguments(postParameters);
            }

            // adiciona a nova transição no
            // conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void add(ActionQuery priorAction, Variable source, 
            SubmachineQuery submachine, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula os índices
        int priorTotal = priorAction.hasArguments() ?
                priorAction.getArguments().size() : 0;
        int postTotal = postAction.hasArguments() ?
                postAction.getArguments().size() : 0;
        int total = 5 + priorTotal + postTotal;
        
        // define os conjuntos para o cálculo
        // do produto cartesiano
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = priorAction.getName().getValues();
        sets[priorTotal + 1] = source.getValues();
        sets[priorTotal + 2] = submachine.getVariable().getValues();
        sets[priorTotal + 3] = target.getValues();
        sets[priorTotal + 4] = postAction.getName().getValues();

        // conjuntos adicionais caso a
        // ação anterior tenha argumentos
        if (priorAction.hasArguments()) {
            for (int i = 1; i <= priorTotal; i++) {
                sets[i] = priorAction.getArguments().get(i - 1).getValues();
            }
        }

        // conjuntos adicionais caso a
        // ação posterior tenha argumentos
        if (postAction.hasArguments()) {
            for (int i = priorTotal + 5; i < total; i++) {
                sets[i] = postAction.getArguments().
                        get(i - (priorTotal + 5)).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // adiciona as novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setPriorActionCall((String) elements.get(0));
            if (priorAction.hasArguments()) {
                Object[] priorParameters = new Object[priorTotal];
                for (int i = 1; i <= priorTotal; i++) {
                    priorParameters[i - 1] = elements.get(i);
                }
                transition.setPriorActionArguments(priorParameters);
            }
            transition.setSourceState((State) elements.get(priorTotal + 1));
            transition.setSubmachineCall((String) elements.get(priorTotal + 2));
            transition.setTargetState((State) elements.get(priorTotal + 3));
            transition.setPostActionCall((String) elements.get(priorTotal + 4));
            if (postAction.hasArguments()) {
                Object[] postParameters = new Object[postTotal];
                for (int i = priorTotal + 5; i < total; i++) {
                    postParameters[i - (priorTotal + 5)] = elements.get(i);
                }
                transition.setPostActionArguments(postParameters);
            }

            // adiciona a nova transição
            // no conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void add(ActionQuery priorAction, Variable source, Variable symbol,
            Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !postAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto estiver vazio
        if (postAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto estiver vazio
        if (postAction.hasArguments()) {
            for (Variable test : postAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !symbol.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (symbol.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto estiver vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // cálculo dos índices
        int priorTotal = priorAction.hasArguments() ?
                priorAction.getArguments().size() : 0;
        int postTotal = postAction.hasArguments() ?
                postAction.getArguments().size() : 0;
        int total = 5 + priorTotal + postTotal;
        
        // conjuntos para o cálculo
        // do produto cartesiano
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = priorAction.getName().getValues();
        sets[priorTotal + 1] = source.getValues();
        sets[priorTotal + 2] = symbol.getValues();
        sets[priorTotal + 3] = target.getValues();
        sets[priorTotal + 4] = postAction.getName().getValues();

        // conjuntos adicionais
        if (priorAction.hasArguments()) {
            for (int i = 1; i <= priorTotal; i++) {
                sets[i] = priorAction.getArguments().get(i - 1).getValues();
            }
        }

        // conjuntos adicionais
        if (postAction.hasArguments()) {
            for (int i = priorTotal + 5; i < total; i++) {
                sets[i] = postAction.getArguments().
                        get(i - (priorTotal + 5)).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // insere novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setPriorActionCall((String) elements.get(0));
            if (priorAction.hasArguments()) {
                Object[] priorParameters = new Object[priorTotal];
                for (int i = 1; i <= priorTotal; i++) {
                    priorParameters[i - 1] = elements.get(i);
                }
                transition.setPriorActionArguments(priorParameters);
            }
            transition.setSourceState((State) elements.get(priorTotal + 1));
            transition.setSymbol((Symbol) elements.get(priorTotal + 2));
            transition.setTargetState((State) elements.get(priorTotal + 3));
            transition.setPostActionCall((String) elements.get(priorTotal + 4));
            if (postAction.hasArguments()) {
                Object[] postParameters = new Object[postTotal];
                for (int i = priorTotal + 5; i < total; i++) {
                    postParameters[i - (priorTotal + 5)] = elements.get(i);
                }
                transition.setPostActionArguments(postParameters);
            }

            // adiciona a nova transição
            // no conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void add(ActionQuery priorAction, Variable source,
            SubmachineQuery submachine, Variable target) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !priorAction.getName().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // validação de conteúdo
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                Validate.isTrue(
                        !test.isAvailable(),
                        "A variável deve estar inicializada."
                );
            }
        }

        // retorna se o conjunto for vazio
        if (priorAction.getName().getValues().isEmpty()) {
            return;
        }

        // retorna se o conjunto for vazio
        if (priorAction.hasArguments()) {
            for (Variable test : priorAction.getArguments()) {
                if (test.getValues().isEmpty()) {
                    return;
                }
            }
        }

        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // cálculo dos índices
        int priorTotal = priorAction.hasArguments() ?
                priorAction.getArguments().size() : 0;
        int total = 4 + priorTotal;
        
        // conjuntos para o cálculo
        // do produto cartesiano
        Set<?>[] sets = new HashSet<?>[total];
        sets[0] = priorAction.getName().getValues();
        sets[priorTotal + 1] = source.getValues();
        sets[priorTotal + 2] = submachine.getVariable().getValues();
        sets[priorTotal + 3] = target.getValues();

        // conjuntos adicionais
        if (priorAction.hasArguments()) {
            for (int i = 1; i <= priorTotal; i++) {
                sets[i] = priorAction.getArguments().get(i - 1).getValues();
            }
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(sets);
        
        // insere novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setPriorActionCall((String) elements.get(0));
            if (priorAction.hasArguments()) {
                Object[] priorParameters = new Object[priorTotal];
                for (int i = 1; i <= priorTotal; i++) {
                    priorParameters[i - 1] = elements.get(i);
                }
                transition.setPriorActionArguments(priorParameters);
            }
            transition.setSourceState((State) elements.get(priorTotal + 1));
            transition.setSubmachineCall((String) elements.get(priorTotal + 2));
            transition.setTargetState((State) elements.get(priorTotal + 3));

            // adiciona nova transição
            transitions.add(transition);
        }
    }

    /**
     * Adiciona transições de acordo com o padrão informado.
     * @param source Estado de origem.
     * @param submachine Submáquina a ser chamada.
     * @param target Estado de destino.
     */
    public void add(Variable source, SubmachineQuery submachine,
            Variable target) {
    
        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(submachine, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // validação de conteúdo
        Validate.isTrue(
                !source.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (source.getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !submachine.getVariable().isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (submachine.getVariable().getValues().isEmpty()) {
            return;
        }
        
        // validação de conteúdo
        Validate.isTrue(
                !target.isAvailable(),
                "A variável deve estar inicializada."
        );
        
        // retorna se o conjunto for vazio
        if (target.getValues().isEmpty()) {
            return;
        }

        // calcula o produto cartesiano
        Set<List<Object>> product = SetOperations.cartesianProduct(
                source.getValues(),
                submachine.getVariable().getValues(),
                target.getValues()
        );
        
        // insere novas transições
        for (List<Object> elements : product) {

            // nova transição
            Transition transition = new Transition();
            transition.setSourceState((State) elements.get(0));
            transition.setSubmachineCall((String) elements.get(1));
            transition.setTargetState((State) elements.get(2));

            // adiciona a nova transição
            // no conjunto de transições
            transitions.add(transition);
        }
    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void query(Variable source, Variable symbol, Variable target,
            ActionQuery postAction) {

        // validações
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");

        // predicado da ação posterior
        Predicate postActionPredicate;

        // tratamento do nome da ação posterior
        Predicate postActionNamePredicate;
        if (!postAction.getName().isAvailable()) {
            Collection postActionNamePredicateList = new ArrayList();
            for (Object currentPostActionName :
                    postAction.getName().getValues()) {
                postActionNamePredicateList.add(
                        new PostActionPredicate((String) currentPostActionName)
                );
            }
            postActionNamePredicate = PredicateUtils.anyPredicate(
                    postActionNamePredicateList
            );
        } else {
            postActionNamePredicate = new PostActionCallPredicate();
        }

        // tratamento dos argumentos da ação
        // posterior, se existirem
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                if (!postActionArguments.get(i).isAvailable()) {
                    postActionArgumentPredicateList = new ArrayList();
                    for (Object currentPostActionArgument :
                            postActionArguments.get(i).getValues()) {
                        postActionArgumentPredicateList.add(
                                new PostActionCheckArgumentPredicate(
                                        i,
                                        currentPostActionArgument
                                )
                        );
                    }
                    postActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    postActionArgumentPredicateList
                            )
                    );
                } else {
                    postActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // predicado do estado de origem
        Predicate sourcePredicate;
        if (!source.isAvailable()) {
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado do símbolo a ser consumido
        Predicate symbolPredicate;
        if (!symbol.isAvailable()) {
            Collection symbolPredicateList = new ArrayList();
            for (Object currentSymbol : symbol.getValues()) {
                if (currentSymbol != null) {
                    symbolPredicateList.add(
                            new SymbolPredicate((Symbol) currentSymbol)
                    );
                } else {
                    symbolPredicateList.add(new EpsilonPredicate());
                }
            }
            symbolPredicate = PredicateUtils.anyPredicate(symbolPredicateList);
        } else {
            symbolPredicate = TruePredicate.truePredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;
        if (!target.isAvailable()) {
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            postActionPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> symbolResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> postActionNameResult = new HashSet<>();
        List<HashSet<Object>> postActionArgumentsResult = new ArrayList<>();

        // se a ação posterior tem argumentos,
        // inicializa o conjunto
        if (postAction.hasArguments()) {
            for (Variable argument : postAction.getArguments()) {
                postActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            symbolResult.add(transition.getSymbol());
            targetResult.add(transition.getTargetState());
            postActionNameResult.add(transition.getPostActionCall());
            if (postAction.hasArguments()) {
                for (int i = 0; i < postAction.getArguments().size(); i++) {
                    postActionArgumentsResult.get(i).add(
                            transition.getPostActionArguments()[i]
                    );
                }
            }
        }

        // preenche o estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche o símbolo
        if (symbol.isAvailable()) {
            symbol.setValues(symbolResult);
        }

        // preenche o estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // preenche o nome da ação posterior
        if (postAction.getName().isAvailable()) {
            postAction.getName().setValues(postActionNameResult);
        }

        // preenche os argumentos da 
        // ação posterior, se existirem
        if (postAction.hasArguments()) {
            for (int i = 0; i < postAction.getArguments().size(); i++) {
                if (postAction.getArguments().get(i).isAvailable()) {
                    postAction.getArguments().get(i).setValues(
                            postActionArgumentsResult.get(i)
                    );
                }
            }
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     */
    public void query(ActionQuery priorAction, Variable source,
            Variable symbol, Variable target) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        
        // predicado da ação anterior
        Predicate priorActionPredicate;

        // tratamento do nome da ação anterior
        Predicate priorActionNamePredicate;
        if (!priorAction.getName().isAvailable()) {
            Collection priorActionNamePredicateList = new ArrayList();
            for (Object currentPriorActionName :
                    priorAction.getName().getValues()) {
                priorActionNamePredicateList.add(
                        new PriorActionPredicate(
                                (String) currentPriorActionName
                        )
                );
            }
            priorActionNamePredicate = PredicateUtils.anyPredicate(
                    priorActionNamePredicateList
            );
        } else {
            priorActionNamePredicate = new PriorActionCallPredicate();
        }

        // tratamento dos argumentos da ação
        // anterior, se existirem
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                if (!priorActionArguments.get(i).isAvailable()) {
                    priorActionArgumentPredicateList = new ArrayList();
                    for (Object currentPriorActionArgument : 
                            priorActionArguments.get(i).getValues()) {
                        priorActionArgumentPredicateList.add(
                                new PriorActionCheckArgumentPredicate(
                                        i,
                                        currentPriorActionArgument
                                )
                        );
                    }
                    priorActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    priorActionArgumentPredicateList
                            )
                    );
                } else {
                    priorActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }
        
        // predicado do estado de origem
        Predicate sourcePredicate;
        if (!source.isAvailable()) {
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado do símbolo
        Predicate symbolPredicate;
        if (!symbol.isAvailable()) {
            Collection symbolPredicateList = new ArrayList();
            for (Object currentSymbol : symbol.getValues()) {
                if (currentSymbol != null) {
                    symbolPredicateList.add(
                            new SymbolPredicate((Symbol) currentSymbol)
                    );
                } else {
                    symbolPredicateList.add(new EpsilonPredicate());
                }
            }
            symbolPredicate = PredicateUtils.anyPredicate(symbolPredicateList);
        } else {
            symbolPredicate = TruePredicate.truePredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;
        if (!target.isAvailable()) {
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            priorActionPredicate
                        }
                )
        );

        // conjunto de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> symbolResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> priorActionNameResult = new HashSet<>();
        List<HashSet<Object>> priorActionArgumentsResult = new ArrayList<>();

        // se a ação anterior tem argumentos,
        // inicializa os conjuntos
        if (priorAction.hasArguments()) {
            for (Variable argument : priorAction.getArguments()) {
                priorActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            symbolResult.add(transition.getSymbol());
            targetResult.add(transition.getTargetState());
            priorActionNameResult.add(transition.getPriorActionCall());
            if (priorAction.hasArguments()) {
                for (int i = 0; i < priorAction.getArguments().size(); i++) {
                    priorActionArgumentsResult.get(i).add(
                            transition.getPriorActionArguments()[i]
                    );
                }
            }
        }

        // preenche o estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche o símbolo
        if (symbol.isAvailable()) {
            symbol.setValues(symbolResult);
        }

        // preenche o estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // preenche o nome da ação anterior
        if (priorAction.getName().isAvailable()) {
            priorAction.getName().setValues(priorActionNameResult);
        }

        // preenche os argumentos da ação
        // anterior, se existirem
        if (priorAction.hasArguments()) {
            for (int i = 0; i < priorAction.getArguments().size(); i++) {
                if (priorAction.getArguments().get(i).isAvailable()) {
                    priorAction.getArguments().get(i).setValues(
                            priorActionArgumentsResult.get(i)
                    );
                }
            }
        }

    }

    /**
     * Consulta as transições de acordo com os parâmetros informados e preenche
     * as variáveis.
     * @param priorAction Ação anterior.
     * @param source Estado de origem.
     * @param symbol Símbolo a ser consumido.
     * @param target Estado de destino.
     * @param postAction Ação posterior.
     */
    public void query(ActionQuery priorAction, Variable source,
            Variable symbol, Variable target, ActionQuery postAction) {

        // validações
        Validate.notNull(priorAction, "A variável não pode ser nula.");
        Validate.notNull(source, "A variável não pode ser nula.");
        Validate.notNull(symbol, "A variável não pode ser nula.");
        Validate.notNull(target, "A variável não pode ser nula.");
        Validate.notNull(postAction, "A variável não pode ser nula.");
        
        // predicado da ação anterior
        Predicate priorActionPredicate;

        // tratamento do nome da ação anterior
        Predicate priorActionNamePredicate;
        if (!priorAction.getName().isAvailable()) {
            Collection priorActionNamePredicateList = new ArrayList();
            for (Object currentPriorActionName : priorAction.getName().getValues()) {
                priorActionNamePredicateList.add(
                        new PriorActionPredicate(
                                (String) currentPriorActionName
                        )
                );
            }
            priorActionNamePredicate = PredicateUtils.anyPredicate(
                    priorActionNamePredicateList
            );
        } else {
            priorActionNamePredicate = new PriorActionCallPredicate();
        }

        // tratamento dos argumentos da ação
        // anterior, se existirem
        if (priorAction.hasArguments()) {
            List<Variable> priorActionArguments = priorAction.getArguments();
            Collection priorActionArgumentsPredicateList = new ArrayList();
            Collection priorActionArgumentPredicateList;
            for (int i = 0; i < priorActionArguments.size(); i++) {
                if (!priorActionArguments.get(i).isAvailable()) {
                    priorActionArgumentPredicateList = new ArrayList();
                    for (Object currentPriorActionArgument :
                            priorActionArguments.get(i).getValues()) {
                        priorActionArgumentPredicateList.add(
                                new PriorActionCheckArgumentPredicate(
                                        i,
                                        currentPriorActionArgument
                                )
                        );
                    }
                    priorActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    priorActionArgumentPredicateList
                            )
                    );
                } else {
                    priorActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            priorActionArgumentsPredicateList.add(
                    new PriorActionCountArgumentsPredicate(
                            priorActionArguments.size()
                    )
            );
            priorActionArgumentsPredicateList.add(priorActionNamePredicate);
            priorActionPredicate = PredicateUtils.allPredicate(
                    priorActionArgumentsPredicateList
            );
        } else {
            priorActionPredicate = priorActionNamePredicate;
        }

        // predicado da ação posterior
        Predicate postActionPredicate;

        // tratamento do nome da ação posterior
        Predicate postActionNamePredicate;
        if (!postAction.getName().isAvailable()) {
            Collection postActionNamePredicateList = new ArrayList();
            for (Object currentPostActionName :
                    postAction.getName().getValues()) {
                postActionNamePredicateList.add(
                        new PostActionPredicate((String) currentPostActionName)
                );
            }
            postActionNamePredicate = PredicateUtils.anyPredicate(
                    postActionNamePredicateList
            );
        } else {
            postActionNamePredicate = new PostActionCallPredicate();
        }

        // tratamento dos argumentos da ação
        // posterior, se existirem
        if (postAction.hasArguments()) {
            List<Variable> postActionArguments = postAction.getArguments();
            Collection postActionArgumentsPredicateList = new ArrayList();
            Collection postActionArgumentPredicateList;
            for (int i = 0; i < postActionArguments.size(); i++) {
                if (!postActionArguments.get(i).isAvailable()) {
                    postActionArgumentPredicateList = new ArrayList();
                    for (Object currentPostActionArgument :
                            postActionArguments.get(i).getValues()) {
                        postActionArgumentPredicateList.add(
                                new PostActionCheckArgumentPredicate(
                                        i,
                                        currentPostActionArgument
                                )
                        );
                    }
                    postActionArgumentsPredicateList.add(
                            PredicateUtils.anyPredicate(
                                    postActionArgumentPredicateList
                            )
                    );
                } else {
                    postActionArgumentsPredicateList.add(
                            TruePredicate.truePredicate()
                    );
                }
            }
            postActionArgumentsPredicateList.add(
                    new PostActionCountArgumentsPredicate(
                            postActionArguments.size()
                    )
            );
            postActionArgumentsPredicateList.add(postActionNamePredicate);
            postActionPredicate = PredicateUtils.allPredicate(
                    postActionArgumentsPredicateList
            );
        } else {
            postActionPredicate = postActionNamePredicate;
        }

        // predicado do estado de origem
        Predicate sourcePredicate;
        if (!source.isAvailable()) {
            Collection sourcePredicateList = new ArrayList();
            for (Object currentSource : source.getValues()) {
                sourcePredicateList.add(
                        new SourceStatePredicate((State) currentSource)
                );
            }
            sourcePredicate = PredicateUtils.anyPredicate(sourcePredicateList);
        } else {
            sourcePredicate = TruePredicate.truePredicate();
        }

        // predicado do símbolo
        Predicate symbolPredicate;
        if (!symbol.isAvailable()) {
            Collection symbolPredicateList = new ArrayList();
            for (Object currentSymbol : symbol.getValues()) {
                if (currentSymbol != null) {
                    symbolPredicateList.add(
                            new SymbolPredicate((Symbol) currentSymbol)
                    );
                } else {
                    symbolPredicateList.add(new EpsilonPredicate());
                }
            }
            symbolPredicate = PredicateUtils.anyPredicate(symbolPredicateList);
        } else {
            symbolPredicate = TruePredicate.truePredicate();
        }

        // predicado do estado de destino
        Predicate targetPredicate;
        if (!target.isAvailable()) {
            Collection targetPredicateList = new ArrayList();
            for (Object currentTarget : target.getValues()) {
                targetPredicateList.add(
                        new TargetStatePredicate((State) currentTarget)
                );
            }
            targetPredicate = PredicateUtils.anyPredicate(targetPredicateList);
        } else {
            targetPredicate = TruePredicate.truePredicate();
        }

        // a consulta é a intersecção dos predicados
        Collection<Transition> query = CollectionUtils.select(
                transitions.getTransitions(),
                PredicateUtils.allPredicate(
                        new Predicate[]{
                            sourcePredicate,
                            symbolPredicate,
                            targetPredicate,
                            priorActionPredicate,
                            postActionPredicate
                        }
                )
        );

        // conjuntos de resultados
        Set<Object> sourceResult = new HashSet<>();
        Set<Object> symbolResult = new HashSet<>();
        Set<Object> targetResult = new HashSet<>();
        Set<Object> priorActionNameResult = new HashSet<>();
        List<HashSet<Object>> priorActionArgumentsResult = new ArrayList<>();
        Set<Object> postActionNameResult = new HashSet<>();
        List<HashSet<Object>> postActionArgumentsResult = new ArrayList<>();

        // inicializa o conjunto de argumentos
        // da ação anterior, se existirem
        if (priorAction.hasArguments()) {
            for (Variable argument : priorAction.getArguments()) {
                priorActionArgumentsResult.add(new HashSet<>());
            }
        }

        // inicializa o conjunto de argumentos
        // da ação posterior, se existirem
        if (postAction.hasArguments()) {
            for (Variable argument : postAction.getArguments()) {
                postActionArgumentsResult.add(new HashSet<>());
            }
        }

        // preenche os conjuntos
        for (Transition transition : query) {
            sourceResult.add(transition.getSourceState());
            symbolResult.add(transition.getSymbol());
            targetResult.add(transition.getTargetState());
            priorActionNameResult.add(transition.getPriorActionCall());
            if (priorAction.hasArguments()) {
                for (int i = 0; i < priorAction.getArguments().size(); i++) {
                    priorActionArgumentsResult.get(i).add(
                            transition.getPriorActionArguments()[i]
                    );
                }
            }
            postActionNameResult.add(transition.getPostActionCall());
            if (postAction.hasArguments()) {
                for (int i = 0; i < postAction.getArguments().size(); i++) {
                    postActionArgumentsResult.get(i).add(
                            transition.getPostActionArguments()[i]
                    );
                }
            }
        }

        // preenche o estado de origem
        if (source.isAvailable()) {
            source.setValues(sourceResult);
        }

        // preenche o símbolo
        if (symbol.isAvailable()) {
            symbol.setValues(symbolResult);
        }

        // preenche o estado de destino
        if (target.isAvailable()) {
            target.setValues(targetResult);
        }

        // preenche o nome da ação anterior
        if (priorAction.getName().isAvailable()) {
            priorAction.getName().setValues(priorActionNameResult);
        }

        // preenche os argumentos da ação
        // anterior, se existirem
        if (priorAction.hasArguments()) {
            for (int i = 0; i < priorAction.getArguments().size(); i++) {
                if (priorAction.getArguments().get(i).isAvailable()) {
                    priorAction.getArguments().get(i).setValues(
                            priorActionArgumentsResult.get(i)
                    );
                }
            }
        }

        // preenche o nome da ação posterior
        if (postAction.getName().isAvailable()) {
            postAction.getName().setValues(postActionNameResult);
        }

        // preenche os argumentos da ação
        // posterior, se existirem
        if (postAction.hasArguments()) {
            for (int i = 0; i < postAction.getArguments().size(); i++) {
                if (postAction.getArguments().get(i).isAvailable()) {
                    postAction.getArguments().get(i).setValues(
                            postActionArgumentsResult.get(i)
                    );
                }
            }
        }
    }

}
