# Student Placement Management System — Release Management & Git Branching Strategy

**Specification Reference:** `prp.md` (Phase D)  
**Repository:** https://github.com/jayakumarhs15-jpg/Student-Placement-Management-System  

---

## 1. Branching Strategy Overview

This project adheres to a structured, predictable Git branching workflow designed for stability, team collaboration, and automated CI/CD validation.

```
       (hotfix/*)
       +--------+
      /          \
[main]------------*------------------------*-------> (Production releases & tags)
      \          /                        /
       \        /  (PR merge after tests)/
[develop]------*-----------*------------*----------> (Integration branch)
                \         /
                 +-------+
                (feature/*)
```

### 1.1 Branch Classifications and Responsibilities

| Branch Name / Pattern | Role & Purpose | Direct Commits Allowed? | Merge Requirements |
| :--- | :--- | :---: | :--- |
| `main` | Production-ready releases. Every commit on `main` is deployable and tagged. | **No** (Protected) | Pull Request passing all Jenkins CI tests, SonarQube quality gates, and code review approval. |
| `develop` | Ongoing integration branch. Integrates completed features before staging and release. | **No** (Protected) | Pull Request from feature branch with passing test suites. |
| `feature/<feature-name>` | Development of new features (e.g. `feature/student-profile-upload`, `feature/drive-notifications`). | **Yes** (Author only) | Branches from `develop`, merges back into `develop` via PR. |
| `fix/<bug-name>` | Bug fixes identified during development or testing. | **Yes** (Author only) | Branches from `develop`, merges back into `develop`. |
| `hotfix/<version>` | Critical production defect patches. | **Yes** (Author only) | Branches from `main`, merges back into both `main` and `develop`. |

---

## 2. Feature Development & Contribution Workflow

### Step 1: Branch from `develop`
```bash
git checkout develop
git pull origin develop
git checkout -b feature/enhanced-placement-analytics
```

### Step 2: Develop and Test Locally
Ensure all test suites pass locally before creating a pull request:
```powershell
# Backend verification
cd backend
.\mvnw.cmd clean test

# Frontend verification
cd ..\frontend
npm run lint
npm test
npm run build
```

### Step 3: Atomic Commits with Conventional Message Format
Format commit messages following the Conventional Commits specification:
```
<type>(<scope>): <short summary>

[optional body explaining motivation and impact]
```
Common types:
- `feat`: A new user-facing feature or enhancement.
- `fix`: A bug fix.
- `test`: Adding or updating test cases.
- `refactor`: Code changes that neither fix a bug nor add a feature.
- `chore`: Build scripts, dependencies, or configuration changes.
- `docs`: Documentation updates.

### Step 4: Submit Pull Request & Merge Verification
1. Push branch to GitHub: `git push -u origin feature/<feature-name>`
2. Open Pull Request targeting `develop`.
3. Jenkins CI automatically runs:
   - Backend compilation and 30 unit tests.
   - Frontend linting, 4 tests, and production build.
   - SonarQube quality analysis.
4. Merge only when all checks are green.

---

## 3. Release Process & Semantic Versioning

Releases follow [Semantic Versioning 2.0.0](https://semver.org/):
`MAJOR.MINOR.PATCH` (e.g., `v1.0.0`)
- **MAJOR**: Incompatible API breaks or database schema restructuring.
- **MINOR**: Backward-compatible new features (e.g., new reporting endpoint, new UI dashboard module).
- **PATCH**: Backward-compatible bug fixes and security patches.

### Release Steps:
1. Merge `develop` into `main` via release PR.
2. Tag release on `main`:
   ```bash
   git checkout main
   git pull origin main
   git tag -a v1.0.0 -m "Release v1.0.0: Initial production release with full CRUD, verified tests, and Docker setup"
   ```
3. Prepare Release Notes summarizing changes, fixed issues, and deployment notes.

---

## 4. Rollback and Disaster Recovery Guidance

In case of an urgent production incident:

### Scenario A: Rollback via Git Revert (Recommended)
Preserves Git history and produces a clear audit trail:
```bash
git checkout main
git pull origin main
# Revert the faulty merge or commit
git revert -m 1 <commit-hash>
git push origin main
```

### Scenario B: Emergency Hotfix
1. Branch from `main`:
   ```bash
   git checkout main
   git checkout -b hotfix/v1.0.1-auth-fix
   ```
2. Apply fix and verify all tests.
3. Merge hotfix into both `main` and `develop`.
4. Tag new patch release `v1.0.1`.

---

## 5. Security & Safety Rules (Non-Negotiable)
- Never force-push (`git push --force`) to `main` or `develop`.
- Never commit `.env` files, database passwords, or private keys.
- Preserve existing branch history and avoid destructive `git clean` or `git reset` commands.
