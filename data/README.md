# Dados e referência da TRI (Matemática, ENEM 2025, prova azul)

Esta pasta guarda a **implementação de referência em Python** do cálculo da nota e os **casos de teste** que o Java (`server/`) precisa reproduzir (ideia da decisão [0004](../docs/decisions/0004-divisao-de-papeis.md)). Escolha da prova e da escala: [0009](../docs/decisions/0009-prova-e-calibracao-da-escala.md).

## De onde vêm os números
- **Fonte:** microdados do ENEM 2025 do INEP, arquivo `DADOS/ITENS_PROVA_2025.csv`, dentro de <https://download.inep.gov.br/microdados/microdados_enem_2025.zip> (consultado em 09/11/2026). O arquivo é público e legível (CSV com `;`).
- **Prova usada:** Matemática, caderno azul, código de prova 1471, 45 itens (posições 136 a 180).
- **Dois itens não entram na conta:** o INEP marcou as posições 172 e 174 como abandonadas ("problema de convergência" e "previamente exposto") e não publicou parâmetros para elas. Sobram **43 itens**. Quem responde essas duas posições não muda a nota.
- **JSON da prova:** `server/src/main/resources/exams/enem-2025-mt-azul.json`, gerado por `data/build_exam_json.py`. O CSV bruto não vai para o repositório.

## A fórmula em português simples
1. **Cada questão tem três números** (parâmetros públicos do INEP): **a** (o quanto a questão separa quem sabe de quem não sabe), **b** (a dificuldade) e **c** (a chance de acertar chutando).
2. **Habilidade do aluno** é um número chamado theta (θ). Quanto maior, mais o aluno sabe.
3. **Chance de acertar** uma questão para um θ: `P(θ) = c + (1 − c) / (1 + e^(−a · (θ − b)))`. É uma curva em S: com θ muito abaixo de b, só sobra o chute (c); com θ muito acima, chega perto de 100%.
4. **Dado o que o aluno acertou e errou**, para cada θ possível multiplicamos as chances: `P(θ)` onde acertou e `1 − P(θ)` onde errou. Isso diz quão provável é aquele conjunto de respostas para aquele θ.
5. **Ponto de partida:** antes de olhar as respostas, supomos que a maioria dos alunos está perto do meio (distribuição normal com média 0 e desvio 1). Multiplicamos isso também.
6. **θ estimado = a média** de todos os θ possíveis, cada um pesado pela chance dos passos 4 e 5 (método EAP, "esperança a posteriori"). Calculamos em uma grade de −4 a 6, de 0,01 em 0,01.
7. **Erro da estimativa** = o desvio padrão dessa mesma distribuição. Ele diz o quanto a nota poderia variar com as mesmas respostas. É um erro estatístico do modelo, **não** a diferença para a nota oficial.
8. **Nota:** `500 + 129,7 × θ`. O 129,7 foi medido contra notas oficiais públicas (ver abaixo).
9. **Mapa de erros:** para cada questão errada, refazemos a conta como se ela estivesse certa. A diferença de nota é o **custo** daquele erro. A lista sai ordenada do custo mais alto ao mais baixo (empate: a menor posição primeiro). O "revise primeiro" são os primeiros dessa lista.

Branco, letra inválida e letra errada contam igual: erro.

## Quanto isso bate com a nota oficial (medido)
`validate_against_inep.py` pega participantes dos microdados públicos (`RESULTADOS_2025.csv`, fora do repositório), calcula a estimativa com as respostas deles e compara com `NU_NOTA_MT`. Nada individual é impresso nem guardado.

| Amostra | Participantes | Viés médio | Mediana do desvio | Percentil 99 | Acima de 2 pontos |
|---|---|---|---|---|---|
| Primeiras linhas do arquivo | 4000 | +0,075 | 0,041 | 0,141 | 3 |
| Uma a cada 12, espalhada | 4000 | +0,089 | 0,042 | 0,145 | 7 |

O que ficou acima de 2 pontos: quem deixou tudo em branco (a nota oficial é 0, a nossa é 315) e quem ficou perto do topo da escala (acima de uns 930, o desvio chega a alguns pontos). Fora isso o desvio é de centésimos de ponto. Limite do teste: só participantes da prova azul de uma parte do arquivo (as primeiras 172 MB de 2,1 GB); não conferimos as outras cores nem outras áreas.

## Como rodar
```
python data/validate_against_inep.py caminho/RESULTADOS_2025.csv 4000 12   # opcional, precisa do CSV do INEP
python data/make_test_cases.py                                              # regenera os casos de teste
python -m unittest discover -s data                                         # testes da referência
```
`make_test_cases.py` usa **respostas fictícias** sorteadas com semente fixa e grava o mesmo arquivo em `data/` e em `server/src/test/resources/`.

## O que o Java precisa reproduzir
Mesma grade, mesma fórmula, mesma escala. A tolerância é definida no teste do Java.
