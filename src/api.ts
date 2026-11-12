export type KnowledgeArea = 'MATHEMATICS' | 'NATURAL_SCIENCES' | 'HUMAN_SCIENCES' | 'LANGUAGES'
export type BookletColor = 'BLUE' | 'YELLOW' | 'WHITE' | 'PINK' | 'GRAY'

export interface ExamSummary {
  year: number
  knowledgeArea: KnowledgeArea
  knowledgeAreaDescription: string
  bookletColor: BookletColor
  bookletColorDescription: string
  totalQuestions: number
}

export interface ExamItemSummary {
  questionNumber: number
  correctAlternative: string
  discriminationA: number
  difficultyB: number
  guessingC: number
  topic: string
  skillCode: string
  skillDescription: string
  difficultyLevel: 'EASY' | 'MEDIUM' | 'HARD'
}

export interface ExamDetail {
  year: number
  knowledgeArea: KnowledgeArea
  knowledgeAreaDescription: string
  bookletColor: BookletColor
  bookletColorDescription: string
  totalQuestions: number
  items: ExamItemSummary[]
}

export interface StudentAnswerItem {
  questionNumber: number
  chosenAlternative: string
}

export interface EvaluationRequest {
  examYear: number
  knowledgeArea: KnowledgeArea
  bookletColor: BookletColor
  answers: StudentAnswerItem[]
}

export interface TopicRecommendation {
  rank: number
  topic: string
  potentialGain: number
  wrongQuestionsCount: number
  summary: string
  questionNumbers: number[]
}

export interface QuestionImpact {
  questionNumber: number
  chosenAlternative: string
  correctAlternative: string
  correct: boolean
  topic: string
  difficulty: string
  pointsLost: number
}

export interface EvaluationResponse {
  examYear: number
  knowledgeArea: KnowledgeArea
  bookletColor: BookletColor
  estimatedScore: number
  standardError: number
  totalQuestions: number
  correctCount: number
  disclaimer: string
  topRecommendations: TopicRecommendation[]
  errorCostMap: QuestionImpact[]
}

export interface CardReading {
  answers: string
  questions: { position: number; answer: string; status: string; confidence: number }[]
  review: number[]
}

const API_URL = import.meta.env.VITE_API_URL ?? ''

/** Busca a lista de provas do ENEM cadastradas na API. */
export async function fetchExams(): Promise<ExamSummary[]> {
  const response = await fetch(`${API_URL}/api/v1/exams`)
  if (!response.ok) {
    throw new Error('unavailable')
  }
  return (await response.json()) as ExamSummary[]
}

/** Busca os detalhes de uma prova específica, com todas as questões e gabaritos. */
export async function fetchExamDetails(
  year: number,
  area: KnowledgeArea,
  color: BookletColor
): Promise<ExamDetail> {
  const response = await fetch(`${API_URL}/api/v1/exams/${year}/${area}/${color}`)
  if (!response.ok) {
    throw new Error('unavailable')
  }
  return (await response.json()) as ExamDetail
}

/** Envia as respostas para cálculo da pontuação TRI, erro e mapa de erros. */
export async function calculateEvaluation(request: EvaluationRequest): Promise<EvaluationResponse> {
  const response = await fetch(`${API_URL}/api/v1/evaluations/calculate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })
  if (!response.ok) {
    throw new Error(response.status === 400 ? 'invalid' : 'unavailable')
  }
  return (await response.json()) as EvaluationResponse
}

const MAX_PHOTO_SIDE = 2000

/** Reduz a foto no aparelho (lado maior de 2000 px, JPEG) antes de enviar. Se falhar, envia a original. */
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

/** Envia a foto do cartão para leitura. A API lê na memória e descarta; nada fica gravado. */
export async function readCard(photo: Blob): Promise<CardReading> {
  const form = new FormData()
  form.append('photo', photo, 'cartao.jpg')
  // Tenta o endpoint da API versionada ou o fallback
  let response = await fetch(`${API_URL}/api/v1/cards/read`, { method: 'POST', body: form })
  if (response.status === 404) {
    response = await fetch(`${API_URL}/api/read-card`, { method: 'POST', body: form })
  }
  if (!response.ok) {
    throw new Error(response.status === 422 ? 'card-not-found' : response.status === 400 ? 'bad-image' : 'unavailable')
  }
  return (await response.json()) as CardReading
}
