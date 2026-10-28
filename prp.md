# MASTER IMPLEMENTATION PROMPT
## Student Placement Management System — Complete Remaining Implementation

Act as a senior full-stack developer, DevOps engineer, software tester, and code quality engineer.

Your task is to inspect, improve, implement, test, and document the remaining implementation of my existing Student Placement Management System.

Work directly inside the currently opened project:
`D:\Student-Placement-Management-System`

Do not create a replacement project. Preserve the existing architecture, working features, Git history, configuration, and database structure wherever possible.

## 1. EXISTING TECHNOLOGY STACK

Frontend:
- React
- TypeScript
- Vite

Backend:
- Java 21
- Spring Boot
- Spring Security
- Maven

Database:
- MySQL 8
- Existing database: placement_db

DevOps:
- Git and GitHub
- Jenkins
- Docker Desktop with WSL 2
- SonarQube Community Build

Repository:
https://github.com/jayakumarhs15-jpg/Student-Placement-Management-System

Existing services:
- Frontend: http://localhost:5173
- Backend: http://localhost:8081
- Jenkins: http://localhost:8080
- SonarQube planned: http://localhost:9000

The backend previously passed four tests in Jenkins. Verify the current state rather than assuming those results are still valid.

## 2. NON-NEGOTIABLE SAFETY RULES

Before changing any files:

1. Inspect the complete repository structure.
2. Inspect README files, Maven configuration, package.json, application configuration, controllers, services, repositories, entities, tests, Jenkinsfile, Docker files, and Git status.
3. Identify implemented features, missing features, failing tests, and configuration problems.
4. Produce an implementation plan divided into phases.
5. Report the plan before making high-impact changes.

Protect existing work:

- Do not delete the existing Git repository or its history.
- Do not run git reset --hard, git clean -fd, or destructive cleanup commands.
- Do not overwrite existing configuration without inspecting it.
- Do not delete databases, tables, Docker volumes, or user data.
- Do not expose credentials, tokens, database passwords, or Jenkins secrets.
- Do not commit secrets to GitHub.
- Never print existing secret values into logs or reports.
- Never disable security or tests merely to make a build pass.
- Do not push to GitHub or trigger production deployment without my approval.
- Ask for approval before destructive operations, schema migrations that could lose data, or changes to existing Jenkins jobs.
- Preserve the existing Jenkins job named Student-Placement-Management-System and the unrelated job DA1_23MIC0019.

Use environment variables or the existing Jenkins credential `mysql-db-password` for database credentials. Never hardcode passwords.

If an external service requires a token or password, create a clear setup instruction instead of inventing credentials.

## 3. PHASE A — REPOSITORY AUDIT

Inspect the existing frontend and backend.

Check:
- Application structure
- Build configuration
- API endpoints
- Database mappings
- Authentication and authorization
- Input validation
- Exception handling
- Frontend API integration
- Loading and error states
- Existing unit and integration tests
- Git ignore rules
- Configuration and secrets
- Current Jenkins pipeline

Create:
- IMPLEMENTATION_AUDIT.md
- IMPLEMENTATION_PLAN.md

Classify each feature as:
- Implemented and verified
- Implemented but not verified
- Partially implemented
- Missing
- Blocked by external configuration

Do not claim that an untested feature works.

## 4. PHASE B — COMPLETE APPLICATION FEATURES

Based on the existing domain model and requirements, inspect the current application and complete the missing placement-management features.

Potential areas to verify include:
- Student records
- Student profiles
- Placement drives
- Eligibility rules
- Applications
- Placement evidence or documents
- Administrative workflows
- Authentication and role-based access

Only implement features appropriate to the existing application. Do not introduce unnecessary modules or replace working components.

Implement missing CRUD operations where required:
- Create
- Read
- Update
- Delete

Add suitable input validation, error responses, and frontend feedback.

Check authorization for protected operations.

Do not report CRUD as complete until all required operations are tested.

## 5. PHASE C — TESTING

Run the existing test suites first.

Backend:
- Maven compilation
- Unit tests
- Controller tests
- Service tests
- Repository tests where appropriate
- Validation tests
- Error-handling tests

Frontend:
- Production build
- TypeScript checks
- Existing test suite
- Add appropriate component and service tests

Fix genuine failures instead of skipping tests.

Avoid adding unnecessary testing frameworks if an appropriate framework is already configured.

Create:
- TESTING_REPORT.md

Record the exact commands executed, pass/fail counts, and any remaining blockers.

## 6. PHASE D — GIT BRANCHING AND RELEASE MANAGEMENT

Inspect the current Git branches and repository state.

Implement a documented branching strategy appropriate for this project.

Include:
- Feature branches
- Development and main/release branch responsibilities
- Pull-request or merge verification
- Version tags
- Release notes
- Rollback guidance

Do not rename or delete existing branches without approval.

Create:
- RELEASE_MANAGEMENT.md

Do not push, merge into protected branches, or create a public release without approval.

## 7. PHASE E — SONARQUBE

Inspect the existing Docker configuration and current SonarQube setup.

Docker Desktop's disk image location has already been configured as:
D:\docker\DockerDesktopWSL

