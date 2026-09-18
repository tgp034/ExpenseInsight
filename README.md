# ExpenseInsight 💰

Personal expense analyzer with AI support.

## 🚀 Tech Stack

### Backend
- Java 21
- Spring Boot 3.2+
- PostgreSQL 15+
- OpenAI API (gpt-4o-mini)

### Infrastructure
- Docker & Docker Compose
- Vercel (Frontend)
- Render/Railway (Backend)

## 📁 Project Structure
```
expense-insight/
├── backend/          # REST API with Spring Boot
├── docker-compose.yml
└── README.md
```

## 🛠️ Prerequisites

- JDK 21
- PostgreSQL 15+
- Docker Desktop (optional)

### Backend configuration

- Copy `backend/src/main/resources/application-local.properties.example` to `application-local.properties` and set your secrets locally (the real file is gitignored).
- The committed `application.properties` uses safe defaults and loads `application-local.properties` automatically when present.
- Flyway migrations live under `backend/src/main/resources/db/migration`.

## 🏃 Quick Start

### Database (Docker)
```bash
docker-compose up -d
```

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

To run the backend tests:

```bash
cd backend
mvn test
```

## 📝 Project Status

🚧 Under active development

## 📄 License

MIT License
