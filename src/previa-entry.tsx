import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Calculator from './Calculator'
import './previa-calculadora.css'

// Monta o cálculo dentro da página do design (previa-visual/index.html).
createRoot(document.getElementById('calculadora-root')!).render(
  <StrictMode>
    <Calculator embedded />
  </StrictMode>,
)
