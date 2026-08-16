# Product Backlog — BacklogForge

## EPIC-001 — Fundação do Projeto e Backend

### US-001 — Estruturar o projeto Spring Boot

**Prioridade:** CRITICAL
**Story Points:** 3

**Como desenvolvedor, quero estruturar o backend do BacklogForge com Spring Boot para estabelecer a base da aplicação.**

**Critérios de aceitação:**

* O projeto deve utilizar Java e Spring Boot.
* O projeto deve possuir configuração adequada para desenvolvimento local.
* A estrutura de pacotes deve separar responsabilidades.
* O projeto deve executar sem erros.

**Tasks:**

* [x] Criar projeto Spring Boot.
* [x] Configurar Maven.
* [x] Configurar estrutura inicial de pacotes.
* [x] Configurar application properties.
* [x] Criar configuração básica de ambiente.
* [x] Validar execução da aplicação.

---

### US-002 — Configurar Spring AI e Gemini

**Prioridade:** CRITICAL
**Story Points:** 5

**Como desenvolvedor, quero integrar o Spring AI ao Gemini para permitir que o BacklogForge utilize um modelo de linguagem.**

**Critérios de aceitação:**

* A aplicação deve conseguir realizar uma chamada ao Gemini.
* A chave da API não pode estar no código-fonte.
* A aplicação deve utilizar configuração externa para a credencial.
* Erros de comunicação com o provedor devem ser tratados.

**Tasks:**

* [x] Adicionar dependências do Spring AI.
* [x] Configurar integração com Gemini.
* [x] Configurar variável de ambiente para a chave.
* [x] Criar serviço de comunicação com o modelo.
* [x] Criar teste inicial de integração.
* [x] Implementar tratamento básico de erros.

---

### US-003 — Criar contrato de entrada da geração

**Prioridade:** HIGH
**Story Points:** 3

**Como sistema, quero possuir um contrato estruturado para os parâmetros de geração para garantir entradas previsíveis.**

**Critérios de aceitação:**

* O contrato deve representar todos os parâmetros necessários.
* Número de Sprints deve ser obrigatório.
* Duração das Sprints deve ser obrigatória.
* Tamanho da equipe deve ser informado.
* Tecnologias devem ser opcionais.
* Texto complementar deve ser opcional quando houver documento.

**Tasks:**

* [x] Criar DTO de requisição.
* [x] Definir campos obrigatórios.
* [x] Implementar Bean Validation.
* [x] Definir limites válidos para os parâmetros.
* [x] Criar testes de validação.

---

## EPIC-002 — Entrada e Processamento de Documentos

### US-004 — Receber arquivos PDF

**Prioridade:** CRITICAL
**Story Points:** 5

**Como usuário, quero enviar documentos PDF para que o sistema utilize seus conteúdos na geração do backlog.**

**Critérios de aceitação:**

* A aplicação deve aceitar arquivos PDF.
* Arquivos incompatíveis devem ser rejeitados.
* O sistema deve suportar múltiplos PDFs.
* O conteúdo textual dos PDFs deve estar disponível para o processo de geração.

**Tasks:**

* [x] Implementar recebimento multipart.
* [x] Validar extensão e tipo MIME.
* [x] Permitir múltiplos arquivos.
* [x] Extrair texto dos PDFs.
* [x] Suportar extração de PDFs escaneados (baseados em imagens) via OCR multimodal por IA.
* [x] Tratar PDF inválido ou ilegível.
* [x] Criar testes para upload e extração.

---

### US-005 — Receber contexto adicional em texto

**Prioridade:** CRITICAL
**Story Points:** 3

**Como usuário, quero fornecer informações adicionais em texto para complementar os documentos do projeto.**

**Critérios de aceitação:**

* O usuário deve poder informar texto livre.
* O texto deve ser opcional.
* O conteúdo deve ser enviado à IA juntamente com os documentos.
* O sistema deve diferenciar documentação e informações adicionais.

**Tasks:**

* [x] Adicionar campo de texto ao contrato.
* [x] Validar tamanho máximo.
* [x] Integrar texto ao contexto de geração.
* [x] Definir estrutura de contexto para o prompt.
* [x] Criar testes.

---

### US-006 — Consolidar contexto do projeto

**Prioridade:** HIGH
**Story Points:** 5

**Como sistema, quero consolidar documentos, texto adicional e parâmetros para fornecer à IA todo o contexto necessário.**

**Critérios de aceitação:**

* Todos os PDFs devem ser considerados.
* O texto adicional deve ser considerado.
* Os parâmetros devem ser explicitamente informados à IA.
* As diferentes fontes devem ser identificáveis no contexto.

