# User Management API

Esta API REST fornece funcionalidades de gerenciamento de usuários, incluindo registro, autenticação, busca, atualização e exclusão de usuários.

## � Requisitos

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
```
