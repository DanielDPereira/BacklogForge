# BacklogForge

**BacklogForge** é uma aplicação web para geração assistida por Inteligência Artificial de Product Backlogs estruturados a partir da documentação e das informações fornecidas sobre um projeto de software.

A aplicação tem como objetivo auxiliar Product Owners, equipes de desenvolvimento e estudantes na transformação de uma descrição de projeto ou conjunto de requisitos em um backlog inicial, detalhado, organizado e pronto para revisão e utilização no planejamento do desenvolvimento.

O projeto surgiu principalmente a partir da necessidade observada nas APIs (Aprendizagem por Projetos Integradores) da FATEC, nas quais os alunos recebem um desafio de desenvolvimento a cada semestre e precisam transformar as especificações fornecidas em um Product Backlog. O processo de elaboração desse backlog pode exigir uma quantidade considerável de análise, decomposição de requisitos, definição de User Stories, criação de Tasks e planejamento das Sprints.

O BacklogForge busca automatizar a primeira versão desse trabalho, mantendo o Product Owner como responsável pela revisão e validação final do backlog.

## Funcionamento

O usuário fornece as informações necessárias para o planejamento do projeto por meio de dois tipos de entrada:

1. **Documentos PDF**, contendo requisitos, guias, especificações, regras ou outras informações relacionadas ao projeto;
2. **Texto complementar**, no qual o usuário pode fornecer observações, requisitos adicionais, decisões já tomadas, restrições, sugestões ou qualquer outro contexto relevante que não esteja presente nos documentos.

Além dessas informações, o usuário define parâmetros determinísticos de planejamento, como:

- nome do projeto;
- número de Sprints;
- duração de cada Sprint em semanas;
- tamanho da equipe;
- tecnologias que serão utilizadas;
- nível de detalhamento desejado, quando aplicável.

O tamanho da equipe é utilizado pela Inteligência Artificial como uma informação de contexto para calibrar a complexidade e a granularidade das Tasks geradas. Entretanto, o BacklogForge não atribui tarefas a integrantes específicos e não realiza distribuição individual de trabalho.

O usuário também poderá permitir que a Inteligência Artificial sugira tecnologias quando nenhuma stack estiver previamente definida.

## Processamento com Inteligência Artificial

O processamento será realizado pelo backend desenvolvido em **Java com Spring Boot**, utilizando **Spring AI** para integração com modelos de linguagem.

Inicialmente, o projeto utilizará a **API do Gemini**, aproveitando sua disponibilidade para experimentação e desenvolvimento.

A aplicação reunirá:

- conteúdo extraído dos documentos PDF;
- texto complementar fornecido pelo usuário;
- parâmetros de planejamento;
- regras de geração do backlog.

Essas informações serão enviadas ao modelo por meio de um prompt estruturado.

A Inteligência Artificial deverá interpretar o contexto do projeto e gerar um Product Backlog estruturado contendo:

- Epics;
- User Stories;
- Tasks;
- critérios de aceitação;
- prioridades;
- estimativas em Story Points;
- dependências quando forem relevantes;
- Sprints;
- objetivos das Sprints;
- distribuição das User Stories entre as Sprints.

A IA deverá respeitar os parâmetros definidos pelo usuário. Por exemplo, se o usuário definir seis Sprints, o resultado deverá conter exatamente seis Sprints.

## Estrutura do backlog

O backlog seguirá uma estrutura hierárquica:

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

As Tasks pertencem às User Stories e não serão atribuídas a integrantes específicos da equipe.

As Sprints referenciarão as User Stories que deverão ser trabalhadas naquele período, evitando duplicação de informações.

## Saída estruturada

A Inteligência Artificial não será responsável por produzir diretamente o Markdown final.

O modelo deverá retornar os dados seguindo um **schema JSON fixo**, definido pelo sistema.

Esse JSON será validado pelo backend antes de ser utilizado.

A separação entre geração e apresentação permite que o mesmo backlog possa futuramente ser exportado para diferentes formatos sem depender da formatação produzida pelo modelo.