**Tasks:**

* [x] Criar serviço de consolidação.
* [x] Estruturar conteúdo dos documentos.
* [x] Estruturar informações adicionais.
* [x] Estruturar parâmetros.
* [x] Implementar controle de tamanho do contexto.
* [x] Criar testes.

---

## EPIC-003 — Geração do Product Backlog com IA

### US-007 — Definir schema do backlog

**Prioridade:** CRITICAL
**Story Points:** 5

**Como sistema, quero possuir um schema fixo de backlog para garantir que as respostas da IA sejam estruturadas e previsíveis.**

**Critérios de aceitação:**

* O schema deve representar o projeto.
* O schema deve representar Epics.
* O schema deve representar User Stories.
* O schema deve representar Tasks.
* O schema deve representar Sprints.
* Sprints devem referenciar User Stories sem duplicar suas informações.

**Tasks:**

* [x] Definir estrutura JSON.
* [x] Criar classes de domínio.
* [x] Definir enums.
* [x] Definir IDs padronizados.
* [x] Implementar serialização.
* [x] Documentar o contrato.

---

### US-008 — Criar prompt de geração do backlog

**Prioridade:** CRITICAL
**Story Points:** 5

**Como sistema, quero possuir um prompt estruturado para orientar a IA na criação do backlog.**

**Critérios de aceitação:**

* O prompt deve instruir a IA a analisar os requisitos.
* O prompt deve exigir o schema definido.
* O prompt deve exigir exatamente o número de Sprints informado.
* O prompt deve considerar a duração das Sprints.
* O prompt deve considerar o tamanho da equipe.
* O prompt deve proibir atribuição de Tasks a pessoas.
* O prompt deve considerar tecnologias informadas.
* O prompt deve permitir sugestões de tecnologia somente quando solicitado.

**Tasks:**

* [x] Criar prompt base.
* [x] Definir regras de geração.
* [x] Definir regras de decomposição.
* [x] Definir instruções de estimativa.
* [x] Definir regras de Sprint.
* [x] Definir instruções sobre tamanho da equipe.
* [x] Testar prompt com projetos diferentes.
* [x] Refinar prompt.

---

### US-009 — Gerar backlog estruturado

**Prioridade:** CRITICAL
**Story Points:** 8

**Como usuário, quero gerar um Product Backlog a partir das informações fornecidas para obter uma primeira versão estruturada do planejamento do projeto.**

**Critérios de aceitação:**

* A IA deve gerar Epics.
* Cada Epic deve possuir User Stories.
* User Stories devem possuir Tasks.
* User Stories devem possuir critérios de aceitação.
* User Stories devem possuir prioridade.
* User Stories devem possuir Story Points.
* As User Stories devem ser distribuídas nas Sprints.
* O número de Sprints deve corresponder exatamente ao parâmetro informado.

**Tasks:**

* [x] Implementar serviço de geração.
* [x] Integrar prompt ao Spring AI.
* [x] Configurar structured output.
* [x] Desserializar resposta.
* [x] Implementar tratamento de resposta inválida.
* [x] Implementar retry controlado.
* [x] Criar testes de geração.
* [x] Testar com documentação real da API.

---

### US-010 — Validar backlog gerado

**Prioridade:** HIGH
**Story Points:** 5

**Como sistema, quero validar o resultado produzido pela IA para impedir que respostas estruturalmente inválidas sejam apresentadas ao usuário.**

**Critérios de aceitação:**

* O JSON deve respeitar o schema.
* IDs devem possuir formato válido.
* Não podem existir IDs duplicados.
* O número de Sprints deve ser correto.
* User Stories referenciadas pelas Sprints devem existir.
* Tasks devem pertencer a User Stories existentes.

**Tasks:**

* [x] Implementar validação estrutural.
* [x] Validar IDs.
* [x] Validar referências.
* [x] Validar quantidade de Sprints.
* [x] Validar campos obrigatórios.
* [x] Criar mensagens de erro.
* [x] Criar testes de inconsistência.

---

## EPIC-004 — Interface Web

### US-011 — Criar aplicação React

**Prioridade:** HIGH
**Story Points:** 3

**Como usuário, quero acessar uma interface web para configurar a geração do backlog.**

**Critérios de aceitação:**

* A aplicação deve utilizar React.
* A interface deve ser responsiva.
* O formulário deve possuir organização clara.
* O usuário deve conseguir iniciar uma geração.

**Tasks:**

