export type ErrorCost = {
  position: number
  skill: number
  scoreIfCorrect: number
  cost: number
}

export type Analysis = {
  notice: string
  exam: { edition: number; area: string; color: string; scoredItems: number; source: string }
  score: number
  error: number
  errorMap: ErrorCost[]
  reviewFirst: ErrorCost[]
}

// Em desenvolvimento o Vite repassa /api para a API em localhost:8080 (ver vite.config.ts).
const API_URL = import.meta.env.VITE_API_URL ?? ''

/** Envia as respostas (45 caracteres) e devolve a análise. Não guarda nada no navegador. */
export async function analyze(answers: string): Promise<Analysis> {
  const response = await fetch(`${API_URL}/api/analysis`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ answers }),
  })
  if (!response.ok) {
    throw new Error(response.status === 400 ? 'invalid' : 'unavailable')
  }
  return (await response.json()) as Analysis
}
