# Urbana — Sistema de Gestão de Cartões de Transporte

Aplicação full stack desenvolvida como solução para um desafio técnico de Analista de Desenvolvimento de Sistemas.

O projeto permite gerenciar usuários e cartões de transporte coletivo, além de oferecer autenticação, autorização por perfis e um fluxo de solicitação e aprovação de cartões.

## Tecnologias utilizadas

### Back-end
- Java 17
- Spring Boot 4
- Spring Security
- Spring Data JPA / Hibernate
- MySQL 8.4
- Maven
- JWT e BCrypt
- Docker Compose

### Front-end
- Angular 21
- TypeScript
- HTML e CSS
- Angular HttpClient
- RxJS

## Funcionalidades

### Usuários
- Cadastrar usuários
- Listar e consultar usuários
- Atualizar dados cadastrais
- Remover usuários
- Ativar e inativar usuários
- Consultar a própria conta e alterar a própria senha

### Cartões
- Adicionar cartões aos usuários
- Listar e consultar cartões
- Ativar e inativar cartões
- Remover cartões
- Trabalhar com os tipos comum, estudante e trabalhador

### Solicitações de cartões
- Usuários comuns podem solicitar cartões
- As solicitações são registradas inicialmente como `PENDING`
- Administradores podem aprovar ou rejeitar solicitações
- A aprovação gera um cartão ativo
- A rejeição não cria um cartão

### Segurança
- Autenticação com JWT
- Armazenamento das senhas com hash BCrypt
- Controle de acesso por perfis `ADMIN` e `USER`
- Validação de permissões no back-end

## Arquitetura

A aplicação utiliza uma arquitetura monolítica organizada em camadas, com o front-end e o back-end separados.

```text
urbana-desafio/
├── README.md
├── .gitignore
├── urbana-back/
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── docker-compose.yml
│   ├── .env.example
│   └── src/
│       ├── main/
│       │   ├── java/br/com/urbana/
│       │   │   ├── config/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── enums/
│       │   │   ├── exception/
│       │   │   ├── repository/
│       │   │   ├── security/
│       │   │   └── service/
│       │   └── resources/
│       └── test/
└── urbana-front/
    ├── angular.json
    ├── package.json
    ├── package-lock.json
    ├── public/
    └── src/
        └── app/
            ├── pages/
            └── services/
```

### Responsabilidades das camadas

| Camada | Responsabilidade |
|---|---|
| Angular Components | Interface, eventos e estado visual |
| Angular Services | Comunicação HTTP com a API |
| Controllers | Receber requisições e devolver respostas HTTP |
| DTOs | Definir os contratos de entrada e saída |
| Services Java | Aplicar as regras de negócio |
| Repositories | Acessar os dados utilizando JPA |
| Entities | Representar os dados persistidos |
| MySQL | Armazenar as informações |

Fluxo principal:

```text
Interface Angular
       ↓
Service Angular
       ↓ HTTP
Controller Spring Boot
       ↓
Service Java
       ↓
Repository JPA
       ↓
MySQL
```

## Pré-requisitos

Para executar a aplicação, são necessários:

- Git
- JDK 17 ou superior compatível com o projeto
- Node.js compatível com Angular 21
- npm
- Docker com Docker Compose
- Conexão com a internet na primeira instalação das dependências

Certifique-se de que as portas `3306`, `8080` e `4200` estejam disponíveis.

## Instalação e execução

### 1. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
cd urbana-desafio
```

### 2. Configurar o banco de dados

Abra um terminal na pasta do back-end:

```bash
cd urbana-back
```

O projeto utiliza um arquivo `.env` para fornecer as credenciais necessárias ao Docker Compose.

Copie o modelo `.env.example` para `.env`.

No Windows, usando PowerShell:

```powershell
Copy-Item .env.example .env
notepad .env
```

No Linux/macOS:

```bash
cp .env.example .env
```

Preencha as variáveis:

```dotenv
MYSQL_ROOT_PASSWORD=SENHA_ROOT_DO_MYSQL
MYSQL_PASSWORD=SENHA_DO_USUARIO_URBANA
```

**Atenção:** o valor de `MYSQL_PASSWORD` precisa ser igual ao valor de `spring.datasource.password` no arquivo:

`urbana-back/src/main/resources/application.properties`

O `application.properties` também possui a configuração do administrador inicial da aplicação.

As variáveis `ADMIN_NAME`, `ADMIN_EMAIL` e `ADMIN_PASSWORD` eventualmente presentes no `.env` não controlam o login enquanto o `application.properties` utilizar valores definidos diretamente para `app.admin.*`.

### 3. Iniciar o MySQL

Dentro da pasta `urbana-back`, execute:

```bash
docker compose up -d
```

O Docker iniciará um container MySQL 8.4 com:

- Banco de dados: `urbana`
- Usuário da aplicação: `urbana`
- Porta: `3306`
- Volume persistente para armazenamento dos dados

Verifique se o banco iniciou:

```bash
docker compose ps
```

Para consultar os logs:

```bash
docker compose logs mysql
```

Espere o MySQL ficar pronto antes de iniciar a API.

### 4. Iniciar o back-end

Ainda em `urbana-back`, execute:

**Windows / PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

A API será disponibilizada em:

`http://localhost:8080`

O Spring Boot utiliza JPA/Hibernate para mapear e persistir as entidades. No ambiente de desenvolvimento, a propriedade `spring.jpa.hibernate.ddl-auto=update` está configurada para atualizar o esquema do banco.

O administrador inicial é configurado pelas propriedades `app.admin.name`, `app.admin.email` e `app.admin.password` do `application.properties`.

### 5. Iniciar o front-end

Abra um segundo terminal e entre na pasta `urbana-front`.

```bash
cd urbana-front
npm ci
npm start
```

A aplicação Angular estará disponível em:

`http://localhost:4200`

Os services Angular utilizam a API em `http://localhost:8080`.

### 6. Fazer login

Acesse:

`http://localhost:4200/login`

Utilize o e-mail e a senha do administrador inicial definidos em `application.properties`.

O primeiro administrador é criado automaticamente pelo inicializador da aplicação quando o banco não possui esse cadastro.

## API REST

As rotas abaixo utilizam autenticação JWT, exceto o login.

Para requisições autenticadas, utilize o cabeçalho:

```http
Authorization: Bearer {seu token}
```

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/login` | Autenticação e emissão de JWT |

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/users` | Listar usuários — ADMIN |
| POST | `/users` | Cadastrar usuário — ADMIN |
| GET | `/users/{id}` | Consultar usuário autorizado |
| PUT | `/users/{id}` | Editar usuário — ADMIN |
| DELETE | `/users/{id}` | Excluir usuário — ADMIN |
| PATCH | `/users/{id}/status` | Alterar status — ADMIN |
| GET | `/users/me` | Consultar a própria conta |
| PUT | `/users/me/password` | Alterar a própria senha |

Exemplo de cadastro:

```json
{
  "name": "Maria Silva",
  "email": "maria@exemplo.com",
  "password": "Senha123"
}
```

Exemplo de edição sem alterar a senha:

```json
{
  "name": "Maria Oliveira",
  "email": "maria@exemplo.com"
}
```

Para a edição, foi criado um DTO específico, `UserUpdateRequestDTO`, permitindo que a senha seja opcional.

### Cartões

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/cards` | Listar cartões acessíveis ao usuário |
| GET | `/cards/{id}` | Consultar cartão |
| POST | `/cards/user/{userId}` | Criar cartão — ADMIN |
| PATCH | `/cards/{id}/status?active=true` | Ativar cartão — ADMIN |
| PATCH | `/cards/{id}/status?active=false` | Inativar cartão — ADMIN |
| DELETE | `/cards/{id}` | Excluir cartão — ADMIN |

Exemplo de criação:

```json
{
  "cardType": "STUDENT"
}
```

Tipos disponíveis:

| Valor da API | Descrição |
|---|---|
| `COMMON` | Comum |
| `STUDENT` | Estudante |
| `WORKER` | Trabalhador |

### Solicitações de cartões

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/card-requests/user/{userId}` | Criar solicitação para a própria conta |
| GET | `/card-requests` | Listar solicitações — ADMIN |
| GET | `/card-requests/mine` | Consultar as próprias solicitações |
| PATCH | `/card-requests/{id}/decision?approved=true` | Aprovar solicitação — ADMIN |
| PATCH | `/card-requests/{id}/decision?approved=false` | Rejeitar solicitação — ADMIN |

## Regras de negócio e decisões técnicas

### Separação de permissões

O administrador pode cadastrar cartões diretamente para usuários.

O usuário comum deve realizar uma solicitação, que poderá ser aprovada ou rejeitada por um administrador.

As verificações de autorização são realizadas no back-end, independentemente das restrições da interface.

### Centralização da emissão

A emissão dos cartões foi centralizada em:

`CardService.issueActiveCard()`

Esse método é reutilizado pelo cadastro administrativo e pelo fluxo de aprovação de solicitações.

Isso reduz a duplicação de código e mantém a lógica de emissão em um único lugar.

### Transações

A aprovação utiliza `@Transactional`, permitindo que a emissão do cartão e a atualização da solicitação participem da mesma transação.

### DTOs

O sistema utiliza DTOs para separar o contrato HTTP do modelo persistido.

Também diferencia o DTO de criação do DTO de atualização de usuários, porque a senha deve ser obrigatória no cadastro, mas opcional durante a edição.

### Persistência

O projeto utiliza entidades JPA, relacionamentos e Spring Data JPA.

Os repositories incluem métodos derivados e consultas SQL nativas utilizando `nativeQuery`.

### Segurança

A API utiliza Spring Security, JWT e BCrypt.

A autorização administrativa ocorre no servidor, não dependendo apenas do front-end.

## Autor

Projeto desenvolvido para demonstração de competências em Java, Spring Boot, Angular, APIs REST, arquitetura em camadas, persistência relacional, autenticação e regras de negócio.