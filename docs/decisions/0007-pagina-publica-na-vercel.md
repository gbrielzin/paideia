# 0007: página pública do rascunho na Vercel, no mesmo repositório

- **Data:** 06/11/2026
- **Quem participou:** Gabriel e Murilo
- **Estado:** **DECIDIDA em 06/11/2026.** (A hospedagem da API é outra decisão, reservada como 0006.)

## Problema
O rascunho no painel pede uma página pública própria, em HTTPS, que mostre um título. Precisamos decidir onde ela fica e se vira o front do produto.

## Ação
Publicar na **Vercel** o front React da raiz do repositório (decisão [0002](0002-front-na-raiz-java-em-server.md)). Para o rascunho, uma página mínima com título, problema, solução e links do repositório e do vídeo. Depois ela evolui para o front real.

## Alternativas consideradas (trade-offs)

**Opção A: Vercel, front da raiz do mesmo repositório (escolhida)**
- Vantagem: nada é refeito depois; a Vercel publica a cada commit; os jurados veem um repositório só.
- Trade-off: se o front quebrar, a página pública quebra junto. Reduz-se com a prévia automática da Vercel em cada PR.

**Opção B: página separada em outro repositório ou subpasta**
- Vantagem: a página não depende do estado do front.
- Trade-off: dois lugares para manter; arrisca contrariar o mapa único de pastas (R9).

## Pendente
- Criar a página mínima: título, problema, solução e links do repositório e do vídeo (o vídeo como "em breve" até gravarem).
- Confirmar quem cria a conta da Vercel e se o acesso ao repositório `gbrielzin/paideia` funciona para um colaborador (não verificado).

## Colaboração
Proposta pelo Gabriel; o Murilo perguntou se a página seria o futuro front, e ficou a opção A.

## Resultado com evidência
Ainda não medi. Como medir: o link abrir em HTTPS com título e o painel aceitar a página.

## Aprendizado
A preencher depois da publicação. Limites do plano gratuito da Vercel: não verificados.
