# User Management API

Esta API REST fornece funcionalidades de gerenciamento de usuários, incluindo registro, autenticação, busca, atualização e exclusão de usuários, com foco em segurança.

## 📋 Requisitos

- Java 17+
- Maven
- PostgreSQL

## 🛠 Configuração

1. Renomeie `application-example.properties` para `application.properties`
2. Configure as variáveis de ambiente:

```java
server.port=8080
spring.application.name=login-api

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=3600
```

## 🔄 Endpoints da API

### Autenticação

#### POST /register
Registra um novo usuário no sistema.

**Request:**
```json
{
  "username": "usuario",
  "password": "senha123",
  "email": "usuario@exemplo.com"
}
```

**Response (201 Created):**
```json
{
  "status": "success",
  "message": "Usuário registrado com sucesso"
}
```

**Response (409 Conflict):**
```json
{
  "status": "error",
  "message": "Nome de usuário já existe"
}
```

**Validações:**
- Username: Não pode ser nulo ou vazio
- Password: Não pode ser nulo ou vazio
- Email: Deve ser um endereço de email válido

#### POST /login
Autentica um usuário e retorna um token JWT.

**Request:**
```json
{
  "username": "usuario",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Login realizado com sucesso",
  "username": "usuario",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (401 Unauthorized):**
```json
{
  "status": "error",
  "message": "Credenciais inválidas"
}
```

**Segurança:**
- Implementa proteção contra ataques de força bruta (máximo 5 tentativas em 15 minutos)
- O ID do usuário não é retornado na resposta, apenas armazenado no token JWT
- A senha é validada usando BCrypt

#### POST /users/logout
Revoga o token JWT atual, invalidando a sessão do usuário.

**Request:**
- Não requer corpo da requisição
- Requer cabeçalho `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Logout realizado com sucesso"
}
```

**Response (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Token inválido"
}
```

**Segurança:**
- O token revogado é armazenado em uma lista de tokens inválidos
- Tentativas de usar um token revogado resultarão em erro 401 Unauthorized

### Gerenciamento de Usuários

#### GET /users/profile
Obtém o perfil do usuário autenticado, incluindo todas as informações pessoais.

**Request:**
- Não requer corpo da requisição
- Requer cabeçalho `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "usuario",
  "email": "usuario@exemplo.com",
  "isOwnProfile": true
}
```

**Response (401 Unauthorized):**
```json
{
  "status": "error",
  "message": "Não autorizado"
}
```

**Segurança:**
- O campo `isOwnProfile` indica que o usuário está visualizando seu próprio perfil
- Todas as informações sensíveis são incluídas quando o usuário visualiza seu próprio perfil

#### GET /users/{username}
Obtém informações de um usuário pelo nome de usuário.

**Request:**
- Parâmetro de caminho: `username`
- Cabeçalho opcional: `Authorization: Bearer {token}`

**Response (200 OK) - Visualizando outro usuário:**
```json
{
  "username": "outro_usuario",
  "isOwnProfile": false
}
```

**Response (200 OK) - Visualizando próprio perfil:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "usuario",
  "email": "usuario@exemplo.com",
  "isOwnProfile": true
}
```

**Response (404 Not Found):**
```json
{
  "status": "error",
  "message": "Usuário não encontrado"
}
```

**Segurança:**
- O email só é visível quando o usuário autenticado está visualizando seu próprio perfil
- O campo `isOwnProfile` indica se o perfil pertence ao usuário autenticado



#### PUT /users/{id}/username
Atualiza o nome de usuário.

**Request:**
- Parâmetro de caminho: `id` (UUID)
- Requer cabeçalho `Authorization: Bearer {token}`
```json
{
  "currentUsername": "usuario_atual",
  "newUsername": "novo_usuario"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Nome de usuário atualizado com sucesso",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Nome de usuário atual incorreto"
}
```

**Response (409 Conflict):**
```json
{
  "status": "error",
  "message": "Nome de usuário já existe"
}
```

**Segurança:**
- Requer autenticação e autorização (apenas o próprio usuário pode alterar seu nome)
- Revoga todos os tokens JWT anteriores
- Retorna um novo token JWT com o nome de usuário atualizado

#### PUT /users/{id}/password
Atualiza a senha do usuário.

**Request:**
- Parâmetro de caminho: `id` (UUID)
- Requer cabeçalho `Authorization: Bearer {token}`
```json
{
  "currentPassword": "senha_atual",
  "newPassword": "nova_senha"
}
```

**Response (200 OK):**
```json
{
  "status": "success",
  "message": "Senha atualizada com sucesso",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Senha atual incorreta"
}
```

**Segurança:**
- Requer autenticação e autorização (apenas o próprio usuário pode alterar sua senha)
- Valida a senha atual antes de permitir a alteração
- Revoga todos os tokens JWT anteriores
- Retorna um novo token JWT após a alteração da senha
- A nova senha é armazenada com hash usando BCrypt

## 🚀 Executando a Aplicação

1. Configure o banco de dados PostgreSQL
2. Configure as variáveis de ambiente
3. Execute a aplicação:
   ```
   mvn spring-boot:run
   ```
4. A API estará disponível em `http://localhost:8080`

## 🔐 Boas Práticas de Segurança

- Utilize HTTPS em produção
- Mantenha o segredo JWT (`jwt.secret`) seguro e complexo
- Considere implementar autenticação de dois fatores para maior segurança
- Em ambientes de produção, substitua o armazenamento em memória de tokens revogados por Redis ou banco de dados
- Implemente logging de segurança para monitorar tentativas de acesso não autorizado
