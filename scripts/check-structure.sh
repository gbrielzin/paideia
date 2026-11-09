#!/bin/sh
# R9: confere se só existem arquivos e pastas previstos no mapa do AGENTS.md.
# Para criar algo novo, atualize o mapa do AGENTS.md E as listas abaixo no mesmo PR.

# Raiz. O front (React) fica na raiz e o Java fica em server/ (decisão 0002).
# Os nomes de arquivo do front são os padrões do Vite/React/Tailwind; ajuste se o gerador criar outros.
# data/ guarda a referência em Python da TRI, os casos de teste e o README dos dados.
ROOT_OK="vercel.json .env.example .gitattributes .githooks .github .gitignore AGENTS.md CLAUDE.md LICENSE README.md data docs scripts server eslint.config.js index.html package-lock.json package.json postcss.config.js public src tailwind.config.js tsconfig.app.json tsconfig.json tsconfig.node.json vite.config.js vite.config.ts"
# Dentro de docs/
DOCS_OK="agents arquitetura.md decisions evidence hackbrasil journey metricas.md referencias.md templates"

fail=0

for item in $(git ls-files | cut -d/ -f1 | sort -u); do
  case " $ROOT_OK " in
    *" $item "*) ;;
    *) echo "ERRO (R9): '$item' na raiz não está no mapa." >&2; fail=1 ;;
  esac
done

for item in $(git ls-files docs | cut -d/ -f2 | sort -u); do
  case " $DOCS_OK " in
    *" $item "*) ;;
    *) echo "ERRO (R9): 'docs/$item' não está no mapa." >&2; fail=1 ;;
  esac
done

if [ "$fail" -ne 0 ]; then
  echo "Atualize o mapa no AGENTS.md e as listas em scripts/check-structure.sh, e explique o porquê no PR." >&2
fi
exit "$fail"
