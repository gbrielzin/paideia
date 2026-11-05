# 0002: front na raiz do repositório e Java em `server/`

- **Data:** 05/11/2026
- **Quem participou:** Murilo (propôs), Gabriel
- **Estado:** decidida. Ainda não há código.

## Problema
Precisamos definir onde fica o front (React) e onde fica o back-end (Java/Spring) no mesmo repositório, para os dois trabalharem sem se atrapalhar e para o mapa de pastas (R9) continuar valendo.

## Ação
- **Front (React + Tailwind) na raiz** do repositório: `src/`, `public/`, `package.json` etc.
- **Back-end (Java 21, Spring Boot 4.1.2)** na pasta `server/`. Ver [`0001`](0001-java-21-spring-boot-4-1.md).
- `data/` fica reservada para scripts de dados em Python, se forem usados.
- Atualizados o mapa do `AGENTS.md` e as listas de `scripts/check-structure.sh`. As pastas `web/` e `api/`, que estavam reservadas, foram retiradas.

## Alternativas consideradas (trade-offs)

**Opção A (escolhida): front na raiz + `server/`**
```
paideia/
├── src/            front (React + Tailwind)
├── public/
├── package.json
├── server/         back-end (Java 21 + Spring Boot)
├── docs/
└── scripts/
```
- Vantagem: é o layout que o gerador do front (Vite) cria por padrão, e é o que o Murilo conhece.
- Trade-off: a raiz mistura arquivos do front com `docs/` e `scripts/`.

**Opção B: `web/` + `server/`**
```
paideia/
├── web/            front
├── server/         back-end
├── docs/
└── scripts/
```
- Vantagem: raiz limpa, cada parte isolada.
- Trade-off: um nível de pasta a mais, e o gerador do front precisa ser apontado para `web/`.

**Opção C: `web/` + `api/`** (primeiro rascunho, já descartada)
- Vantagem: nomes mais explícitos.
- Trade-off: o mesmo da opção B, sem ganho extra sobre ela.

## Colaboração
Proposta do Murilo, registrada pelo Gabriel. Heurística 1 do fluxo de trabalho: usar o formato que a pessoa domina.

## Resultado com evidência
Ainda não medi. Como medir: criar o front e o `server/` e conferir que `scripts/check-structure.sh` passa na CI. Se o gerador do front criar arquivos de raiz que não estão na lista, a CI vai apontar e a lista é atualizada no mesmo PR.

## Aprendizado
A preencher depois do primeiro código.
