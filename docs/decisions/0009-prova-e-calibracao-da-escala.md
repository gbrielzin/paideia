# 0009: prova usada no núcleo e como a escala da nota foi calibrada

- **Data:** 09/11/2026
- **Quem participou:** Gabriel (com apoio de agente de IA). Murilo ainda precisa revisar.

## Problema
O núcleo precisa de uma prova com parâmetros públicos. Sem eles não há como estimar a nota. Também não sabíamos em que escala os parâmetros do INEP estão: o arquivo traz `b` perto de 0 a 4, e não perto de 500.

## Ação
- Prova: **Matemática, ENEM 2025, caderno azul (código 1471)**. É a edição mais recente com itens publicados em 09/11/2026 (o INEP publicou o `ITENS_PROVA_2025.csv`). Fonte e data estão no cabeçalho do JSON e em `data/README.md`.
- Método: modelo logístico de 3 parâmetros com estimativa EAP (média a posteriori), prior normal padrão, `D = 1`.
- Escala: `nota = 500 + 129,7 × θ`. O 129,7 **não vem de documento**: foi ajustado comparando nossa estimativa com `NU_NOTA_MT` de 4000 participantes da prova azul nos microdados públicos. Depois do ajuste, a mediana do desvio é 0,04 ponto.
- Itens abandonados pelo INEP (posições 172 e 174) ficam fora da conta.

## Alternativas consideradas (trade-offs)
**Opção A (escolhida): EAP + escala ajustada nos microdados**
- Vantagem: bate com a nota oficial a centésimos de ponto na amostra e dá o erro da estimativa pelo próprio método.
- Trade-off: o 129,7 é empírico e só foi conferido na prova azul de Matemática de 2025.

**Opção B: máxima verossimilhança (sem prior)**
- Vantagem: não depende de suposição sobre a população.
- Trade-off: quem acerta tudo ou erra tudo não tem estimativa finita. Não testamos; é só um palpite.

**Opção C: D = 1,7 (constante comum na literatura)**
- Trade-off: testado em 600 participantes e piorou o desvio (média de cerca de 32 pontos contra 25 com D = 1 sem ajustar a escala, e correlação menor). Descartada pela medida. Heurística 3 (meça antes de construir).

## Colaboração
Agente de IA baixou o arquivo, escreveu o código e mediu. Gabriel e Murilo revisam o PR.

## Resultado com evidência
Em 4000 participantes (primeiras linhas) e 4000 espalhados, o desvio mediano para a nota oficial é 0,04 ponto e o percentil 99 é 0,15. Exceções: cartão todo em branco (oficial 0) e notas acima de ~930. Detalhes em `data/README.md` e na linha M2 de `docs/metricas.md`.

## Aprendizado
O INEP parece usar exatamente esse método (EAP, prior normal, D = 1) e só muda a escala. Se for usar outra área ou outro ano, o 129,7 precisa ser medido de novo.
