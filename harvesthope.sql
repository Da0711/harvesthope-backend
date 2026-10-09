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