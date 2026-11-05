# Fluxo de trabalho dos agentes

Rascunho v0. Siga na ordem. Pule um passo só se a pessoa disser que pode.

## 1. Antes de começar
- [ ] Li o `AGENTS.md` e as regras R1 a R7.
- [ ] Sei qual é a tarefa em uma frase. Se não souber, pergunto.
- [ ] Estou numa branch própria (`feat/...`, `fix/...`, `docs/...`), nunca direto na `main`.

## 2. Durante
- Mudanças pequenas. Um assunto por commit e por PR.
- Mensagem de commit em uma linha, no formato `tipo(área): o que mudou`.
- Sem IA como coautor na mensagem (R4).
- Se precisar de dado, use dado fictício marcado como fictício (R3).

## 3. Antes de dizer "pronto" (revisão de vazamento)
- [ ] `git diff --staged`: li tudo o que vai subir.
- [ ] Nenhum segredo, `.env`, banco, chave ou token (R1).
- [ ] Nenhum dado pessoal real, nem em print (R3).
- [ ] Nenhum código copiado de fora (R2).
- [ ] Testes e lint passam. Se não rodei, digo que não rodei.
- [ ] Toda estimativa tem aviso e erro medido (R5).

## 4. PR
- Descrição no padrão do `.github/pull_request_template.md`.
- Um humano do time revisa. O agente não faz merge sozinho.

## 5. Se algo der errado
- Segredo foi commitado: **parar**, avisar o time, revogar a chave. Apagar o arquivo não basta, o histórico guarda.
- Dúvida sobre regra do evento: ver `docs/hackbrasil/regras-do-evento.md` e, se preciso, escrever para oi@hackbrasil.com.
