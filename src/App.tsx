
function App() {

  return (
      <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between font-sans">
          <header className="border-b border-slate-800 bg-slate-900/50 backdrop-blur sticky top-0 z-50">
              <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                      <span className="text-xs bg-blue-500/10 text-blue-400 border border-blue-500/20 px-2.5 py-0.5 rounded-full font-medium">
              Trilha: Acesso à educação
            </span>
                  </div>
                  <a
                      href="#demo"
                      className="text-xs bg-blue-600 hover:bg-blue-500 text-white font-semibold px-4 py-2 rounded-lg transition"
                  >
                      Ver Rascunho
                  </a>
              </div>
          </header>
          <main className="max-w-4xl mx-auto px-6 py-12 space-y-16">
              <section className="text-center space-y-6">
                  <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight text-white leading-tight">
                      <span className="text-blue-500">PAIDEIA</span>
                  </h1>

                  <h2 className="text-3xl sm:text-2xl font-extrabold tracking-tight text-white leading-tight">
                      O Mapa de Erros Inteligente,
                      <span className="text-blue-500"> para o Estudante do ENEM</span>
                  </h2>

                  <p className="text-lg sm:text-xl text-slate-300 max-w-2xl mx-auto leading-relaxed text-justify">
                      Descubra exatamente quantos pontos cada erro custou na sua nota final e saiba quais são os <strong className="text-white">temas de maior impacto</strong> que você deve revisar primeiro.
                  </p>

                  <div className="inline-flex items-center gap-2 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs px-3 py-1.5 rounded-md">
                      <span>🔒 Privacidade: o cálculo roda na nossa API, que não armazena as suas respostas.</span>
                  </div>
              </section>
              <section className="bg-slate-900 border border-slate-800 rounded-2xl p-6 sm:p-8 space-y-4 text-justify">
                  <h2 className="text-xl font-bold text-blue-400 flex items-center gap-2">
                      🎯 Para quem é o Paideia?
                  </h2>
                  <ul className="grid sm:grid-cols-2 gap-4 text-sm text-slate-300">
                      <li className="bg-slate-950 p-4 rounded-xl border border-slate-800/80">
                          <strong className="block text-white mb-1">Estudantes de Escola Pública</strong>
                          Alunos prestando o ENEM sem acesso a cursinhos preparatórios caros que precisam otimizar o tempo de estudo pós-prova.
                      </li>
                      <li className="bg-slate-950 p-4 rounded-xl border border-slate-800/80">
                          <strong className="block text-white mb-1">Candidatos sem Orientação de TRI</strong>
                          Quem fez a prova e não sabe interpretar a Teoria de Resposta ao Item (TRI) nem por onde começar a revisão.
                      </li>
                      <li className="bg-slate-950 p-4 rounded-xl border border-slate-800/80">
                          <strong className="block text-white mb-1">Alunos que Exigem Acessibilidade</strong>
                          Planejado: leitor de voz, alto contraste e simplificação de texto, pensados desde o início do projeto.
                      </li>
                      <li className="bg-slate-950 p-4 rounded-xl border border-slate-800/80">
                          <strong className="block text-white mb-1">Usuários Focados em Privacidade</strong>
                          Quem quer conferir as respostas sem que elas sejam guardadas depois do cálculo.
                      </li>
                  </ul>
              </section>
              <section className="space-y-6">
                  <h2 className="text-xl font-bold text-slate-200 text-center">
                      ⚡ Como o Paideia transforma seu gabarito em notas
                  </h2>

                  <div className="grid sm:grid-cols-3 gap-6">
                      <div className="bg-slate-900/60 border border-slate-800 p-5 rounded-xl space-y-2">
                          <div className="w-8 h-8 bg-blue-500/20 text-blue-400 font-bold rounded-lg flex items-center justify-center text-sm">1</div>
                          <h3 className="font-semibold text-white text-base">Entrada Flexível</h3>
                          <p className="text-xs text-slate-400">Preenchimento manual primeiro. Foto do cartão de respostas e comando de voz estão planejados.</p>
                      </div>

                      <div className="bg-slate-900/60 border border-slate-800 p-5 rounded-xl space-y-2">
                          <div className="w-8 h-8 bg-blue-500/20 text-blue-400 font-bold rounded-lg flex items-center justify-center text-sm">2</div>
                          <h3 className="font-semibold text-white text-base">Custo Real do Erro</h3>
                          <p className="text-xs text-slate-400">O cálculo da TRI estima quanto cada erro custou na nota. É sempre uma estimativa, nunca a nota oficial, e a margem de erro ainda será medida.</p>
                      </div>

                      <div className="bg-slate-900/60 border border-slate-800 p-5 rounded-xl space-y-2">
                          <div className="w-8 h-8 bg-blue-500/20 text-blue-400 font-bold rounded-lg flex items-center justify-center text-sm">3</div>
                          <h3 className="font-semibold text-white text-base">O Que Revisar Agora</h3>
                          <p className="text-xs text-slate-400">Planejado: um plano destacando os temas de maior impacto para você estudar primeiro.</p>
                      </div>
                  </div>
              </section>
              <section id="demo" className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center space-y-4">
                  <h2 className="font-bold text-slate-200">Demonstração & Pitch (Rascunho Inicial)</h2>
                  <div className="w-full aspect-video bg-slate-950 rounded-xl border border-slate-800 flex flex-col items-center justify-center gap-2 text-slate-500">
                      <span className="text-3xl">▶️</span>
                      <p className="text-sm font-medium">Vídeo em breve</p>
                      <span className="text-xs text-slate-600">Hack Brasil 2026</span>
                  </div>
              </section>
          </main>
          <footer className="border-t border-slate-900 bg-slate-950 py-6 text-center text-xs text-slate-500">
              <p>Paideia • Projeto em construção para o Hack Brasil 2026 • <a className="underline hover:text-slate-300" href="https://github.com/gbrielzin/paideia">Repositório no GitHub</a></p>
          </footer>
      </div>
  )
}

export default App
