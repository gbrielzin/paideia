#!/bin/sh
# R4: rejeita mensagens de commit com IA como coautora ou com marca de ferramenta de IA.
# Uso: some-comando | scripts/check-commit-message.sh
# Coautor humano (ex.: o parceiro de time) continua permitido.

msg="$(cat)"

if printf '%s\n' "$msg" | grep -qiE '^co-authored-by:.*(claude|anthropic|copilot|gemini|openai|chatgpt|codex|cursor|devin|noreply@anthropic\.com)'; then
  echo "ERRO (R4): a mensagem tem IA como coautora. Remova a linha Co-Authored-By." >&2
  exit 1
fi

if printf '%s\n' "$msg" | grep -qiE 'generated (with|by) .*(claude|copilot|gemini|chatgpt|codex|cursor)|claude-session:'; then
  echo "ERRO (R4): a mensagem tem marca de ferramenta de IA. Remova essa linha." >&2
  exit 1
fi

exit 0
