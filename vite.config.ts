import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
      react(),
      tailwindcss(),
  ],
  // Duas páginas: o app React (index.html) e a página do design com o cálculo (previa-visual/index.html).
  build: {
    rollupOptions: {
      input: {
        main: 'index.html',
        previa: 'previa-visual/index.html',
      },
    },
  },
  // Em desenvolvimento, repassa /api para a API Spring local.
  server: { proxy: { '/api': 'http://localhost:8080' } },
})
