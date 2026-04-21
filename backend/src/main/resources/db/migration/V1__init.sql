CREATE TABLE paises (
    id UUID PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    codigo_iso CHAR(3) NOT NULL UNIQUE
);

CREATE TABLE locais (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cidade VARCHAR(120) NOT NULL,
    capacidade INTEGER
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL
);

CREATE TABLE atletas (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    pais_id UUID NOT NULL REFERENCES paises (id)
);

CREATE TABLE competicoes (
    id UUID PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    modalidade VARCHAR(120),
    data_inicio TIMESTAMPTZ NOT NULL,
    data_fim TIMESTAMPTZ NOT NULL,
    local_id UUID REFERENCES locais (id)
);

CREATE TABLE inscricoes (
    id UUID PRIMARY KEY,
    atleta_id UUID NOT NULL REFERENCES atletas (id),
    competicao_id UUID NOT NULL REFERENCES competicoes (id),
    UNIQUE (atleta_id, competicao_id)
);

CREATE TABLE alocacoes (
    id UUID PRIMARY KEY,
    competicao_id UUID NOT NULL UNIQUE REFERENCES competicoes (id),
    local_id UUID NOT NULL REFERENCES locais (id)
);

CREATE TABLE resultados (
    id UUID PRIMARY KEY,
    competicao_id UUID NOT NULL REFERENCES competicoes (id),
    atleta_id UUID NOT NULL REFERENCES atletas (id),
    posicao INTEGER NOT NULL CHECK (posicao > 0),
    UNIQUE (competicao_id, atleta_id),
    UNIQUE (competicao_id, posicao)
);

CREATE INDEX idx_resultados_competicao ON resultados (competicao_id);
CREATE INDEX idx_atletas_pais ON atletas (pais_id);
