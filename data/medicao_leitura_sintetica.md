# Medição da leitura do cartão em fotos SINTÉTICAS (09/11/2026)

Leitor: `server/src/main/java/br/com/paideia/card/CardReader.java`. Gerador das fotos e da medição (só testes): `server/src/test/java/br/com/paideia/card/SyntheticCard.java` e `SyntheticBenchmarkTest.java`.

Como rodar de novo (demora uns 14 minutos para N = 40 por condição; não roda no CI):
```
cd server
mvn test -Dtest=SyntheticBenchmarkTest -Dpaideia.benchmark=true -Dpaideia.n=40 -Dpaideia.out=../scratch/medicao.md
```
Cada foto: cartão com 45 respostas sorteadas (cerca de 10% em branco) desenhado em memória, entortado por perspectiva sobre um fundo de mesa, com as degradações da condição, salvo como JPEG (qualidade 85) e lido pelo mesmo caminho da API. Sementes fixas: o resultado se repete.

| Condição | Cartões (N) | Recusados | Cartões 45/45 certos | Questões certas | Erros que o leitor mandou conferir |
|---|---|---|---|---|---|
| limpo (sem defeito) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| giro de até 12 graus | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| giro forte, 25 a 35 graus | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| inclinação forte (perspectiva) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| inclinação extrema | 40 | 1 | 34 de 40 | 89,46% (1570 de 1755) | 141 de 185 |
| luz desigual | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| sombra em cima da folha | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| desfoque médio (raio 2) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| desfoque forte (raio 4) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| desfoque extremo (raio 7) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| ruído | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| ruído forte | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| caneta fraca (cinza 100 a 140) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| caneta muito fraca (cinza 140 a 180) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| folha pequena na foto (longe) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| tudo junto (difícil) | 40 | 0 | 40 de 40 | 100,00% (1800 de 1800) | nenhum erro |
| tudo junto, extremo | 40 | 0 | 34 de 40 | 99,67% (1794 de 1800) | 6 de 6 |
| **Total** | 680 | 1 | 668 de 680 | 99,37% (30364 de 30555) | 147 de 191 |

Como ler: "Questões certas" conta cada questão (uma das 45 de cada cartão lido; recusados ficam de fora). Não medi por bolinha individual, só por questão. "Erros que o leitor mandou conferir" = dos erros, quantos estavam na lista `review` (o aluno seria avisado).

## O que a medição achou e mudou no leitor
Na primeira rodada, "giro forte" teve 21 de 40 recusados. A causa era um filtro rígido demais para o quadrado preto (exigia preenchimento de 55% da caixa envolvente; um quadrado girado a 30 graus preenche cerca de 54%). Baixei para 45% e o caso passou a 40 de 40. Um erro do gerador também apareceu: com a folha grande demais, o giro deixava os cantos fora da foto; ajustei a condição. Há teste de regressão para o giro de 30 graus.

## O que esta medição NÃO prova
- **Não é foto de celular de verdade.** O gerador e o leitor compartilham as mesmas medidas do cartão e a mesma ideia de "bolinha preenchida"; foi eu quem escreveu os dois. Acertos de 100% mostram que o leitor funciona nas situações que eu soube simular, não que funcione nas que eu não pensei.
- Não há papel impresso, impressora, caneta de verdade, marcas feitas à mão (borrão, rasura, bolinha só riscada), dobra, reflexo de flash, dedo na foto, foco ruim de lente real, nem diferença de cor do papel.
- A sombra e a luz são simplificadas (gradiente e uma faixa escura suave).
- As marcas fabricadas pelo gerador são discos cheios com pequena variação; marcação real é mais irregular.
- Giro de 90 graus, folha de cabeça para baixo e marca coberta ou cortada fora do enquadramento: não medidos (o leitor assume folha em pé, girada em menos de uns 80 graus).
- **Inclinação extrema** (cantos deslocados até 12% da largura da folha) mostra o limite: 10,5% das questões erradas, e 44 dos 185 erros **não** foram avisados. Não sei se uma foto real de celular chega a essa inclinação.
- Com N = 40 por condição e sementes fixas, não dá para falar em intervalo de confiança.
