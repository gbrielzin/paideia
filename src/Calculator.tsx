import { useEffect, useState } from 'react'
import {
  calculateEvaluation,
  fetchExamDetails,
  fetchExams,
  readCard,
  shrinkPhoto,
  type EvaluationResponse,
  type ExamDetail,
  type ExamItemSummary,
  type ExamSummary,
} from './api'

const LETTERS = ['A', 'B', 'C', 'D', 'E']
const BLANK_CHANCE = 0.12

// Prova padrão para inicialização enquanto o catálogo carrega
const DEFAULT_EXAM: ExamSummary = {
  year: 2024,
  knowledgeArea: 'MATHEMATICS',
  knowledgeAreaDescription: 'Matemática e suas Tecnologias',
  bookletColor: 'BLUE',
  bookletColorDescription: 'Azul',
  totalQuestions: 45,
}

// 45 questões padrão (136 a 180) para inicialização imediata
const DEFAULT_ITEMS: ExamItemSummary[] = Array.from({ length: 45 }, (_, i) => ({
  questionNumber: 136 + i,
  correctAlternative: '',
  discriminationA: 1.0,
  difficultyB: 0.0,
  guessingC: 0.2,
  topic: 'Matemática',
  skillCode: `H${(i % 30) + 1}`,
  skillDescription: '',
  difficultyLevel: 'MEDIUM',
}))

const points = (n: number) => n.toLocaleString('pt-BR', { maximumFractionDigits: 0 })
const gain = (n: number) =>
  n.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 })

const SECONDARY_BUTTON =
  'border border-rule bg-transparent px-4 py-3 text-sm text-ink hover:border-ink cursor-pointer transition-colors'

type Status = 'idle' | 'loading' | 'invalid' | 'unavailable' | 'done'
type PhotoStatus = 'idle' | 'reading' | 'read' | 'not-found' | 'bad-image' | 'unavailable'

