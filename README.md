# Employee Demo

A full-stack demo application with a Spring Boot backend and a React frontend, containerized with Docker.

## Project Structure

```
employee-demo/
│
├── backend/               Spring Boot REST API (Java 17, Maven)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│
├── frontend/               React application (Vite)
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   ├── Dockerfile
│   └── src/
│
├── docker-compose.yml      Orchestrates backend, frontend, and Postgres
└── README.md
```

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ and npm
- Docker and Docker Compose

## Running locally (without Docker)

### Backend

```bash
cd backend
mvn spring-boot:run
```

The API will start on `http://localhost:8080`. You'll need a local Postgres instance running with a database named `employeedb` (see `src/main/resources/application.properties` for connection defaults), or adjust the properties to point at your own database.

### Frontend

Built with [Vite](https://vitejs.dev/).

```bash
cd frontend
npm install
npm run dev
```

The app will start on `http://localhost:3000`. In dev mode, requests to `/api/*` are proxied to the backend at `http://localhost:8080` (see `vite.config.js`). To point at a different backend URL, set `VITE_API_URL` in a `.env` file.

To build for production:

```bash
npm run build
```

This outputs static assets to `frontend/dist/`, which the frontend `Dockerfile` serves via nginx.

## Running with Docker Compose

From the project root:

```bash
docker-compose up --build
```

This starts:
- **postgres** on port `5432`
- **backend** on port `8080`
- **frontend** on port `3000`

## API Endpoints

| Method | Endpoint              | Description          |
|--------|------------------------|-----------------------|
| GET    | `/api/employees`       | List all employees   |
| GET    | `/api/employees/{id}`  | Get employee by ID   |
| POST   | `/api/employees`       | Create new employee  |
| PUT    | `/api/employees/{id}`  | Update employee      |
| DELETE | `/api/employees/{id}`  | Delete employee      |

## Running Tests

```bash
cd backend
mvn test
```

Tests use an in-memory H2 database, so no external database is required to run `mvn test`.
