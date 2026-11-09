import { useState } from 'react'
import { analyze, readCard, shrinkPhoto, type Analysis } from './api'

const LETTERS = ['A', 'B', 'C', 'D', 'E']
const FIRST_POSITION = 136
const TOTAL = 45
const BLANK_CHANCE = 0.12

const points = (n: number) => n.toLocaleString('pt-BR', { maximumFractionDigits: 0 })
const gain = (n: number) => n.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 1 })

const SECONDARY_BUTTON =
  'border border-rule bg-transparent px-4 py-3 text-sm text-ink hover:border-ink'

type Status = 'idle' | 'loading' | 'invalid' | 'unavailable' | 'done'
type PhotoStatus = 'idle' | 'reading' | 'read' | 'not-found' | 'bad-image' | 'unavailable'

/** embedded: a página que hospeda o componente já traz o título e a introdução. O aviso de estimativa sempre aparece. */
export default function Calculator({ embedded = false }: { embedded?: boolean }) {
  // Uma letra por questão, ou '.' para branco. Fica só na memória da página.
  const [answers, setAnswers] = useState<string[]>(() => Array(TOTAL).fill('.'))
  const [status, setStatus] = useState<Status>('idle')
  const [result, setResult] = useState<Analysis | null>(null)
  // Questões que a leitura da foto pediu para conferir (somem quando o aluno mexe na questão)
  const [review, setReview] = useState<number[]>([])
  const [photoStatus, setPhotoStatus] = useState<PhotoStatus>('idle')

  const marked = answers.filter((a) => a !== '.').length

  // Só para teste e demonstração: letras sorteadas no próprio navegador, algumas questões em branco de propósito.
  function fillRandom() {
    setAnswers(Array.from({ length: TOTAL }, () => (Math.random() < BLANK_CHANCE ? '.' : LETTERS[Math.floor(Math.random() * LETTERS.length)])))
    setStatus('idle')
    setResult(null)
  }

  function clearAll() {
    setAnswers(Array(TOTAL).fill('.'))
    setReview([])
    setPhotoStatus('idle')
    setStatus('idle')
    setResult(null)
  }

  async function sendPhoto(file: File) {
    setPhotoStatus('reading')
    try {
      const reading = await readCard(await shrinkPhoto(file))
      setAnswers(reading.answers.split(''))
      setReview(reading.review)
      setResult(null)
      setStatus('idle')
      setPhotoStatus('read')
    } catch (e) {
      const code = e instanceof Error ? e.message : ''
      setPhotoStatus(code === 'card-not-found' ? 'not-found' : code === 'bad-image' ? 'bad-image' : 'unavailable')
    }
  }

  function mark(index: number, letter: string) {
    setReview((prev) => prev.filter((p) => p !== FIRST_POSITION + index))
    setAnswers((prev) => prev.map((a, i) => (i === index ? letter : a)))
  }

  async function calculate() {
    setStatus('loading')
    try {
      setResult(await analyze(answers.join('')))
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
            <h2 className="font-serif text-2xl font-semibold">Calcule com a prova de Matemática do ENEM 2025</h2>
            <p className="max-w-2xl text-lg leading-relaxed text-muted">
              Prova azul, questões 136 a 180. Marque o que você respondeu e deixe em branco o que você não
              marcou. As questões que o INEP anulou não entram na conta.
            </p>
          </>
        )}
        <p
          role="note"
          className="max-w-2xl border-l-2 border-pen bg-ink/5 px-4 py-3 text-sm font-medium text-pen"
        >
          Estimativa, não é a nota oficial. O resultado tem um erro, mostrado junto.
        </p>
      </div>

      <form
        onSubmit={(e) => {
          e.preventDefault()
          void calculate()
        }}
        className="space-y-5"
      >
        {/* Grade compacta, como um cartão-resposta: colunas de 15 (3 no computador, 2 no celular). */}
        <div className="max-w-xl columns-2 gap-x-5 sm:columns-3 sm:gap-x-6" role="group" aria-label="Gabarito, questões 136 a 180">
          {answers.map((current, index) => {
            const position = FIRST_POSITION + index
            return (
              <fieldset
                key={position}
                className={`m-0 flex min-w-0 break-inside-avoid items-center border-0 p-0 ${review.includes(position) ? 'bg-pen/20' : ''}`}
              >
                <legend className="sr-only">Questão {position}{review.includes(position) ? ', conferir' : ''}</legend>
                <span aria-hidden="true" className={`w-6 font-mono text-[11px] sm:w-6 ${review.includes(position) ? 'font-bold text-pen' : 'text-muted'}`}>
                  {position}
                </span>
                {LETTERS.map((letter) => (
                  <label key={letter} className="grid h-[26px] w-6 cursor-pointer place-items-center sm:h-[19px] sm:w-[18px]">
                    <input
                      type="radio"
                      name={`q${position}`}
                      value={letter}
                      checked={current === letter}
                      onChange={() => {}}
                      // Tocar de novo na marcada limpa a resposta
                      onClick={() => mark(index, current === letter ? '.' : letter)}
                      className="peer sr-only"
                    />
                    <span className="grid h-5 w-5 place-items-center rounded-full border border-ink/70 text-[11px] font-medium leading-none text-ink peer-checked:border-pen peer-checked:bg-pen peer-checked:font-bold peer-checked:text-paper peer-focus-visible:outline peer-focus-visible:outline-2 peer-focus-visible:outline-offset-1 peer-focus-visible:outline-pen sm:h-[15px] sm:w-[15px] sm:text-[10px]">
                      {letter}
                    </span>
                  </label>
                ))}
              </fieldset>
            )
          })}
        </div>

        <div className="flex flex-wrap items-center gap-4">
          <button
            type="submit"
            disabled={status === 'loading'}
            className="bg-ink px-6 py-3 text-paper hover:bg-pen disabled:opacity-60"
          >
            {status === 'loading' ? 'Calculando...' : 'Calcular'}
          </button>
          <a href="/cartao-resposta-mt-2025.pdf" download className={`${SECONDARY_BUTTON} inline-block no-underline`}>
            Baixar cartão em PDF
          </a>
          <label className={`${SECONDARY_BUTTON} cursor-pointer`}>
            {photoStatus === 'reading' ? 'Lendo a foto...' : 'Enviar foto do cartão'}
            <input
              type="file"
              accept="image/jpeg,image/png"
              className="sr-only"
              disabled={photoStatus === 'reading'}
              onChange={(e) => {
                const file = e.target.files?.[0]
                e.target.value = ''
                if (file) void sendPhoto(file)
              }}
            />
          </label>
          <button type="button" onClick={clearAll} className={SECONDARY_BUTTON}>
            Limpar tudo
          </button>
          <button type="button" onClick={fillRandom} className={SECONDARY_BUTTON}>
            Preencher com respostas aleatórias (só teste)
          </button>
        </div>
        <p className="max-w-2xl text-sm text-muted">
          A foto do cartão é enviada à nossa API só para ler as bolinhas: ela é lida na memória, descartada e não vai
          para nenhum registro. Você sempre confere as respostas na grade antes de calcular.
        </p>
        <p role="status" className="max-w-2xl text-sm font-medium text-pen">
          {photoStatus === 'read' &&
            (review.length > 0
              ? `Foto lida. Confira as ${review.length} questões destacadas e corrija se preciso. Depois clique em Calcular.`
              : 'Foto lida. Confira a grade e clique em Calcular.')}
          {photoStatus === 'not-found' && 'Não encontrei os 4 quadrados pretos dos cantos. Fotografe a folha inteira, de frente e com boa luz.'}
          {photoStatus === 'bad-image' && 'Envie uma foto JPEG ou PNG de até 8 MB.'}
          {photoStatus === 'unavailable' && 'Não consegui falar com a leitura agora. A API pode estar fora do ar.'}
        </p>
        <p className="text-sm text-muted">
          {marked} de {TOTAL} marcadas. Toque de novo na marcada para limpar. O botão de respostas aleatórias é só para
          testar: ele não representa uma prova real.
        </p>
      </form>

      <div aria-live="polite" className="space-y-8">
        {status === 'unavailable' && (
          <p className="max-w-2xl border border-pen px-4 py-3 text-pen">
            Não consegui falar com o cálculo agora. A API pode estar fora do ar. Tente de novo em instantes.
          </p>
        )}
        {status === 'invalid' && (
          <p className="max-w-2xl border border-pen px-4 py-3 text-pen">
            As respostas não estão no formato esperado. Recarregue a página e tente de novo.
          </p>
        )}
        {status === 'done' && result && <Result result={result} />}
      </div>
    </section>
  )
}

