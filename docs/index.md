# BacklogForge

**BacklogForge** é uma aplicação web para geração assistida por Inteligência Artificial de Product Backlogs estruturados a partir da documentação de projetos de software (arquivos PDF vetoriais ou escaneados) e informações complementares.

## 📚 Central de Documentação Técnica

Acesse os documentos detalhados sobre o projeto e a arquitetura:

- 🏛️ **[Arquitetura e Decisões de Design (`architecture.md`)](./architecture.md)** — Explicação da Clean Architecture, camadas Java/Spring Boot, React Frontend, engenharia de prompt, OCR multimodal, resiliência de chaves e exportação PDF.
- 🚀 **[Guia de Configuração e Execução Local (`setup-and-execution.md`)](./setup-and-execution.md)** — Passo a passo para configurar variáveis de ambiente (`.env`), compilar e rodar o Backend e Frontend.
- 📡 **[Contratos de API REST e Schemas JSON (`api-contracts.md`)](./api-contracts.md)** — Especificação detalhada dos endpoints, payloads de requisição, schemas JSON do `ProductBacklog`, exportação Markdown e PDF e tratamento de erros.
- 📋 **[Product Backlog e Roadmap (`backlog.md`)](./backlog.md)** — Épicos, User Stories, Tasks e status do roadmap MVP.

---

A aplicação tem como objetivo auxiliar Product Owners, equipes de desenvolvimento e estudantes na transformação de uma descrição de projeto ou conjunto de requisitos em um backlog inicial, detalhado, organizado e pronto para revisão e utilização no planejamento do desenvolvimento.

O projeto surgiu principalmente a partir da necessidade observada nas APIs (Aprendizagem por Projetos Integradores) da FATEC, nas quais os alunos recebem um desafio de desenvolvimento a cada semestre e precisam transformar as especificações fornecidas em um Product Backlog. O processo de elaboração desse backlog pode exigir uma quantidade considerável de análise, decomposição de requisitos, definição de User Stories, criação de Tasks e planejamento das Sprints.

O BacklogForge busca automatizar a primeira versão desse trabalho, mantendo o Product Owner como responsável pela revisão e validação final do backlog.

## Funcionamento

O usuário fornece as informações necessárias para o planejamento do projeto por meio de dois tipos de entrada:

1. **Documentos PDF**, contendo requisitos, guias, especificações ou regras (suportando texto vetorial e documentos escaneados/baseados em imagens via OCR multimodal por IA);
2. **Texto complementar**, no qual o usuário pode fornecer observações, requisitos adicionais, decisões já tomadas, restrições, sugestões ou qualquer outro contexto relevante que não esteja presente nos documentos.

Além dessas informações, o usuário define parâmetros determinísticos de planejamento, como:

- nome do projeto;
- número de Sprints;
- duração de cada Sprint em semanas;
- tamanho da equipe;
- tecnologias que serão utilizadas;
- opção de permitir sugestão de tecnologias pela IA.

O tamanho da equipe é utilizado pela Inteligência Artificial como uma informação de contexto para calibrar a complexidade e a granularidade das Tasks geradas. Entretanto, o BacklogForge não atribui tarefas a integrantes específicos e não realiza distribuição individual de trabalho.

## Processamento com Inteligência Artificial

O processamento é realizado pelo backend desenvolvido em **Java 21 com Spring Boot 3.3.2**, utilizando **Spring AI** e integração com a **Google Gemini API** (com fallback automático entre modelos candidatos como `gemini-2.5-flash`, `gemini-2.5-pro`, `gemini-3.6-flash` e `gemini-flash-latest`).

A aplicação reúne:

- conteúdo extraído dos documentos PDF (vetoriais ou transcritos via OCR multimodal);
- texto complementar fornecido pelo usuário;
- parâmetros de planejamento;
- regras estritas de geração do backlog.

Essas informações são enviadas ao modelo por meio de um prompt estruturado (`BacklogPromptBuilder`).

A Inteligência Artificial interpreta o contexto do projeto e gera um Product Backlog estruturado contendo:

