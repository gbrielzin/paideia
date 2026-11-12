import Calculator from './Calculator'

const LETTERS = ['A', 'B', 'C', 'D', 'E']

type Row = { q: number; marked: number; wrong: boolean; rank?: string }

const ROWS: Row[] = [
  { q: 1, marked: 2, wrong: false },
  { q: 2, marked: 0, wrong: true, rank: '1º a revisar' },
  { q: 3, marked: 4, wrong: false },
  { q: 4, marked: 1, wrong: true, rank: '2º a revisar' },
]

function AnswerSheet() {
  return (
    <figure
      className="border border-rule bg-white/60 p-5 sm:p-6"
      aria-label="Ilustração do conceito: gabarito com dois erros marcados, ordenados pelo que revisar primeiro"
    >
      <ol className="space-y-3" aria-hidden="true">
        {ROWS.map((r) => (
          <li
            key={r.q}
            className={`flex items-center gap-3 sm:gap-4 border-l-2 pl-3 ${
              r.wrong ? 'border-pen' : 'border-transparent'
            }`}
          >
            <span className="w-6 font-mono text-sm text-muted">{r.q}</span>
            <span className="flex gap-2">
              {LETTERS.map((l, i) => {
                const on = i === r.marked
                return (
                  <span
                    key={l}
                    className={`grid h-8 w-8 place-items-center rounded-full border text-xs ${
                      on
                        ? r.wrong
                          ? 'border-pen bg-pen text-paper'
                          : 'border-ink bg-ink text-paper'
                        : 'border-rule text-muted'
                    }`}
                  >
                    {l}
                  </span>
                )
              })}
            </span>
            {r.rank && (
              <span className="ml-auto whitespace-nowrap font-serif text-sm italic text-pen">{r.rank}</span>
            )}
          </li>
        ))}
      </ol>
      <figcaption className="mt-5 border-t border-rule pt-3 text-xs text-muted">
        Ilustração do conceito, sem dados reais. O cálculo ainda está em construção.
      </figcaption>
    </figure>
  )
}

function App() {
  return (
    <div className="min-h-screen bg-paper font-sans text-ink">
      <header className="mx-auto flex max-w-5xl items-baseline justify-between px-5 py-6 sm:px-8">
        <span className="font-serif text-xl font-semibold tracking-tight">Paideia</span>
        <span className="text-sm text-muted">Hack Brasil 2026 · Acesso à educação</span>
      </header>

      <main className="mx-auto max-w-5xl space-y-20 px-5 pb-20 pt-8 sm:px-8 sm:pt-14">
        <section className="grid items-center gap-10 md:grid-cols-2">
          <div className="space-y-6">
            <p className="text-sm font-medium uppercase tracking-wide text-pen">Em construção</p>
            <h1 className="font-serif text-4xl font-semibold leading-tight sm:text-5xl">
              Você errou no ENEM. Quais erros custaram mais pontos?
            </h1>
            <p className="max-w-xl text-lg leading-relaxed text-muted">
              Depois da prova você sabe quantas questões errou, mas não quanto cada uma pesou na
              nota. O Paideia estima esse custo e mostra o que revisar primeiro.
            </p>
            <div className="flex flex-wrap gap-x-6 gap-y-2 text-sm">
              <a className="underline underline-offset-4 hover:text-pen" href="#calcular">
                Calcular nota agora
              </a>
              <a className="underline underline-offset-4 hover:text-pen" href="#video">
                Vídeo de apresentação
              </a>
              <a
                className="underline underline-offset-4 hover:text-pen"
                href="https://github.com/gbrielzin/paideia"
              >
                Código no GitHub
              </a>
            </div>
          </div>
          <AnswerSheet />
        </section>

        <Calculator />

        <section className="grid gap-8 md:grid-cols-[1fr_2fr]">
          <h2 className="font-serif text-2xl font-semibold">Para quem é</h2>
          <p className="max-w-2xl text-lg leading-relaxed">
            Para o aluno de escola pública que faz o ENEM sem cursinho e, no dia seguinte, quer
            estudar com método, e não por intuição. A nota do ENEM usa a Teoria de Resposta ao
            Item (TRI), em que cada questão tem um peso diferente.
          </p>
        </section>

        <section className="grid gap-8 md:grid-cols-[1fr_2fr]">
          <h2 className="font-serif text-2xl font-semibold">Como vai funcionar</h2>
          <ol className="max-w-2xl space-y-5 text-lg leading-relaxed">
            <li>
              <strong className="font-semibold">1. Você marca o que respondeu.</strong> Primeiro
              por tabela. Foto do cartão e voz estão planejadas.
            </li>
            <li>
              <strong className="font-semibold">2. Calculamos o custo de cada erro.</strong> Com os
              parâmetros públicos dos itens do INEP.
            </li>
            <li>
              <strong className="font-semibold">3. Você vê o que revisar primeiro.</strong> Os
              erros que mais pesaram, em ordem.
            </li>
          </ol>
        </section>

        <section className="grid gap-8 md:grid-cols-[1fr_2fr]">
          <h2 className="font-serif text-2xl font-semibold">O que prometemos e o que não</h2>
          <ul className="max-w-2xl list-disc space-y-3 pl-5 text-lg leading-relaxed">
            <li>O resultado é sempre uma estimativa, nunca a nota oficial.</li>
            <li>A margem de erro ainda vai ser medida, e só depois a mostramos.</li>
            <li>O cálculo roda na nossa API, que não armazena as suas respostas.</li>
            <li>Acessibilidade está planejada desde o início, mas ainda não foi construída.</li>
          </ul>
        </section>

        <section id="video" className="grid gap-8 md:grid-cols-[1fr_2fr]">
          <h2 className="font-serif text-2xl font-semibold">Vídeo</h2>
          <div className="grid aspect-video max-w-2xl place-items-center border border-dashed border-rule text-muted">
            Vídeo em breve
          </div>
        </section>

        <section className="grid gap-8 md:grid-cols-[1fr_2fr]">
          <h2 className="font-serif text-2xl font-semibold">Quem está construindo</h2>
          <p className="max-w-2xl text-lg leading-relaxed">
            Gabriel e Murilo, dois estudantes que decidiram construir isso juntos na abertura do
            Hack Brasil.
          </p>
        </section>
      </main>

      <footer className="border-t border-rule">
        <div className="mx-auto flex max-w-5xl flex-wrap justify-between gap-2 px-5 py-6 text-sm text-muted sm:px-8">
          <span>Paideia, projeto em construção para o Hack Brasil 2026.</span>
          <a className="underline underline-offset-4 hover:text-pen" href="https://github.com/gbrielzin/paideia">
            github.com/gbrielzin/paideia
          </a>
        </div>
      </footer>
    </div>
  )
}

export default App
