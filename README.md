# Documentação da API FloraFolio

## Visão Geral

A API FloraFolio é uma interface de programação de aplicações RESTful para gerenciamento de plantas e usuários. Esta documentação fornece informações detalhadas sobre os endpoints disponíveis, parâmetros necessários, respostas esperadas e exemplos de uso.

## Base URL

```
http://localhost:8080
```

## Autenticação

A maioria dos endpoints requer autenticação usando um token JWT (JSON Web Token). O token deve ser incluído no cabeçalho de autorização de todas as requisições que exigem autenticação.

Formato do cabeçalho:
```
Authorization: Bearer {seu_token_jwt}
```

### Níveis de Acesso

A API possui dois níveis de acesso:

1. **Usuário Regular**: Acesso a operações básicas de leitura e gerenciamento do próprio perfil
2. **Administrador**: Acesso completo, incluindo operações de gerenciamento de plantas

## Estrutura de Dados
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

## Endpoints

### Autenticação e Gerenciamento de Usuários

#### Registrar Usuário

```
POST /register
```

**Descrição:** Cria um novo usuário no sistema.

**Corpo da Requisição:**
```json
{
  "username": "exemplo_usuario",
  "password": "senha123",
  "email": "usuario@exemplo.com"
}
```

**Respostas:**
- `201 Created`: Usuário registrado com sucesso
- `400 Bad Request`: Dados inválidos ou incompletos
- `409 Conflict`: Nome de usuário já existe

#### Login

```
POST /login
```

**Descrição:** Autentica um usuário e retorna um token JWT.

**Corpo da Requisição:**
```json
{
  "username": "exemplo_usuario",
  "password": "senha123"
}
```

**Respostas:**
- `200 OK`: Login bem-sucedido, retorna token JWT
- `401 Unauthorized`: Credenciais inválidas
- `429 Too Many Requests`: Muitas tentativas de login, IP bloqueado temporariamente

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Login bem-sucedido",
  "username": "exemplo_usuario",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Logout

```
POST /users/logout
```

**Descrição:** Invalida o token JWT do usuário, realizando o logout.

**Cabeçalho:** Requer token JWT

**Respostas:**
- `200 OK`: Logout bem-sucedido
- `401 Unauthorized`: Token inválido ou ausente

#### Obter Perfil do Usuário Atual

```
GET /users/profile
```

**Descrição:** Retorna os dados do perfil do usuário autenticado.

**Cabeçalho:** Requer token JWT

**Respostas:**
- `200 OK`: Perfil obtido com sucesso
- `401 Unauthorized`: Token inválido ou ausente
- `404 Not Found`: Usuário não encontrado

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Perfil obtido com sucesso",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "exemplo_usuario",
    "email": "usuario@exemplo.com",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

**Exemplo de Resposta (401 Unauthorized):**
```json
{
  "status": "error",
  "message": "Token inválido ou expirado"
}
```

#### Buscar Usuário por Nome de Usuário

```
GET /users/{username}
```

**Descrição:** Retorna os dados públicos de um usuário pelo nome de usuário.

**Cabeçalho:** Requer token JWT

**Parâmetros de URL:**
- `username`: Nome de usuário a ser buscado

**Respostas:**
- `200 OK`: Usuário encontrado com sucesso
- `401 Unauthorized`: Token inválido ou ausente
- `404 Not Found`: Usuário não encontrado

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Usuário encontrado",
  "user": {
    "username": "exemplo_usuario",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

**Exemplo de Resposta (404 Not Found):**
```json
{
  "status": "error",
  "message": "Usuário não encontrado"
}
```

#### Atualizar Nome de Usuário

```
PUT /users/{id}/username
```

**Descrição:** Atualiza o nome de usuário de uma conta existente.

**Cabeçalho:** Requer token JWT

**Parâmetros de URL:**
- `id`: ID do usuário

**Corpo da Requisição:**
```json
{
  "currentUsername": "nome_atual",
  "newUsername": "novo_nome"
}
```

**Respostas:**
- `200 OK`: Nome de usuário atualizado com sucesso
- `400 Bad Request`: Dados inválidos ou incompletos
- `401 Unauthorized`: Token inválido ou ausente
- `404 Not Found`: Usuário não encontrado

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Nome de usuário atualizado com sucesso",
  "user": {
    "username": "novo_nome"
  }
}
```

**Exemplo de Resposta (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Nome de usuário inválido ou já existe",
  "errors": [
    "O nome de usuário deve ter entre 3 e 20 caracteres"
  ]
}
```

#### Atualizar Senha

```
PUT /users/{id}/password
```

**Descrição:** Atualiza a senha de uma conta existente.

**Cabeçalho:** Requer token JWT

**Parâmetros de URL:**
- `id`: ID do usuário

**Corpo da Requisição:**
```json
{
  "currentPassword": "senha_atual",
  "newPassword": "nova_senha"
}
```

