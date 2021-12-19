# `AA4J` — Autômatos adaptativos em Java

Este repositório contém o código-fonte da biblioteca `AA4J` para implementação de autômatos adaptativos, de acordo com a teoria proposta em `#5` por José Neto. Três versões da biblioteca são disponibilizadas:

- `original`: como o nome sugere, esta é a implementação original. Para mais detalhes, sugiro consultar `#1` e `#2`.
- `dot`: esta versão foi implementada a partir do código original como suporte ao programa `XML2AA` descrito em `#3` e permite gerar arquivos `.dot` referentes à topologia corrente do autômato em cada passo de execução.
- `metrics`: esta versão insere, como prova de conceito, uma camada de instrumentação ao código original para validação dos resultados descritos em `#4`.

## Requisitos

- Java 7 ou superior.
- Apache Maven.
- Ler a tese de José Neto (ver `#5`).

## Referências

1. Cereda, P. R. M.; José Neto, J.; *AA4J: uma biblioteca para implementação de autômatos adaptativos*. Em: Memórias do X Workshop de Tecnologia Adaptativa — WTA 2016. Escola Politécnica da Universidade de São Paulo, São Paulo. ISBN: 978-85-86686-86-3, pp. 16-26. 28 e 29 de Janeiro, 2016.
2. Cereda, P. R. M. *Macros como mecanismos de abstração em transformações textuais.* Tese de Doutorado, Departamento de Engenharia de Computação e Sistemas Digitais, Escola Politécnica da Universidade de São Paulo, São Paulo, 2018.
3. Cereda, P. R. M.; José Neto, J.; *XML2AA: geração automática de autômatos adaptativos a partir de especificações XML*. Em: Memórias do XI Workshop de Tecnologia Adaptativa — WTA 2017. Escola Politécnica da Universidade de São Paulo, São Paulo. ISBN: 978-85-86686-90-0, pp. 72-81. 26 e 27 de Janeiro, 2017.
4. Cereda, P. R. M.; José Neto, J.; *Towards performance-focused implementations of adaptive devices*. Procedia Computer Science, Volume 109, 2017, Pages 1164-1169, ISSN 1877-0509.
5. Neto, J. J.; *Contribuições à metodologia de construção de compiladores*. Tese de Livre Docência, Escola Politécnica da Universidade de São Paulo, São Paulo, 1993.
