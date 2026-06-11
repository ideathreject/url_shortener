# URL Shortener API

A robust REST API service for shortening URLs, managing user links, and executing secure redirects. Built using Java, Spring Boot, JWT authentication, and a PostgreSQL database.

## 🛠 Tech Stack
- Java 17+
- Spring Boot 3.x (Web, Data JPA, Security)
- PostgreSQL + Flyway (database migrations)
- JWT (JSON Web Tokens)
- JUnit 5 + Mockito + JaCoCo (Code coverage > 80%)
- Docker & Docker Compose
- Swagger / OpenAPI 3.0

---

## ⚙️ Environment Configuration (.env)
To run the project, create a `.env` file in the root directory of the project with the following variables:

```env
# Database Configuration
DB_URL=jdbc:postgresql://url_shortener_db:5432/url_shortener_db
DB_NAME=url_shortener_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_db_password

# Security Configuration (Minimum 256-bit / 32 characters long)
JWT_SECRET=your_password
```
## 🚀 Running with Docker Compose

The application is fully containerized. Ensure you have Docker installed, open a terminal in the project root, and execute:

````
docker-compose up --build -d
````

The application will be accessible at port 8080, and PostgreSQL will run on port 5432.

## 🧪 Running Tests & Coverage Reports

The project is covered by unit tests using Mockito. To execute all tests and generate the JaCoCo coverage report, run:

````
./gradlew clean test
````
Once the build succeeds, you can view the detailed HTML coverage report here:
👉 build/reports/jacoco/test/html/index.html

## 📚 API Documentation (Swagger)

Interactive OpenAPI documentation is available once the application is running. You can test all endpoints directly from your browser:
👉 http://localhost:8080/swagger-ui/index.html

# 🔒 Endpoint Security

## Public Endpoints

| Method | Endpoint | Description |
|----------|----------|----------|
| POST | `/api/v1/auth/register` | Register user |
| POST | `/api/v1/auth/authenticate` | Authenticate user |
| GET | `/{shortCode}` | Redirect to original URL |

---

## Protected Endpoints

Require:

```http
Authorization: Bearer <JWT_TOKEN>
```

| Method | Endpoint | Description |
|----------|----------|----------|
| POST | `/api/v1/urls` | Create URL |
| GET | `/api/v1/urls/my` | Get all user URLs |
| GET | `/api/v1/urls/my/active` | Get active URLs |
| PATCH | `/api/v1/urls/{shortCode}` | Update URL |
| DELETE | `/api/v1/urls/{shortCode}` | Delete URL |

---
# 💡 API Usage Examples

## 1. Register / Authenticate

### Request

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 2. Create Short URL

### Request

```http
POST /api/v1/urls
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json

{
  "originalUrl": "https://www.postgresql.org/docs/current/index.html"
}
```

### Response

```json
{
  "shortUrl": "http://localhost:8080/QH6hX36j",
  "originalUrl": "https://www.postgresql.org/docs/current/index.html",
  "expiresAt": "2026-06-12T11:33:33"
}
```

---

## 3. Redirect

### Request

```http
GET http://localhost:8080/AbC12xyZ
```

### Response

```http
302 FOUND
```

The client is redirected to the original URL.

---