CREATE DATABASE harvesthope;

USE harvesthope;
GO

-- Ver todas as tabelas
SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO

-- Ver todas as colunas de todas as tabelas
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('doacao', 'instituicao', 'usuario')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO

SELECT * FROM doacao;

SELECT * FROM instituicao;

SELECT * FROM usuario;

SELECT *
FROM doacao
WHERE status = 'DISPONIVEL';

SELECT
    id,
    nome_alimento,
    quantidade,
    unidade,
    status,
    data_validade
FROM doacao;

INSERT INTO doacao
    (data_validade, descricao, foto, nome_alimento, quantidade, status, unidade)
VALUES
    ('2027-01-30', 'Arroz para teste', 'arroz.jpg', 'Arroz', 10, 'DISPONIVEL', 'kg');

SELECT *
FROM doacao;

SELECT *
FROM doacao
WHERE nome_alimento = 'Arroz';

SELECT *
FROM doacao
WHERE quantidade > 5;

SELECT *
FROM doacao
ORDER BY quantidade DESC;

SELECT *
FROM doacao
WHERE status = 'DISPONIVEL'
  AND quantidade > 5;

  UPDATE doacao
SET quantidade = 15
WHERE id = 2;

SELECT *
FROM doacao
WHERE id = 2;

INSERT INTO doacao
    (data_validade, descricao, foto, nome_alimento, quantidade, status, unidade)
VALUES
    ('2027-02-15', 'Feijão para teste', 'feijao.jpg', 'Feijão', 20, 'DISPONIVEL', 'kg');

SELECT *
FROM doacao;

DELETE FROM doacao
WHERE nome_alimento = 'Feijão';

SELECT
    id,
    nome,
    cnpj,
    email,
    telefone,
    localizacao
FROM instituicao;

-- Cadastrar instituição teste

INSERT INTO instituicao
    (cnpj, descricao, email, localizacao, nome, site, telefone)
VALUES
    (
        '12.345.678/0001-90',
        'Instituição que atende famílias em situação de vulnerabilidade.',
        'contato@esperanca.com',
        'Barueri - SP',
        'Instituição Esperança',
        'https://www.esperanca.com',
        '(11) 99999-9999'
    );

SELECT *
FROM instituicao
WHERE localizacao = 'Barueri - SP';

SELECT
    nome,
    email,
    telefone
FROM instituicao;

INSERT INTO usuario
    (email, nome, senha, tipo_usuario)
VALUES
    (
        'teste@harvesthope.com',
        'Usuário Teste',
        '123456',
        'DOADOR'
    );

SELECT *
FROM usuario;

SELECT *
FROM usuario
WHERE tipo_usuario = 'DOADOR';

SELECT *
FROM usuario
WHERE nome LIKE '%Teste%';

SELECT *
FROM doacao
WHERE nome_alimento LIKE '%Arroz%';

SELECT *
FROM doacao
WHERE quantidade > 5;

SELECT *
FROM doacao
WHERE status = 'DISPONIVEL'
  AND quantidade > 5;

  --conferindo chaves estrangeiras do banco
SELECT
    fk.name AS chave_estrangeira,
    OBJECT_NAME(fk.parent_object_id) AS tabela_origem,
    COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
    ) AS coluna_origem,
    OBJECT_NAME(fk.referenced_object_id) AS tabela_destino,
    COL_NAME(
        fkc.referenced_object_id,
        fkc.referenced_column_id
    ) AS coluna_destino
FROM sys.foreign_keys AS fk
INNER JOIN sys.foreign_key_columns AS fkc
    ON fk.object_id = fkc.constraint_object_id
ORDER BY tabela_origem, chave_estrangeira;

--conferir as chaves primárias 
USE harvesthope;
GO

SELECT
    t.name AS tabela,
    c.name AS coluna,
    ty.name AS tipo_dado,
    c.is_identity AS auto_incremento,
    CASE
        WHEN pk.column_id IS NOT NULL THEN 'SIM'
        ELSE 'NAO'
    END AS chave_primaria
FROM sys.tables t
INNER JOIN sys.columns c
    ON t.object_id = c.object_id