**Respostas:**
- `200 OK`: Senha atualizada com sucesso
- `400 Bad Request`: Dados inválidos ou incompletos
- `401 Unauthorized`: Token inválido ou senha atual incorreta
- `404 Not Found`: Usuário não encontrado
- `500 Internal Server Error`: Erro interno ao atualizar senha

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Senha atualizada com sucesso"
}
```

**Exemplo de Resposta (401 Unauthorized):**
```json
{
  "status": "error",
  "message": "Senha atual incorreta"
}
```

#### Excluir Conta de Usuário

```
DELETE /users/delete
```

**Descrição:** Exclui a conta do usuário autenticado.

**Cabeçalho:** Requer token JWT

**Respostas:**
- `200 OK`: Usuário excluído com sucesso
- `401 Unauthorized`: Token inválido ou ausente
- `404 Not Found`: Usuário não encontrado

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Conta excluída com sucesso"
}
```

**Exemplo de Resposta (401 Unauthorized):**
```json
{
  "status": "error",
  "message": "Token inválido ou ausente"
}
```

### Gerenciamento de Plantas

#### Endpoints Públicos

##### Listar Todas as Plantas

```
GET /plants
```

**Descrição:** Retorna uma lista com todas as plantas cadastradas no sistema.

**Respostas:**
- `200 OK`: Plantas encontradas com sucesso

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Plantas encontradas com sucesso",
  "plants": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "popularName": "Samambaia",
      "scientificName": "Nephrolepis exaltata",
      "description": "A samambaia é uma planta ornamental muito popular, conhecida por suas folhas verdes e delicadas.",
      "family": "Nephrolepidaceae",
      "origin": "América Central e do Sul",
      "careInstructions": "Manter em local com luz indireta e solo úmido. Regar regularmente.",
      "imageUrl": "https://example.com/samambaia.jpg"
    }
  ]
}
```

##### Buscar Planta por ID

```
GET /plants/{id}
```

**Descrição:** Retorna uma planta específica com base no ID fornecido.

**Parâmetros de URL:**
- `id`: ID da planta a ser buscada

**Respostas:**
- `200 OK`: Planta encontrada com sucesso
- `404 Not Found`: Planta não encontrada

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Planta encontrada com sucesso",
  "plant": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "popularName": "Samambaia",
    "scientificName": "Nephrolepis exaltata",
    "description": "A samambaia é uma planta ornamental muito popular, conhecida por suas folhas verdes e delicadas.",
    "family": "Nephrolepidaceae",
    "origin": "América Central e do Sul",
    "careInstructions": "Manter em local com luz indireta e solo úmido. Regar regularmente.",
    "imageUrl": "https://example.com/samambaia.jpg"
  }
}
```

**Exemplo de Resposta (404 Not Found):**
```json
{
  "status": "error",
  "message": "Planta não encontrada"
}
```

##### Buscar Plantas por Nome Popular

```
GET /plants/search/popular?name={termo}
```

**Descrição:** Retorna uma lista de plantas que contêm o termo de busca no nome popular.

**Parâmetros de Query:**
- `name`: Nome popular ou parte do nome para busca

**Respostas:**
- `200 OK`: Busca realizada com sucesso

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Plantas encontradas com sucesso",
  "plants": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "popularName": "Samambaia",
      "scientificName": "Nephrolepis exaltata"
    },
    {
      "id": "987fcdeb-a654-3210-9876-543210987654",
      "popularName": "Samambaiaçu",
      "scientificName": "Dicksonia sellowiana"
    }
  ]
}
```

##### Buscar Plantas por Nome Científico

```
GET /plants/search/scientific?name={termo}
```

**Descrição:** Retorna uma lista de plantas que contêm o termo de busca no nome científico.

**Parâmetros de Query:**
- `name`: Nome científico ou parte do nome para busca

**Respostas:**
- `200 OK`: Busca realizada com sucesso

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Plantas encontradas com sucesso",
  "plants": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "popularName": "Samambaia",
      "scientificName": "Nephrolepis exaltata"
    },
    {
      "id": "987fcdeb-a654-3210-9876-543210987654",
      "popularName": "Samambaiaçu",
      "scientificName": "Dicksonia sellowiana"
    }
  ]
}
```

##### Buscar Plantas por Termo (Nome Popular ou Científico)

```
GET /plants/search?term={termo}
```

**Descrição:** Retorna uma lista de plantas que contêm o termo de busca no nome popular ou científico.

**Parâmetros de Query:**
- `term`: Termo para busca em nome popular ou científico

