# Registro de números

Um arquivo só, uma linha por medida. É daqui que saem o README, o vídeo e o pitch.

## Regras
- **Registre na hora.** Mediu no PR? Anote aqui no mesmo PR.
- **Só número real.** Sem medida, a linha fica como "ainda não medi" e diz como vai medir (R6).
- **Todo número fecha com uma frase** do que ele significa.
- **Antes e depois:** "antes" é o processo manual ou o estado anterior, medido de verdade, não estimado de cabeça.
- **Estimativa do produto nunca é nota oficial** e sempre tem o erro medido (R5).

## Tabela

| ID | O que medimos | Antes | Depois | Como medimos | Data | Quem | O que significa (1 frase) | Ligado a (decisão, PR) |
|---|---|---|---|---|---|---|---|---|
| M1 | Tempo para o aluno sair das respostas marcadas e chegar a "quais erros custaram mais" | ainda não medi | ainda não medi | Cronometrar 3 pessoas fazendo à mão (gabarito + planilha) e depois na ferramenta | | | | |
| M2 | Erro da estimativa de nota contra a nota oficial, por área | ainda não medi neste repositório | Matemática 2025, prova azul: mediana do desvio 0,04 ponto e percentil 99 de 0,15, em 4000 participantes; 3 de 4000 acima de 2 pontos (cartão em branco e notas acima de ~930). Outras áreas e cores: ainda não medi | `data/validate_against_inep.py` compara a estimativa com `NU_NOTA_MT` dos microdados públicos do INEP | 09/11/2026 | Gabriel (agente de IA rodou) | A estimativa reproduz a nota oficial de Matemática da prova azul de 2025 com folga de centésimos de ponto na amostra testada | [0009](decisions/0009-prova-e-calibracao-da-escala.md) |
| M3 | Alunos que testaram com dado deles, sem nos dar dado pessoal | 0 | ainda não medi | Contar quem usou a demo | | | | |
| M4 | Tempo da demo no ar, do primeiro acesso ao mapa de erros | ainda não medi | ainda não medi | Cronometrar na demo hospedada | | | | |
| M5 | Entrevistas de validação do problema com estudantes (meta: 5), sem coletar dado pessoal | 0 | ainda não medi | Contar entrevistas feitas; perguntas em `docs/journey/` do dia | | | | |
| M6 | Acerto da leitura do cartão em fotos **sintéticas** (por questão e por cartão) | ainda não medi (sem leitura) | 680 cartões em 17 condições: 668 de 680 cartões com 45/45 certos, 99,37% das questões certas (30364 de 30555), 1 recusado. Só as duas condições extremas erraram; nas outras 15, 100% | `SyntheticBenchmarkTest`, detalhes e limites em `data/medicao_leitura_sintetica.md` | 09/11/2026 | Gabriel (agente de IA rodou) | O leitor funciona nas situações simuladas; **não prova nada sobre foto de celular de verdade** | [0010](decisions/0010-leitura-do-cartao-resposta-por-foto.md) |
| M7 | Acerto da leitura do cartão em **fotos reais de celular** | ainda não medi | ainda não medi | Imprimir o PDF, preencher a caneta com respostas fictícias anotadas, fotografar com 2 ou mais celulares em condições variadas (pelo menos 30 fotos), contar cartões 45/45, questões certas e recusadas, sempre com o N ao lado | | | | [0010](decisions/0010-leitura-do-cartao-resposta-por-foto.md) |
