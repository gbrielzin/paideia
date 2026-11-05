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
| R1 | Nunca commitar segredo: senha, chave de API, token, `.env`, banco de dados. Só nomes em `.env.example` | Repositório público | Hook `pre-commit` com gitleaks + CI + revisão antes de cada commit |
| R2 | Nenhum código de fora deste repositório. Só biblioteca ou open source já público, com a licença citada | Regulamento 6.1/6.2: desclassifica | Revisão humana do diff |
| R3 | Nenhum dado pessoal real de aluno ou de terceiros, nem em teste, exemplo ou print | Regulamento 6.9 e LGPD | Revisão humana do diff |
| R4 | Commits sem IA como coautor: nada de `Co-Authored-By` de IA nem "Generated with" | A autoria é das pessoas do time | Hook `commit-msg` + CI |
| R5 | Estimativa nunca aparece como nota oficial. Sempre com aviso e erro medido | Honestidade com o aluno e com o jurado | Revisão da tela |
| R6 | Nunca inventar número, fonte ou resultado. Sem medida, escreva "ainda não medi" e como vai medir | Credibilidade | Revisão do texto |
| R7 | Não reescrever histórico nem fazer force push sem pedir | Os commits são acompanhados pela organização | Humano |
| R8 | Texto, artigo, transcrição ou imagem de terceiros, por inteiro, só em `scratch/fontes/` (ignorado pelo git). No repositório vai apenas um resumo em palavras próprias, com link e data, em `docs/referencias.md` | Direitos autorais e regulamento | Revisão humana do diff |
| R9 | Não criar pasta nem arquivo fora do mapa abaixo. Se faltar lugar, atualize o mapa no mesmo PR e diga por quê | Evitar bagunça: tudo tem um lugar só | Revisão humana do diff |

## Onde as coisas ficam (mapa único)
- Raiz: `AGENTS.md`, `CLAUDE.md`, `README.md` e os arquivos de configuração (`.gitignore`, `.gitattributes`, `.env.example`, `LICENSE`).
- `.githooks/` e `scripts/`: travas locais (R1 e R4). Depois de clonar, rode `sh scripts/setup-hooks.sh` e instale o gitleaks (`winget install Gitleaks.Gitleaks`).
- `.github/`: modelo de PR e `workflows/guardrails.yml` (a CI que confere R1 e R4 depois do push).
- `docs/hackbrasil/`: regras do evento e resumo da live.
- `docs/agents/`: fluxo de trabalho e estilo de resposta.
- `docs/decisions/`: uma decisão por arquivo, no modelo de `docs/templates/problema.md`.
- `docs/journey/`: linha do tempo escrita. `docs/evidence/`: prints e diagramas aprovados, com legenda no `journey/`.
- `docs/referencias.md`: um arquivo só, uma linha por fonte (link, data, o que aprendemos).
- `docs/metricas.md`: um arquivo só, uma linha por número medido (antes, depois, como medimos).
- `docs/arquitetura.md`: um arquivo só, criado depois da call: diagrama, como funciona o fluxo e links para as decisões (stack e framework).
- `scratch/fontes/`: material bruto de terceiros, **ignorado pelo git**.
- **Front (React)** na raiz do repositório (`src/`, `public/`, `package.json` etc.). **Back-end Java/Spring** em `server/`. `data/` reservada para scripts de dados, se houver. Decisão: [`0002`](docs/decisions/0002-front-na-raiz-java-em-server.md).
- **Este mapa é conferido por máquina:** `scripts/check-structure.sh` (rodado na CI) falha se existir algo fora dele. Ao criar algo novo, atualize o mapa e as listas do script no mesmo PR.

## Prioridades de esforço (orientação, não regra inviolável)
Quando o tempo apertar, nesta ordem. Os 4 critérios do evento valem o mesmo (25 pontos cada).
1. **Demo ponta a ponta, no ar e funcionando** (execução técnica).
2. **Vídeo de até 3 minutos** mostrando o produto funcionando (comunicação).
3. **Encaixe na trilha:** o problema e o público dentro de "Acesso à educação" (regulamento 5.4).
4. **Código legível**, com decisões registradas em `docs/decisions/` (implementação).
5. **Documentação e prints** ao redor de tudo isso, em torno de 10% do tempo. É uma estimativa do time, não uma regra do evento.

**Regra de corte:** se atrasar, tire da camada mais externa para a mais interna (acessibilidade, depois voz, depois foto). A tabela manual, o mapa de erros e o "o que revisar agora" não saem.

## Como trabalhar
- Passo a passo (antes, durante, antes de dizer "pronto"): [`docs/agents/workflow.md`](docs/agents/workflow.md)
- **Antes de qualquer funcionalidade nova, pare e faça a pergunta do "gancho de história"** (seção 1b do fluxo): dá para medir o jeito manual agora e virar um antes e depois em `docs/metricas.md`?
- Como responder ao time: [`docs/agents/resposta.md`](docs/agents/resposta.md)
- Decisões importantes viram arquivo em `docs/decisions/`. O padrão é [`docs/templates/problema.md`](docs/templates/problema.md).

## Pontos em aberto (decidir na call)
- Idioma do código e do README.
- Stack final e divisão de papéis.
