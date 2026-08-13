# Guia de Configuração e Execução Local — BacklogForge

Este guia fornece o passo a passo completo para configurar o ambiente de desenvolvimento, compilar e executar o **BacklogForge** (Backend Spring Boot e Frontend React) na sua máquina local.

---

## 📋 Pré-requisitos de Sistema

Antes de começar, certifique-se de ter os seguintes softwares instalados:

1. **Java Development Kit (JDK) 21**:
   - Verifique a instalação no terminal com: `java -version`
2. **Node.js (versão 18.0 ou superior)** e **NPM**:
   - Verifique com: `node -v` e `npm -v`
3. **Chave de API do Google Gemini**:
   - Obtenha uma chave gratuita no [Google AI Studio](https://aistudio.google.com/).

---

## 🔑 1. Configuração das Variáveis de Ambiente

O projeto utiliza um arquivo `.env` na raiz do repositório para carregar variáveis de ambiente locais com segurança.

1. Na raiz do projeto, copie o arquivo `.env.example` para `.env`:
   ```bash
   cp .env.example .env
   ```
2. Abra o arquivo `.env` e insira sua chave da API do Gemini:
   ```env
   # Configurações do Provedor de IA (Google Gemini API)
   GEMINI_API_KEY=AIzaSyYourActualGeminiApiKeyHere

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
2. Defina a variável de ambiente no seu terminal (ou utilize o arquivo `.env`):
   ```powershell
   $env:GEMINI_API_KEY="SuaChaveDoGeminiAqui"
   ```
3. Execute o servidor:
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
   export GEMINI_API_KEY="SuaChaveDoGeminiAqui"
   ./mvnw spring-boot:run
   ```

O backend estará ativo e ouvindo requisições na porta **8080**: `http://localhost:8080`

### Rodando os Testes Automatizados do Backend:
Para executar a suíte de testes unitários:
```powershell
java "-Dmaven.multiModuleProjectDirectory=c:\Users\Daniel\Documents\.Projetos\BacklogForge\backend" -classpath .mvn/wrapper/maven-wrapper.jar org.apache.maven.wrapper.MavenWrapperMain test
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
| **`GEMINI_API_KEY não configurada`** | A variável de ambiente não foi definida no terminal ou no `.env`. | Certifique-se de definir `$env:GEMINI_API_KEY="sua_chave"` no mesmo terminal onde o Spring Boot é iniciado. |
| **`Porta 8080 já em uso`** | Outro processo está utilizando a porta 8080. | Altere o valor de `SERVER_PORT` no `.env` ou feche a aplicação conflitante. |
| **`Erro de CORS no Frontend`** | O backend não permitiu a origem do frontend. | O `BacklogController` já possui a anotação `@CrossOrigin(origins = "*")` habilitada por padrão. |
| **`Erro de leitura do arquivo PDF`** | O PDF enviado está protegido por senha ou corrompido. | Certifique-se de que o PDF contém texto selecionável e não é uma imagem rasterizada sem OCR. |