**Respostas:**
- `200 OK`: Busca realizada com sucesso

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Plantas encontradas com sucesso",
  "plants": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "popularName": "Samambaia",
      "scientificName": "Nephrolepis exaltata"
    },
    {
      "id": "987fcdeb-a654-3210-9876-543210987654",
      "popularName": "Samambaiaçu",
      "scientificName": "Dicksonia sellowiana"
    }
  ]
}
```

#### Endpoints Administrativos

##### Criar Nova Planta

```
POST /admin/plants
```

**Descrição:** Cria uma nova planta no sistema (requer permissão de administrador).

**Cabeçalho:** Requer token JWT com permissão de administrador

**Corpo da Requisição:**
```json
{
  "popularName": "Lírio da Paz",
  "scientificName": "Spathiphyllum wallisii",
  "description": "Planta com flores brancas elegantes, conhecida por purificar o ar.",
  "family": "Araceae",
  "origin": "América Central e Colômbia",
  "careInstructions": "Luz indireta, solo úmido mas não encharcado, ambiente com umidade.",
  "imageUrl": "https://example.com/lirio-paz.jpg"
}
```

**Respostas:**
- `201 Created`: Planta criada com sucesso
- `400 Bad Request`: Dados inválidos
- `401 Unauthorized`: Token inválido ou ausente
- `403 Forbidden`: Usuário não tem permissão de administrador

**Exemplo de Resposta (201 Created):**
```json
{
  "status": "success",
  "message": "Planta criada com sucesso",
  "plant": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "popularName": "Lírio da Paz",
    "scientificName": "Spathiphyllum wallisii",
    "description": "Planta com flores brancas elegantes, conhecida por purificar o ar.",
    "family": "Araceae",
    "origin": "América Central e Colômbia",
    "careInstructions": "Luz indireta, solo úmido mas não encharcado, ambiente com umidade.",
    "imageUrl": "https://example.com/lirio-paz.jpg"
  }
}
```

**Exemplo de Resposta (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Dados inválidos",
  "errors": [
    "O nome popular é obrigatório",
    "O nome científico é obrigatório",
    "A descrição deve ter no mínimo 10 caracteres"
  ]
}
```

**Exemplo de Resposta (403 Forbidden):**
```json
{
  "status": "error",
  "message": "Acesso negado. Permissão de administrador necessária"
}
```

##### Atualizar Planta Existente

```
PUT /admin/plants/{id}
```

**Descrição:** Atualiza os dados de uma planta existente (requer permissão de administrador).

**Cabeçalho:** Requer token JWT com permissão de administrador

**Parâmetros de URL:**
- `id`: ID da planta a ser atualizada

**Corpo da Requisição:**
```json
{
  "popularName": "Lírio da Paz Atualizado",
  "scientificName": "Spathiphyllum wallisii",
  "description": "Descrição atualizada da planta.",
  "family": "Araceae",
  "origin": "América Central e Colômbia",
  "careInstructions": "Instruções de cuidado atualizadas.",
  "imageUrl": "https://example.com/lirio-paz-novo.jpg"
}
```

**Respostas:**
- `200 OK`: Planta atualizada com sucesso
- `400 Bad Request`: Dados inválidos
- `401 Unauthorized`: Token inválido ou ausente
- `403 Forbidden`: Usuário não tem permissão de administrador
- `404 Not Found`: Planta não encontrada

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Planta atualizada com sucesso",
  "plant": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "popularName": "Lírio da Paz Atualizado",
    "scientificName": "Spathiphyllum wallisii",
    "description": "Descrição atualizada da planta.",
    "family": "Araceae",
    "origin": "América Central e Colômbia",
    "careInstructions": "Instruções de cuidado atualizadas.",
    "imageUrl": "https://example.com/lirio-paz-novo.jpg"
  }
}
```

**Exemplo de Resposta (404 Not Found):**
```json
{
  "status": "error",
  "message": "Planta não encontrada"
}
```

##### Excluir Planta

```
DELETE /admin/plants/{id}
```

**Descrição:** Remove uma planta do sistema (requer permissão de administrador).

**Cabeçalho:** Requer token JWT com permissão de administrador

**Parâmetros de URL:**
- `id`: ID da planta a ser excluída

**Respostas:**
- `200 OK`: Planta excluída com sucesso
- `401 Unauthorized`: Token inválido ou ausente
- `403 Forbidden`: Usuário não tem permissão de administrador
- `404 Not Found`: Planta não encontrada

**Exemplo de Resposta (200 OK):**
```json
{
  "status": "success",
  "message": "Planta excluída com sucesso"
}
```

**Exemplo de Resposta (403 Forbidden):**
```json
{
  "status": "error",
  "message": "Acesso negado. Permissão de administrador necessária"
}
```

## Documentação Interativa

Uma documentação interativa da API está disponível através do Swagger UI. Para acessá-la, inicie a aplicação e navegue para:

```
http://localhost:8080/swagger-ui/index.html
```

Esta interface permite explorar todos os endpoints, ver os modelos de dados e testar as requisições diretamente no navegador.

## Códigos de Status

- `200 OK`: Requisição bem-sucedida
- `201 Created`: Recurso criado com sucesso
- `400 Bad Request`: Requisição inválida ou dados incorretos
- `401 Unauthorized`: Autenticação necessária ou falha na autenticação
- `403 Forbidden`: Usuário autenticado, mas sem permissão para o recurso
- `404 Not Found`: Recurso não encontrado
- `429 Too Many Requests`: Limite de requisições excedido
- `500 Internal Server Error`: Erro interno do servidor