
function App() {

  return (
      <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between font-sans">
          <header className="border-b border-slate-800 bg-slate-900/50 backdrop-blur sticky top-0 z-50">
              <div className="max-w-6xl mx-auto px-6 py-4 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                      <span className="text-2xl font-black tracking-wider text-blue-500">PAIDEIA</span>
                      <span className="text-xs bg-blue-500/10 text-blue-400 border border-blue-500/20 px-2.5 py-0.5 rounded-full font-medium">
              Trilha 03: Educação
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
                      O Mapa de Erros Inteligente <br />
                      <span className="text-blue-500">para o Estudante do ENEM</span>
                  </h1>

                  <p className="text-lg sm:text-xl text-slate-300 max-w-2xl mx-auto leading-relaxed">
                      Descubra exatamente quantos pontos cada erro custou na sua nota final e saiba quais os <strong className="text-white">3 temas prioritários</strong> você precisa revisar agora.
                  </p>

                  <div className="inline-flex items-center gap-2 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs px-3 py-1.5 rounded-md">
                      <span>🔒 100% Privado: Suas respostas ficam salvas apenas no seu aparelho (Regra 6.9).</span>
                  </div>
              </section>
              <section className="bg-slate-900 border border-slate-800 rounded-2xl p-6 sm:p-8 space-y-4">
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
                          Interface preparada com leitor de voz, alto contraste e simplificação de texto para promover inclusão real.
                      </li>
                      <li className="bg-slate-950 p-4 rounded-xl border border-slate-800/80">
                          <strong className="block text-white mb-1">Usuários Focados em Privacidade</strong>
                          Quem deseja conferir respostas sem registrar dados pessoais ou cadastros em servidores externos.
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
                          <p className="text-xs text-slate-400">Preenchimento manual garantido, com suporte a foto do cartão de respostas (OCR) e comando de voz.</p>
                      </div>

                      <div className="bg-slate-900/60 border border-slate-800 p-5 rounded-xl space-y-2">
                          <div className="w-8 h-8 bg-blue-500/20 text-blue-400 font-bold rounded-lg flex items-center justify-center text-sm">2</div>
                          <h3 className="font-semibold text-white text-base">Custo Real do Erro</h3>
                          <p className="text-xs text-slate-400">O motor TRI calcula a estimativa com margem de erro medido e mostra: <i>"Esta questão custou 18,5 pontos"</i>.</p>
                      </div>

                      <div className="bg-slate-900/60 border border-slate-800 p-5 rounded-xl space-y-2">
                          <div className="w-8 h-8 bg-blue-500/20 text-blue-400 font-bold rounded-lg flex items-center justify-center text-sm">3</div>
                          <h3 className="font-semibold text-white text-base">O Que Revisar Agora</h3>
                          <p className="text-xs text-slate-400">Gera um plano acionável destacando os 3 temas de maior impacto para você estudar primeiro.</p>
                      </div>
                  </div>
              </section>
              <section id="demo" className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center space-y-4">
                  <h2 className="font-bold text-slate-200">Demonstração & Pitch (Rascunho Inicial)</h2>
                  <div className="w-full aspect-video bg-slate-950 rounded-xl border border-slate-800 flex flex-col items-center justify-center gap-2 text-slate-500">
                      <span className="text-3xl">▶️</span>
                      <p className="text-sm font-medium">Vídeo de demonstração de até 3 minutos</p>
                      <span className="text-xs text-slate-600">Hack Brasil 2026</span>
                  </div>
              </section>
          </main>
          <footer className="border-t border-slate-900 bg-slate-950 py-6 text-center text-xs text-slate-500">
              <p>Paideia • Projeto desenvolvido para o Hack Brasil 2026</p>
          </footer>
      </div>
  )
}

export default App
