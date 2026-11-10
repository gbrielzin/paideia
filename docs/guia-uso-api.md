# Uso da API Tabela de Endpoint

O servidor de back-end tem três requisições, descritas na tabela abaixo:


| Método | Endpoint                            | Descrição                                                                           |
|--------|-------------------------------------|-------------------------------------------------------------------------------------|
| `GET`    | `/api/v1/exams`                       | Lista todos os cadernos/provas do ENEM disponíveis                                  |
| `GET`    | `/api/v1/exams/{year}/{area}/{color}` | Retorna detalhes e todas as questões/itens de uma prova                             |
| `POST`   | `/api/v1/evaluations/calculate`       | Principal: Envia respostas do aluno e recebe a nota TRI, erro e top 3 recomendações |

## A. Listar provas disponíveis

- **Requisição**: `GET /api/v1/exams`
- **Status de Sucesso**: `200 OK`
- **Corpo enviado**: Nenhum (vazio)
- **Exemplo de JSON Retornado (Array de `InepExamSummaryDto`)**:
```json
[
  {
    "year": 2024,
    "knowledgeArea": "MATHEMATICS",
    "knowledgeAreaDescription": "Matemática e suas Tecnologias",
    "bookletColor": "BLUE",
    "bookletColorDescription": "Azul",
    "totalQuestions": 45
  }
]
```

## B. Consultar questões de uma prova

- **Requisição**: `GET /api/v1/exams/{year}/{knowledgeArea}/{color}`
    - *year*: ano da prova (ex: 2024)
    - *area*: área de conhecimento (`MATHEMATICS`, `NATURAL_SCIENCES`, `HUMAN_SCIENCES`, `LANGUAGES`)
    - *color*: cor do caderno (`BLUE`, `YELLOW`, `WHITE`, `PINK`, `GRAY`)
- **Corpo enviado**: Nenhum (vazio)
- **Status de Sucesso**: `200 OK`
- **Exemplo de JSON Retornado (`InepExamDetailDto`)**:
```json
{
  "year": 2024,
  "knowledgeArea": "MATHEMATICS",
  "knowledgeAreaDescription": "Matemática e suas Tecnologias",
  "bookletColor": "BLUE",
  "bookletColorDescription": "Azul",
  "totalQuestions": 45,
  "items": [
    {
      "questionNumber": 136,
      "correctAlternative": "B",
      "discriminationA": 1.85,
      "difficultyB": -1.2,
      "guessingC": 0.18,
      "topic": "Grandezas Proporcionais e Escalas",
      "skillCode": "H1",
      "skillDescription": "Reconhecer e aplicar grandezas direta ou inversamente proporcionais e leitura de escalas.",
      "difficultyLevel": "EASY"
    },
    {
      "questionNumber": 137,
      "correctAlternative": "D",
      "discriminationA": 2.1,
      "difficultyB": -0.9,
      "guessingC": 0.15,
      "topic": "Estatística e Médias",
      "skillCode": "H27",
      "skillDescription": "Calcular e interpretar medidas de tendência central em conjuntos de dados.",
      "difficultyLevel": "EASY"
    }
  ]
}
```

## C. Calcular Diagnóstico TRI e Mapa de Erros
- **Requisição**: `POST /api/v1/evaluations/calculate`
- **Cabeçalho**: `Content-Type: application/json`
- **JSON que deve ser enviado (`EvaluationRequest`)**:
```json
{
  "examYear": 2024,
  "knowledgeArea": "MATHEMATICS",
  "bookletColor": "BLUE",
  "answers": [
    {
      "questionNumber": 136,
      "chosenAlternative": "B"
    },
    {
      "questionNumber": 137,
      "chosenAlternative": "A"
    },
    {
      "questionNumber": 138,
      "chosenAlternative": "A"
    }
  ]
}
```
>Nota de Validação:
    - *examYear*: Obrigatório, ano válido.
    - *knowledgeArea*: `MATHEMATICS`, `NATURAL_SCIENCES`, `HUMAN_SCIENCES` ou `LANGUAGES`.
    - *bookletColor*: `BLUE`, `YELLOW`, `WHITE`, `PINK` ou `GRAY`.
    - *answers*: Lista com 1 a 90 itens contendo `questionNumber` e `chosenAlternative` (deve ser `A`, `B`, `C`, `D` ou `E`).

- **Status de Sucesso**: `200 OK`
- **JSON Retornado (`EvaluationResponse`)**:
```json
{
  "estimatedScore": 642.8,
  "standardError": 18.4,
  "totalQuestions": 45,
  "correctCount": 28,
  "disclaimer": "Estimativa estatística calculada com base nos parâmetros oficiais da TRI/INEP. Não substitui o resultado oficial.",
  "topRecommendations": [
    {
      "rank": 1,
      "topic": "Geometria Espacial (Prismas e Cilindros)",
      "potentialGain": 38.6,
      "wrongQuestionsCount": 3,
      "summary": "Revisar Geometria Espacial (Prismas e Cilindros) pode acrescentar até +38.6 pontos na sua nota estimada."
    },
    {
      "rank": 2,
      "topic": "Estatística e Médias",
      "potentialGain": 24.2,
      "wrongQuestionsCount": 2,
      "summary": "Revisar Estatística e Médias pode acrescentar até +24.2 pontos na sua nota estimada."
    },
    {
      "rank": 3,
      "topic": "Função Afim e Gráficos",
      "potentialGain": 19.5,
      "wrongQuestionsCount": 1,
      "summary": "Revisar Função Afim e Gráficos pode acrescentar até +19.5 pontos na sua nota estimada."
    }
  ],
  "errorCostMap": [
    {
      "questionNumber": 137,
      "chosenAlternative": "A",
      "correctAlternative": "D",
      "correct": false,
      "topic": "Estatística e Médias",
      "difficultyLevel": "EASY",
      "pointsLost": 14.8
    },
    {
      "questionNumber": 136,
      "chosenAlternative": "B",
      "correctAlternative": "B",
      "correct": true,
      "topic": "Grandezas Proporcionais e Escalas",
      "difficultyLevel": "EASY",
      "pointsLost": 0.0
    }
  ]
}
```
