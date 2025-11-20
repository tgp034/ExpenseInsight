# ExpenseInsight 💰

Personal expense analyzer with AI support.

## 🚀 Tech Stack

### Frontend
- Next.js 14+ (React, TypeScript)
- Tailwind CSS
- React Query
- Recharts

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
├── frontend/         # Next.js Application
├── docker-compose.yml
└── README.md
```

## 🛠️ Prerequisites

- Node.js 22.x LTS
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


### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📝 Project Status

🚧 Under active development

## 📄 License

MIT License