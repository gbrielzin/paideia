# 🎓 Paideia · O Mapa de Erros Inteligente para o ENEM

> **Trilha 03:** Acesso à Educação | **Hack Brasil 2026**  
> *Transformando a opacidade da Teoria de Resposta ao Item (TRI) em um plano de estudo acionável e prioritário para estudantes do ensino público.*

---

## 📌 O Problema

Para o estudante de escola pública que se prepara para o ENEM sem cursinho preparatório, entender o resultado de um simulado ou prova anterior é um desafio:
* **Falta de Clareza na TRI:** O aluno sabe quantas questões errou, mas **não sabe quanto cada erro custou na sua nota final**.
* **Estudo por Intuição:** Sem saber quais erros tiveram maior peso na nota, o estudante perde tempo revisando matérias de baixo impacto.
* **Privacidade e Acessibilidade:** Soluções existentes exigem cadastros complexos, retêm dados dos alunos ou ignoram requisitos básicos de acessibilidade.

---

## 🚀 A Solução

O **Paideia** é uma plataforma open-source que traduz os parâmetros estatísticos do INEP ($a, b, c$) em um **Mapa de Erros Por Custo em Pontos**, entregando exatamente o que o aluno precisa revisar primeiro.

### Principais Funcionalidades

* 📝 **Entrada Flexível:** Preenchimento por tabela manual (base garantida), leitura de cartão-resposta via OCR (foto) e entrada por voz.
* 📊 **Engine TRI e Medição de Erro:** Estimativa calibrada com base nos itens oficiais do INEP e gabarito oficial, exibindo a margem de erro medido.
* 🎯 **Mapa de Erros Quantificado:** Exibição clara do tipo: *"Esta questão custou 18,5 pontos na sua nota"*.
* 📚 **Recomendação Prioritária:** Algoritmo que seleciona os **3 temas de maior impacto** para revisão imediata.
* ♿ **Acessibilidade Nativa:** Suporte a leitor de tela (síntese de voz), alto contraste e simplificação de texto (Linguagem Simples).
* 🔒 **Privacidade:** a API calcula o mapa de erros a partir das respostas enviadas e **não as armazena** (sem banco e sem registro do conteúdo delas). Compromisso de projeto, a ser verificado no código (ver [decisão 0003](docs/decisions/0003-onde-roda-o-calculo-e-privacidade.md)). A demo usa só dados fictícios ou do próprio aluno, nunca dados pessoais de terceiros (regulamento 6.9).

---

## 🛠️ Arquitetura e Tecnologias

| Módulo | Tecnologia | Descrição |
| :--- | :--- | :--- |
| **API Backend** | Java 21 + Spring Boot 4.1.2(ver [decisão 0001](docs/decisions/0001-java-21-spring-boot-4-1.md)) | ... |
| **TRI Data Engine** | Python | ... |
| **Frontend UI** | React | ... |
| **AI Agents & Tooling** | Antigravity, Claude Code, JunieAI, | ... |
| **Deploy** | Vercel | ... |
| **IDEs** | Intellij IDEA, | ... |

---

## ⚙️ Como Executar o Projeto Localmente

**Pré-requisitos:**
- Java 21+
- Maven 3.8+
- Node.js xx
- Python xx

### 1. Clonar repositório

```bash
git clone [https://github.com/gbrielzin/paideia.git](https://github.com/gbrielzin/paideia.git)
cd paideia
```

### 2. Ativar travas (para desenvolvimento)

Feito apenas uma vez após a clonagem

**Windows**:
```bash
winget install Gitleaks.Gitleaks
sh scripts/setup-hooks.sh
```

**Linux** (mude o gerenciador de pacotes conforme distribuição):
```bash
sudo pacman -S gitleaks
sh scripts/setup-hooks.sh
```

### 3. Se atentar ao contexto

**Leia o AGENTS.md, que tem as regras do projeto, vale para pessoas e para IA.**

As pastas de dependências (node_modules/, target/, .venv/ e similares) são baixadas na sua máquina pelo gerenciador de pacotes e não vão para o GitHub: elas já estão no .gitignore.

### 4. Executar API Spring

```bash
cd server
mvn clean install
mvn spring-boot:run
```

### 5. Executar o Frontend

```bash
cd ..
npm install
npm run dev
```

## Equipe

| Foto | LinkedIn | GitHub |
|:----:|------------|-----|
| <img src="https://avatars.githubusercontent.com/u/258125376?v=4" alt="Foto de Gabriel" width="80" /> | [Gabriel Cardoso Santos](https://www.linkedin.com/in/gabriel-cardoso-santos-43b474375/) | [@gbrielzin](https://github.com/gbrielzin) |
| <img src="https://avatars.githubusercontent.com/u/161900046?v=4" alt="Foto de Murilo" width="80" /> | [Murilo de Souza](https://linkedin.com/in/murilo-a-souza) | [@murilo-a-souza](https://github.com/murilo-a-souza) |

## 📜 Licença e Termos

Este projeto é distribuído sob a licença MIT. Consulte o arquivo [LICENSE](https://github.com/gbrielzin/paideia/blob/main/LICENSE) para mais detalhes.

Declaração de Avaliação: Conforme o [regulamento do Hack Brasil 2026](https://hackbrasil.com/regulamento), é concedida permissão total ao júri para leitura, compilação e teste deste código-fonte.