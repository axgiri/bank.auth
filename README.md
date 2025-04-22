# Bank Authentication Microservice

A Spring Boot microservice that provides user authentication and management via phone number and password, using JWT for API protection. Supports both synchronous and asynchronous registration and updates.

---

## Features

- **User Registration** (synchronous & asynchronous)  
- **User Login** with JWT issuance  
- **Token Validation** endpoint  
- **CRUD operations** on `Person` entity (by ID and by phone number)  
- **Asynchronous JWT filter** using a thread pool  
- **Role-based access control** via `RoleEnum` and Spring Security  

---

## Tech Stack

- **Java 17+**  
- **docker**
- **kind**
- **minikube**

---

## Prerequisites

- **JDK 17+**  
- **Maven 3.6+**  
- **Database** (H2 for development, PostgreSQL/MySQL for production)  
- **Docker & Docker Compose** (optional)  
- ***K8S engine** (optional)

---

## Installation & Run

1. **Clone repository**  
   ```bash
   git clone https://github.com/axgiri/bank.authentication.git
   cd bank.authentication
   ```

2. **Build**  
   ```bash
   ./mvnw clean package
   ```

3. **Run locally**  
   ```bash
   java -jar target/bank.authentication-0.0.1-SNAPSHOT.jar
   ```

4. **Run with Docker Compose**  
   ```bash
   docker-compose up --build
   ```

---

## Configuration

Configure Spring Boot via `src/main/resources/application.yml` or `application.properties`.  

Key properties:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:authdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

security:
  jwt:
    secret: mySuperSecretKey123
    expiration-ms: 86400000
```

- `spring.datasource.url` – JDBC URL  
- `spring.datasource.username` / `.password` – credentials  
- `security.jwt.secret` – signing secret  
- `security.jwt.expiration-ms` – token TTL (milliseconds)  

---

## API Endpoints

| Method | Path                                      | Description                            | Request DTO       | Response DTO                                |
| ------ | ----------------------------------------- | -------------------------------------- | ----------------- | ------------------------------------------- |
| POST   | `/api/v1/persons/signup`                  | Register user (synchronous)            | `PersonRequest`   | `PersonResponse`                            |
| POST   | `/api/v1/persons/async/signup`            | Register user (asynchronous, no body)  | `PersonRequest`   | —                                           |
| POST   | `/api/v1/persons/login`                   | User login, receive JWT                | `LoginRequest`    | `AuthResponse`                              |
| GET    | `/api/v1/persons/validate`                | Validate JWT (Authorization header)    | —                 | `String` (“validation successful”)          |
| POST   | `/api/v1/persons/findById/{id}`           | Get user by ID                         | —                 | `PersonResponse`                            |
| POST   | `/api/v1/persons/findByPhoneNumber/{phone}`| Get user by phone number             | —                 | `PersonResponse`                            |
| POST   | `/api/v1/persons/async/update/{id}`       | Update user asynchronously             | `PersonRequest`   | `CompletableFuture<PersonResponse>`         |
| POST   | `/api/v1/persons/delete/{id}`             | Delete user                            | —                 | —                                           |

---

## Data Transfer Objects (DTOs)

- **PersonRequest** – for create/update operations  
- **PersonResponse** – user data in responses  
- **LoginRequest** – `{ phoneNumber: String, password: String }`  
- **AuthResponse** – `{ token: String, person: PersonResponse }`  

---

## Security

- **JWT** creation & validation in `TokenService`  
- **AsyncJwtFilter** for async token checks  
- **Stateless** Spring Security configuration  
- Public endpoints: `/signup`, `/async/signup`, `/login`  

---

## Development & Testing

- Unit and integration tests in `src/test/java/...`  
- H2 in-memory database for quick start  
- Use IDE with `dev` profile for local development  

---

## Contributing

1. Fork the repository  
2. Create a feature branch:  
   ```bash
   git checkout -b feature/your-feature
   ```  
3. Commit your changes  
4. Push and open a Pull Request  

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.


---

## Deployment

### Kubernetes

Below is an example of deploying the microservice to a Kubernetes cluster.

**1. ConfigMap** (for JWT secret and database settings)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: auth-config
data:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/authdb
  SPRING_DATASOURCE_USERNAME: auth_user
  SPRING_DATASOURCE_PASSWORD: secret
  SECURITY_JWT_SECRET: mySuperSecretKey123
  SECURITY_JWT_EXPIRATION_MS: "86400000"
```

**2. Deployment**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bank-auth-deployment
spec:
  replicas: 2
  selector:
    matchLabels:
      app: bank-auth
  template:
    metadata:
      labels:
        app: bank-auth
    spec:
      containers:
        - name: bank-auth
          image: axgiri/bank-authentication:latest
          imagePullPolicy: IfNotPresent
          envFrom:
            - configMapRef:
                name: auth-config
          ports:
            - containerPort: 8080
          resources:
            limits:
              cpu: "500m"
              memory: "512Mi"
            requests:
              cpu: "250m"
              memory: "256Mi"
```

**3. Service**
```yaml
apiVersion: v1
kind: Service
metadata:
  name: bank-auth-service
spec:
  selector:
    app: bank-auth
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: ClusterIP
```

---
*Author: axgiri*  
*Date: April 21, 2025*  
