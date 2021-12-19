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
package br.usp.poli.lta.cereda.aa.dot;

import br.usp.poli.lta.cereda.aa.model.State;
import br.usp.poli.lta.cereda.aa.model.Submachine;
import br.usp.poli.lta.cereda.aa.model.Transition;
import br.usp.poli.lta.cereda.aa.model.sets.Mapping;
import br.usp.poli.lta.cereda.aa.model.sets.SubmachinesSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementa a representação gráfica do autômato adaptativo.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Dot {

    // contém o resultado da conversão do
    // autômato em sua representação gráfica
    private final StringBuilder sb;
    
    // variáveis auxiliares para mapeamento
    private final Map<String, String> states;
    private final Set<String> accepting;
    
    // prefixos dos estados e rótulos
    // de submáquinas do autômato
    private final String STATE_PREFIX;
    private final String SUBMACHINE_PREFIX;

    // objetos do autômato corrente
    private final Mapping mapping;
    private final SubmachinesSet submachines;
    private final String main;

    /**
     * Construtor.
     * @param mapping Mapeamento.
     * @param submachines Conjunto de submáquinas.
     * @param main Submáquina principal.
     */
    public Dot(Mapping mapping, SubmachinesSet submachines, String main) {
        this.mapping = mapping;
        this.submachines = submachines;
        this.main = main;

        sb = new StringBuilder();
        states = new HashMap<>();
        accepting = new HashSet<>();

        STATE_PREFIX = "q";
        SUBMACHINE_PREFIX = "sm";

        sb.append("digraph aa {").append("\n");
        sb.append("\t").append("rankdir=LR;").append("\n");

    }

    /**
     * Realiza o mapeamento dos estados do autômato.
     * @param mapping Mapeamento do autômato.
     * @param submachines Submáquinas.
     */
    private void mapStates(Mapping mapping, SubmachinesSet submachines) {
        for (Transition t : filter(mapping.getTransitions())) {
            addState(t.getSourceState());
            addState(t.getTargetState());
        }
        for (Submachine submachine : submachines.getSubmachines()) {
            for (State state : submachine.getStates()) {
                addState(state);
            }
        }
    }

    /**
     * Adiciona o estado no mapeamento.
     * @param state Estado do autômato.
     */
    private void addState(State state) {
        if (!states.containsKey(state.toString())) {
            states.put(
                    state.toString(),
                    STATE_PREFIX.concat(state.toString())
            );
        }
    }

    /**
     * Desenha a representação gráfica do autômato.
     */
    private void draw() {
        mapStates(mapping, submachines);
        mapAcceptingStates(submachines);
        drawStates();
        updateSubmachines(submachines);
        drawTransitions(mapping);
        close();
    }

    /**
     * Retorna a representação gráfica do autômato.
     * @return Representação gráfica do autômato.
     */
    public String dot() {
        draw();
        return sb.toString();
    }

    /**
     * Desenha os estados do autômato.
     */
    private void drawStates() {
        for (Map.Entry<String, String> entry : states.entrySet()) {
            sb.append("\t");
            sb.append(
                    drawState(
                            entry.getKey(),
                            entry.getValue(),
                            accepting.contains(entry.getKey())
                    )
            );
            sb.append("\n");
        }
    }

    /**
     * Desenha as transições do autômato.
     * @param mapping Mapeamento.
     */
    private void drawTransitions(Mapping mapping) {
        for (Transition t : filter(mapping.getTransitions())) {
            sb.append("\t").append(drawTransition(t)).append("\n");
        }
    }

    /**
     * Desenha uma transição do autômato.
     * @param t Transição.
     * @return Representação gráfica da transição.
     */
    private String drawTransition(Transition t) {
        String text = t.isEpsilonTransition()
                ? "ε"
                : (t.isSubmachineCall()
                        ? t.getSubmachineCall()
                        : t.getSymbol().toString());

        String priorFunction = t.hasPriorActionCall()
                ? t.getPriorActionCall().
                concat(
                        buildArguments(t.getPriorActionArguments()
                        )
                )
                : "";

        String postFunction = t.hasPostActionCall()
                ? t.getPostActionCall().
                concat(
                        buildArguments(t.getPostActionArguments()
                        )
                )
                : "";

        String functions = priorFunction.isEmpty() && postFunction.isEmpty()
                ? ""
                : (!priorFunction.isEmpty() && postFunction.isEmpty()
                        ? priorFunction.concat(" •")
                        : (priorFunction.isEmpty()
                        && !postFunction.isEmpty()
                                ? "• ".
                                concat(postFunction)
                                : priorFunction.
                                concat(" • ").
                                concat(postFunction)));

        text = text.concat(
                functions.isEmpty()
                        ? ""
                        : ", ".concat(functions)
        );

        String color = t.isSubmachineCall() ? "black:invis:black" : "black";

        String pattern = "%s -> %s [ label = \"%s\", color = \"%s\" ];";
        return String.format(
                pattern,
                states.get(t.getSourceState().toString()),
                states.get(t.getTargetState().toString()),
                text,
                color
        );
    }

    /**
     * Constrói os argumentos de uma função adaptativa para sua representação
     * gráfica.
     * @param objects Vetor de objetos.
     * @return Representação gráfica de uma função adaptativa.
     */
    private String buildArguments(Object[] objects) {
        return "(".
                concat(
                        objects == null
                                ? ""
                                : StringUtils.join(objects, ", ")
                ).
                concat(")");
    }

    /**
     * Desenha um estado.
     * @param key Rótulo.
     * @param value Valor de representação.
     * @param accepting Sinalizador que determina se o estado corrente é de
     * aceitação ou retorno.
     * @return Representação gráfica do estado.
     */
    private String drawState(String key, String value, boolean accepting) {
        String pattern = "node [ shape=%s, color=black, "
                + "fontcolor=black, label=\"%s\" ]; %s;";
        return String.format(
                pattern,
                accepting ? "doublecircle" : "circle",
                key,
                value
        );
    }

    /**
     * Realiza o mapeamento dos estados de aceitação do autômato.
     * @param submachines Submáquinas.
     */
    private void mapAcceptingStates(SubmachinesSet submachines) {
        for (Submachine submachine : submachines.getSubmachines()) {
            for (State state : submachine.getAcceptingStates()) {
                accepting.add(state.toString());
            }
        }
    }

    /**
     * Filtra as transições, removendo as transições de retorno de submáquina.
     * @param transitions Conjunto de transições.
     * @return Conjunto de transições sem transições de retorno de submáquina.
     */
    private Set<Transition> filter(Set<Transition> transitions) {
        Set<Transition> result = new HashSet<>();
        for (Transition t : transitions) {
            if (!t.isSubmachineReturn()) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Atualiza a representação das submáquinas.
     * @param submachines Submáquinas.
     */
    private void updateSubmachines(SubmachinesSet submachines) {
        for (Submachine submachine : submachines.getSubmachines()) {
            updateSubmachine(submachine);
        }
    }

    /**
     * Atualiza a submáquina, adicionando o rótulo.
     * @param s Submáquina.
     */
    private void updateSubmachine(Submachine s) {
        sb.append("\t").append(createSubmachineLabel(s)).append("\n");
        sb.append("\t").append(
                String.format(
                        "%s -> %s;",
                        SUBMACHINE_PREFIX.concat(s.getName()),
                        states.get(s.getInitialState().toString())
                )
        ).append("\n");
    }

    /**
     * Cria o rótulo da submáquina.
     * @param s Submáquina.
     * @return Representação gráfica da submáquina.
     */
    private String createSubmachineLabel(Submachine s) {
        String pattern = "node [ shape = plaintext, color = white, "
                + "fontcolor = black, label = \"%s\" ]; %s;";
        return String.format(
                pattern,
                s.getName().equals(main)
                    ? s.getName().concat(" (main)")
                    : s.getName(),
                SUBMACHINE_PREFIX.concat(s.getName())
        );
    }

    /**
     * Encerra a representação gráfica.
     */
    private void close() {
        sb.append("}").append("\n");
    }
}
