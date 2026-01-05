# NexTask - Project Management System

NexTask is a robust, professional project management application built with a modern tech stack. It follows **Hexagonal Architecture** principles to ensure maintainability, scalability, and high-quality code standards.

## 🚀 Key Features

- **User Authentication**: Secure login and registration using JWT (JSON Web Tokens).
- **Project Management**: Create, view, and manage projects with status tracking (DRAFT/ACTIVE).
- **Task Management**: Create and track tasks within projects.
- **Soft Delete**: Data integrity is maintained through a soft-delete system for both projects and tasks.
- **Interactive API Documentation**: Fully integrated Swagger UI for easy API exploration.
- **Modern UI**: A sleek, responsive frontend built with Vanilla JavaScript and CSS, featuring glassmorphism aesthetics and Lucide icons.

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.4.0
- **Security**: Spring Security, JWT
- **Database**: PostgreSQL (Production), H2 (Testing/Development)
- **Migration**: Flyway
- **Documentation**: Springdoc OpenAPI (Swagger)
- **Frontend**: HTML5, Vanilla CSS, Vanilla JavaScript, Lucide Icons

## 🏗 Architecture

The project implements **Hexagonal Architecture** (Ports and Adapters), separating the core business logic from external concerns:

- **Domain**: Pure business logic and entities.
- **Application**: Use cases and service orchestration.
- **Infrastructure**: External adapters (REST Controllers, Persistence, Security).

## 🚦 Getting Started

### Prerequisites

- Java 17 or higher
- Maven (included via `./mvnw`)

### Installation & Running

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
   ```
   *Note: The application is configured to run on port 8081 by default to avoid common port conflicts.*

3. **Access the application**:
   - **Frontend**: [http://localhost:8081/index.html](http://localhost:8081/index.html)
   - **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

## 🔐 Security

The API is secured using JWT. To access protected endpoints:
1. Register a new user via `/api/v1/auth/register`.
2. Login via `/api/v1/auth/login` to receive a token.
3. Include the token in the `Authorization` header as `Bearer <token>`.

## 📄 API Documentation

The project uses Springdoc OpenAPI to generate interactive documentation. You can explore all available endpoints, request/response models, and security requirements directly through the Swagger UI.

---
*Developed as a professional assessment project.*