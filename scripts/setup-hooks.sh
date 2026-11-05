#!/bin/sh
# Ativa as travas locais (R1 e R4). Rode uma vez depois de clonar o repositório.
git config core.hooksPath .githooks
echo "Travas ativadas: .githooks/pre-commit (segredos) e .githooks/commit-msg (sem IA como coautora)."
if ! command -v gitleaks >/dev/null 2>&1; then
  echo "Falta instalar o gitleaks. Windows: winget install Gitleaks.Gitleaks"
fi