* [x] Criar projeto React.
* [x] Configurar TypeScript.
* [x] Configurar ferramenta de build.
* [x] Criar estrutura inicial de componentes.
* [x] Criar layout principal.

---

### US-012 — Criar formulário de configuração

**Prioridade:** CRITICAL
**Story Points:** 5

**Como usuário, quero informar os parâmetros do projeto para controlar a geração do backlog.**

**Critérios de aceitação:**

* Deve existir campo para nome do projeto.
* Deve existir campo para texto adicional.
* Deve existir upload de PDF.
* Deve existir número de Sprints.
* Deve existir duração das Sprints.
* Deve existir tamanho da equipe.
* Deve existir campo para tecnologias.
* Deve existir opção para permitir sugestões de tecnologia.

**Tasks:**

* [x] Criar campo de nome.
* [x] Criar campo de texto adicional.
* [x] Criar componente de upload.
* [x] Criar campos de Sprint.
* [x] Criar campo de tamanho da equipe.
* [x] Criar campo de tecnologias.
* [x] Criar opção de sugestão de tecnologias.
* [x] Implementar validação.

---

### US-013 — Exibir backlog gerado

**Prioridade:** HIGH
**Story Points:** 5

**Como usuário, quero visualizar o backlog gerado de forma organizada para poder revisá-lo.**

**Critérios de aceitação:**

* Epics devem ser visualizados hierarquicamente.
* User Stories devem ser identificáveis.
* Tasks devem aparecer dentro das User Stories.
* Sprints devem ser visualizadas separadamente.
* Critérios de aceitação devem ser acessíveis.
* Story Points e prioridades devem ser exibidos.

**Tasks:**

* [x] Criar componente de backlog.
* [x] Criar visualização de Epics.
* [x] Criar visualização de User Stories.
* [x] Criar visualização de Tasks.
* [x] Criar visualização de Sprints.
* [x] Implementar expansão/recolhimento.
* [x] Melhorar apresentação visual.

---

## EPIC-005 — Exportação e Confiabilidade

### US-014 — Gerar Markdown

**Prioridade:** HIGH
**Story Points:** 5

**Como usuário, quero exportar o backlog em Markdown para poder utilizá-lo na documentação do projeto.**

**Critérios de aceitação:**

* O Markdown deve ser gerado pelo backend.
* O arquivo deve conter as informações do projeto.
* Epics devem ser apresentados.
* User Stories devem ser apresentadas.
* Tasks devem ser apresentadas.
* Sprints devem ser apresentadas.
* O resultado deve possuir formatação consistente.

**Tasks:**

* [x] Criar MarkdownService.
* [x] Criar template do documento.
* [x] Implementar geração de Epics.
* [x] Implementar geração de User Stories.
* [x] Implementar geração de Tasks.
* [x] Implementar geração de Sprints.
* [x] Criar endpoint ou resposta para download.
* [x] Testar Markdown gerado.

---

### US-015 — Implementar tratamento de erros

**Prioridade:** HIGH
**Story Points:** 3

**Como usuário, quero receber mensagens claras quando ocorrer algum problema durante a geração.**

**Critérios de aceitação:**

* Erros de validação devem ser apresentados.
* PDFs inválidos devem gerar mensagens adequadas.
* Falhas da API devem ser tratadas.
* Respostas inválidas da IA devem ser tratadas.
* O frontend não deve ficar indefinidamente em estado de carregamento.

**Tasks:**

* [x] Criar tratamento global de exceções.
* [x] Mapear erros HTTP.
* [x] Criar respostas padronizadas.
* [x] Implementar tratamento no React.
* [x] Criar estados de loading e erro.

---

### US-016 — Implementar gerenciamento de chaves de API

**Prioridade:** MEDIUM
**Story Points:** 5

**Como administrador da aplicação, quero configurar múltiplas credenciais do provedor de IA para permitir fallback quando uma chave atingir limites de uso ou apresentar indisponibilidade.**

**Critérios de aceitação:**

* As chaves não devem ser armazenadas no código.
* As credenciais devem ser fornecidas por configuração externa.
* O sistema deve conseguir identificar falhas relacionadas a quota ou limite.
* Uma chave indisponível não deve impedir automaticamente novas tentativas utilizando outra credencial configurada.
* O mecanismo deve ser desacoplado do serviço de geração de backlog.

**Tasks:**

* [x] Definir abstração de credenciais.
* [x] Implementar leitura de múltiplas chaves por configuração.
* [x] Criar gerenciamento de estado das chaves.
* [x] Detectar erros de quota e rate limit.
* [x] Implementar seleção da próxima credencial.
* [x] Implementar retry controlado.
* [x] Criar testes de fallback.
* [x] Documentar configuração.

