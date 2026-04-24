# SGO — Sistema de Gestão das Olimpíadas

Aplicação **fullstack** para gestão de competições olímpicas: cadastro de competições e atletas, alocação de locais, registro de resultados e relatório de medalhas. O repositório é um **monorepo** com API em **Micronaut (Java 21)**, interface em **React + TypeScript (Vite 5)** e banco **PostgreSQL**, com autenticação **JWT**, migrações **Flyway** e contrato documentado em **OpenAPI/Swagger**. A **API** inclui respostas de erro padronizadas, `X-Request-ID`, **rate limit** no login, testes de unidade e **CI**; o **frontend** evita credenciais pré-preenchidas e segue o tema no espírito olímpico. Para **produção**, exige-se [configuração explícita de segredos e de seed](#segurança-e-ambiente-de-produção).

**Stack:** Java 21 · Micronaut 4 · React 19 · PostgreSQL 16 · Docker Compose

---

## Sumário

- [Arquitetura](#arquitetura)
- [Interface (frontend)](#interface-frontend)
- [API: erros, rastreio e limites](#api-erros-rastreio-e-limites)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Executar com Docker (recomendado)](#executar-com-docker-recomendado)
- [Desenvolvimento local (sem Docker)](#desenvolvimento-local-sem-docker)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Segurança e ambiente de produção](#segurança-e-ambiente-de-produção)
- [Testes e CI](#testes-e-ci)
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

Detalhes adicionais da API: [API: erros, rastreio e limites](#api-erros-rastreio-e-limites).

---

## Interface (frontend)

A SPA em React usa um **tema** inspirado no espírito dos Jogos Olímpicos: paleta com azul, verde, dourado e toques ciano/coral, gradiente de fundo e acentos nas medalhas. A tipografia combina **Lexend** (texto e interface) e **Bebas Neue** (títulos e marca **SGO**), carregadas via Google Fonts. Há indicadores visuais dos cinco anéis (ilustração, não logotipo oficial) e o menu em formato de pílulas. O ficheiro central de estilos é `frontend/src/index.css`. Em desenvolvimento, o Vite continua a servir a app na porta padrão (**5173**). Se `VITE_API_URL` **não** for definida no `.env`, o axios usa a base relativa `/api` e o proxy do Vite reencaminha para a API. Testes: **Vitest** (`npm test`; inclui por exemplo `config.test.ts`).

---

## API: erros, rastreio e limites

| Aspeto | Comportamento |
|--------|----------------|
| **Corpo de erro** | DTO `ErrorResponse` (tipo, título, `status`, `detail`, `path` e opcionalmente `requestId`), incluindo validação Bean Validation (400) e problemas de serialização JSON (400) quando aplicável. |
| **`X-Request-ID`** | Filtro global gera ou reenvia o ID, repõe o cabeçalho na resposta e o inclui no MDC (logs) e nos erros quando houver. |
| **Exceções de domínio** | `BusinessException` (409), `NotFoundException` (404), `UnauthorizedException` (401); falhas genéricas devolvem 500 com mensagem amigável e registo de log. |
| **Login** | Limite de tentativas no `POST /auth/login` (janela móvel por IP / `X-Forwarded-For`), com resposta **429** e cabeçalho `Retry-After` quando o limite é excedido. Pode desativar com `sgo.security.login-rate.enabled` / variáveis listadas [abaixo](#variáveis-de-ambiente). |

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
├── diagramas/
│   ├── codigos/             # Fontes PlantUML (.puml) — estilo inlinado, prontas para a Web
│   └── imagens/             # PNG/SVG exportados (opcional; .gitignore ou commit conforme a equipa)
├── docker-compose.yml       # Postgres + API + Nginx (stático do front, proxy /api)
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
| Nginx (build do Vite, *SPA*) | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui |
| Health | http://localhost:8080/health |

No *build* Docker, o *frontend* usa `VITE_API_URL=/api`: o browser fala com a **mesma origem** (porta **3000**); o Nginx encaminha `location /api/` para o serviço `backend:8080` (ver `frontend/nginx.conf`), sem o cliente tratar a API noutro *host* explícito. O Postgres mapeia **5432** no *host*; se a porta estiver ocupada, ajuste o mapeamento no `docker-compose.yml` ou use outro Postgres noutro mapeamento.

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
| `SGO_LOGIN_RATE_ENABLED` | (implícito `true`) | `true` / `false` — ativa ou desativa o rate limit do login. |
| `SGO_LOGIN_RATE_MAX` | `20` | Máximo de tentativas de login **por chave (IP/forwarded)** na janela. |
| `SGO_LOGIN_RATE_WINDOW_SECONDS` | `60` | Tamanho da janela em segundos para o contador. |
| `SGO_SEED_DEMO_ACCOUNTS` | `true` | Criar as contas `admin@` e `usuario@` na primeira subida, se ainda não houver utilizadores. |
| `SGO_SEED_ADMIN_PASSWORD` | (herda padrão `Admin@123` no `application.yml`) | Palavra-passe mín. 8 carateres. **Defina outra em produção** ou defina `SGO_SEED_DEMO_ACCOUNTS=false`. |
| `SGO_SEED_USUARIO_PASSWORD` | (herda padrão `Usuario@123`) | Idem. |

(Equivalentes `sgo.*` e `sgo.security.*` no `application.yml` do backend. No arranque, a API emite *warnings* se o `JWT_SECRET` tiver o valor de demonstração ou as palavras-passe de seed forem as de exemplo; não aborta, para não bloquear o desenvolvimento local.)

### Frontend

| Variável | Descrição |
|----------|-----------|
| `VITE_API_URL` | URL base da API (ex.: `http://localhost:8080`) |

Referência: `frontend/.env.example`. Em dev, pode omitir `VITE_API_URL` para usar o proxy do Vite em `/api` (veja `frontend/vite.config.ts`).

---

## Segurança e ambiente de produção

- **Nunca** confie nos valores padrão de `JWT_SECRET` (nem no valor `changeMe...` do repositório). Gere e injete segredo longo, aleatório, por variável de ambiente. Em qualquer fim de compromisso do repositório, gire a chave e forçe re-login.
- O **formulário de login** do frontend deixa e-mail e palavra-passe vazios intencionalmente, para evitar vazar credenciais de demonstração no código da UI.
- **Seed** (`DataSeed`): o `docker-compose` declara `SGO_SEED_*` para o ambiente local. Em **produção** use `SGO_SEED_DEMO_ACCOUNTS=false` (ou nunca exponha a API sem alterar o segredo) e crie a primeira administração de forma controlada (migração, outro processo, ou fornecer palavras-passe fortes via `SGO_SEED_ADMIN_PASSWORD` e `SGO_SEED_USUARIO_PASSWORD` apenas de forma confidencial).
- **Rate limit** e **CORS** devem ser ajustados ao *hostname* e à política reais; revisão periódica de dependências (`./gradlew dependencyUpdates` / `npm audit`).

---

## Testes e CI

**Backend (JUnit + Mockito):** todos os casos de uso em `application` têm testes de unidade, junto de testes para autenticação, cadastro de competição e o limitador de janela móvel do rate limit. Executar:

```bash
cd backend
./gradlew test
```

No Windows: `.\gradlew.bat test`.

**Frontend (Vitest):** exercícios básicos e script:

```bash
cd frontend
npm test
```

**CI:** o workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) (GitHub Actions) corre em `push` e `pull request`: `chmod +x` no wrapper Gradle, `test` e `assemble` do backend, `npm ci` + `npm test` + `npm run build` do frontend (Node 20, cache npm).

---

## Usuários de seed (desenvolvimento e demonstração)

Com **`SGO_SEED_DEMO_ACCOUNTS=true`** (padrão no `application.yml` e no Compose) e **base ainda vazia**, o `DataSeed` pode criar os utilizadores abaixo, desde que `SGO_SEED_ADMIN_PASSWORD` e `SGO_SEED_USUARIO_PASSWORD` sejam definições com **pelo menos 8 carateres** (os valores padrão no ficheiro de configuração são as palavras-passe de exemplo, apenas para início local).

| Email | Perfil comum (quando o seed corre) | Palavra-passe padrão do exemplo |
|-------|----------------------------------|--------------------------------|
| `admin@sgo.local` | `ADMIN` | A definir por `SGO_SEED_ADMIN_PASSWORD` (ex.: de desenvolvimento `Admin@123`) |
| `usuario@sgo.local` | `USUARIO` | Idem, `SGO_SEED_USUARIO_PASSWORD` (ex.: `Usuario@123`) |

Se o seed de contas for ignorado (palavras-passe muito curtas) ou se `SGO_SEED_DEMO_ACCOUNTS=false` com base sem utilizadores, é necessário criar o primeiro acesso fora do fluxo automático.

Há ainda atletas e países de exemplo; **IDs** estáveis estão em `DataSeed`.

---

## Endpoints úteis

- **Autenticação:** `POST /auth/login`
- **Documentação interativa:** `/swagger-ui` (após compilar, o UI é gerado em `META-INF/swagger/views/swagger-ui`, ativado por `backend/openapi.properties` com `swagger-ui.enabled=true`)
- **OpenAPI (YAML):** `GET /swagger/sgo-api-0.1.0.yml` (ficheiro gerado na compilação em `META-INF/swagger/`)
- **Saúde:** `GET /health`

Foram expostos **GET** auxiliares (ex.: listagens de competições, locais, atletas) para alimentar formulários no front; o núcleo do fluxo permanece nos **POST** previstos na especificação do MVP.

---

## Diagramas UML

As fontes estão em `diagramas/codigos/`. O ficheiro `diagramas/codigos/_estilo-plantuml.puml` é a **referência** de tipografia e cores: o mesmo bloco de *skinparam* está **inlinado** em cada `diagrama-*.puml`, para o [PlantUML Web](https://www.plantuml.com/plantuml/uml) e a CLI **sem** `!include` (basta abrir o `.puml` e colar, ou apontar o *jar* à pasta). Quando globalizar o estilo, edite `_estilo-plantuml.puml` e **reaplique** o conteúdo a todos os `diagrama-*.puml`, ou use *find & replace* no diretório `diagramas/codigos/`.

| Ficheiro | Conteúdo |
|----------|----------|
| `diagrama-de-pacotes.puml` | Pacotes `com.sgo`, dependências (facade, use cases, JPA, *rate limit*), `allowmixing` (pacotes + classes) |
| `diagrama-de-classes.puml` | Entidades JPA, `PerfilUsuario` e relações, restrições de unicidade |
| `diagrama-de-componentes.puml` | Nível de contentores: browser, Nginx, API Micronaut, PostgreSQL, *proxy* **/api** |
| `diagrama-de-implantacao.puml` | *Docker Compose*: nós, portas, volume, *health check* (rótulos com `component "..."` para o PlantUML 1.2026) |
| `diagrama-de-caso-de-uso.puml` | US01–US06, atores, `<<include>>` a partir de U01, alinhamento com o *front* |
| `diagrama-sequencia-autenticacao.puml` | *POST /auth/login* (filtro, facade, *BCrypt*, *JWT*), *skinparam* extra para *sequence* |

Gere **PNG** ou **SVG** para `diagramas/imagens/`, com a [CLI do PlantUML](https://plantuml.com/download) (**Java** + *jar* oficial) ou o *textarea* e “Exportar”. A partir da raiz do repositório (ajuste o caminho do *jar*):

```bash
java -Dfile.encoding=UTF-8 -jar plantuml.jar -charset UTF-8 -tpng -Sdpi=200 -o ../imagens diagramas/codigos/diagrama-*.puml
```

Ficheiros cujo nome começa por `_` (o estilo de referência) **não** entram nesse padrão `diagrama-*.puml` — evita gerar o `_estilo` sozinho. Cada ficheiro `diagrama-*.puml` é **autocontido**; cola-o integralmente no PlantUML Web.

---

## Troubleshooting

| Problema | O que verificar |
|----------|------------------|
| **Porta 5432 em uso** | Outro Postgres no host; pare o serviço ou mude `ports:` do serviço `postgres` no Compose. |
| **Porta 8080 ou 3000 em uso** | Libere a porta ou ajuste mapeamentos no `docker-compose.yml` / `SERVER_PORT`. |
| **Front não fala com a API** | `VITE_API_URL` no `.env` do frontend; CORS (`CORS_ALLOWED_ORIGINS`) contendo a origem do Vite (`http://localhost:5173`) ou do Nginx (`http://localhost:3000`). |
| **JWT inválido após mudar segredo** | Faça login de novo; tokens antigos foram assinados com outro `JWT_SECRET`. |
| **Login retorna 429 (Too Many Requests)** | Muitas tentativas; aguarde o tempo em `Retry-After` ou desative/ajuste o rate limit com `SGO_LOGIN_RATE_ENABLED` e variáveis relacionadas. |
| **Build Docker do backend falha (rede)** | Intermitência ao baixar dependências Gradle/Maven; execute `docker compose build --no-cache backend` novamente. |
| **Swagger UI em branco ou 404** | Confirme `swagger-ui.enabled=true` em `backend/openapi.properties`; execute `./gradlew clean classes` e veja o log a mencionar *Writing OpenAPI View to destination: .../swagger-ui/index.html*. Ajuste a URL de servidor na spec se a API não estiver em `http://localhost:8080` (ou use *Try it out* com o *server* corrigido). |

---

## Decisões de produto e técnica

- **GET adicionais** em recursos como competições, locais e atletas para suportar UX dos formulários sem abandonar o contrato mínimo dos POST do MVP.
- **Tratamento global de exceções** e **erros de validação/JSON** alinhados ao DTO de erro, **request ID** para suporte e logs, e **rate limit** no login para reduzir força bruta (janela em memória por instância; em várias instâncias de API seria necessário outro mecanismo, p.ex. cache partilhado).
- **Reactor (reactor-core)** no class path para filtros HTTP que mapeiam a cadeia reativa de resposta.
- **Interface** com identidade “olímpiadas / desporto”: tipografia, cores e padrão visual descritos em [Interface (frontend)](#interface-frontend), sem reutilizar o logotipo olímpico protegido.
- **Vite 5** fixado no `package.json` para evitar problemas de binding nativo (ex.: Rolldown/Vite 8) em alguns ambientes Windows.
- **Imagem Docker do backend** usa a distribuição Gradle (`*.tar` com `lib/` + script `bin/sgo-backend`), compatível com o empacotamento Micronaut 4 (o artefato `*-runner.jar` gerado é fino e não substitui o classpath completo sozinho).
- **Seed e credenciais:** contas iniciais passíveis de configuração por `SGO_SEED_*` e `sgo.seed.*`; o arranque regista *warnings* se o JWT ou as palavras-passe forem de demonstração. O ecrã de **login** não preenche e-mail nem palavra-passe (evita expor credenciais de exemplo no cliente).
