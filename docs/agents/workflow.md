# Fluxo de trabalho dos agentes

Rascunho v0. Siga na ordem. Pule um passo só se a pessoa disser que pode.

## 1. Antes de começar
- [ ] Li o `AGENTS.md` e as regras R1 a R7.
- [ ] Sei qual é a tarefa em uma frase. Se não souber, pergunto.
- [ ] Estou numa branch própria (`feat/...`, `fix/...`, `docs/...`), nunca direto na `main`.

## 0. Abertura do dia (máximo 10 minutos)
Antes de qualquer trabalho, os dois respondem estas perguntas e o agente escreve o **Plano do dia** no topo de `docs/journey/AAAA-MM-DD.md`:
1. **Onde paramos?** Ler "Próximos passos" e "Em andamento com" do último resumo.
2. **Quanto tempo temos?** Dias até 30/11 às 23h59 e horas reais de cada um hoje (lembrar as datas de pouca disponibilidade).
3. **Qual é o resultado de hoje, em uma frase?** Um só. Se forem dois, o segundo vira "se sobrar".
4. **O que está bloqueado ou depende de decisão?** Listar em ordem de dependência (seção 1e).
5. **Tem algo para medir antes de construir?** Gancho de história (seção 1b).
6. **Quem está com qual arquivo hoje?** Preencher "Em andamento com" (seção 1a).
7. **Estamos no prazo?** Se não, qual camada cortamos (regra de corte do `AGENTS.md`)?
8. **O que vamos documentar hoje?** Call, prints, entrevistas (seção 1f).

Depois disso, siga para a seção 1a e comece.

## 1a. Sincronizar e ver quem está com o quê
Antes de editar qualquer coisa:
- [ ] `git pull --rebase` (no dia 05/11 um push foi recusado porque o Murilo tinha subido um README no meio).
- [ ] Olhe a seção "Em andamento com" do resumo do dia mais recente em `docs/journey/`.
- [ ] **Não edite um arquivo que está com outra pessoa** sem avisar. Se for urgente, proponha a mudança em vez de aplicar.

## 1e. Planejar antes de decidir
Antes de resolver qualquer decisão, escreva o plano em 4 linhas (no resumo do dia ou na decisão):
1. **O que precisa ser decidido** e **quem decide**.
2. **Do que depende** (ordem: decida primeiro o que destrava os outros).
3. **O que verificar antes** (fato, medida ou teste barato).
4. **Qual o próximo passo** se a resposta for sim, e se for não.
Só depois disso abra o `docs/decisions/NNNN`.

## 1f. Oportunidade de documentar (o agente cobra)
O agente fica atento e **pergunta na hora**, em uma frase, quando acontecer algo da tabela:

| Gatilho | O que cobrar |
|---|---|
| Call ou reunião (começou, terminou ou foi combinada) | Início, fim, **onde** (Discord, WhatsApp, Meet), **por quê**, qual era o problema, o que saiu. Vai para "Tempo juntos" do dia. Horas reais, nunca estimadas (R6) |
| Decisão tomada em conversa | Registrar em `docs/decisions/` ou marcar "pendente de registro" |
| Conversa de WhatsApp que mostra um problema, uma decisão ou um combinado | Sugerir print. Antes: o outro concordou em aparecer? Telefone e nome ocultos (R3). Salvar em `docs/evidence/screenshots/NN_descricao_AAAA-MM-DD.png` e legenda em `docs/journey/` |
| Conversa ou entrevista com aluno ou pessoa de teste | Perfil **sem nome**, perguntas, resposta. Conta na linha M5 de `docs/metricas.md` (meta: 5). Pedir permissão |
| Passo manual que poderia ser medido | Gancho de história (seção 1b) |

Formato da cobrança: "Vi uma chance de documentar: [o quê]. Registro agora ou deixo pendente no resumo do dia?"
Se a resposta for "pendente", entra em **Pendências de documentação** do dia, e o fechamento lista o que ficou sem registro.
Limites: não interromper tarefa pequena e cobrar uma vez por gatilho. O agente só sabe o que você conta: ele não enxerga o WhatsApp nem a call.

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

## 6. Fechamento do dia (e quando o projeto para)
Se o trabalho parar, por fim de dia ou por qualquer motivo ("vamos parar", "encerrar", "resumo do dia"), **o resumo é atualizado antes**. O agente cria ou completa `docs/journey/AAAA-MM-DD.md` a partir de `docs/templates/dia.md`:
1. **Feito hoje**: decisões com ID, PRs, o que foi publicado.
2. **Tempo juntos**: calls e sessões com início, fim, horas, onde, por quê, problema e resultado.
3. **Números medidos** (ou "nada medido").
4. **Decisões pendentes**, em ordem de dependência, com diagrama Mermaid.
5. **Em andamento com**: quem está com qual arquivo.
6. **Evidências**: prints tirados e prints a tirar (com consentimento e R3 conferidos).
7. **Pendências de documentação** (seção 1f) e **entrevistas em aberto**.
8. **Próximos passos**, no máximo 5, na ordem.

Atualizações menores durante o dia: quando muda quem está com um arquivo, só a linha de "Em andamento com"; quando uma decisão fecha, "Feito hoje" e "Decisões pendentes".
Formato visual: Mermaid dentro do `.md`; Excalidraw em `docs/evidence/diagrams/` se o dia teve fluxo complexo.
Termine com commit e push, para o outro ver ao puxar.
