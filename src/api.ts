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

export type CardReading = {
  answers: string
  questions: { position: number; answer: string; status: string; confidence: number }[]
  review: number[]
}

const MAX_PHOTO_SIDE = 2000

/** Reduz a foto no aparelho (lado maior de 2000 px, JPEG) antes de enviar. Se não der, envia a original. */
export async function shrinkPhoto(file: File): Promise<Blob> {
  try {
    const bitmap = await createImageBitmap(file, { imageOrientation: 'from-image' })
    const scale = Math.min(1, MAX_PHOTO_SIDE / Math.max(bitmap.width, bitmap.height))
    const canvas = document.createElement('canvas')
    canvas.width = Math.round(bitmap.width * scale)
    canvas.height = Math.round(bitmap.height * scale)
    canvas.getContext('2d')!.drawImage(bitmap, 0, 0, canvas.width, canvas.height)
    const blob = await new Promise<Blob | null>((resolve) => canvas.toBlob(resolve, 'image/jpeg', 0.9))
    return blob ?? file
  } catch {
    return file
  }
}

/** Envia a foto do cartão para leitura. A API lê na memória e descarta; nada fica guardado. */
export async function readCard(photo: Blob): Promise<CardReading> {
  const form = new FormData()
  form.append('photo', photo, 'cartao.jpg')
  const response = await fetch(`${API_URL}/api/read-card`, { method: 'POST', body: form })
  if (!response.ok) {
    throw new Error(response.status === 422 ? 'card-not-found' : response.status === 400 ? 'bad-image' : 'unavailable')
  }
  return (await response.json()) as CardReading
}
