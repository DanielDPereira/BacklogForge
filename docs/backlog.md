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

* [ ] Definir abstração de credenciais.
* [ ] Implementar leitura de múltiplas chaves por configuração.
* [ ] Criar gerenciamento de estado das chaves.
* [ ] Detectar erros de quota e rate limit.
* [ ] Implementar seleção da próxima credencial.
* [ ] Implementar retry controlado.
* [ ] Criar testes de fallback.
* [ ] Documentar configuração.

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

* [ ] Selecionar documentação de uma API da FATEC.
* [ ] Configurar parâmetros reais.
* [ ] Gerar backlog.
* [ ] Avaliar Epics.
* [ ] Avaliar User Stories.
* [ ] Avaliar Tasks.
* [ ] Avaliar distribuição das Sprints.
* [ ] Registrar problemas.
* [ ] Refinar prompt.
* [ ] Executar nova geração.

---

# Roadmap sugerido

## Sprint 1 — Fundação e IA

* US-001
* US-002
* US-003
* US-007
* US-008
* US-009

**Resultado esperado:** backend capaz de receber parâmetros e gerar um backlog JSON estruturado utilizando Spring AI e Gemini.

## Sprint 2 — Documentos e exportação

* US-004
* US-005
* US-006
* US-010
* US-014
* US-015

**Resultado esperado:** aplicação capaz de receber PDFs + texto complementar, validar o resultado e gerar `BACKLOG.md`.

## Sprint 3 — Interface e validação real

* US-011
* US-012
* US-013
* US-016
* US-017

**Resultado esperado:** aplicação web funcional e validada com um projeto real.

# Critério de conclusão do MVP

O MVP do BacklogForge será considerado concluído quando um usuário puder:

1. acessar a interface web;
2. informar o nome do projeto;
3. enviar um ou mais PDFs;
4. inserir informações complementares em texto;
5. definir o número de Sprints;
6. definir a duração das Sprints;
7. informar o tamanho da equipe;
8. informar as tecnologias;
9. solicitar ou não sugestões de tecnologias;
10. gerar o backlog;
11. visualizar o resultado estruturado;
12. baixar o Product Backlog em Markdown;
13. executar o processo novamente com outra documentação.

O sistema deverá gerar um backlog contendo, no mínimo:

* Epics;
* User Stories;
* Tasks;
* critérios de aceitação;
* prioridade;
* Story Points;
* Sprints;
* objetivos das Sprints;
* distribuição das User Stories entre as Sprints.

A atribuição de Tasks a pessoas e a execução automática das tarefas não fazem parte do produto.
