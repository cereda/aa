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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Relatório.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Report {

    // variáveis auxiliares
    private final File file;
    private final List<String> lines;

    /**
     * Construtor.
     * @param file Arquivo do relatório.
     */
    public Report(File file) {
        this.file = file;
        this.lines = new ArrayList<>();
    }

    /**
     * Adiciona uma linha ao relatório.
     * @param format Padrão de formatação da linha.
     * @param values Vetor de objetos.
     */
    public void add(String format, Object... values) {
        lines.add(String.format(Locale.US, format, values));
    }

    /**
     * Grava o relatório em um arquivo.
     */
    public void write() {
        try {
            Files.write(file.toPath(), lines);
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

}
