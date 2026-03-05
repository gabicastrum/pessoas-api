# DESAFIO TÉCNICO – API REST com Spring Boot

API REST para gestão de pessoas com um CRUD para a consolidação de conhecimento  boas práticas de arquitetura, organização de código e
princípios REST, garantindo qualidade, clareza e manutenibilidade.

---

# Stack do Projeto

* Java 21
* Spring Boot 3.4.3
* Maven
* Spring Data JPA / Hibernate
* H2 Database (em memória para desenvolvimento e testes)
* Springdoc OpenAPI (Swagger UI)
* Lombok
* MapStruct (1.6.3)
* JUnit 5 (com AssertJ e Mockito)

---

# Requisitos

Antes de rodar o projeto, deve-se ter instalado:

* Java 21
* Maven 3.9

Para verificar:

```bash
java -version
mvn -version
```

---

# Como Rodar o Projeto

## Compilando o projeto

```bash
mvn clean compile
```

---

## Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação será iniciada em:

```
http://localhost:8081
```

---

# Banco de Dados H2

O projeto utiliza H2 em memória.

## Console do H2

Acesse:

```
http://localhost:8081/h2-console
```

Configuração padrão:

* **JDBC URL:** `jdbc:h2:mem:testdb`
* **User:** `gestao`
* **Password:** password

### **Essas credenciais são utilizadas apenas para fins acadêmicos e ambiente local de desenvolvimento.
### Não representam um padrão seguro para ambientes produtivos e não devem ser reutilizadas em aplicações reais.**

---

# Documentação com Swagger

Após subir a aplicação, acesse:

```
http://localhost:8081/swagger-ui/index.html
```

Lá é possível:

* Visualizar todos os endpoints
* Testar requisições
* Ver exemplos de request/response

---

# Executar os Testes

Para rodar os testes unitários e de integração:

```bash
mvn test
```
---

# Gerar e Visualizar JavaDocs

Para gerar a documentação JavaDoc:

```bash
mvn javadoc:javadoc
```

Após a geração, acesse o arquivo:

```
target/reports/apidocs/index.html
```

Abra esse arquivo no browser para visualizar a documentação das classes.

---

# Estrutura do Projeto

* `config` → Configurações gerais da aplicação (ex: Swagger/OpenAPI)
* `controller` → Camada de exposição da API (endpoints REST)
* `domain` → Entidades JPA (modelo de domínio que define a estrutura e relacionamentos dos dados)
* `dto` → Objetos de transferência de dados (request e response)
* `exception` → Exceções customizadas e tratamento global de erros
* `mapper` → Conversão entre entidades e DTOs (MapStruct)
* `repository` → Camada de acesso a dados (Spring Data JPA)
* `service` → Camada de regras de negócio da aplicação