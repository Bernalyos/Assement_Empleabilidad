# Project & Task Management System (Hexagonal Architecture)

Este proyecto es una implementación de un sistema de gestión de proyectos y tareas siguiendo los principios de **Arquitectura Hexagonal** (Puertos y Adaptadores) y **Clean Architecture**.

## Tecnologías Utilizadas
- **Java 17**
- **Spring Boot 3.4.0**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL** (Producción/Docker)
- **H2 Database** (Pruebas/Local)
- **Docker & Docker Compose**
- **Swagger/OpenAPI**

## Estructura del Proyecto
- `domain`: Lógica de negocio pura (Modelos, Excepciones, Puertos).
- `application`: Casos de uso y servicios de aplicación.
- `infrastructure`: Adaptadores de entrada (REST) y salida (Persistencia, Seguridad, Notificaciones).

## Cómo Ejecutar el Proyecto

### Requisitos
- Docker y Docker Compose instalados.

### Pasos
1. Construir el proyecto:
   ```bash
   mvn clean package -DskipTests
   ```
2. Levantar los contenedores:
   ```bash
   docker-compose up --build -d
   ```

La aplicación estará disponible en `http://localhost:8080`.

## Documentación de la API (Swagger)
Puedes acceder a la documentación interactiva en:
`http://localhost:8080/swagger-ui/index.html`

## Autenticación
La API está protegida con JWT.
1. **Registro:** `POST /api/auth/register`
2. **Login:** `POST /api/auth/login` (Devuelve un Token)
3. **Uso:** Incluye el token en el header `Authorization: Bearer <TOKEN>` para las peticiones protegidas.

## Base de Datos
- En Docker, se utiliza **PostgreSQL** mapeado al puerto `5433` del host (para evitar conflictos con instalaciones locales).
- Credenciales: `postgres` / `postgres`.