---

### US-017 — Validar BacklogForge com um projeto real

**Prioridade:** CRITICAL
**Story Points:** 5

**Como Product Owner, quero utilizar o BacklogForge com uma documentação real de projeto para avaliar a qualidade do backlog produzido.**

**Critérios de aceitação:**

* O sistema deve processar um documento real.
* O backlog gerado deve possuir estrutura válida.
* O resultado deve ser revisado manualmente.
* Problemas encontrados devem ser registrados.
* O prompt e as regras devem ser ajustados com base nos resultados.

**Tasks:**

* [x] Selecionar documentação de uma API da FATEC.
* [x] Configurar parâmetros reais.
* [x] Gerar backlog.
* [x] Avaliar Epics.
* [x] Avaliar User Stories.
* [x] Avaliar Tasks.
* [x] Avaliar distribuição das Sprints.
* [x] Registrar problemas.
* [x] Refinar prompt.
* [x] Executar nova geração.

---

### US-018 — Exportar Backlog em PDF

**Prioridade:** HIGH
**Story Points:** 5

**Como usuário, quero exportar o backlog em um arquivo PDF profissional para apresentação e documentação.**

**Critérios de aceitação:**

* O PDF deve ser gerado no backend via Apache PDFBox.
* O documento deve ter diagramação limpa, cabeçalho do projeto, resumo e badges de tecnologias.
* Épicos, User Stories, Tasks, critérios de aceitação e Sprints devem ser renderizados de forma clara.
* O layout deve suportar quebras de página dinâmicas mantendo o estado das fontes e cores.
* O endpoint deve retornar cabeçalhos HTTP de download (`Content-Disposition`) em conformidade com a RFC 6266.

**Tasks:**

* [x] Criar `PdfExportService` com Apache PDFBox.
* [x] Implementar renderização de cabeçalho, resumo e stack de tecnologias.
* [x] Implementar renderização de Épicos e User Stories.
* [x] Implementar renderização de Sprints.
* [x] Implementar controle de fontes e cores dinâmico em quebras de página (`PageContext`).
* [x] Implementar endpoint `POST /api/v1/backlog/export-pdf`.
* [x] Adicionar botão de exportação em PDF no frontend.
* [x] Criar testes unitários e de integração para exportação PDF.

---

### US-019 — Edição Inline Interativa do Backlog no Frontend

**Prioridade:** MEDIUM
**Story Points:** 3

**Como usuário/PO, quero poder editar o nome do projeto, resumo, épicos, histórias, tarefas, critérios e sprints diretamente na tela para ajustar o backlog antes de exportar.**

**Critérios de aceitação:**

