# 0004: divisão de papéis (quem faz o quê)

- **Data:** 05/11/2026
- **Quem participou:** Gabriel e Murilo
- **Estado:** **DECIDIDA em 06/11/2026: opção D** (flexível, proposta pelo Gabriel; o Murilo concordou por mensagem).

## Problema
Temos duas pessoas, ~25 dias até 30/11 e três frentes: **front** (React + Tailwind, na raiz), **back-end** (Java 21 + Spring Boot em `server/`, que roda o cálculo da TRI, decisão [0003](0003-onde-roda-o-calculo-e-privacidade.md)) e **dados** (parâmetros dos itens do INEP, hoje previstos em Python). A **acessibilidade** vive no front e é a camada que o Murilo já conhece. O Murilo prefere back-end, mas disse que fica com os dois se o time precisar. A disponibilidade de cada um varia até 19/11 (Murilo) e em 08 e 15/11 (Gabriel).

## Ação
**Opção D:** na call ou quando trabalham juntos, definem na hora quem faz o quê naquele dia, em tarefas separadas. Sozinho, cada um pode mexer em qualquer parte (front, back-end, dados) sem pedir. Duas regras: antes de começar, `git pull` e avisar o outro em uma linha o que vai mexer; tudo por PR pequeno, que o outro revisa quando puder, sem travar. Se alguém ficar sobrecarregado, trocam e registram aqui.

## Alternativas consideradas (trade-offs)

**Opção A: Murilo no back-end e na revisão de acessibilidade; Gabriel no front e nos dados**
- Vantagem: cada um fica no que prefere ou domina (heurística 1). O Murilo mantém a Java/Spring, que é o núcleo do cálculo, e a experiência dele em acessibilidade entra como revisão do front.
- Trade-off: depende do Gabriel ter conforto com React + Tailwind, o que **não foi verificado**. A acessibilidade fica como revisão, e não como autoria, o que pode chegar tarde.

**Opção B: Murilo no front (com acessibilidade) e no back-end; Gabriel nos dados, na especificação e nos testes da TRI**
- Vantagem: a acessibilidade nasce com quem a conhece, e a lógica de cálculo tem um responsável só.
- Trade-off: o Murilo acumula as duas frentes em três semanas, com a faculdade até 19/11. O Gabriel fica fora do código visível da demo, que pesa na execução técnica.

**Opção C: Murilo no front (com acessibilidade); Gabriel no back-end em Java e nos dados**
- Vantagem: acessibilidade com quem conhece, e carga mais equilibrada.
- Trade-off: o núcleo (cálculo da TRI) fica em Java com quem tem menos prática nessa stack. Contraria a heurística 1 justamente no núcleo.

**Opção D (a escolhida): papéis flexíveis**
- Vantagem: ninguém fica parado esperando o outro; o front é fácil de dividir em React, segundo o Murilo; a carga se ajusta à disponibilidade de cada um.
- Trade-off: sem dono por área, pode haver conflito de merge ou trabalho repetido; as duas regras (aviso e PR pequeno) existem para reduzir isso.

O Murilo chegou a sugerir a opção B antes de ver a D, condicionada aos créditos de IA do rascunho.

## O que já sabemos
- O app de ENEM do Gabriel é em React Native e não usa Tailwind (conferido no `package.json` dele). Esse código não entra aqui (R2). O conforto com Java/Spring não foi medido.
- O Murilo disse ter gerenciado um projeto em React (declaração dele, sem evidência no repositório).
- Uma forma de aproveitar o Python em qualquer opção: o Gabriel escreve a **implementação de referência da TRI em Python** e gera casos de teste (entrada e saída esperada), que o código Java do `server/` precisa reproduzir. Assim o Python valida o Java.

## Para decidir (perguntas)
1. Gabriel: qual o seu conforto com React + Tailwind, de 1 a 5, e com Java/Spring?
2. Murilo: dá para acumular front e back-end nos dias até 19/11?
3. Quem lidera a revisão de acessibilidade e escreve a lista mínima de requisitos do front antes de ele começar?

## Colaboração
Levantado pelo Gabriel ao discutir a acessibilidade.

## Resultado com evidência
Ainda não medi. Como medir: depois de decidir, registrar quem entregou o quê por PR e conferir se a divisão se manteve na semana seguinte.

## Aprendizado
A preencher depois do primeiro PR de front. Como medir: quem abriu cada PR e se houve conflito de merge na primeira semana.
