# BacklogForge — AI-Powered Product Backlog Generator

**BacklogForge** é uma aplicação web de alta performance para geração assistida por Inteligência Artificial de Product Backlogs estruturados a partir da documentação de projetos de software (arquivos PDF vetoriais ou escaneados) e informações complementares.

O projeto foi concebido para auxiliar Product Owners, equipes de desenvolvimento e estudantes (especialmente nas APIs da FATEC) a transformarem especificações técnicas em um backlog inicial completo contendo Épicos, User Stories com estimativas em Story Points (Fibonacci), Tasks técnicas, critérios de aceitação, planejamento determinístico de Sprints e exportação para **Markdown (.md)** e **PDF (.pdf)**.

---

## ✨ Principais Funcionalidades

- 📄 **Processamento Híbrido de PDFs**: Extração direta de texto vetorial e OCR visual multimodal com IA para documentos escaneados ou imagens.
- 🔑 **Rotação de Chaves e Resiliência (Multi-Key Fallback)**: Suporte a múltiplas chaves de API (`GEMINI_API_KEYS`) com cooldown de rate limit (HTTP 429) e alternância automática entre modelos candidatos (`gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-3.6-flash`, `gemini-flash-latest`).
- 🎯 **Geração Determinística de Sprints**: Normalização automática e garantia de quantidade exata de Sprints solicitadas pelo usuário.
- 📥 **Exportação Multiformato**: Download do backlog em arquivo **Markdown (.md)** formatado e em **PDF (.pdf)** profissional com identidade visual Slate/Indigo e paginação inteligente.
- ⚛️ **Interface Web Glassmorphic**: UI moderna em React 18 + TypeScript com controle de tags para tecnologias, drag-and-drop de arquivos e cópia de JSON para a área de transferência.

---

## 📚 Documentação do Projeto

Toda a documentação técnica está organizada de forma modular dentro da pasta `docs/`:

- 🏛️ **[Arquitetura e Decisões de Design](./docs/architecture.md)** — Estrutura em camadas (Clean Architecture em Java 21 / Spring Boot 3.3.2), frontend React com TypeScript, engenharia de prompt, OCR multimodal e resiliência de chaves.
- 🚀 **[Guia de Configuração e Execução Local](./docs/setup-and-execution.md)** — Passo a passo para configurar variáveis de ambiente (`.env`), compilar e rodar o Backend e Frontend.
- 📡 **[Contratos de API REST e Schemas JSON](./docs/api-contracts.md)** — Especificação completa dos endpoints REST, payloads, schemas JSON e exportação PDF/Markdown.
- 📋 **[Product Backlog e Roadmap](./docs/backlog.md)** — Épicos, User Stories, Tasks e progresso detalhado das funcionalidades.
- 📖 **[Visão Geral e Índice de Documentação](./docs/index.md)** — Visão conceitual do produto e mapa completo da documentação.

---

## 🛠️ Tecnologias Utilizadas

### Backend (`backend/`)
- **Java 21**
- **Spring Boot 3.3.2** (`spring-boot-starter-web`, `spring-boot-starter-validation`)
- **Spring AI / Google Gemini API** (`gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-3.6-flash`, `gemini-flash-latest`)
- **Apache PDFBox 3.0.2** (Extração de texto vetorial e renderização PNG para OCR)
- **Dotenv Java 3.1.0** & **Maven Wrapper**

### Frontend (`frontend/`)
- **React 18**
- **TypeScript**
- **Vite**
- **Lucide React** (Ícones vetoriais)
- **CSS3 com Design Tokens & UI Glassmorphic**

---

## ⚡ Início Rápido (Quick Start)

### 1. Clonar e Configurar o Ambiente
```bash
git clone https://github.com/DanielDPereira/BacklogForge.git
cd BacklogForge

# Copiar arquivo de exemplo e inserir sua chave do Gemini API
cp .env.example .env
```

### 2. Iniciar o Backend (Spring Boot)
```bash
cd backend
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Linux/macOS
```
> Acesse a API em: `http://localhost:8080`

### 3. Iniciar o Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
> Acesse a interface web em: `http://localhost:5173`

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT — consulte o arquivo [LICENSE](./LICENSE) para mais detalhes.