export default function Calculator({ embedded = false }: { embedded?: boolean }) {
  const [exams, setExams] = useState<ExamSummary[]>([DEFAULT_EXAM])
  const [selectedExam, setSelectedExam] = useState<ExamSummary>(DEFAULT_EXAM)
  const [examItems, setExamItems] = useState<ExamItemSummary[]>(DEFAULT_ITEMS)

  // Respostas indexadas por número da questão (ex: { 136: 'A', 137: 'C' })
  const [answers, setAnswers] = useState<Record<number, string>>({})
  const [status, setStatus] = useState<Status>('idle')
  const [result, setResult] = useState<EvaluationResponse | null>(null)

  // Questões que a leitura da foto pediu para o aluno conferir
  const [review, setReview] = useState<number[]>([])
  const [photoStatus, setPhotoStatus] = useState<PhotoStatus>('idle')

  // Carrega lista de exames ao montar
  useEffect(() => {
    let cancelled = false
    void fetchExams()
      .then((list) => {
        if (!cancelled && list.length > 0) {
          setExams(list)
          setSelectedExam(list[0])
        }
      })
      .catch(() => {
        // Mantém a prova padrão se a API estiver offline
      })
    return () => {
      cancelled = true
    }
  }, [])

  // Carrega detalhes da prova selecionada
  useEffect(() => {
    let cancelled = false
    void fetchExamDetails(selectedExam.year, selectedExam.knowledgeArea, selectedExam.bookletColor)
      .then((detail: ExamDetail) => {
        if (!cancelled && detail.items && detail.items.length > 0) {
          setExamItems(detail.items)
          setAnswers({})
          setReview([])
          setResult(null)
          setStatus('idle')
        }
      })
      .catch(() => {
        // Mantém os itens padrão se não conseguir buscar
      })
    return () => {
      cancelled = true
    }
  }, [selectedExam])

  const markedCount = Object.keys(answers).length
  const totalQuestions = examItems.length

  function handleExamChange(examKey: string) {
    const found = exams.find((e) => `${e.year}-${e.knowledgeArea}-${e.bookletColor}` === examKey)
    if (found) {
      setSelectedExam(found)
    }
  }

  // Preenche respostas aleatórias para fins de teste e demonstração
  function fillRandom() {
    const generated: Record<number, string> = {}
    for (const item of examItems) {
      if (Math.random() >= BLANK_CHANCE) {
        generated[item.questionNumber] = LETTERS[Math.floor(Math.random() * LETTERS.length)]
      }
    }
    setAnswers(generated)
    setStatus('idle')
    setResult(null)
  }

  function clearAll() {
    setAnswers({})
    setReview([])
    setPhotoStatus('idle')
    setStatus('idle')
    setResult(null)
  }

  async function sendPhoto(file: File) {
    setPhotoStatus('reading')
    try {
      const reading = await readCard(await shrinkPhoto(file))
      const newAnswers: Record<number, string> = {}
      const letters = reading.answers.split('')
      examItems.forEach((item, index) => {
        const letter = letters[index]
        if (letter && letter !== '.') {
          newAnswers[item.questionNumber] = letter
        }
      })
      setAnswers(newAnswers)
      setReview(reading.review)
      setResult(null)
      setStatus('idle')
      setPhotoStatus('read')
    } catch (e) {
      const code = e instanceof Error ? e.message : ''
      setPhotoStatus(
        code === 'card-not-found'
          ? 'not-found'
          : code === 'bad-image'
          ? 'bad-image'
          : 'unavailable'
      )
    }
  }

  function toggleMark(questionNumber: number, letter: string) {
    setReview((prev) => prev.filter((p) => p !== questionNumber))
    setAnswers((prev) => {
      const copy = { ...prev }
      if (copy[questionNumber] === letter) {
        delete copy[questionNumber]
      } else {
        copy[questionNumber] = letter
      }
      return copy
    })
  }

  async function calculate() {
    if (markedCount === 0) {
      setStatus('invalid')
      return
    }

    setStatus('loading')
    try {
      const studentAnswers = Object.entries(answers).map(([qNum, letter]) => ({
        questionNumber: Number(qNum),
        chosenAlternative: letter,
      }))

      const evaluation = await calculateEvaluation({
        examYear: selectedExam.year,
        knowledgeArea: selectedExam.knowledgeArea,
        bookletColor: selectedExam.bookletColor,
        answers: studentAnswers,
      })

      setResult(evaluation)
      setStatus('done')
    } catch (e) {
      setResult(null)
      setStatus(e instanceof Error && e.message === 'invalid' ? 'invalid' : 'unavailable')
    }
  }

  return (
    <section id={embedded ? undefined : 'calcular'} className="space-y-6">
      <div className="space-y-3">
        {!embedded && (
          <>
            <h2 className="font-serif text-2xl font-semibold">
              Calcule sua nota estimada e mapa de erros
            </h2>
            <p className="max-w-2xl text-lg leading-relaxed text-muted">
              Selecione o caderno, marque o que você respondeu e deixe em branco o que você não
              marcou. O cálculo usa os parâmetros oficiais dos itens do INEP.
            </p>
          </>
        )}

        <div className="flex flex-wrap items-center gap-3 pt-2">
          <label htmlFor="exam-select" className="text-sm font-medium">
            Caderno de prova:
          </label>
          <select
            id="exam-select"
            value={`${selectedExam.year}-${selectedExam.knowledgeArea}-${selectedExam.bookletColor}`}
            onChange={(e) => handleExamChange(e.target.value)}
            className="border border-rule bg-white/70 px-3 py-2 text-sm text-ink outline-none focus:border-ink"
          >
            {exams.map((exam) => (
              <option
                key={`${exam.year}-${exam.knowledgeArea}-${exam.bookletColor}`}
                value={`${exam.year}-${exam.knowledgeArea}-${exam.bookletColor}`}
              >
                ENEM {exam.year} · {exam.knowledgeAreaDescription} ({exam.bookletColorDescription})
              </option>
            ))}
          </select>
        </div>

        <p
          role="note"
          className="max-w-2xl border-l-2 border-pen bg-ink/5 px-4 py-3 text-sm font-medium text-pen"
        >
          Estimativa, não é a nota oficial. O resultado tem um erro medido, mostrado junto.
        </p>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault()
          void calculate()
        }}
        className="space-y-5"
      >
        {/* Grade compacta como cartão-resposta em colunas de 15 */}
        <div
          className="max-w-xl columns-2 gap-x-5 sm:columns-3 sm:gap-x-6"
          role="group"
          aria-label={`Gabarito, questões ${examItems[0]?.questionNumber ?? 1} a ${
            examItems[examItems.length - 1]?.questionNumber ?? totalQuestions
          }`}
        >
          {examItems.map((item) => {
            const qNum = item.questionNumber
            const current = answers[qNum] ?? ''
            const needsReview = review.includes(qNum)

            return (
              <fieldset
                key={qNum}
                className={`m-0 flex min-w-0 break-inside-avoid items-center border-0 p-0 ${
                  needsReview ? 'bg-pen/20' : ''
                }`}
              >
                <legend className="sr-only">
                  Questão {qNum}
                  {needsReview ? ', conferir' : ''}
                </legend>
                <span
                  aria-hidden="true"
                  className={`w-7 font-mono text-[11px] sm:w-8 ${
                    needsReview ? 'font-bold text-pen' : 'text-muted'
                  }`}
                >
                  {qNum}
                </span>
                <span className="flex py-[3px]">
                  {LETTERS.map((letter) => {
                    const isSelected = current === letter
                    return (
                      <button
                        key={letter}
                        type="button"
                        onClick={() => toggleMark(qNum, letter)}
                        aria-pressed={isSelected}
                        aria-label={`Questão ${qNum}, alternativa ${letter}`}
                        className={`grid h-6 w-6 place-items-center rounded-full border text-[11px] transition-colors sm:h-7 sm:w-7 sm:text-xs ${
                          isSelected
                            ? 'border-ink bg-ink text-paper font-semibold'
                            : 'border-rule text-muted hover:border-ink hover:text-ink'
                        }`}
                      >
                        {letter}
                      </button>
                    )
                  })}
                </span>
              </fieldset>
            )
          })}
        </div>

        <div className="flex flex-wrap items-center gap-3 pt-2">
          <button
            type="submit"
            disabled={status === 'loading'}
            className="border border-pen bg-pen px-6 py-3 font-semibold text-paper transition-opacity hover:opacity-90 disabled:opacity-50 cursor-pointer"
          >
            {status === 'loading' ? 'Calculando...' : 'Calcular nota e mapa de erros'}
          </button>
          <button type="button" onClick={fillRandom} className={SECONDARY_BUTTON}>
            Respostas aleatórias
          </button>
          <button type="button" onClick={clearAll} className={SECONDARY_BUTTON}>
            Limpar
          </button>
          <label className={SECONDARY_BUTTON}>
            <span>{photoStatus === 'reading' ? 'Lendo foto...' : 'Enviar foto do cartão'}</span>
            <input
              type="file"
              accept="image/*"
              className="sr-only"
              disabled={photoStatus === 'reading'}
              onChange={(e) => {
                const file = e.target.files?.[0]
                if (file) void sendPhoto(file)
                e.target.value = ''
              }}
            />
          </label>
        </div>

        <p className="max-w-2xl text-xs leading-relaxed text-muted">
          A foto do cartão é usada apenas para preencher as alternativas: ela é processada na memória,
          descartada e não é gravada em nenhum banco ou log. Você sempre confere e pode corrigir as
          respostas na grade antes de calcular.
        </p>

        <p role="status" className="max-w-2xl text-sm font-medium text-pen">
          {photoStatus === 'read' &&
            (review.length > 0
              ? `Foto lida. Confira as ${review.length} questões destacadas e corrija se preciso. Depois clique em Calcular.`
              : 'Foto lida. Confira a grade e clique em Calcular.')}
          {photoStatus === 'not-found' &&
            'Não encontrei os 4 quadrados pretos dos cantos. Fotografe a folha inteira, de frente e com boa luz.'}
          {photoStatus === 'bad-image' && 'Envie uma foto JPEG ou PNG de até 8 MB.'}
          {photoStatus === 'unavailable' &&
            'Leitura de foto em desenvolvimento na API. Você pode preencher manualmente na grade.'}
        </p>

        <p className="text-sm text-muted">
          {markedCount} de {totalQuestions} marcadas. Toque na alternativa selecionada para limpar.
        </p>
      </form>

      <div aria-live="polite" className="space-y-8">
        {status === 'unavailable' && (
          <p className="max-w-2xl border border-pen px-4 py-3 text-pen">
            Não consegui falar com a API de cálculo agora. Verifique se o servidor está ativo em
            localhost:8080 ou tente novamente em instantes.
          </p>
        )}
        {status === 'invalid' && (
          <p className="max-w-2xl border border-pen px-4 py-3 text-pen">
            Marque ao menos uma questão na grade antes de solicitar o cálculo da nota.
          </p>
        )}
        {status === 'done' && result && <Result result={result} />}
      </div>
    </section>
  )
}

