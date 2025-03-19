# Spring Boot JWT Authentication and Authorization

Este proyecto implementa un sistema de autenticación y autorización utilizando Spring Boot, Spring Security y JWT (JSON Web Tokens).

## Características

- Autenticación segura con JWT
- Autorización basada en roles (DOCENTE y RECTOR)
- Protección de rutas según roles
- Encriptación BCrypt para contraseñas
- Base de datos H2 en memoria para facilitar pruebas

## Requisitos

- Java 17+
- Maven 3.6+ o Gradle 7+

## Ejecutar la aplicación

### Con Maven

```bash
mvn spring-boot:run
```

### Con Gradle

```bash
gradle bootRun
```

## Rutas disponibles

- Registro: `POST /api/auth/register`
- Login: `POST /api/auth/login`
- Ruta protegida para DOCENTE y RECTOR: `GET /api/docente`
- Ruta protegida solo para RECTOR: `GET /api/rector`
- Ruta pública: `GET /api/all`

## Pruebas con Postman

### 1. Registrar un usuario con rol DOCENTE

```
POST /api/auth/register
Content-Type: application/json

{
    "username": "docente1",
    "password": "123456",
    "roles": ["docente"]
}
```

### 2. Registrar un usuario con rol RECTOR

```
POST /api/auth/register
Content-Type: application/json

{
    "username": "rector1",
    "password": "123456",
    "roles": ["rector"]
}
```

### 3. Iniciar sesión y obtener token JWT

```
POST /api/auth/login
Content-Type: application/json

{
    "username": "docente1",
    "password": "123456"
}
```

La respuesta incluirá un token JWT que deberás usar para las siguientes peticiones.

### 4. Acceder a ruta protegida para DOCENTE

```
GET /api/docente
Authorization: Bearer <token_JWT>
```

### 5. Acceder a ruta protegida para RECTOR

```
GET /api/rector
Authorization: Bearer <token_JWT>
```

## Estructura del Proyecto

- `models`: Entidades JPA (User, Role)
- `repository`: Interfaces de repositorios para acceso a datos
- `security`: Configuración de Spring Security y manejo de JWT
- `controllers`: Controladores REST para endpoints
- `payload`: Clases para peticiones y respuestas 