# Harvest Hope — Backend

API REST do projeto **Harvest Hope**, desenvolvido como Trabalho de Conclusão de Curso (TCC).

O backend foi desenvolvido com **Java**, **Spring Boot**, **Spring Data JPA** e **SQL Server**. Ele fornece endpoints para autenticação e gerenciamento dos dados utilizados pelo sistema.

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- SQL Server
- Maven (Maven Wrapper)
- Testes de endpoints pelo arquivo `testes.http`

## Estrutura do projeto

- `config/` — configurações da aplicação, CORS e segurança.
- `controller/` — endpoints HTTP da API.
- `exception/` — tratamento global de exceções.
- `model/dto/` — objetos usados para receber e retornar dados da API.
- `model/entity/` — entidades mapeadas para as tabelas do banco.
- `repository/` — acesso aos dados com Spring Data JPA.
- `service/` — regras de negócio.
- `src/main/resources/` — configurações e recursos da aplicação.
- `harvesthope.sql` — script SQL do banco de dados.
- `testes.http` — requisições HTTP para testar os endpoints.

## Pré-requisitos

- JDK compatível com Java 21
- SQL Server
- Visual Studio Code (opcional, para executar `testes.http`)
- Extensão **REST Client** do VS Code, para enviar as requisições do arquivo `testes.http`

## Configuração do banco de dados

1. Abra o SQL Server Management Studio (SSMS).
2. Execute o script `harvesthope.sql`, caso ele contenha a criação/estrutura inicial do banco.
3. Confira o nome do banco e as credenciais da sua instância SQL Server.
4. Configure a conexão em `src/main/resources/application.properties` (ou no arquivo de configuração equivalente utilizado no projeto).

**Importante:** não publique senhas, tokens ou outras credenciais reais no GitHub. Se houver credenciais no arquivo de configuração, remova-as do commit e utilize variáveis de ambiente ou um arquivo local ignorado pelo Git.

## Como executar o backend

No terminal, entre na pasta que contém `pom.xml` e execute:

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

Para compilar sem iniciar a aplicação:

```powershell
.\mvnw.cmd compile
```

Quando a aplicação iniciar corretamente, a API estará disponível localmente em:

`http://localhost:8080`

## Testando a API

O arquivo `testes.http` contém requisições HTTP usadas para validar os endpoints.

1. Inicie o SQL Server e confirme que o banco está configurado.
2. Execute o backend.
3. Abra `testes.http` no VS Code.
4. Se necessário, instale a extensão **REST Client**.
5. Clique em **Send Request** acima de cada requisição e confira o status HTTP e o corpo da resposta.

Execute as requisições na ordem indicada no arquivo quando uma depender de dados criados por outra. Não compartilhe tokens ou dados pessoais presentes nas respostas.

## Endpoints

Os endpoints implementados podem ser consultados nos controllers dentro de `src/main/java`.

### Autenticação

- `POST /api/auth/signin` — autenticação de usuário.

### Usuários

- `POST /api/v1/usuarios` — cadastro de usuário. No fluxo atual, o tipo é definido pelo backend como `DOADOR`.
- `GET /api/v1/usuarios/{id}` — consulta os dados do próprio usuário conforme as regras de segurança.
- `PUT /api/v1/usuarios/{id}` — atualização de dados do usuário conforme as regras de segurança.
- A listagem geral de usuários não fica disponível para acesso público.

Consulte `testes.http` e os controllers para verificar os demais endpoints e os campos exigidos por cada requisição.

## Banco de dados

O banco utilizado pelo projeto chama-se `harvesthope` e inclui tabelas para:

- `usuario`
- `instituicao`
- `doacao`
- `solicitacao`

As relações entre as tabelas estão definidas por chaves estrangeiras no banco. O diagrama entidade-relacionamento (DER) pode ser adicionado à documentação do projeto após sua geração no SQL Server Management Studio.

## Arquivos importantes para versionamento

Mantenha no repositório os arquivos necessários para que o professor consiga analisar e executar o projeto, incluindo:

- `README.md`
- `testes.http`
- `harvesthope.sql` (desde que não contenha senhas ou dados pessoais)
- `pom.xml`
- `mvnw`, `mvnw.cmd` e `.mvn/`

Não versione pastas geradas, como `target/`, nem logs locais. Confira o `.gitignore` antes de enviar.

## Autoria e finalidade

Projeto acadêmico desenvolvido para o Trabalho de Conclusão de Curso (TCC), com finalidade educacional.
