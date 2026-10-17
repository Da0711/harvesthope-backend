
CREATE DATABASE harvesthope;
GO

USE harvesthope;
GO

-- Tabela: usuario
CREATE TABLE dbo.usuario (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(50) NOT NULL,

    CONSTRAINT PK_usuario PRIMARY KEY (id),
    CONSTRAINT UQ_usuario_email UNIQUE (email)
);
GO

-- Tabela: instituicao
-- Cada instituição está associada a um usuário.
CREATE TABLE dbo.instituicao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome VARCHAR(255) NULL,
    categoria VARCHAR(255) NULL,
    cnpj VARCHAR(30) NULL,
    descricao VARCHAR(MAX) NULL,
    localizacao VARCHAR(255) NULL,
    telefone VARCHAR(30) NULL,
    email VARCHAR(255) NULL,
    site VARCHAR(255) NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT PK_instituicao PRIMARY KEY (id),
    CONSTRAINT FK_instituicao_usuario
        FOREIGN KEY (usuario_id) REFERENCES dbo.usuario(id),
    CONSTRAINT UQ_instituicao_usuario UNIQUE (usuario_id)
);
GO

-- Tabela: doacao
CREATE TABLE dbo.doacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    nome_alimento VARCHAR(255) NULL,
    quantidade FLOAT NULL,
    unidade VARCHAR(50) NULL,
    data_validade VARCHAR(255) NULL,
    descricao VARCHAR(MAX) NULL,
    foto VARCHAR(MAX) NULL,
    status VARCHAR(50) NULL,
    local_retirada VARCHAR(255) NULL,
    usuario_id BIGINT NULL,
    categoria VARCHAR(255) NULL,

    CONSTRAINT PK_doacao PRIMARY KEY (id),
    CONSTRAINT FK_doacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES dbo.usuario(id)
);
GO

-- Tabela: solicitacao
CREATE TABLE dbo.solicitacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    instituicao_id BIGINT NOT NULL,
    doacao_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    data_solicitacao DATETIME2 NOT NULL,

    CONSTRAINT PK_solicitacao PRIMARY KEY (id),
    CONSTRAINT FK_solicitacao_instituicao
        FOREIGN KEY (instituicao_id) REFERENCES dbo.instituicao(id),
    CONSTRAINT FK_solicitacao_doacao
        FOREIGN KEY (doacao_id) REFERENCES dbo.doacao(id)
);
GO

-- Tabela: notificacao
-- Usada para registrar avisos ligados às solicitações.
CREATE TABLE dbo.notificacao (
    id BIGINT IDENTITY(1,1) NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    mensagem VARCHAR(1000) NOT NULL,
    tipo VARCHAR(40) NOT NULL
        CONSTRAINT DF_notificacao_tipo DEFAULT ('INFO'),
    lida BIT NOT NULL
        CONSTRAINT DF_notificacao_lida DEFAULT (0),
    data_criacao DATETIME2 NOT NULL
        CONSTRAINT DF_notificacao_data_criacao DEFAULT (SYSDATETIME()),
    solicitacao_id BIGINT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT PK_notificacao PRIMARY KEY (id),
    CONSTRAINT FK_notificacao_usuario
        FOREIGN KEY (usuario_id) REFERENCES dbo.usuario(id)
);
GO

-- Índices para relacionamentos e consultas frequentes.
CREATE INDEX IX_doacao_usuario_id
    ON dbo.doacao(usuario_id);
GO

CREATE INDEX IX_solicitacao_instituicao_id
    ON dbo.solicitacao(instituicao_id);
GO

CREATE INDEX IX_solicitacao_doacao_id
    ON dbo.solicitacao(doacao_id);
GO

CREATE INDEX IX_notificacao_usuario_data
    ON dbo.notificacao(usuario_id, data_criacao DESC);
GO

-- Conferência da estrutura criada
SELECT
    TABLE_SCHEMA AS esquema,
    TABLE_NAME AS tabela
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO
