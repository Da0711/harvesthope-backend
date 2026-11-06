USE harvesthope;
GO

IF OBJECT_ID('dbo.chamado_suporte', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.chamado_suporte (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        assunto NVARCHAR(120) NOT NULL,
        mensagem NVARCHAR(3000) NOT NULL,
        status NVARCHAR(30) NOT NULL
            CONSTRAINT DF_chamado_suporte_status DEFAULT 'ABERTO',
        data_criacao DATETIME2 NOT NULL
            CONSTRAINT DF_chamado_suporte_data DEFAULT GETDATE(),
        usuario_id BIGINT NOT NULL,

        CONSTRAINT FK_chamado_suporte_usuario
            FOREIGN KEY (usuario_id)
            REFERENCES dbo.usuario(id)
    );
END;
GO