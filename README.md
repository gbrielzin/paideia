# paideia

Projeto do Hack Brasil 2026, trilha **Acesso à educação**.

> Rascunho. A descrição do produto, o vídeo e a demo entram depois da definição da peça e da stack.

## Primeiros passos para quem entra no time
Faça uma vez, depois de clonar o repositório (o repositório é público, então estas travas evitam subir senha ou chave por engano):

1. Instale o gitleaks (Windows): `winget install Gitleaks.Gitleaks`
2. Ative as travas: `sh scripts/setup-hooks.sh`
3. Leia o [`AGENTS.md`](AGENTS.md), que tem as regras do projeto, vale para pessoas e para IA.

As pastas de dependências (`node_modules/`, `target/`, `.venv/` e similares) são baixadas na sua máquina pelo gerenciador de pacotes e **não vão para o GitHub**: elas já estão no `.gitignore`.

## Licença
MIT. Veja [`LICENSE`](LICENSE).