- Epics;
- User Stories;
- Tasks;
- critérios de aceitação;
- prioridades (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`);
- estimativas em Story Points (Fibonacci: 1, 2, 3, 5, 8, 13);
- Sprints;
- objetivos das Sprints;
- distribuição das User Stories entre as Sprints.

A aplicação garante determinismo no quantitativo de Sprints: o resultado conterá exatamente o número de Sprints solicitado pelo usuário através do mecanismo de normalização de backlog.

## Estrutura do Backlog

O backlog segue uma estrutura hierárquica imutável:

```text
Product Backlog
│
├── Epic
│   ├── User Story
│   │   ├── Task
│   │   ├── Task
│   │   └── Task
│   │
│   └── User Story
│       └── Tasks
│
└── Sprints
    ├── Sprint 1
    │   ├── User Story
    │   └── User Story
    │
    ├── Sprint 2
    │   └── User Stories
    │
    └── ...
```

As Tasks pertencem às User Stories e não são atribuídas a integrantes específicos da equipe.

As Sprints referenciam as User Stories que deverão ser trabalhadas naquele período por seus IDs (`userStoryIds`), evitando duplicação de dados.

## Saída Estruturada e Exportação

A Inteligência Artificial retorna os dados seguindo um **schema JSON fixo**, validado pelo backend antes da exibição.

Inicialmente, são disponibilizadas as seguintes formas de saída e exportação:

- **JSON Estruturado** (visualização e cópia via Clipboard);
- **Arquivo Markdown (`.md`)** gerado deterministicamente pelo `MarkdownExportService`;
- **Arquivo PDF (`.pdf`)** com layout profissional, gerado via Apache PDFBox (`PdfExportService`).

## Arquitetura

A aplicação é dividida em frontend e backend sob um repositório estruturado.

### Frontend

O frontend é desenvolvido em **React 18** com **TypeScript** e **Vite**, proporcionando uma interface web moderna com estética glassmorphic.

A interface permite:

- informar o nome do projeto;
- inserir texto complementar;
- fazer upload de um ou mais arquivos PDF (drag-and-drop);
- definir o número de Sprints e duração em semanas;
- informar o tamanho da equipe;
- informar as tecnologias utilizadas ou solicitar sugestões;
- gerar o backlog com feedback visual durante o processamento;
- visualizar o resultado em abas (*Épicos & User Stories* vs. *Planejamento de Sprints*);
- baixar o backlog em **Markdown (.md)** e em **PDF (.pdf)**;
- copiar o JSON do backlog.

### Backend

O backend é desenvolvido utilizando **Java 21**, **Spring Boot 3.3.2**, **Spring AI**, **Apache PDFBox 3.0.2** e **Bean Validation**.

O backend é responsável por:

- receber e validar as requisições HTTP REST;
- extrair texto vetorial de PDFs ou executar OCR multimodal para documentos escaneados;
- consolidar o contexto e parâmetros;
- executar chamadas resilientes ao Gemini com rotação de chaves (`ApiKeyManager`);
- validar e normalizar a resposta JSON gerada pela IA;
- exportar o backlog para formatos Markdown (`.md`) e PDF (`.pdf`);
- retornar as respostas estruturadas ao frontend.

## Provedores e Chaves de API (Resiliência & Multi-Key Fallback)

A aplicação inclui uma camada de abstração para gerenciamento de credenciais do Gemini (`ApiKeyManager`).

Essa camada suporta múltiplas chaves de API (`GEMINI_API_KEYS` ou `GEMINI_API_KEY` separadas por vírgula) e realiza rotação e fallback automático quando uma chave atinge o limite de uso (HTTP 429 / Rate Limit), entrando em cooldown de 60 segundos antes de reativar a chave.

As credenciais não são armazenadas no código-fonte e são lidas a partir de variáveis de ambiente do arquivo `.env`.

---

## Objetivo do MVP

O objetivo do MVP é disponibilizar uma aplicação funcional e robusta capaz de receber documentações em PDF e informações em texto, aplicar parâmetros determinísticos e gerar um Product Backlog estruturado pronto para ser revisado e validado pelo Product Owner.