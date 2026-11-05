# AGENTS.md: regras para qualquer agente de IA neste repositório

> Rascunho v0 de 05/11/2026. Ainda precisa do ok do Murilo. Mantenha este arquivo curto: o que for longo vai em `docs/agents/`.

## Contexto
- Projeto do Hack Brasil 2026, trilha **Acesso à educação**. Entrega em 30/11 às 23h59.
- Regras do evento: [`docs/hackbrasil/regras-do-evento.md`](docs/hackbrasil/regras-do-evento.md). O regulamento oficial prevalece.
- Repositório **público**. Tudo o que for commitado pode ser lido por qualquer pessoa.

## Regras invioláveis
Cada regra diz o que fazer, por quê e como é conferida. Se uma regra atrapalhar, **pare e pergunte**. Não contorne.

| # | Regra | Por quê | Como é conferida |
|---|---|---|---|
| R1 | Nunca commitar segredo: senha, chave de API, token, `.env`, banco de dados. Só nomes em `.env.example` | Repositório público | gitleaks (a configurar) + revisão antes de cada commit |
| R2 | Nenhum código de fora deste repositório. Só biblioteca ou open source já público, com a licença citada | Regulamento 6.1/6.2: desclassifica | Revisão humana do diff |
| R3 | Nenhum dado pessoal real de aluno ou de terceiros, nem em teste, exemplo ou print | Regulamento 6.9 e LGPD | Revisão humana do diff |
| R4 | Commits sem IA como coautor: nada de `Co-Authored-By` de IA nem "Generated with" | A autoria é das pessoas do time | Hook `commit-msg` (a criar) + CI |
| R5 | Estimativa nunca aparece como nota oficial. Sempre com aviso e erro medido | Honestidade com o aluno e com o jurado | Revisão da tela |
| R6 | Nunca inventar número, fonte ou resultado. Sem medida, escreva "ainda não medi" e como vai medir | Credibilidade | Revisão do texto |
| R7 | Não reescrever histórico nem fazer force push sem pedir | Os commits são acompanhados pela organização | Humano |

## Como trabalhar
- Passo a passo (antes, durante, antes de dizer "pronto"): [`docs/agents/workflow.md`](docs/agents/workflow.md)
- Como responder ao time: [`docs/agents/resposta.md`](docs/agents/resposta.md)
- Decisões importantes viram arquivo em `docs/decisions/`. O padrão é [`docs/templates/problema.md`](docs/templates/problema.md).

## Pontos em aberto (decidir na call)
- Idioma do código e do README.
- Stack final e divisão de papéis.
