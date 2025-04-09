# Spring Boot JWT Authentication and Authorization

Esta API implementa un sistema de autenticación y autorización utilizando Spring Boot, Spring Security y JWT (JSON Web Tokens).

## Características

- Autenticación segura con JWT
- Autorización basada en roles (DOCENTE y RECTOR)
- Protección de rutas según roles
- Encriptación BCrypt para contraseñas
- Base de datos H2 en memoria para facilitar pruebas
- Base de datos PostgreSQL 
## Requisitos

- Java 17+
- Maven 3.6+

## Ejecutar la aplicación

### Con Maven

```bash
mvn spring-boot:run
```

### Con Gradle

```bash
gradle bootRun
```

## Endpoints API

### 1. Autenticación

#### Registro de Usuario

**Endpoint:** `POST /api/auth/register`

**Descripción:** Crea un nuevo usuario en el sistema.

**Acceso:** Público

**Ejemplo de solicitud:**
```json
{
  "username": "docente1",
  "password": "12345678",       // Texto plano
  "roles": ["docente"]
}
```

o usando Base64 para la contraseña:

```json
{
  "username": "rector1",
  "password": "MTIzNDU2Nzg=",   // "12345678" en Base64
  "roles": ["rector"]
}
```

**Ejemplo de respuesta:**
```json
{
  "message": "Usuario registrado exitosamente!"
}
```

#### Inicio de Sesión

**Endpoint:** `POST /api/auth/login`

**Descripción:** Autentica al usuario y genera un token JWT.

**Acceso:** Público

**Ejemplo de solicitud:**
```json
{
  "username": "docente1",
  "password": "12345678"        // Texto plano
}
```

o usando Base64:

```json
{
  "username": "rector1",
  "password": "MTIzNDU2Nzg="    // "12345678" en Base64
}
```

**Ejemplo de respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "docente1",
  "roles": ["ROLE_DOCENTE"]
}
```

### 2. Endpoints Protegidos por Rol

#### Recursos de Docente

**Endpoint:** `GET /api/docente`

**Descripción:** Acceso a recursos para docentes.

**Acceso:** DOCENTE, RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```
Contenido para DOCENTES y RECTORES.
```

#### Recursos de Rector

**Endpoint:** `GET /api/rector`

**Descripción:** Acceso a recursos exclusivos para rectores.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```
Contenido solo para RECTORES.
```

### 3. Gestión de Usuarios (Solo para Rectores)

#### Listar Todos los Usuarios

**Endpoint:** `GET /api/users/all`

**Descripción:** Obtiene la lista de todos los usuarios.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "username": "docente1",
    "roles": ["ROLE_DOCENTE"]
  },
  {
    "id": 2,
    "username": "rector1",
    "roles": ["ROLE_RECTOR"]
  }
]
```

#### Listar Docentes

**Endpoint:** `GET /api/users/docentes`

**Descripción:** Obtiene la lista de usuarios con rol DOCENTE.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "username": "docente1",
    "roles": ["ROLE_DOCENTE"]
  },
  {
    "id": 3,
    "username": "docente2",
    "roles": ["ROLE_DOCENTE"]
  }
]
```

#### Listar Rectores

**Endpoint:** `GET /api/users/rectores`

**Descripción:** Obtiene la lista de usuarios con rol RECTOR.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```json
[
  {
    "id": 2,
    "username": "rector1",
    "roles": ["ROLE_RECTOR"]
  }
]
```

#### Obtener Usuario por ID

**Endpoint:** `GET /api/users/{id}`

**Descripción:** Obtiene información de un usuario específico.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```json
{
  "id": 1,
  "username": "docente1",
  "roles": ["ROLE_DOCENTE"]
}
```

#### Actualizar Usuario

**Endpoint:** `PUT /api/users/{id}`

**Descripción:** Actualiza información de un usuario existente.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de solicitud:**
```json
{
  "username": "docente_nuevo",
  "password": "87654321",       // Texto plano
  "roles": ["docente"]
}
```

o usando Base64:

```json
{
  "username": "docente_nuevo",
  "password": "ODc2NTQzMjE=",   // "87654321" en Base64
  "roles": ["docente"]
}
```

**Ejemplo de respuesta:**
```json
{
  "id": 1,
  "username": "docente_nuevo",
  "roles": ["ROLE_DOCENTE"]
}
```

#### Eliminar Usuario

**Endpoint:** `DELETE /api/users/{id}`

**Descripción:** Elimina un usuario del sistema.

**Acceso:** Solo RECTOR

**Headers:**
```
Authorization: Bearer {token_jwt}
```

**Ejemplo de respuesta:**
```json
{
  "message": "Usuario eliminado con éxito"
}
```

## Codificación de Contraseñas en Base64

La API acepta contraseñas tanto en texto plano como en formato Base64. Para mayor seguridad, se recomienda enviar las contraseñas en Base64.

### Codificar en JavaScript (navegador)

```javascript
const password = "12345678";
const base64Password = btoa(password); // "MTIzNDU2Nzg="
```

### Codificar en JavaScript (Node.js)

```javascript
const password = "12345678";
const base64Password = Buffer.from(password).toString('base64');
```

### Codificar en Java

```java
String password = "12345678";
String base64Password = Base64.getEncoder().encodeToString(password.getBytes());
```

### Codificar en Python

```python
import base64
password = "12345678"
base64_password = base64.b64encode(password.encode()).decode()
```

## Migración a PostgreSQL

Para usar PostgreSQL en lugar de H2:

1. Añadir dependencia en pom.xml:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Configurar application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jwt_auth_db
spring.datasource.username=postgres
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

3. Crear base de datos en PostgreSQL:
```sql
CREATE DATABASE jwt_auth_db;
``` 