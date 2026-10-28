# Student Placement Management System — SonarQube Setup & Quality Analysis Guide

**Specification Reference:** `prp.md` (Phase E)  
**Target Server:** http://localhost:9000  
**Project Key:** `Student-Placement-Management-System-Backend`  

---

## 1. Current Status & Blocker Analysis

| Item | Current State | Root Cause & Evidence | Next Required Action |
| :--- | :--- | :--- | :--- |
| **Port 9000** | Not listening | No process active on port 9000 | Launch SonarQube container |
| **Docker Daemon** | **STOPPED** | `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine` | Start Docker Desktop application |
| **WSL 2 Distro** | `docker-desktop` is Stopped | Output of `wsl -l -v`: `docker-desktop Stopped 2` | Docker Desktop will start this distro automatically |
| **Storage Allocation** | Safe on Drive D: | Configured location: `D:\docker\DockerDesktopWSL` (109 GB available) | Prevents exhausting Drive C: (16.8 GB free) |

---

## 2. Step-by-Step Instructions to Unblock and Run SonarQube

### Step 1: Start Docker Desktop
Launch Docker Desktop from Windows Start Menu, or via PowerShell:
```powershell
Start-Process "C:\Users\hsmys\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe"
```
Wait approximately 30–60 seconds until the Docker whale icon in the Windows taskbar tray shows "Docker Desktop is running".

### Step 2: Inspect Existing Images and Containers First
**Do not pull or download redundant images.** Verify existing state:
```powershell
# Check if a SonarQube image already exists locally
docker images sonarqube

# Check if a stopped SonarQube container already exists
docker ps -a --filter name=sonarqube
```

### Step 3: Launch SonarQube Container
If a stopped container already exists:
```powershell
docker start placement-sonarqube
```

If launching for the first time, use the configured `compose.yaml`:
```powershell
docker compose --profile tools up -d sonarqube
```
*Note: Persistent data is stored in the Docker WSL disk on Drive D: (`sonarqube_data`, `sonarqube_extensions`, `sonarqube_logs`).*

### Step 4: Verify SonarQube Availability
Wait 60–90 seconds for Elasticsearch and Java runtime to initialize:
```powershell
curl.exe -s -I http://localhost:9000/api/system/status
```
Expected response: HTTP 200 with status `UP`.

---

## 5. Security & Jenkins Credential Integration

1. Log in to SonarQube at http://localhost:9000 with default credentials (`admin` / `admin`).
2. Update the default password when prompted.
3. Generate an Analysis Token:
   - Navigate to: **My Account -> Security -> Generate Tokens**
   - Name: `jenkins-sonar-token`
   - Type: **User Token** or **Project Analysis Token**
   - Click **Generate** and copy the generated token.
4. Add Token in Jenkins:
   - Go to: **Jenkins -> Manage Jenkins -> Credentials -> System -> Global credentials**
   - Kind: **Secret text**
   - Secret: `<paste-sonar-token>`
   - ID: `sonar-token`
   - Description: `SonarQube Analysis Token`
5. *Never commit or hardcode this token in files or Git history.*

---

## 6. Running Code Quality Analysis Locally

Once SonarQube is active and your token is generated:
```powershell
cd D:\Student-Placement-Management-System\backend
$env:SONAR_TOKEN="<your-sonar-token>"
.\mvnw.cmd clean test jacoco:report sonar:sonar `
  -Dsonar.token=$env:SONAR_TOKEN `
  -Dsonar.host.url=http://localhost:9000
```

The scan transmits:
- JaCoCo code coverage report (`target/site/jacoco/jacoco.xml`)
- Static code analysis across all Java controllers, entities, repositories, and configuration classes
- Security hotspot and vulnerability scan
