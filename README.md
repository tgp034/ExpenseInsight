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