function Result({ result }: { result: EvaluationResponse }) {
  const {
    estimatedScore,
    standardError,
    correctCount,
    totalQuestions,
    disclaimer,
    topRecommendations,
    errorCostMap,
    examYear,
    bookletColor,
  } = result

  return (
    <div className="space-y-8 border-t border-rule pt-6">
      <div className="space-y-2">
        <p className="font-serif text-4xl font-semibold">
          {points(estimatedScore)}{' '}
          <span className="text-2xl text-muted">± {points(standardError)}</span>
        </p>
        <p className="text-base text-ink">
          {correctCount} acertos de {totalQuestions} questões computadas.
        </p>
        <p className="max-w-2xl text-xs text-muted">
          {disclaimer} (ENEM {examYear}, caderno {bookletColor.toLowerCase()}).
        </p>
      </div>

      {errorCostMap.length === 0 || errorCostMap.every((e) => e.correct) ? (
        <p className="text-lg font-medium text-ink">
          Parabéns! Todas as questões respondidas estão corretas.
        </p>
      ) : (
        <>
          {topRecommendations && topRecommendations.length > 0 && (
            <div className="space-y-3">
              <h3 className="font-serif text-xl font-semibold">O que revisar primeiro</h3>
              <p className="max-w-2xl text-sm text-muted">
                Os 3 tópicos pedagógicos onde seus erros mais custaram pontos na nota final:
              </p>
              <ol className="max-w-2xl list-decimal space-y-3 pl-6 text-base">
                {topRecommendations.map((rec) => (
                  <li key={rec.rank} className="space-y-1">
                    <span className="font-semibold">{rec.topic}</span>
                    <span className="ml-2 font-medium text-pen">
                      (+{gain(rec.potentialGain)} pts potenciais)
                    </span>
                    <p className="text-sm text-muted">
                      {rec.summary} (questões:{' '}
                      {rec.questionNumbers.join(', ')})
                    </p>
                  </li>
                ))}
              </ol>
            </div>
          )}

          <div className="space-y-3">
            <h3 className="font-serif text-xl font-semibold">Mapa de custo de erros</h3>
            <p className="max-w-2xl text-sm text-muted">
              Para cada questão incorreta, veja o impacto direto estimado na sua pontuação TRI:
            </p>
            <div className="overflow-x-auto">
              <table className="w-full max-w-2xl border-collapse text-left text-sm">
                <caption className="sr-only">
                  Mapa de erros detalhado, ordenado pelo impacto em pontos perdidos
                </caption>
                <thead>
                  <tr className="border-b border-ink">
                    <th scope="col" className="py-2 pr-3 font-semibold">
                      Questão
                    </th>
                    <th scope="col" className="py-2 pr-3 font-semibold">
                      Marcou
                    </th>
                    <th scope="col" className="py-2 pr-3 font-semibold">
                      Gabarito
                    </th>
                    <th scope="col" className="py-2 pr-3 font-semibold">
                      Tópico
                    </th>
                    <th scope="col" className="py-2 pr-3 font-semibold">
                      Nível
                    </th>
                    <th scope="col" className="py-2 text-right font-semibold">
                      Custo
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {errorCostMap
                    .filter((e) => !e.correct)
                    .sort((a, b) => b.pointsLost - a.pointsLost)
                    .map((item) => (
                      <tr key={item.questionNumber} className="border-b border-rule">
                        <td className="py-2 pr-3 font-mono font-medium">{item.questionNumber}</td>
                        <td className="py-2 pr-3 font-mono text-pen">
                          {item.chosenAlternative || '—'}
                        </td>
                        <td className="py-2 pr-3 font-mono font-bold text-ink">
                          {item.correctAlternative}
                        </td>
                        <td className="py-2 pr-3">{item.topic}</td>
                        <td className="py-2 pr-3 text-xs text-muted">{item.difficulty}</td>
                        <td className="py-2 text-right font-medium text-pen">
                          +{gain(item.pointsLost)} pts
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
