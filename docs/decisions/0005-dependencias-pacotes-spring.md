# 0005: Nomes dos pacotes do Java e dependências iniciais

- **Data:** 06/11/2026
- **Quem participou:** Murilo (criou) e Gabriel (confirmar)
- **Estado:** **PENDENTE.** 

## Problema
Para facilitar o desenvolvimento em **Spring** desde o início do projeto e manter uma arquitetura e estrutura de dados coerentes, é preciso definir nomes dos pacotes Java e quais serão as dependências iniciais do projeto.

## Ação
Para nomes do pacote escolher qual tem mais coerência, para quais serão as dependências iniciais apenas confirmar a lista.

## Alternativas consideradas (trade-offs)

**Group:**
- Opção 1.A: "br.com.paideia"
  - Tem domínio BR e é mais curta.
- Opção 1.B: "com.hackbrasil.paideia"
  - Tem domínio a ver com o hackaton.

**Artifact:**
- Opção 2.A: "paideia"
  - Mais simples e fácil.
- Opção 2.B: "paideia-backend"
  - Melhor na hora de buscar o arquivo de build.

Lista de dependências:
- Spring Web: Framework para desenvolvimento web;
- Lombok: Reduz a quantidade de código necessário:
- Validation: Validação de dados;
- Spring Boot DeTools;
- [Não oficial] SpringDoc OpenAPI: Documentação da API (tabela de endpoints);
- [Não oficial] Spring Dotenv: para permitir o  uso de chaves do .env;

Depêndencias possíveis (retiradas):
- Spring Data JPA: Para persistência de dados, mas como o Java não vai guardar provavelmente não é necessário;
- H2 Database: Banco de dados em memória, não incluído pelo mesmo motivo do JPA;

## O que já sabemos
- Os nomes de pacotes não tem regulamento específico, mas a parte estrutural é importante e vale pontos.
- Os pacotes não oficiais colocados são reconhecidos pela comunidade Spring e são segurs.

## Para decidir (perguntas)
1. Escolher uma opção de nome para artifact e outra para o group
2. Confirmar a lista, verificar se nada está faltando

## Colaboração
Levantado por Murilo ao preparar criação de projeto de Spring (não precisa ser adicionado ao código ainda).

## Resultado com evidência
Ainda não medi. Como medir: após adição do projeto Spring verificar se os nomes do pacote estão certos e todas as dependências foram adicionadas e não há nenhuma a mais.

## Aprendizado
A preencher depois da decisão.
