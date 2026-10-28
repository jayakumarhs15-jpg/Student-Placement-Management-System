# PlaceTrack — Student Placement Management System

A full-stack web application designed for universities and colleges to streamline the student placement lifecycle. Manage student records, partner companies, placement drives, and application tracking with role-based validation, automated eligibility verification, and CI/CD pipelines.

---

## Technology Stack

- **Frontend:** React 19, Vite 8, Vitest, Oxlint, Vanilla CSS (DM Sans & Manrope)
- **Backend:** Java 21 LTS, Spring Boot 4, Spring Data JPA, Hibernate, Bean Validation
- **Database:** MySQL 8 (`placement_db`), H2 (in-memory test profile)
- **DevOps & Containers:** Docker, Docker Compose, Nginx, Jenkins CI/CD, SonarQube

---

## Key Features

1. **Placement Dashboard:** Live statistics cards, recent drive summaries, and visual application progress pipeline.
2. **Student Directory:** Full CRUD operations with student details (Name, Email, Department, CGPA, Graduation Year) and duplicate email protection.
3. **Company Management:** Full CRUD management of recruiting companies, industry domains, locations, and HR contact information.
4. **Placement Drives:** Job postings with minimum CGPA requirements, locations, job descriptions, and application deadlines.
5. **Application Lifecycle:** Automated eligibility verification (student existence, drive existence, CGPA threshold check, deadline validation, and duplicate prevention), status transition management (`APPLIED`, `SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED`), and withdrawal support.
6. **Search & Filter:** Instant table search across all entity fields.
7. **Automated Testing:** 30 comprehensive backend unit/slice tests and 4 frontend component/integration tests with 100% pass rate.

---

## Project Structure

```
Student-Placement-Management-System/
├── backend/                  # Spring Boot 4 (Java 21) REST API
│   ├── src/main/java/        # Controllers, Entities, Repositories, Config
│   ├── src/test/java/        # JUnit 5 & MockMvc automated test suites
│   ├── src/test/resources/   # In-memory H2 test configuration
│   ├── Dockerfile            # Multi-stage Java 21 non-root container
│   └── pom.xml               # Maven configuration with JaCoCo & Sonar
├── frontend/                 # React 19 + Vite Frontend
│   ├── src/                  # App.jsx, App.css, App.test.jsx
│   ├── nginx.conf            # Production Nginx reverse-proxy config
│   ├── Dockerfile            # Multi-stage Nginx production container
│   └── package.json          # Vite, Vitest, Oxlint configuration
├── compose.yaml              # Docker Compose full-stack orchestration
├── Jenkinsfile               # 10-stage Jenkins CI/CD pipeline
├── DOCKER_SETUP.md           # Docker deployment and WSL storage guide
├── JENKINS_CICD_GUIDE.md     # Jenkins configuration and credential guide
├── SONARQUBE_SETUP.md        # SonarQube setup and unblocking guide
├── TESTING_REPORT.md         # Detailed test execution report
├── RELEASE_MANAGEMENT.md     # Git branching and release strategy
└── IMPLEMENTATION_AUDIT.md   # System audit and component inventory
```

---

## Quick Start & Local Development

### 1. Backend (Spring Boot)
Ensure MySQL is running on port 3306 with database `placement_db`.
```powershell
cd backend
$env:DB_PASSWORD="your-mysql-password"
.\mvnw.cmd spring-boot:run
```
The REST API starts on `http://localhost:8081`.

### 2. Frontend (React + Vite)
```powershell
cd frontend
npm install
npm run dev
```
The application opens on `http://localhost:5173` with automatic API proxying to port 8081.

---

## Running Automated Tests

### Backend Tests (30 tests)
```powershell
cd backend
.\mvnw.cmd clean test
```

### Frontend Tests & Linting
```powershell
cd frontend
npm run lint
npm test
npm run build
```

---

## Container Deployment (Docker Compose)

```powershell
$env:DB_PASSWORD="your-mysql-password"
docker compose up -d backend frontend
```
Access the application at `http://localhost:80` or `http://localhost:5173`.
