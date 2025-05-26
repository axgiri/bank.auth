# Bank Authentication Service

**Regi Authentication Service** is one of the microservices in the full-fledged supply of services application.

This service provides user registration, login, and JWT-based authentication. It is built with Spring Boot and secured using RS256-signed JSON Web Tokens (JWT). Kubernetes manifests are included for containerized deployment.

## Other Microservices

Below is the list of other microservices in this banking ecosystem (see central GitHub organization for details):

- Regi Gateway: https://github.com/axgiri/regi.gateway (private)
- Regi Company Service: https://github.com/axgiri/regi.company (private)
- Regi Notification Service: https://github.com/axgiri/regi.notification (private)

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Building and Testing](#building-and-testing)
- [Kubernetes Deployment](#kubernetes-deployment)
- [API Endpoints](#api-endpoints)
- [License](#license)

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker (for containerization)
- Kubernetes cluster (for k8s deployment)

## Getting Started

 **Clone the repository**

   ```bash
   git clone https://github.com/axgiri/regi.authentication.git
   cd bank.authentication
   ```


## Configuration

Edit `src/main/resources/application.yml` to configure:

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://<DB_HOST>:5432/authdb
    username: <DB_USER>
    password: <DB_PASS>
```

## Running Locally

Build and start the service with Maven:

```bash
mvn clean package
java -jar target/bank-authentication-0.0.1-SNAPSHOT.jar
```

The service listens on port **8081** by default.

## Building and Testing

Run all tests:

```bash
mvn test
```

Generate the Docker image:

```bash
docker build -t axgiri/bank-authentication:latest .
```

## Kubernetes Deployment

Manifests are available in the `k8s/` directory:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## API Endpoints

| Method | Path                                      | Description                            | Request DTO       | Response DTO                                |
| ------ | ----------------------------------------- | -------------------------------------- | ----------------- | ------------------------------------------- |
| POST   | `/api/v1/persons/signup`                  | Register user (synchronous)            | `PersonRequest`   | `PersonResponse`                            |
| POST   | `/api/v1/persons/async/signup`            | Register user (asynchronous, no body)  | `PersonRequest`   | —                                           |
| POST   | `/api/v1/persons/login`                   | User login, receive JWT                | `LoginRequest`    | `AuthResponse`                              |
| GET    | `/api/v1/persons/validate`                | Validate JWT (Authorization header)    | —                 | `String` (“validation successful”)          |
| POST   | `/api/v1/persons/findById/{id}`           | Get user by ID                         | —                 | `PersonResponse`                            |
| POST   | `/api/v1/persons/findByPhoneNumber/{phone}`| Get user by phone number              | —                 | `PersonResponse`                            |
| POST   | `/api/v1/persons/async/update/{id}`       | Update user asynchronously             | `PersonRequest`   | `CompletableFuture<PersonResponse>`         |
| POST   | `/api/v1/persons/delete/{id}`             | Delete user                            | —                 | —                                           |
| GET    | `/api/v1/jwks/.well-known/jwks.json`      | Delete user                            | —                 | —                                           |

Secured endpoints require `Authorization: Bearer <JWT>` header.

## License

This project is licensed under the MIT License.
