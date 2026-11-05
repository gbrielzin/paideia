# Fluxo de trabalho dos agentes

Rascunho v0. Siga na ordem. Pule um passo só se a pessoa disser que pode.

## 1. Antes de começar
- [ ] Li o `AGENTS.md` e as regras R1 a R7.
- [ ] Sei qual é a tarefa em uma frase. Se não souber, pergunto.
- [ ] Estou numa branch própria (`feat/...`, `fix/...`, `docs/...`), nunca direto na `main`.

## 1b. Gancho de história (antes de começar uma funcionalidade)
**Pare e pergunte** antes de escrever o código, quando a tarefa for:
- uma funcionalidade nova que o aluno vai ver, **ou**
- algo que troca um passo manual por um automático, **ou**
- uma decisão de arquitetura ou de framework com mais de uma opção.

Não pergunte em ajuste pequeno, correção de texto ou refatoração sem efeito visível.

A pergunta é sempre esta, curta:
> "Isto pode virar uma história de antes e depois? (1) Qual é o problema, em uma frase? (2) Dá para medir o jeito manual **agora**, antes de construir (tempo, passos ou erros)? (3) Que número vamos registrar?"

Se a resposta for sim:
1. Meça o "antes" **primeiro** e registre em `docs/metricas.md` (commit só do "antes").
2. Construa a funcionalidade.
3. Meça o "depois" do mesmo jeito e complete a linha da tabela.
4. Se houve escolha de arquitetura, registre em `docs/decisions/` e ligue o ID na linha da tabela.

Assim o histórico mostra o "antes" medido **antes** do código, e ninguém precisa lembrar de cabeça.
Se a resposta for não, anote "sem história" no PR e siga em frente.

## 1c. Heurísticas de decisão (regras de bolso)
Toda decisão em `docs/decisions/` diz qual heurística usou e mostra o trade-off na tabela "Alternativas consideradas".
1. **Núcleo na stack que a pessoa domina.** Novidade só onde pode falhar sem derrubar a demo.
2. **Demo no ar vale mais que tecnologia nova.** O jurado julga o que abre (regulamento 7.3).
3. **Meça antes de construir** (gancho de história, seção 1b).
4. **Na dúvida, corte da camada mais externa** (acessibilidade, voz, foto), nunca do núcleo.
5. **Prefira o que dá para desfazer.** Decisão difícil de reverter pede uma linha a mais de justificativa.

## 1d. Visualização (desenho preparado junto)
Se a tarefa mexe em **fluxo, arquitetura, modelo de dados ou layout de pastas**, prepare o desenho **antes de pedir revisão**, não depois.
- **Simples:** Mermaid ou árvore de pastas dentro do `.md` da decisão ou de `docs/arquitetura.md` (o GitHub desenha o Mermaid sozinho).
- **Complexo:** Excalidraw em `docs/evidence/diagrams/`, com o PNG exportado ao lado, e o link no `.md`.
- Atualize `docs/arquitetura.md` se o fluxo principal mudou.
- Sem desenho necessário? Escreva "sem desenho" no PR.

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
