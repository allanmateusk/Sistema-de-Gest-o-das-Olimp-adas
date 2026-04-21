# Prompt Cursor — SGO (Sistema de Gestão das Olimpíadas)

Documento de contexto para geração de um sistema **fullstack** com critérios de qualidade e entrega alinhados a um padrão **profissional/sênior**. Use este arquivo como **fonte única de verdade** ao implementar ou refatorar o projeto.

---

## 1. Objetivo e resultado esperado

**Objetivo:** entregar o SGO com backend **Micronaut (Java)**, frontend **React + TypeScript**, autenticação **JWT**, documentação **OpenAPI/Swagger**, orquestração **Docker Compose**, organização em **Clean Architecture**, princípios **SOLID** e uso de **Facade** onde fizer sentido para orquestração de casos de uso, além de diagramas **PlantUML**.

**Resultado esperado (Definition of Done):**

- Subir o stack com `docker-compose up --build` (ou comando documentado equivalente).
- API exposta e documentada no Swagger UI; autenticação JWT operando em rotas protegidas.
- Frontend consumindo a API com cliente HTTP configurado e fluxo de login.
- Modelo de dados persistido em PostgreSQL com migrações versionadas (Liquibase ou Flyway).
- Testes automatizados no backend (unitários nos domínios/use cases; integração nos controllers/repositórios onde aplicável).
- Diagramas PlantUML gerados/atualizados e referenciados no README.

---

## 2. Escopo e premissas

**Dentro do escopo (MVP):**

- Autenticação por email/senha com JWT e perfis **ADMIN** e **USUARIO**.
- Endpoints funcionais para o fluxo olímpico: competições, inscrições, alocações, resultados e relatório de medalhas (conforme lista na seção 5).
- Containerização de backend, frontend e banco; variáveis de ambiente para URLs, segredos JWT e credenciais do banco.

**Fora do escopo (a menos que explicitamente solicitado depois):**

- SSO/OAuth2 corporativo, MFA, filas assíncronas, cache distribuído, Kubernetes.
- App mobile nativo.

**Premissas:**

- Ambiente local com Docker disponível; portas padrão podem ser 3000 (front), 8080 (API), 5432 (Postgres) — documentar qualquer desvio.
- Senhas armazenadas com hash forte (por exemplo BCrypt ou equivalente suportado pelo stack).

---

## 3. Stack técnica (fixa)

| Camada        | Tecnologia                          |
|---------------|-------------------------------------|
| Backend       | Java, Micronaut                     |
| API docs      | OpenAPI / Swagger UI                |
| Auth          | JWT (Bearer)                        |
| Frontend      | React, TypeScript                   |
| HTTP client   | Axios (ou fetch com camada única)   |
| Banco         | PostgreSQL                          |
| Containers    | Docker + Docker Compose             |
| Diagramas     | PlantUML (`.puml` + export imagens)  |

---

## 4. Arquitetura e decisões

**Clean Architecture (camadas sugeridas):**

- **domain:** entidades e regras puras (sem framework).
- **application:** casos de uso, ports (interfaces) para repositórios e serviços externos.
- **infrastructure:** adapters JPA, implementação JWT, configuração OpenAPI, etc.
- **presentation:** controllers REST (Micronaut).
- **facade (opcional mas desejável):** `SistemaOlimpiadasFacade` orquestrando casos de uso quando simplificar composição sem misturar regra de negócio com HTTP.

**Padrões:** SOLID; dependências apontando para **interfaces** na borda interna; evitar “God classes” e acoplamento a frameworks no domínio.

---

## 5. Contrato da API (REST)

**Base path:** `/` (prefixo comum opcional, ex. `/api/v1` — se usar, documentar e aplicar em todo o projeto).

**Endpoints mínimos:**

| Método | Caminho                    | Proteção   | Descrição resumida        |
|--------|----------------------------|------------|---------------------------|
| POST   | `/auth/login`              | Público    | Login email/senha → JWT   |
| POST   | `/competicoes`             | ADMIN      | Cadastrar competição      |
| POST   | `/inscricoes`              | ADMIN      | Inscrever atleta          |
| POST   | `/alocacoes`               | ADMIN      | Alocar local              |
| POST   | `/resultados`              | ADMIN      | Registrar resultado       |
| GET    | `/relatorios/medalhas`     | Autenticado| Relatório de medalhas     |

**Regras de API (padrão sênior):**

- Identificadores estáveis (UUID ou long) e **datas em ISO-8601** (UTC ou offset explícito).
- Respostas de erro **consistentes** (preferir Problem Details / JSON estruturado com `title`, `status`, `detail`, `path` ou equivalente documentado).
- Códigos HTTP adequados: 201 em criação quando aplicável, 400 validação, 401/403 authz, 404 recurso, 409 conflito de regra de negócio, 500 apenas para falhas não tratadas.
- Validação de entrada nos DTOs (Bean Validation ou equivalente); não vazar stack trace ao cliente.
- Swagger com **exemplos** de request/response e descrição de segurança Bearer JWT.

