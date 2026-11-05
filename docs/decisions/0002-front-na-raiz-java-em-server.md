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

## Alternativas consideradas
| Opção | Ganha | Perde |
|---|---|---|
| Front na raiz + `server/` (escolhida) | É o formato que o gerador do React (Vite) cria por padrão; o Murilo propôs porque é o que ele conhece | A raiz fica com vários arquivos do front misturados com `docs/` e `scripts/` |
| `web/` + `server/` | Raiz limpa, cada parte isolada | Mais um nível de pasta; o gerador do front precisa ser apontado para `web/` |
| `web/` + `api/` (primeiro rascunho) | Nomes mais claros | Já foi trocado por esta |

## Colaboração
Proposta do Murilo, registrada pelo Gabriel. Heurística 1 do fluxo de trabalho: usar o formato que a pessoa domina.

## Resultado com evidência
Ainda não medi. Como medir: criar o front e o `server/` e conferir que `scripts/check-structure.sh` passa na CI. Se o gerador do front criar arquivos de raiz que não estão na lista, a CI vai apontar e a lista é atualizada no mesmo PR.

## Aprendizado
A preencher depois do primeiro código.