Inicialmente, serão disponibilizadas duas formas de saída:

- JSON estruturado;
- arquivo Markdown (`.md`).

O Markdown será produzido deterministicamente pelo backend a partir do objeto de backlog validado.

## Arquitetura

A aplicação será dividida em frontend e backend.

### Frontend

O frontend será desenvolvido em **React**, proporcionando uma interface web simples e profissional.

A interface deverá permitir:

- informar o nome do projeto;
- inserir texto complementar;
- fazer upload de um ou mais arquivos PDF;
- definir o número de Sprints;
- definir a duração das Sprints;
- informar o tamanho da equipe;
- informar as tecnologias utilizadas;
- solicitar sugestões de tecnologias;
- definir opções de detalhamento;
- gerar o backlog;
- visualizar o resultado;
- baixar o backlog em Markdown.

### Backend

O backend será desenvolvido utilizando:

- Java;
- Spring Boot;
- Spring AI;
- Spring Web;
- Bean Validation;
- Jackson.

O backend será responsável por:

- receber as entradas do frontend;
- validar os parâmetros;
- processar os documentos;
- combinar documentos e texto complementar;
- montar o contexto enviado à IA;
- executar a chamada ao modelo;
- obter o JSON estruturado;
- validar a estrutura retornada;
- verificar o cumprimento dos parâmetros determinísticos;
- gerar o Markdown;
- retornar o resultado ao frontend.

## Provedores e chaves de API

A primeira versão utilizará exclusivamente o Gemini.

Entretanto, a arquitetura deverá prever uma camada de abstração para gerenciamento de credenciais de IA.

Essa camada permitirá futuramente trabalhar com múltiplas chaves do mesmo provedor e realizar fallback quando uma chave atingir limites de uso, apresentar erro de quota ou estiver temporariamente indisponível.

A aplicação não deverá armazenar chaves diretamente no código-fonte ou no repositório.

As credenciais deverão ser fornecidas por variáveis de ambiente ou mecanismos equivalentes de configuração segura.

O suporte completo a múltiplos provedores de IA não faz parte do MVP.

## Princípios do projeto

O BacklogForge não será desenvolvido como um agente autônomo.

Ele será uma aplicação tradicional que utiliza um modelo de linguagem como componente de processamento.

O fluxo principal será:

```text
Entrada do usuário
       ↓
Processamento dos documentos
       ↓
Construção do contexto
       ↓
Spring AI
       ↓
Gemini
       ↓
JSON estruturado
       ↓
Validação
       ↓
Backlog
       ↓
Markdown
```

A aplicação não terá, inicialmente:

- RAG;
- banco de dados;
- agentes autônomos;
- vector database;
- autenticação;
- integração com Jira;
- integração com GitHub;
- atribuição de Tasks;
- gerenciamento individual da equipe;
- execução automática de tarefas;
- múltiplos agentes;
- fine-tuning.

Esses recursos poderão ser considerados futuramente caso surjam necessidades reais.

## Objetivo do MVP

O objetivo do MVP é construir, em aproximadamente duas semanas, uma aplicação funcional capaz de receber um conjunto de requisitos em PDF e informações complementares em texto, aplicar parâmetros de planejamento definidos pelo usuário e gerar um Product Backlog estruturado utilizando Spring AI e Gemini.

O MVP deverá ser suficientemente estável para ser utilizado na elaboração do Product Backlog de uma API da FATEC.

O BacklogForge não pretende substituir o Product Owner. Seu objetivo é produzir uma primeira versão do backlog que possa ser analisada, corrigida e validada pelo responsável pelo produto.

O fluxo esperado é:

```text
Documentação
     +
Contexto adicional
     +
Parâmetros
     ↓
BacklogForge
     ↓
Backlog inicial
     ↓
Revisão do Product Owner
     ↓
Backlog validado
```

Dessa forma, o projeto também funcionará como um experimento prático de utilização do Spring AI para uma aplicação real de Engenharia de Software e Product Management.