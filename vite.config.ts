import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
      react(),
      tailwindcss(),
  ],
  // Em desenvolvimento, repassa /api para a API Spring local.
  server: { proxy: { '/api': 'http://localhost:8080' } },
})