The C: drive has limited free space. Avoid unnecessary downloads and ensure large Docker images and persistent SonarQube data use Docker's configured storage.

Check whether the SonarQube container or image already exists before creating another.

If SonarQube is unavailable:
1. Explain the blocker.
2. Provide the exact command or UI step needed.
3. Do not repeatedly download large images without checking current state.

When SonarQube is running:

- Configure the project for analysis.
- Analyze the Java backend.
- Configure frontend analysis if supported by the installed edition and tooling.
- Generate tokens only through the documented UI or secure configuration.
- Never store tokens in source code.
- Use Jenkins credentials for tokens.
- Integrate analysis into the Jenkins pipeline.
- Make the pipeline fail on explicitly configured required quality checks.
- Review reliability, maintainability, security hotspots, and code coverage where available.
- Fix genuine issues and rerun analysis.

Do not invent quality scores or claim a clean analysis without actual results.

Create:
- SONARQUBE_SETUP.md
- CODE_QUALITY_REPORT.md

## 8. PHASE F — DOCKER CONTAINERIZATION

Inspect the existing application and Docker installation before creating new resources.

Implement where appropriate:
- Backend Dockerfile
- Frontend Dockerfile
- .dockerignore files
- Docker Compose configuration
- Environment variable configuration
- Health checks
- Service networking
- Persistent database storage when the database is containerized

Requirements:

- Keep credentials out of Dockerfiles and Git.
- Use non-root containers where practical.
- Use reproducible build steps.
- Avoid exposing the MySQL port publicly unless necessary.
- Preserve the existing Windows MySQL installation and data.
- Do not stop, replace, or migrate the existing MySQL service without approval.
- Make the application configuration work in both local development and container environments.
- Do not assume localhost inside a container refers to the Windows host.
- Document how the frontend reaches the backend.
- Ensure the frontend does not depend on a development-only Vite server in production.

If containerizing MySQL would risk existing data, retain the current database and document that dependency until I approve a migration.

Create:
- DOCKER_SETUP.md

## 9. PHASE G — JENKINS CI/CD

Inspect the current Jenkinsfile and job configuration.

Preserve existing jobs and credentials.

Improve the existing pipeline to support these stages where appropriate:

1. Checkout
2. Validate configuration
3. Compile/build backend
4. Run backend tests
5. Build frontend
6. Run frontend tests
7. Run SonarQube analysis
8. Enforce required quality checks
9. Build Docker images
10. Validate release artifacts
11. Publish artifacts only when explicitly configured and authorized

Requirements:
- Use the existing GitHub repository.
- Use Jenkins credentials instead of hardcoded secrets.
- Do not echo passwords or tokens.
- Do not assume Jenkins automatically has access to the Docker daemon.
- Detect and explain missing Jenkins plugins, permissions, tools, or credentials.
- Preserve the existing MySQL credential.
- Do not overwrite the unrelated Jenkins job.
- Do not deploy or push Docker images to a registry without approval.

The pipeline must report meaningful errors when a stage fails.

Create:
- JENKINS_CICD_GUIDE.md

## 10. PHASE H — SECURITY AND CODE QUALITY

Inspect for:
- Hardcoded secrets
- Insecure endpoint access
- Missing authorization checks
- Input-validation gaps
- SQL injection risks
- Unsafe CORS configuration
- Exposed management endpoints
- Dependency vulnerabilities
- Sensitive information in logs
- Insecure Docker configuration

Fix verified issues without breaking the application.

Do not weaken Spring Security to make frontend requests succeed.

Document any findings that require manual configuration or a security review.

## 11. PHASE I — FINAL VERIFICATION

Run all relevant checks that can be executed in the available environment.

Verify:
- Backend compiles
- Backend tests pass
- Frontend builds
- Frontend tests pass where configured
- Database connectivity works where available
- Required APIs respond correctly
- Docker images build
- Container health checks pass
- SonarQube analysis succeeds when configured
- Jenkins pipeline executes successfully
- Git working tree contains only intended changes

Do not claim a service is running unless its actual status has been checked.

If a check cannot run because a service, token, plugin, or permission is missing, record it as BLOCKED and explain the exact next step.

Create:
- FINAL_IMPLEMENTATION_REPORT.md

Include:
- Completed tasks
- Files changed
- Commands executed
- Test results
- SonarQube findings
- Docker build and runtime status
- Jenkins pipeline results
- Remaining blockers
- Manual steps required
- Recommended next steps

## 12. EXECUTION STRATEGY

Work incrementally.

First:
- Audit the repository.
- Produce the plan.
- Identify critical blockers.

Then:
- Complete application functionality.
- Add and execute tests.
- Implement branching and release documentation.
- Configure SonarQube.
- Containerize the application.
- Improve Jenkins CI/CD.
- Perform final verification.

After each phase:
1. Summarize the changes.
2. List the files changed.
3. Run relevant tests.
4. Report failures honestly.
5. Continue to the next phase when safe.

Do not stop after merely writing documentation. Implement the actual code and configuration wherever possible.

Do not claim completion merely because files were created.

If you need user input, ask only for the specific missing information required to continue.

Begin by inspecting the current repository and producing the audit and implementation plan. Then proceed with the implementation in safe, verifiable stages.