INNER JOIN sys.types ty
    ON c.user_type_id = ty.user_type_id
LEFT JOIN (
    SELECT
        ic.object_id,
        ic.column_id
    FROM sys.indexes i
    INNER JOIN sys.index_columns ic
        ON i.object_id = ic.object_id
       AND i.index_id = ic.index_id
    WHERE i.is_primary_key = 1
) pk
    ON pk.object_id = c.object_id
   AND pk.column_id = c.column_id
WHERE t.name IN ('usuario', 'instituicao', 'doacao')
ORDER BY t.name, c.column_id;

USE harvesthope;
GO

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('usuario', 'instituicao', 'doacao')
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- Consultar indices únicos
USE harvesthope;
GO

SELECT
    t.name AS tabela,
    i.name AS indice,
    i.is_unique AS indice_unico,
    c.name AS coluna
FROM sys.indexes i
INNER JOIN sys.tables t
    ON i.object_id = t.object_id
INNER JOIN sys.index_columns ic
    ON i.object_id = ic.object_id
   AND i.index_id = ic.index_id
INNER JOIN sys.columns c
    ON ic.object_id = c.object_id
   AND ic.column_id = c.column_id
WHERE t.name IN ('usuario', 'instituicao', 'doacao')
  AND i.is_unique = 1
  AND i.is_primary_key = 0
ORDER BY t.name, i.name;

--Verificar se existem emails duplicados
USE harvesthope;
GO

SELECT
    email,
    COUNT(*) AS quantidade
FROM usuario
WHERE email IS NOT NULL
GROUP BY email
HAVING COUNT(*) > 1;s

USE harvesthope;

SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'doacao';


USE harvesthope;

SELECT
    fk.name AS nome_chave_estrangeira,
    OBJECT_NAME(fk.parent_object_id) AS tabela_origem,
    COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS coluna_origem,
    OBJECT_NAME(fk.referenced_object_id) AS tabela_referenciada,
    COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) AS coluna_referenciada
FROM sys.foreign_keys fk
INNER JOIN sys.foreign_key_columns fkc
    ON fk.object_id = fkc.constraint_object_id
WHERE OBJECT_NAME(fk.parent_object_id) = 'doacao';

USE harvesthope;

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'doacao'
ORDER BY ORDINAL_POSITION;


USE harvesthope;

ALTER TABLE doacao
ADD local_retirada VARCHAR(255) NULL;


USE harvesthope;

SELECT COLUMN_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'doacao'
  AND COLUMN_NAME = 'local_retirada';

SELECT id, nome_alimento, local_retirada
FROM doacao
WHERE id = 13;

SELECT id, nome_alimento, local_retirada
FROM doacao
ORDER BY id DESC;

SELECT TOP 5 id, nome_alimento, local_retirada
FROM doacao
ORDER BY id DESC;

USE harvesthope;
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'solicitacao'
ORDER BY ORDINAL_POSITION;

SELECT DB_NAME() AS banco_atual;

SELECT TABLE_SCHEMA, TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME LIKE '%solicit%';

SELECT name
FROM sys.databases
WHERE name = 'harvesthope';

USE harvesthope;
GO

SELECT DB_NAME() AS banco_atual;

SELECT
    TABLE_SCHEMA,
    TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME LIKE '%solicit%';

USE harvesthope;
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'solicitacao'
ORDER BY ORDINAL_POSITION;

USE harvesthope;
GO

SELECT
    fk.name AS chave_estrangeira,
    OBJECT_NAME(fk.parent_object_id) AS tabela,
    COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
    ) AS coluna,
    OBJECT_NAME(fk.referenced_object_id) AS tabela_referenciada,
    COL_NAME(
        fkc.referenced_object_id,
        fkc.referenced_column_id
    ) AS coluna_referenciada
FROM sys.foreign_keys fk
INNER JOIN sys.foreign_key_columns fkc
    ON fk.object_id = fkc.constraint_object_id
WHERE OBJECT_NAME(fk.parent_object_id) = 'solicitacao';

USE harvesthope;
GO

