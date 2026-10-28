# Student Placement Management System — Implementation Plan

**Date:** 2026-10-28  
**Master Specification:** `prp.md`  
**Target Project:** `D:\Student-Placement-Management-System`  

---

## 1. Safety Rules & Preservation Principles

1. **Non-Destructive Execution**: No `git reset --hard`, no `git clean -fd`, no deletion of database tables or data.
2. **Credential Safety**: No passwords, tokens, or private secrets in source code, commits, or reports. Use existing Jenkins credential `mysql-db-password` and environment variables.
3. **Preserve Existing Resources**:
   - Preserve Git history and branches.
   - Preserve Jenkins jobs: `Student-Placement-Management-System` and `DA1_23MIC0019`.
   - Preserve Windows MySQL installation (`MySQL80` service on port 3306) and existing `placement_db` database.
4. **Incremental Verification**: Verify each phase with actual builds and test executions before moving forward.

---

## 2. Phase-by-Phase Execution Roadmap

### Phase A: Repository Audit & Planning (Completed)
- [x] Full source tree and configuration inspection.
- [x] Environment and runtime verification (Java 21, Maven 3.9.16, Node v22.19.0, npm 10.9.3, MySQL, Jenkins, Docker, SonarQube).
- [x] Current test execution and baseline test count verification.
- [x] Generated `IMPLEMENTATION_AUDIT.md`.
- [x] Generated `IMPLEMENTATION_PLAN.md`.

---

### Phase B: Complete Application Features
#### Backend Enhancements
1. **Clean Stray File**: Remove `backend/src/main/java/com/placement/backend/BackendApplicationTests.java` from `src/main` (an empty misplaced class).
2. **Complete Student CRUD**:
   - Implement `PUT /api/students/{id}` (update student details with email duplicate protection).
   - Implement `DELETE /api/students/{id}` (delete student by ID with 404 handling).
3. **Complete Application Operations**:
   - Implement `DELETE /api/applications/{id}` (withdraw / remove application).
4. **Global Exception Handling & CORS**:
   - Add a global `@ControllerAdvice` or WebMvcConfigurer bean to provide clean structured error messages and safe CORS support for both Vite dev server (`http://localhost:5173`) and containerized frontend.

#### Frontend Enhancements
1. **Full CRUD in React UI**:
   - Add **Edit / Update** button and modal/form for Students, Companies, and Placement Drives.
   - Add **Delete** button with modal confirmation for Students, Companies, Placement Drives, and Applications.
   - Add **Status Update** dropdown for Applications to trigger `PATCH /api/applications/{id}/status`.
2. **Search and Filter**:
   - Add quick search/filter inputs for each directory table.
3. **Fix Oxlint / Code Quality Warnings**:
   - Remove unused parameter `section` in `openForm`.
   - Extract impure `new Date()` out of JSX render cycle into stable helpers or memoized headers.
   - Fix synchronous `setState` in `useEffect`.

---

### Phase C: Testing & Test Suite Expansion
1. **Backend Tests**:
   - Add test profile / in-memory H2 dependency scoped to `test` in `pom.xml` so `BackendApplicationTests.contextLoads()` can pass reliably both offline/locally without requiring hardcoded passwords, and in CI with MySQL.
   - Expand unit tests with `@WebMvcTest`:
     - `CompanyControllerTest.java` (CRUD tests: list, get by ID, create, update, delete, 404 checks).
     - `PlacementDriveControllerTest.java` (CRUD tests: list, get by ID, create, update, delete, 404 checks).
     - `ApplicationControllerTest.java` (Full business rule tests: valid submission, CGPA ineligible, past deadline, duplicate application, status patch, delete).
     - `HomeControllerTest.java` (greeting test).
2. **Frontend Tests**:
   - Add testing tooling to `frontend/package.json` (`vitest`, `@testing-library/react`, `jsdom`).
   - Add component smoke and unit tests (`App.test.jsx`).
3. **Execution & Documentation**:
   - Execute all backend and frontend tests.
   - Generate `TESTING_REPORT.md` recording commands, pass/fail counts, and outputs.

---

### Phase D: Git Branching & Release Management
1. **Branching Strategy Definition**:
   - Define standard Git workflow (`main` as protected release branch, `develop` for integration, `feature/*` and `fix/*` for active work).
   - Define commit message conventions, PR review checklists, semantic version tags (`v1.0.0`), release notes templates, and rollback procedures.
2. **Documentation**:
   - Generate `RELEASE_MANAGEMENT.md`.

---

### Phase E: SonarQube Code Quality Configuration
1. **Analyze Docker & SonarQube Blocker**:
   - Docker Desktop is installed at `C:\Users\hsmys\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe` with storage on `D:\docker\DockerDesktopWSL`.
   - Current blocker: Docker Desktop daemon is not running.
2. **Setup Instructions & Configuration**:
   - Provide exact non-destructive instructions to launch Docker Desktop and SonarQube container on port 9000 using persistent storage on drive D:.
   - Add SonarQube Maven plugin configuration to `backend/pom.xml`.
   - Document project key, quality gate configuration, and Jenkins credential integration.
3. **Documentation**:
   - Generate `SONARQUBE_SETUP.md`.
   - Generate `CODE_QUALITY_REPORT.md`.

---

### Phase F: Docker Containerization
1. **Container Assets**:
   - Create `backend/Dockerfile` using multi-stage build with Eclipse Temurin Java 21 JRE, running as non-root user.
   - Create `frontend/Dockerfile` using multi-stage build (Vite build + Nginx alpine reverse proxy).
   - Create `.dockerignore` files for both frontend and backend.
   - Create `compose.yaml` (root) orchestrating backend, frontend, and connecting cleanly to existing Windows MySQL or containerized MySQL profile.
2. **Documentation**:
   - Generate `DOCKER_SETUP.md` detailing build commands, network flow, and port bindings.

---

### Phase G: Jenkins CI/CD Pipeline
1. **Enhance `Jenkinsfile`**:
   - Expand the existing 4-stage pipeline to 10 comprehensive stages:
     1. Checkout
     2. Configuration & Tool Verification
     3. Backend Build & Compilation
     4. Backend Unit & Slice Tests
     5. Frontend Dependencies & Build
     6. Frontend Unit Tests & Lint
     7. SonarQube Code Quality Analysis (conditional on SonarQube availability)
     8. Quality Gate Enforcement
     9. Docker Image Build (backend & frontend)
     10. Release Artifact Validation
2. **Security & Preservation**:
   - Retain `mysql-db-password` credential.
   - Protect both existing Jenkins jobs (`Student-Placement-Management-System` and `DA1_23MIC0019`).
3. **Documentation**:
   - Generate `JENKINS_CICD_GUIDE.md`.

---

### Phase H: Security & Code Quality Hardening
1. **CORS & Headers**: Ensure secure CORS filter and HTTP headers in Spring Boot.
2. **Input Validation**: Check all request models for strict bounds and sanitize inputs.
3. **Verify Secrets**: Validate that no credentials leak into Docker images, logs, or Git tree.

---

### Phase I: Final Verification & Reporting
1. **Run Full Verification**:
   - Run backend Maven clean test.
   - Run frontend lint, test, and build.
   - Verify Git status is clean and contains only expected files.
2. **Generate Final Artifact**:
   - Create `FINAL_IMPLEMENTATION_REPORT.md` documenting completed work, test results, blockers, and next steps.
