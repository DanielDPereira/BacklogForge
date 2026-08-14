# Guia de Configuração e Execução Local — BacklogForge

Este guia fornece o passo a passo completo para configurar o ambiente de desenvolvimento, compilar e executar o **BacklogForge** (Backend Spring Boot e Frontend React) na sua máquina local.

---

## 📋 Pré-requisitos de Sistema

Antes de começar, certifique-se de ter os seguintes softwares instalados:

1. **Java Development Kit (JDK) 21**:
   - Verifique a instalação no terminal com: `java -version`
2. **Node.js (versão 18.0 ou superior)** e **NPM**:
   - Verifique com: `node -v` e `npm -v`
3. **Chave(s) de API do Google Gemini**:
   - Obtenha uma ou mais chaves gratuitas no [Google AI Studio](https://aistudio.google.com/).

---

## 🔑 1. Configuração das Variáveis de Ambiente

O projeto utiliza um arquivo `.env` na raiz do repositório para carregar variáveis de ambiente locais com segurança.

1. Na raiz do projeto, copie o arquivo `.env.example` para `.env`:
   ```bash
   cp .env.example .env
   ```
2. Abra o arquivo `.env` e insira sua(s) chave(s) da API do Gemini:
   ```env
   # Configurações do Provedor de IA (Google Gemini API)
   # Opção 1: Chave única
   GEMINI_API_KEY=AIzaSyYourActualGeminiApiKeyHere

   # Opção 2: Múltiplas chaves separadas por vírgula para rotação automática de cota (HTTP 429 / Rate Limit):
   # GEMINI_API_KEYS=chave1,chave2,chave3
   
   # Modelo preferencial configurado (com fallback automático para gemini-2.5-pro, gemini-3.6-flash, gemini-flash-latest)
   GEMINI_MODEL=gemini-2.5-flash

   # Configurações do Servidor Backend
   SERVER_PORT=8080
   SPRING_PROFILES_ACTIVE=dev
   ```

---

## 🚀 2. Executando o Backend (Spring Boot)

O backend possui o **Maven Wrapper** incluído, eliminando a necessidade de instalar o Maven globalmente.

### No Windows (PowerShell / CMD):

1. Acesse o diretório `backend`:
   ```powershell
   cd backend
   ```
2. Execute o servidor:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

### No Linux / macOS:

1. Acesse a pasta `backend`:
   ```bash
   cd backend
   ```
2. Execute o servidor:
   ```bash
   ./mvnw spring-boot:run
   ```

O backend estará ativo e ouvindo requisições na porta **8080**: `http://localhost:8080`
- 📑 **Swagger UI (Documentação Interativa da API)**: `http://localhost:8080/swagger-ui.html`
- 📡 **OpenAPI 3.0 Spec**: `http://localhost:8080/v3/api-docs`

### Rodando os Testes Automatizados do Backend:
Para executar a suíte de testes unitários:
```bash
# Dentro do diretório backend:
.\mvnw.cmd test     # Windows
./mvnw test         # Linux/macOS
```

---

## 💻 3. Executando o Frontend (React + Vite)

1. Em uma nova janela de terminal, navegue até a pasta `frontend`:
   ```bash
   cd frontend
   ```
2. Instale as dependências da aplicação:
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```

O frontend estará acessível no navegador em: `http://localhost:5173`

### Compilando o Frontend para Produção:
Para validar a compilação do TypeScript e a geração do bundle estático:
```bash
npm run build
```

---

## 🧪 4. Testando a API REST via curl / Terminal

Se quiser testar a geração de backlog diretamente via linha de comando:

### Exemplo de Requisição (POST `/api/v1/backlog/generate`):

```bash
curl -X POST http://localhost:8080/api/v1/backlog/generate \
  -H "Content-Type: application/json" \
  -d '{
    "projectName": "Sistema de Gestão Escolar",
    "sprintCount": 4,
    "sprintDurationWeeks": 2,
    "teamSize": 3,
    "technologies": ["Java", "Spring Boot", "React"],
    "suggestTechnologies": false,
    "additionalText": "O sistema deve possuir módulo de cadastro de alunos e emissão de boletins."
  }'
```

---

## ❓ Solução de Problemas (Troubleshooting)

| Problema | Causa Provável | Solução |
| :--- | :--- | :--- |
| **`GEMINI_API_KEY não configurada`** | Variável de ambiente ausente no arquivo `.env`. | Certifique-se de configurar `GEMINI_API_KEY` ou `GEMINI_API_KEYS` no arquivo `.env` na raiz do projeto. |
| **`Todas as tentativas de geração falharam (HTTP 429)`** | Limite de cota esgotado na chave única. | Configure múltiplas chaves separadas por vírgula em `GEMINI_API_KEYS` no arquivo `.env` e reinicie o Spring Boot. |
| **`Porta 8080 já em uso`** | Outro processo está utilizando a porta 8080. | Altere o valor de `SERVER_PORT` no `.env` ou encerre a aplicação conflitante. |
| **`Erro de CORS no Frontend`** | O backend não permitiu a origem do frontend. | O `BacklogController` possui a anotação `@CrossOrigin(origins = "*")` habilitada por padrão. |
| **`Erro de leitura do arquivo PDF`** | Arquivo corrompido ou protegido por senha. | O BacklogForge suporta texto vetorial e documentos escaneados/imagens via OCR multimodal. Verifique se o arquivo não possui senha de proteção. |
