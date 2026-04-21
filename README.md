# SGO — Sistema de Gestão das Olimpíadas

Aplicação **fullstack** para gestão de competições olímpicas: cadastro de competições e atletas, alocação de locais, registro de resultados e relatório de medalhas. O repositório é um **monorepo** com API em **Micronaut (Java 21)**, interface em **React + TypeScript (Vite 5)** e banco **PostgreSQL**, com autenticação **JWT**, migrações **Flyway** e contrato documentado em **OpenAPI/Swagger**.

**Stack:** Java 21 · Micronaut 4 · React 19 · PostgreSQL 16 · Docker Compose

---

## Sumário

- [Arquitetura](#arquitetura)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Executar com Docker (recomendado)](#executar-com-docker-recomendado)
- [Desenvolvimento local (sem Docker)](#desenvolvimento-local-sem-docker)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Testes](#testes)
- [Usuários de seed](#usuários-de-seed)
- [Endpoints úteis](#endpoints-úteis)
- [Diagramas UML](#diagramas-uml)
- [Troubleshooting](#troubleshooting)
- [Decisões de produto e técnica](#decisões-de-produto-e-técnica)

---

## Arquitetura

| Camada | Responsabilidade |
|--------|------------------|
| **domain** | Entidades e regras centrais do domínio |
| **application** | Casos de uso (orquestração) |
| **infrastructure** | JPA/Hibernate, Flyway, segurança (BCrypt, JWT), integrações |
| **presentation** | Controllers HTTP, DTOs expostos à API |
| **facade** | `SistemaOlimpiadasFacade` — ponto de entrada agregador quando aplicável |

**Persistência:** Micronaut Data + JPA/Hibernate; schema versionado pelo Flyway (`src/main/resources/db/migration`).

**Segurança:** JWT assinado (HS256), claims de perfil (`ADMIN`, `USUARIO`); senhas com **BCrypt**. No browser, o token vai em **`sessionStorage`** e o Axios envia o header `Authorization` via interceptor (menos persistente em disco que `localStorage`).

**Histórias de usuário (escopo):**

| ID | Funcionalidade |
|----|----------------|
| US01 | Login |
| US02 | Cadastrar competição |
| US03 | Inscrever atleta |
| US04 | Alocar local |
| US05 | Registrar resultados |
| US06 | Relatório de medalhas |

---

## Estrutura do repositório

```
.
├── backend/                 # API Micronaut + Gradle
├── frontend/                # SPA React + Vite
├── diagramas/codigos/       # Fontes PlantUML (.puml)
├── docker-compose.yml       # Postgres + API + Nginx (static do front)
├── .env.example             # Exemplo para JWT no Compose
└── README.md
```

---

## Pré-requisitos

| Ferramenta | Uso |
|------------|-----|
| **Docker Desktop** (ou Docker Engine + Compose v2) | Subir stack completa |
| **Java 21** + **Gradle** (wrapper em `backend/`) | Backend sem container |
| **Node.js 20+** (LTS recomendado) | Frontend em modo dev (`npm run dev`) |
| **PostgreSQL 16** (opcional) | Apenas se rodar API fora do Docker |

---

## Executar com Docker (recomendado)

Na **raiz** do repositório:

```bash
docker compose up --build
```

Modo detached (segundo plano):

```bash
docker compose up --build -d
```

**URLs após subir:**

| Serviço | URL |
|---------|-----|
| Frontend (Nginx servindo o build do Vite) | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui |
| Health | http://localhost:8080/health |

O frontend na imagem Docker é buildado com `VITE_API_URL=http://localhost:8080` (navegador chama a API no host). O Postgres expõe **5432** no host; se já houver outro Postgres local, altere o mapeamento de porta no `docker-compose.yml` ou pare o serviço conflitante.

**JWT em desenvolvimento:** copie `.env.example` para `.env` na raiz se quiser sobrescrever `JWT_SECRET` usado pelo serviço `backend` (veja [Variáveis de ambiente](#variáveis-de-ambiente)).

Para encerrar:

```bash
docker compose down
```

Remover também o volume do Postgres (apaga dados locais):

```bash
docker compose down -v
```

---

## Desenvolvimento local (sem Docker)

### 1. Banco de dados

Suba um PostgreSQL acessível com credenciais alinhadas ao `application.yml` (padrão: host `localhost`, porta `5432`, database `sgo`, usuário/senha `sgo`). As migrações Flyway rodam na inicialização da API.

### 2. Backend

**Linux / macOS / Git Bash:**

```bash
cd backend
./gradlew run
```

**Windows (PowerShell ou CMD):**

```powershell
cd backend
.\gradlew.bat run
```

A API sobe em **http://localhost:8080** (ajustável via `SERVER_PORT`).

### 3. Frontend

```bash
cd frontend
copy .env.example .env
npm install
npm run dev
```

No Windows, `copy` funciona no CMD; no PowerShell você pode usar `Copy-Item .env.example .env`.

Garanta que `frontend/.env` contenha algo como:

```env
VITE_API_URL=http://localhost:8080
```

O Vite usa por padrão a porta **5173**; o CORS da API já inclui `http://localhost:5173`.

---

## Variáveis de ambiente

### Raiz (Docker Compose)

Arquivo `.env` opcional na raiz — hoje o principal é o segredo JWT:

| Variável | Descrição |
|----------|-----------|
| `JWT_SECRET` | Chave HS256 (use valor longo e aleatório; o exemplo do repositório é só para dev) |

Referência: `.env.example` na raiz.

### Backend (`application.yml`)

| Variável | Padrão (exemplo) | Descrição |
|----------|------------------|-----------|
| `SERVER_PORT` | `8080` | Porta HTTP |
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `sgo` | Nome do database |
| `DB_USER` / `DB_PASSWORD` | `sgo` / `sgo` | Credenciais JDBC |
| `JWT_SECRET` | (valor longo em dev) | Segredo assinatura/validação JWT |
| `JWT_EXPIRATION_MINUTES` | `480` | TTL do token |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Origem permitida (pode listar a do Vite em dev) |

### Frontend

| Variável | Descrição |
|----------|-----------|
| `VITE_API_URL` | URL base da API (ex.: `http://localhost:8080`) |

Referência: `frontend/.env.example`.

---

## Testes

```bash
cd backend
./gradlew test
```

No Windows: `.\gradlew.bat test`.

---

## Usuários de seed

Na primeira execução com banco vazio, dados iniciais (incluindo usuários abaixo) são criados pelo código de seed (`DataSeed`).

| Email | Senha | Perfil |
|-------|-------|--------|
| `admin@sgo.local` | `Admin@123` | ADMIN |
| `usuario@sgo.local` | `Usuario@123` | USUARIO |

Também há atletas e países de exemplo; IDs estáveis estão documentados no código em `DataSeed`.

---

## Endpoints úteis

- **Autenticação:** `POST /auth/login`
- **Documentação interativa:** `/swagger-ui`
- **OpenAPI (YAML):** exposto via recursos estáticos Micronaut em `/swagger/**`
- **Saúde:** `GET /health`

Foram expostos **GET** auxiliares (ex.: listagens de competições, locais, atletas) para alimentar formulários no front; o núcleo do fluxo permanece nos **POST** previstos na especificação do MVP.

---

## Diagramas UML

Fontes em `diagramas/codigos/*.puml`. Gere imagens (PNG/SVG) para `diagramas/imagens/` com a ferramenta PlantUML de sua preferência (CLI, plugin de IDE ou [plantuml.com](https://plantuml.com)).

---

## Troubleshooting

| Problema | O que verificar |
|----------|------------------|
| **Porta 5432 em uso** | Outro Postgres no host; pare o serviço ou mude `ports:` do serviço `postgres` no Compose. |
| **Porta 8080 ou 3000 em uso** | Libere a porta ou ajuste mapeamentos no `docker-compose.yml` / `SERVER_PORT`. |
| **Front não fala com a API** | `VITE_API_URL` no `.env` do frontend; CORS (`CORS_ALLOWED_ORIGINS`) contendo a origem do Vite (`http://localhost:5173`) ou do Nginx (`http://localhost:3000`). |
| **JWT inválido após mudar segredo** | Faça login de novo; tokens antigos foram assinados com outro `JWT_SECRET`. |
| **Build Docker do backend falha (rede)** | Intermitência ao baixar dependências Gradle/Maven; execute `docker compose build --no-cache backend` novamente. |

---

## Decisões de produto e técnica

- **GET adicionais** em recursos como competições, locais e atletas para suportar UX dos formulários sem abandonar o contrato mínimo dos POST do MVP.
- **Vite 5** fixado no `package.json` para evitar problemas de binding nativo (ex.: Rolldown/Vite 8) em alguns ambientes Windows.
- **Imagem Docker do backend** usa a distribuição Gradle (`*.tar` com `lib/` + script `bin/sgo-backend`), compatível com o empacotamento Micronaut 4 (o artefato `*-runner.jar` gerado é fino e não substitui o classpath completo sozinho).
