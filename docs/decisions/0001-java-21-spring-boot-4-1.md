# 0001: Java 21 com Spring Boot 4.1.2 no back-end

- **Data:** 05/11/2026
- **Quem participou:** Murilo (decidiu a versão), Gabriel
- **Estado:** decidida pelo Murilo. Falta confirmar que o projeto compila e roda com essas versões.

## Problema
O back-end precisa rodar com segurança e ficar no ar durante a avaliação (regulamento 7.3). O Murilo tinha pensado em Java 25. Não conseguimos confirmar se os serviços de hospedagem grátis (Render, Railway, Fly.io) instalam o Java 25 sozinhos. O risco de a demo não abrir pesa mais que o benefício de uma versão nova.

## Ação
Usar **Java 21** (LTS) com **Spring Boot 4.1.2**.

Verificado em 05/11: a documentação oficial do Spring Boot 4.0 diz que ele exige no mínimo o Java 17 e é compatível até o Java 25, então o Java 21 está dentro do intervalo. A página oficial indicava o Spring Boot 4.1.0 como o estável mais recente. **Não verificamos** o intervalo de Java da versão 4.1.x nem que a 4.1.2 existe: confirmar ao gerar o projeto.

## Alternativas consideradas
| Opção | Ganha | Perde |
|---|---|---|
| Java 21 + Spring Boot 4.1.2 (escolhida) | Mais tutoriais, plugins e imagens prontas; menos risco na hospedagem; o Murilo já vai trabalhar com ela | Menos recente |
| Java 25 + Spring Boot 4.x | Versão LTS mais nova | A hospedagem pode não conhecer; exigiria Dockerfile desde o início |
| Sem back-end (só front) | Quase impossível cair | Perde arquitetura e a leitura por foto |

## Colaboração
O Murilo escolheu a versão. O Gabriel levantou o risco de hospedagem e registrou a decisão.

## Resultado com evidência
Ainda não medi. Como medir: gerar o projeto em start.spring.io com essas versões, compilar, rodar uma rota de teste e subir uma vez na hospedagem escolhida, anotando o tempo da primeira visita em `docs/metricas.md` (M4).

## Aprendizado
A preencher depois do primeiro deploy.