SELECT
    id,
    data_solicitacao,
    status,
    doacao_id,
    usuario_id,
    instituicao_id
FROM dbo.solicitacao;

USE harvesthope;
GO

-- Remover a chave estrangeira de usuario_id
ALTER TABLE dbo.solicitacao
DROP CONSTRAINT [FKfu0flughprsk76ojkdh1rfdb];
GO

-- Remover a coluna antiga de usuário
ALTER TABLE dbo.solicitacao
DROP COLUMN usuario_id;
GO

USE harvesthope;
GO

SELECT
    fk.name AS nome_constraint,
    COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
    ) AS coluna
FROM sys.foreign_keys fk
INNER JOIN sys.foreign_key_columns fkc
    ON fk.object_id = fkc.constraint_object_id
WHERE fk.parent_object_id = OBJECT_ID('dbo.solicitacao')
  AND COL_NAME(
        fkc.parent_object_id,
        fkc.parent_column_id
      ) = 'usuario_id';

USE harvesthope;
GO

ALTER TABLE dbo.solicitacao
DROP CONSTRAINT [FKfu0flughpsk76ojkdh1rfdb];
GO

ALTER TABLE dbo.solicitacao
DROP COLUMN usuario_id;
GO

USE harvesthope;
GO

ALTER TABLE dbo.solicitacao
DROP CONSTRAINT [FKffu0flughrpsk76ojkdh1rfdb];
GO

ALTER TABLE dbo.solicitacao
DROP COLUMN usuario_id;
GO

SELECT
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'solicitacao'
ORDER BY ORDINAL_POSITION;

SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_TYPE = 'BASE TABLE'
  AND TABLE_NAME LIKE '%instituicao%';

USE harvesthope;
EXEC sp_help 'dbo.doacao';

SELECT
    fk.name AS nome_fk,
    OBJECT_NAME(fk.parent_object_id) AS tabela_origem,
    COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS coluna_origem,
    OBJECT_NAME(fk.referenced_object_id) AS tabela_referenciada,
    COL_NAME(fkc.referenced_object_id, fkc.referenced_column_id) AS coluna_referenciada,
    fk.delete_referential_action_desc AS acao_ao_excluir
FROM sys.foreign_keys fk
INNER JOIN sys.foreign_key_columns fkc
    ON fk.object_id = fkc.constraint_object_id
WHERE fk.name = 'FK_instituicao_usuario';

SELECT
    i.id AS instituicao_id,
    i.nome AS instituicao,
    u.id AS usuario_id,
    u.nome AS responsavel,
    u.email
FROM instituicao i
INNER JOIN usuario u
    ON i.usuario_id = u.id
ORDER BY i.id;

USE harvesthope;
SELECT
    d.id AS doacao_id,
    d.nomeAlimento,
    d.usuario_id AS doador_id,
    u.nome AS nome_doador,
    u.tipoUsuario
FROM Doacao d
LEFT JOIN Usuario u ON u.id = d.usuario_id
ORDER BY d.id;

USE harvesthope;
GO

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('Doacao', 'Usuario')
ORDER BY TABLE_NAME, ORDINAL_POSITION;

USE harvesthope;
GO

SELECT
    d.id AS doacao_id,
    d.nome_alimento,
    d.usuario_id AS doador_id,
    u.nome AS nome_doador,
    u.tipo_usuario
FROM dbo.doacao d
LEFT JOIN dbo.usuario u
    ON u.id = d.usuario_id
ORDER BY d.id;

USE harvesthope;
GO

SELECT
    id,
    nome,
    tipo_usuario
FROM dbo.usuario
WHERE tipo_usuario = 'DOADOR'
ORDER BY id;

USE harvesthope;
GO

SELECT
    d.id AS doacao_id,
    d.nome_alimento,
    d.usuario_id AS doador_id,
    u.nome AS nome_doador
FROM dbo.doacao d
INNER JOIN dbo.usuario u
    ON u.id = d.usuario_id
WHERE d.usuario_id <> 3
ORDER BY d.id;

USE harvesthope;
GO

SELECT
    id,
    nome,
    tipo_usuario
FROM dbo.usuario
WHERE id IN (3, 5);