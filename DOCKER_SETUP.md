# Student Placement Management System — Docker Containerization Guide

**Specification Reference:** `prp.md` (Phase F)  
**Configuration File:** `compose.yaml` (root)  

---

## 1. Containerization Architecture Overview

The container configuration provides isolated, reproducible environments for both development and production deployment without disrupting the existing Windows MySQL 8 installation or stored data.

```
+--------------------------------------------------------------------------------+
| Host Machine (Windows 11)                                                      |
|                                                                                |
|  [MySQL 8 Server] (Service: MySQL80, Port 3306, Database: placement_db)       |
|         ^                                                                      |
|         | (via host.docker.internal:3306)                                      |
|         |                                                                      |
|  +------|------------------ placement-network ------------------------------+  |
|  |      |                                                                 |  |
|  |  +---|--------------------+                  +-----------------------+ |  |
|  |  | Backend Container      |                  | Frontend Container    | |  |
|  |  | Image: placement-backend                  | Image: placement-front| |  |
|  |  | Java 21 JRE (non-root) |                  | Nginx Alpine Reverse  | |  |
|  |  | Port: 8081             |<--- Reverse -----| Port: 80, 5173        | |  |
|  |  | Health: /api/home      |     Proxy        | Serves static Vite    | |  |
|  |  +------------------------+     (/api/*)     +-----------------------+ |  |
|  |                                                                        |  |
|  +------------------------------------------------------------------------+  |
+--------------------------------------------------------------------------------+
```

---

## 2. Docker Assets Overview

| Component | File Path | Description | Security / Operational Features |
| :--- | :--- | :--- | :--- |
| **Backend** | `backend/Dockerfile` | Multi-stage build (Temurin 21 JDK -> Temurin 21 JRE Alpine) | Runs as non-root `appuser`, includes HTTP health check, excludes test dependencies in runner. |
| **Backend Ignore** | `backend/.dockerignore` | Build context exclusions | Prevents copying `target/`, `.git/`, and IDE files into build context. |
| **Frontend** | `frontend/Dockerfile` | Multi-stage build (Node 22 -> Nginx Alpine) | Produces optimized static bundle; eliminates Vite dev server in production. |
| **Frontend Nginx** | `frontend/nginx.conf` | Web server configuration | Serves static assets, reverse-proxies `/api/*` to `backend:8081`, injects security headers. |
| **Frontend Ignore** | `frontend/.dockerignore` | Build context exclusions | Prevents copying local `node_modules/` or `dist/`. |
| **Orchestration** | `compose.yaml` | Service definitions | Orchestrates backend, frontend, and SonarQube on `placement-network` bridge. |

---

## 3. Storage & Resource Safeguards

1. **Storage Drive Preservation:**
   - Docker Desktop's disk image is pre-configured at: `D:\docker\DockerDesktopWSL`
   - Docker volumes for tools (`sonarqube_data`, `sonarqube_extensions`, `sonarqube_logs`) are managed within the Docker WSL vhdx image on Drive D: to preserve Drive C: space.
2. **Database Data Preservation:**
   - The container setup connects directly to the host's Windows MySQL service via `host.docker.internal:3306`.
   - No database container is forced; existing tables and rows in `placement_db` remain strictly intact.

---

## 4. Execution Commands

### 4.1 Prerequisites
Ensure Docker Desktop is running. To launch Docker Desktop on Windows:
```powershell
Start-Process "C:\Users\hsmys\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe"
```

Verify connection to Docker daemon:
```powershell
docker version
```

### 4.2 Build Images
```powershell
# Build both frontend and backend images
docker compose build
```

### 4.3 Run Application Containers
```powershell
# Pass your MySQL password as an environment variable (never hardcode in files)
$env:DB_PASSWORD="your-database-password"
docker compose up -d backend frontend
```

### 4.4 Inspect Status and Health
```powershell
docker compose ps
```
Both containers will report `healthy` once their internal health checks pass.

### 4.5 Access Services
- **Frontend Portal:** http://localhost:80 or http://localhost:5173
- **Backend API:** http://localhost:8081/api/home
- **Application Health:** `http://localhost:8081/api/home` returns HTTP 200.

### 4.6 Stop Containers
```powershell
docker compose down
```
*(Does not affect host MySQL or stored database records).*