**Perfis:**

- **ADMIN:** gestão do sistema (criações e registros operacionais conforme endpoints).
- **USUARIO:** leitura/consulta conforme política definida (ex.: relatório e consultas públicas internas ao sistema).

---

## 6. Domínio (entidades de referência)

Entidades principais para modelagem e diagramas:

- `Competicao`, `Atleta`, `Pais`, `Local`, `Resultado`, `Usuario`

Casos de uso (application layer), alinhados ao domínio:

- `CadastrarCompeticaoUseCase`
- `InscreverAtletaUseCase`
- `AlocarLocalUseCase`
- `RegistrarResultadoUseCase`
- `GerarRelatorioUseCase`
- `AutenticarUsuarioUseCase`

---

## 7. Persistência e dados

- PostgreSQL via Docker Compose.
- Tabelas esperadas (nomes podem ser ajustados com prefixo/schema, mas manter consistência): `usuarios`, `atletas`, `competicoes`, `resultados`, `paises`, `locais`.
- **Migrações obrigatórias** (Liquibase ou Flyway); proibir “schema só na primeira subida” sem histórico.
- Índices e FKs coerentes com consultas do relatório de medalhas.

---

## 8. Segurança

- JWT com expiração configurável; segredo via variável de ambiente.
- Filtro/guard de autenticação em rotas protegidas; checagem de papel (ADMIN vs USUARIO) onde definido.
- CORS configurado de forma explícita para o origin do frontend em dev (e documentação para produção).
- Não commitar segredos; usar `.env.example` ou documentação de variáveis no README.

---

## 9. Frontend (React + TypeScript)

**Páginas mínimas:** Login, Dashboard, Competições, Inscrição de atletas, Alocação de locais, Resultados, Relatório de medalhas.

**Padrões:**

- Camada única de API (`api.ts` ou similar): `baseURL`, interceptors com `Authorization: Bearer <token>`.
- Estado de autenticação centralizado (Context ou equivalente); persistência do token com decisão explícita (ex.: `sessionStorage` vs `localStorage` e implicações de XSS).
- Tipagem dos DTOs compartilhada ou espelhada a partir dos contratos OpenAPI quando possível.
- Tratamento de erro de API com mensagens amigáveis e mapeamento de 401 (logout/redirecionar login).

---

## 10. Docker

**Backend Dockerfile:** build Gradle (ou wrapper), JVM adequada, expor 8080.

**Frontend Dockerfile:** build de produção e servir artefatos estáticos (nginx ou imagem node conforme padrão escolhido — documentar).

**docker-compose.yml:** serviços `backend`, `frontend`, `postgres`; healthchecks quando viável; rede e volumes nomeados.

---

## 11. Observabilidade e qualidade

- Health/readiness (ex.: Micronaut Health) expostos e documentados.
- Logs estruturados ou pelo menos com correlação clara de requisição em erros.
- Testes: unitários em domínio e use cases; testes de integração para API (Testcontainers ou banco em memória conforme viabilidade do projeto).

---

## 12. Diagramas UML (PlantUML)

Arquivos `.puml` desejados:

- `diagrama-de-caso-de-uso.puml`
- `diagrama-de-classes.puml`
- `diagrama-de-pacotes.puml`
- `diagrama-de-componentes.puml`
- `diagrama-de-implantacao.puml`

**Componentes lógicos a refletir:** React App, API Micronaut, Auth (JWT), PostgreSQL. **Implantação:** containers frontend, backend e banco.

---

## 13. README

Incluir: descrição do sistema, arquitetura, tecnologias, como rodar, variáveis de ambiente, URLs (frontend, API, Swagger), e histórias de usuário:

- US01 — Login  
- US02 — Cadastrar competição  
- US03 — Inscrever atleta  
- US04 — Alocar local  
- US05 — Registrar resultados  
- US06 — Relatório de medalhas  

Diagramas: referenciar imagens exportadas (por exemplo via Markdown com caminhos relativos).

**Comando sugerido:**

```bash
docker-compose up --build
```

**Acessos típicos (ajustar se portas mudarem):**

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui`

---

## 14. Princípios de implementação (para o agente/IDE)

- Priorizar **clareza** e **consistência** sobre complexidade desnecessária.
- Código **didático e profissional**: nomes expressivos, camadas respeitadas, sem duplicar regra de negócio em controller.
- Qualquer desvio das premissas acima deve ser **explícito** em comentário de PR ou seção “Decisões” no README, não apenas no código.

**Instrução final:** gerar ou evoluir o projeto completo com base neste documento, tratando-o como especificação mínima; onde faltar detalhe, aplicar defaults sensatos e documentá-los no README.
