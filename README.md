# BacklogForge — AI-Powered Product Backlog Generator

**BacklogForge** é uma aplicação web para geração assistida por Inteligência Artificial de Product Backlogs estruturados a partir da documentação de projetos de software (arquivos PDF) e informações complementares.

O projeto foi concebido para auxiliar Product Owners, equipes de desenvolvimento e estudantes (especialmente nas APIs da FATEC) a transformarem especificações técnicas em um backlog inicial contendo Épicos, User Stories com estimativas em Story Points, Tasks técnicas, critérios de aceitação e planejamento de Sprints.

---

## 📚 Documentação do Projeto

Toda a documentação técnica está organizada com caminhos relativos e dinâmicos dentro da pasta `docs/`:

- 🏛️ **[Arquitetura e Decisões de Design](./docs/architecture.md)** — Estrutura em camadas (Clean Architecture em Java 21 / Spring Boot 3), frontend React com TypeScript, engenharia de prompt e segurança de credenciais.
- 🚀 **[Guia de Configuração e Execução Local](./docs/setup-and-execution.md)** — Passo a passo para configurar variáveis de ambiente (`.env`), compilar e rodar o Backend e Frontend.
- 📡 **[Contratos de API REST e Schemas JSON](./docs/api-contracts.md)** — Especificação completa dos endpoints REST, payloads, schemas JSON e tratamento de erros.
- 📋 **[Product Backlog e Roadmap MVP](./docs/backlog.md)** — Épicos, User Stories, Tasks e progresso do desenvolvimento.
- 📖 **[Visão Geral e Índice de Documentação](./docs/index.md)** — Visão conceitual do produto e mapa da documentação.

---

## 🛠️ Tecnologias Utilizadas

### Backend (`backend/`)
- **Java 21**
- **Spring Boot 3.3.2** (`spring-boot-starter-web`, `spring-boot-starter-validation`)
- **Spring AI / Google Gemini 1.5 API**
- **Apache PDFBox 3.x** (Extração de texto de PDFs)
- **Maven Wrapper**

### Frontend (`frontend/`)
- **React 18**
- **TypeScript**
- **Vite**
- **Lucide React** (Ícones vetoriais)
- **CSS3 com Design Tokens & Glassmorphic UI**

---

## ⚡ Início Rápido (Quick Start)

### 1. Clonar e Configurar o Ambiente
```bash
git clone https://github.com/DanielDPereira/BacklogForge.git
cd BacklogForge

# Copiar arquivo de exemplo e inserir sua chave do Gemini API
cp .env.example .env
```

### 2. Iniciar o Backend
```bash
cd backend
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Linux/macOS
```
> Acesse: `http://localhost:8080`

### 3. Iniciar o Frontend
```bash
cd frontend
npm install
npm run dev
```
> Acesse no navegador: `http://localhost:5173`

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT — consulte o arquivo [LICENSE](./LICENSE) para mais detalhes.
