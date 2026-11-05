#!/bin/sh
# R9: confere se só existem arquivos e pastas previstos no mapa do AGENTS.md.
# Para criar algo novo, atualize o mapa do AGENTS.md E as listas abaixo no mesmo PR.

# Raiz. As pastas web, api e data estão reservadas para o código; ajuste depois da call da stack.
ROOT_OK=".env.example .gitattributes .githooks .github .gitignore AGENTS.md CLAUDE.md LICENSE README.md api data docs scripts web"
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