* Botão de alternância ("Editar Backlog" / "Modo Edição ON") presente no visualizador do backlog.
* Edição inline para Nome do Projeto e Resumo.
* Edição inline para Título e Descrição dos Épicos e inclusão/exclusão de Épicos.
* Edição inline para Título, Descrição, Prioridade (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`) e Story Points das User Stories.
* Edição inline para Critérios de Aceitação (adicionar, alterar e excluir).
* Edição inline para Tasks Operacionais (título, descrição, prioridade, adicionar e excluir).
* Edição inline para Nome e Objetivos das Sprints na aba de planejamento.
* Todas as alterações devem ser preservadas em memória e refletidas instantaneamente na exportação em PDF e Markdown.

**Tasks:**

* [x] Adicionar botão de alternância do Modo Edição no `BacklogViewer.tsx`.
* [x] Adicionar estilos CSS utilitários para inputs e textareas inline (`index.css`).
* [x] Implementar formulários inline em `EpicCard.tsx` para épicos, histórias, critérios e tarefas.
* [x] Implementar formulários inline em `SprintBoard.tsx` para nomes e objetivos de sprints.
* [x] Conectar propagação de estado com o `App.tsx` para garantia de exportação atualizada.

---

### US-020 — Exportação em CSV Universal (Jira / Trello / Azure DevOps)

**Prioridade:** HIGH
**Story Points:** 3

**Como usuário, quero exportar o backlog em um arquivo CSV formatado para importação direta em ferramentas de gestão ágil como Jira, Trello e Azure DevOps.**

**Critérios de aceitação:**

* O CSV deve conter os cabeçalhos padrão: `Issue Type`, `Issue Id`, `Parent Id`, `Summary`, `Description`, `Priority`, `Story Points`, `Sprint`, `Epic Name`.
* Mapeamento automático de tipos (`Epic`, `Story`, `Sub-task`).
* Vínculos de `Parent Id` configurados (Stories vinculadas aos Épicos; Sub-tasks vinculadas às Stories).
* Formatação de UTF-8 com BOM (`\uFEFF`) para preservar acentuação no Excel, Jira e Trello.
* Botão de download "Baixar CSV (Jira/Trello)" disponibilizado na interface web.
* Endpoint `POST /api/v1/backlog/export-csv` integrado e testado.

**Tasks:**

* [x] Criar `CsvExportService` com suporte a BOM UTF-8 e escape de campos CSV.
* [x] Implementar endpoint `POST /api/v1/backlog/export-csv` no `BacklogController.java`.
* [x] Adicionar método `exportCsv` no `api.ts` do frontend.
* [x] Adicionar botão "Baixar CSV (Jira/Trello)" no `BacklogViewer.tsx`.
* [x] Criar testes unitários e de integração em `CsvExportServiceTest.java`.

---

# Roadmap e Status de Execução

## Sprint 1 — Fundação e IA (Concluída ✅)

* [x] US-001 — Estruturar o projeto Spring Boot
* [x] US-002 — Configurar Spring AI e Gemini
* [x] US-003 — Criar contrato de entrada da geração
* [x] US-007 — Definir schema do backlog
* [x] US-008 — Criar prompt de geração do backlog
* [x] US-009 — Gerar backlog estruturado

**Resultado alcançado:** Backend funcional com Spring Boot 3.3.2 e Java 21, gerando backlog em JSON estruturado com IA.

## Sprint 2 — Documentos e Exportação (Concluída ✅)

* [x] US-004 — Receber arquivos PDF (texto vetorial e OCR multimodal)
* [x] US-005 — Receber contexto adicional em texto
* [x] US-006 — Consolidar contexto do projeto
* [x] US-010 — Validar backlog gerado
* [x] US-014 — Gerar Markdown (.md)
* [x] US-015 — Implementar tratamento de erros
* [x] US-018 — Exportar Backlog em PDF (.pdf)

**Resultado alcançado:** Processamento híbrido de PDFs (vetorial + OCR visual Gemini), exportação profissional em Markdown e PDF com paginação inteligente e headers RFC 6266.

## Sprint 3 — Interface e Validação Real (Concluída ✅)

* [x] US-011 — Criar aplicação React
* [x] US-012 — Criar formulário de configuração
* [x] US-013 — Exibir backlog gerado (Épicos e Sprints)
* [x] US-016 — Implementar gerenciamento resiliente de chaves de API (Multi-Key)
* [x] US-017 — Validar BacklogForge com um projeto real (APIs FATEC)

**Resultado alcançado:** Interface web React 18 + TypeScript rica e moderna com rotação de chaves e validação prática.

## Sprint 4 — Extensões: Edição Interativa e Exportação Ágil (Concluída ✅)

* [x] US-019 — Edição Inline Interativa do Backlog no Frontend
* [x] US-020 — Exportação em CSV Universal (Jira / Trello / Azure DevOps)

**Resultado alcançado:** Edição em tempo real de épicos, histórias, tarefas, critérios e sprints antes da exportação, além de download em CSV compatível com Jira e Trello.

---

# Status do MVP: 100% Concluído e Validado 🎉

Todos os 13 critérios de conclusão do MVP foram totalmente atingidos e homologados:

1. [x] Acessar a interface web moderna e responsiva;
2. [x] Informar o nome do projeto;
3. [x] Enviar um ou mais PDFs (com drag-and-drop e suporte a OCR multimodal);
4. [x] Inserir informações complementares em texto;
5. [x] Definir o número de Sprints (com normalização determinística);
6. [x] Definir a duração das Sprints;
7. [x] Informar o tamanho da equipe;
8. [x] Informar as tecnologias do projeto;
9. [x] Solicitar ou não sugestões de tecnologias;
10. [x] Gerar o backlog estruturado via IA;
11. [x] Visualizar o resultado em abas com modo de edição inline interativo;
12. [x] Baixar o Product Backlog em **Markdown (.md)**, **PDF (.pdf)** e **CSV (.csv para Jira/Trello)**;
13. [x] Executar o processo novamente com nova documentação de forma limpa.

O backlog gerado contém integralmente:
- Épicos e User Stories com prioridade e pontuação Fibonacci;
- Tasks técnicas autoexplicativas com níveis de prioridade;
- Critérios de aceitação detalhados;
- Sprints com metas claras e alocação por referência (`userStoryIds`).
