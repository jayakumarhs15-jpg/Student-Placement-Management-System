# Student Placement Management System — Jenkins CI/CD Pipeline Guide

**Specification Reference:** PRP Phase 2, Phase 3, Phase 4, & Phase 5  
**Jenkins URL:** http://localhost:8080  
**Target Pipeline Job:** `Student-Placement-Management-System`  
**Unrelated Job Preserved:** `DA1_23MIC0019`  
**Date of Verification:** 2026-10-28  

---

## 1. Overview and Architecture

The enhanced CI/CD pipeline automates verification, compilation, automated testing, code coverage generation, code quality inspection, container packaging, and artifact archival across the full-stack repository.

```
+---------------------+     +-------------------------+     +----------------------+     +----------------------+
| 1. Checkout Source  | --> | 2. Validate Environment | --> | 3. Compile Backend   | --> | 4. Run Tests & JaCoCo|
+---------------------+     +-------------------------+     +----------------------+     +----------------------+
                                                                                                     |
+---------------------+     +-----------------------+     +------------------------+                 |
| 7. SonarQube Scan   | <-- | 6. Run Frontend Tests | <-- | 5. Build Frontend      | <---------------+
+---------------------+     +-----------------------+     +------------------------+
           |
           v
+---------------------+     +-----------------------+     +------------------------+
| 8. Enforce Gate     | --> | 9. Build Docker Images| --> | 10. Archive Artifacts  |
+---------------------+     +-----------------------+     +------------------------+
```

---

## 2. Pipeline Stages Breakdown

| Stage | Stage Name | Action / Command | Key Artifacts & Output |
| :---: | :--- | :--- | :--- |
| **1** | **Checkout Source** | Checks out Git commit ref from GitHub repository. | Workspace directory tree |
| **2** | **Validate Environment & Tools** | Validates Java 21, Node.js, npm, and workspace file structure. | Validated environment |
| **3** | **Compile Backend** | Runs `mvnw.cmd compile -DskipTests` in `backend/`. | Compiled class files |
| **4** | **Run JUnit Tests & JaCoCo** | Executes `mvnw.cmd clean test jacoco:report` with `mysql-db-password`. Automatically publishes JUnit results and archives JaCoCo reports. | `backend/target/surefire-reports/*.xml`, `backend/target/site/jacoco/**` |
| **5** | **Build Frontend** | Runs `npm ci` and `npm run build` in `frontend/`. | Production bundle in `frontend/dist/` |
| **6** | **Run Frontend Tests & Lint** | Runs `npm run lint` (`oxlint`) and `npm test` (`vitest`). | Frontend test & lint verification |
| **7** | **SonarQube Quality Analysis** | Transmits JaCoCo XML report to SonarQube on `http://localhost:9000` via token. | SonarQube quality analysis |
| **8** | **Enforce SonarQube Quality Gate** | Queries SonarQube `/api/qualitygates/project_status` and enforces gate criteria. | Quality Gate enforcement |
| **9** | **Build Docker Images** | Builds `placement-backend:latest` and `placement-frontend:latest` via Docker. | Packaged container images |
| **10** | **Archive Build Artifacts** | Archives `backend/target/*.jar` and `frontend/dist/**` with fingerprinting. | Jenkins downloadable build artifacts |

---

## 3. Test & Coverage Reporting in Jenkins

### 3.1 JUnit Test Result Publishing
- **Plugin:** Built-in `junit` plugin (`v1343.v34cb_9b_232a_39` or higher).
- **Pattern:** `backend/target/surefire-reports/*.xml`
- **Configuration in `Jenkinsfile`:**
  ```groovy
  junit testResults: 'backend/target/surefire-reports/*.xml', allowEmptyResults: true
  ```
- **Features Enabled in UI:**
  - Total test count (44 tests)
  - Passed vs. Failed breakdown
  - Historical test duration trends
  - Detailed error stack traces if regressions occur

### 3.2 JaCoCo Code Coverage Report Archival
- **Archival Path:** `backend/target/site/jacoco/**`
- **Configuration in `Jenkinsfile`:**
  ```groovy
  archiveArtifacts artifacts: 'backend/target/site/jacoco/**', allowEmptyArchive: true, fingerprint: true
  ```
- **Accessing the Report:**
  Navigate to **Jenkins -> Job -> Build # -> Build Artifacts -> backend -> target -> site -> jacoco -> index.html**. The full interactive web report is browsable directly within Jenkins.

---

## 4. SonarQube Quality Gate Integration

### 4.1 Token Setup in Jenkins
1. Open Jenkins at http://localhost:8080.
2. Navigate to: **Manage Jenkins -> Credentials -> System -> Global credentials (unrestricted)**.
3. Click **Add Credentials**:
   - **Kind:** Secret text
   - **Scope:** Global
   - **Secret:** `squ_0adde5b2ffbd914b0b910f4f078d66397e27c63d` (or token generated in SonarQube)
   - **ID:** `sonar-token`
   - **Description:** `SonarQube Analysis Token`
4. Click **Create**.

### 4.2 Quality Gate Verification Command
The pipeline queries the SonarQube REST API directly:
```powershell
curl.exe -s -u %SONAR_TOKEN%: "http://localhost:9000/api/qualitygates/project_status?projectKey=Student-Placement-Management-System-Backend"
```
Expected response:
```json
{"projectStatus":{"status":"OK","conditions":[],"ignoredConditions":false,"caycStatus":"compliant"}}
```

---

## 5. Storage & Disk Allocation Rules

To protect limited storage on Drive C: (17.14 GB free), all persistent data and large assets are directed to Drive D: (107.98 GB free):

| Component | Storage Location | Notes |
| :--- | :--- | :--- |
| **Project Workspace** | `D:\Student-Placement-Management-System` | Primary Git workspace |
| **Docker Engine WSL Disk** | `D:\dacker\DockerDesktopWSL\data\ext4.vhdx` | Container images, volumes, and layer cache |
| **SonarQube Volumes** | Stored inside Docker WSL on Drive D: | Preserves database indices & history |
| **Build Artifacts** | `backend/target/` and `frontend/dist/` on D: | Cleaned and regenerated on demand |

---

## 6. Preserved Resources & Safety Verification

1. **Jenkins Jobs Preserved:**
   - `Student-Placement-Management-System`
   - `DA1_23MIC0019` (unrelated course job untouched)
2. **Credentials Preserved:**
   - `mysql-db-password` (masked and injected safely)
3. **Database Integrity:**
   - Automated tests run against isolated test configuration with MySQL dialect emulation.
   - Live database `placement_db` is untouched.
