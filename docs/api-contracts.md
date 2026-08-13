# Contratos da API REST e Schemas JSON — BacklogForge

Este documento detalha os contratos dos endpoints REST disponibilizados pelo backend do **BacklogForge**, bem como a especificação completa do schema JSON retornado pela inteligência artificial.

**Base URL**: `http://localhost:8080/api/v1/backlog`

---

## 📡 Endpoints REST

### 1. `POST /api/v1/backlog/generate`

Gera um Product Backlog estruturado a partir de parâmetros determinísticos em JSON e texto complementar (sem anexar arquivos PDF).

- **Content-Type**: `application/json`
- **Body da Requisição (`GenerateBacklogRequest`)**:

```json
{
  "projectName": "Portal do Aluno FATEC",
  "sprintCount": 6,
  "sprintDurationWeeks": 2,
  "teamSize": 4,
  "technologies": ["Java 21", "Spring Boot", "React", "PostgreSQL"],
  "suggestTechnologies": false,
  "additionalText": "Sistema para acompanhamento de notas, frequências e rematrícula online dos alunos."
}
```

- **Validações do Contrato**:
  - `projectName`: Obrigatório (`@NotBlank`), máximo de 100 caracteres.
  - `sprintCount`: Obrigatório (`@NotNull`), mínimo 1, máximo 20.
  - `sprintDurationWeeks`: Obrigatório (`@NotNull`), mínimo 1, máximo 8.
  - `teamSize`: Obrigatório (`@NotNull`), mínimo 1, máximo 50.
  - `technologies`: Opcional (Lista de strings).
  - `suggestTechnologies`: Opcional (boolean, padrão `false`).
  - `additionalText`: Opcional (máximo de 10.000 caracteres).

- **Resposta de Sucesso (HTTP 200 OK)**: Objeto `ProductBacklog` (ver schema abaixo).

---

### 2. `POST /api/v1/backlog/generate-with-pdf`

Gera um Product Backlog combinando os parâmetros determinísticos com o texto extraído de um ou mais arquivos PDF anexados.

- **Content-Type**: `multipart/form-data`
- **Partes da Requisição**:
  - `request` (`application/json`): Objeto `GenerateBacklogRequest` serializado em JSON.
  - `files` (`application/pdf`, opcional): Um ou múltiplos arquivos PDF enviados no upload.

- **Resposta de Sucesso (HTTP 200 OK)**: Objeto `ProductBacklog`.

---

### 3. `POST /api/v1/backlog/export-markdown`

Recebe um objeto `ProductBacklog` e gera o conteúdo formatado em arquivo Markdown (`.md`) para download.

- **Content-Type**: `application/json`
- **Body da Requisição**: Objeto `ProductBacklog`.
- **Headers da Resposta**:
  - `Content-Type`: `text/markdown; charset=UTF-8`
  - `Content-Disposition`: `attachment; filename="Nome_Do_Projeto.md"`
- **Resposta de Sucesso (HTTP 200 OK)**: Fluxo de bytes do arquivo Markdown.

---

## 🧩 Schema JSON do `ProductBacklog`

A Inteligência Artificial (Gemini) é orientada por um prompt estruturado a retornar estritamente a seguinte estrutura JSON:

```json
{
  "projectName": "Portal do Aluno FATEC",
  "summary": "Sistema completo para gestão acadêmica e acompanhamento do histórico escolar.",
  "suggestedTechnologies": [
    "Java 21",
    "Spring Boot",
    "React",
    "PostgreSQL"
  ],
  "epics": [
    {
      "id": "EPIC-001",
      "title": "Gestão de Autenticação e Perfis",
      "description": "Épico responsável pelo controle de acesso de alunos, professores e coordenação.",
      "userStories": [
        {
          "id": "US-001",
          "title": "Autenticar Aluno",
          "description": "Como aluno, quero efetuar login com RA e senha para acessar meu boletim.",
          "priority": "CRITICAL",
          "storyPoints": 5,
          "acceptanceCriteria": [
            "Deve validar o RA e senha no banco de dados.",
            "Deve retornar um token JWT com expiração de 8 horas."
          ],
          "tasks": [
            {
              "id": "TASK-001",
              "title": "Criar endpoint de Login REST",
              "description": "Desenvolver a camada Controller e Service de autenticação.",
              "priority": "CRITICAL"
            },
            {
              "id": "TASK-002",
              "title": "Configurar Spring Security e JWT",
              "description": "Implementar filtro de validação de tokens JWT.",
              "priority": "HIGH"
            }
          ]
        }
      ]
    }
  ],
  "sprints": [
    {
      "id": "SPRINT-01",
      "name": "Sprint 1",
      "goal": "Estabelecer a fundação do sistema e a camada de autenticação dos alunos.",
      "userStoryIds": [
        "US-001"
      ]
    }
  ]
}
```

### Regras de Domínio do Schema:
1. **Prioridades Válidas**: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.
2. **Story Points**: Valores da sequência Fibonacci (`1`, `2`, `3`, `5`, `8`, `13`).
3. **Sprints por Referência**: O campo `userStoryIds` da Sprint armazena apenas os identificadores (ex: `["US-001", "US-002"]`), evitando duplicar os dados das User Stories.
4. **Sem Atribuição de Pessoas**: O objeto `Task` **não possui** campo de responsável individual.

---

## ⚠️ Schema de Respostas de Erro

Quando ocorre uma exceção tratada pelo `GlobalExceptionHandler`, o backend retorna a seguinte estrutura JSON:

```json
{
  "timestamp": "2026-08-13T10:30:00.123456",
  "status": 400,
  "error": "Erro de Validação",
  "errors": {
    "projectName": "O nome do projeto é obrigatório.",
    "sprintCount": "O projeto deve ter no mínimo 1 Sprint."
  }
}
```

### Tabela de Status HTTP da API:

| Status Code | Tipo de Erro | Descrição |
| :--- | :--- | :--- |
| **`400 Bad Request`** | Erro de Validação | Parâmetros DTO inválidos ou arquivo PDF corrompido/sem extensão válida. |
| **`422 Unprocessable Entity`** | Backlog Inválido | A IA produziu um backlog que viola validações estruturais (ex: quantidade incorreta de Sprints). |
| **`502 Bad Gateway`** | Erro no Provedor de IA | Erro de comunicação, limite de quota ou chave de API inválida no Google Gemini. |
| **`500 Internal Server Error`** | Erro Inesperado | Erro genérico de execução no servidor backend. |