function Result({ result }: { result: Analysis }) {
  const { score, error, errorMap, reviewFirst, exam } = result
  return (
    <div className="space-y-8">
      <div className="space-y-1">
        <p className="font-serif text-4xl font-semibold">
          {points(score)} <span className="text-2xl text-muted">± {points(error)}</span>
        </p>
        <p className="text-sm text-muted">
          Nota estimada em Matemática, ENEM {exam.edition}, prova {exam.color.toLowerCase()}. O erro é o quanto a
          estimativa poderia variar com as mesmas respostas. {exam.scoredItems} questões contam. Estimativa, não é a
          nota oficial.
        </p>
      </div>

      {errorMap.length === 0 ? (
        <p className="text-lg">Nenhum erro: todas as questões que contam estão certas.</p>
      ) : (
        <>
          <div className="space-y-3">
            <h3 className="font-serif text-xl font-semibold">Revise primeiro</h3>
            <ol className="max-w-2xl list-decimal space-y-2 pl-6 text-lg">
              {reviewFirst.map((e) => (
                <li key={e.position}>
                  Questão {e.position}{' '}
                  <span className="text-muted">
                    (habilidade {e.skill}), vale +{gain(e.cost)} pontos se estivesse certa
                  </span>
                </li>
              ))}
            </ol>
          </div>

          <div className="space-y-3">
            <h3 className="font-serif text-xl font-semibold">Mapa de erros</h3>
            <p className="max-w-2xl text-sm text-muted">
              Para cada questão que você errou, a nota estimada se ela estivesse certa. Do erro mais caro ao mais
              barato.
            </p>
            <div className="overflow-x-auto">
              <table className="w-full max-w-2xl border-collapse text-left text-sm">
                <caption className="sr-only">Mapa de erros, do mais caro ao mais barato</caption>
                <thead>
                  <tr className="border-b border-ink">
                    <th scope="col" className="py-2 pr-4 font-semibold">Questão</th>
                    <th scope="col" className="py-2 pr-4 font-semibold">Habilidade</th>
                    <th scope="col" className="py-2 pr-4 text-right font-semibold">Nota se estivesse certa</th>
                    <th scope="col" className="py-2 text-right font-semibold">Ganho</th>
                  </tr>
                </thead>
                <tbody>
                  {errorMap.map((e) => (
                    <tr key={e.position} className="border-b border-rule">
                      <td className="py-2 pr-4 font-mono">{e.position}</td>
                      <td className="py-2 pr-4">{e.skill}</td>
                      <td className="py-2 pr-4 text-right">{points(e.scoreIfCorrect)}</td>
                      <td className="py-2 text-right font-medium text-pen">+{gain(e.cost)}</td>
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
