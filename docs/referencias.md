# Referências e ferramentas

Um arquivo só, uma linha por item. Resumos em palavras próprias, com link e data de consulta (R8).
Material bruto de terceiros fica em `scratch/fontes/` (ignorado pelo git).

## 1. Ferramentas do time

Regra do evento: tecnologia é livre, IA e agentes são permitidos e recomendados (6.3, 6.4). Bibliotecas e open source valem, respeitando a licença (6.5). O regulamento não fala de "skills".
**Usar** uma skill como ferramenta é seguro. **Copiar o texto ou o código dela para este repositório** só com licença conferida e fonte citada (R2).

| Ferramenta | Para quê | Quem | Situação em 05/11 |
|---|---|---|---|
| GitHub (`gh`) + GitHub Actions | Repositório, PRs, tarefas, CI | os dois | Ativo |
| [gitleaks](https://github.com/gitleaks/gitleaks) | Barrar segredo antes do commit e na CI (R1) | os dois | A configurar (hook + CI) |
| [AGENTS.md (formato aberto)](https://github.com/agentsmd/agents.md) | Instruções para agentes de código | os dois | Em uso; falta confirmar se o Antigravity e o IntelliJ leem o arquivo |
| Claude Code | Escrever e revisar com IA | Gabriel | Ativo |
| Antigravity e IntelliJ AI | Escrever e revisar com IA | Murilo | Ativo (informado pelo Murilo) |
| Excalidraw | Diagramas e arquitetura | os dois | Desenho em `docs/evidence/diagrams/` (a copiar) |
| Vercel | Página HTTPS e front | Gabriel | Conta existe; projeto novo a criar |
| Playwright / Chrome | Testar telas e tirar prints | Gabriel | Disponível na sessão do Gabriel; Murilo não verificado |
| Patrocinadores: Firecrawl, Cognition, Resend, WorkOS, AbacatePay | Opcionais; a live disse que têm plano grátis | a decidir | Nenhuma é obrigatória |

### Skills e repositórios para avaliar (nenhum é obrigatório)
Estão instaladas só na máquina do Gabriel. O Murilo não as tem, e nada delas entra no repositório sem passar pelo R2.

| Item | O que faz | Para este projeto | Licença |
|---|---|---|---|
| [cloudflare/security-audit-skill](https://github.com/cloudflare/security-audit-skill) | Auditoria de segurança com achados verificados | Revisão antes do envio | conferir |
| [trailofbits/skills](https://github.com/trailofbits/skills) | Skills de revisão de segurança | Alternativa à anterior | conferir |
| [mattpocock/skills](https://github.com/mattpocock/skills) (grilling) | Perguntas até fechar cada decisão | Fechar decisões de produto | conferir |
| [github/spec-kit](https://github.com/github/spec-kit) | Desenvolvimento guiado por especificação | Só como referência de método | conferir |
| [HKUDS/DeepTutor](https://github.com/HKUDS/DeepTutor) | Tutor com IA | Só como referência de ideia; não copiar código | conferir |
| Skills de LGPD ([mukul975](https://skills.sh/mukul975/privacy-data-protection-skills/br), [goul4rt](https://skills.sh/goul4rt/lgpd-skills/lgpd-audit)) | Checagem de LGPD | Apoio à regra R3 e ao item 6.9; não substitui advogado | conferir |

## 2. Hack Brasil

| Fonte | Consulta | O que aprendemos |
|---|---|---|
| [Regulamento](https://hackbrasil.com/regulamento) | 05/11/2026 | Não menciona "rascunho". Créditos aparecem como prêmios (item 10.3). Resumo em `docs/hackbrasil/regras-do-evento.md` |
| [Site oficial e FAQ](https://hackbrasil.com) | 05/11/2026 | O FAQ cita salvar rascunhos em /painel/projeto. Não menciona edições anteriores. Créditos aparecem como prêmios para vencedores e finalistas |
| Painel, etapa "02 rascunho inicial" | 06/11/2026 | Confirma por escrito: a organização revisa o rascunho para liberar os créditos de patrocinadores por integrante, e a etapa não bloqueia a submissão final. Pede trilha, repositório público (README com 300+ caracteres), vídeo de cerca de 3 min com cada integrante na câmera (público ou não listado) e página pública em HTTPS com título. **Prazo do rascunho e tempo de liberação não aparecem na tela** |
| Live de abertura (04/11) | 04/11/2026 | O vídeo diz que o rascunho libera créditos em 24 a 48 horas. O painel confirma os créditos, mas não esse prazo |

## 3. Hackathons de educação (padrão de julgamento)

Notícias de anúncio. Não mostram o raciocínio dos vencedores, só critérios e o tipo de problema.

| Fonte | Consulta | Critérios e o que aprendemos |
|---|---|---|
| [Hackathon de Dados Educacionais (Inep + Fundação Lemann)](https://jornaldebrasilia.com.br/brasilia/inep-e-fundacao-lemann-realizam-neste-fim-de-semana-no-df-hackathon-com-dados-educacionais/) | 05/11/2026 | Interesse público, criatividade e qualidade técnica. Usa dados do Inep, o mesmo domínio do nosso projeto. A página não nomeia vencedores |
| [Hackathon da Educação (Porto Alegre)](https://prefeitura.poa.br/smed/noticias/conhecidos-os-vencedores-do-hackathon-da-educacao) | 05/11/2026 | Impacto em pais e sociedade, execução técnica, **interface e acessibilidade**. O 1º lugar resolveu um problema simples e concreto: ajudar pais a escolher escola |
| [Desafio Tech Estudar (2022)](https://napratica.org.br/conheca-os-projetos-vencedores-do-hackathon-da-fundacao-estudar-para-estudantes-do-ensino-medio/) | 05/11/2026 | A página não diz o critério. O time vencedor citou mentoria e pitch como ajuda |
| [Rubrica Educational Tech (GDG AI Hack)](https://gdgaihack.notion.site/Evaluation-Process-1eebe7b144dd8188a522e4e35111badf) | 05/11/2026 | De outro evento, serve só de ideia: viabilidade técnica pesa mais que demo e implementação |

**Referência fixa:** ainda não escolhida. Aguardar a pesquisa do Murilo.
