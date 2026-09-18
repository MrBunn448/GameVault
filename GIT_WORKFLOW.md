# GameVault – Git & Branching Strategy

**Project:** GameVault (Semester 3 Individual Project)  
**Primary Learning Outcome:** LO2 (Agile Development)  
**Secondary Learning Outcome:** LO3 (Process Automation & CI/CD)  
**Version:** 1.0 (Sprint 1 – September 2026)  

---

## 1. Overview & Strategy

To maintain a clean, traceable, and professional codebase throughout all development sprints, GameVault follows **GitHub Flow**.

GitHub Flow is a lightweight, branch-based workflow where all active development occurs in dedicated feature/task branches that are reviewed and tested via **Pull Requests (PRs)** before merging into `main`.

```text
main ─────────────────────────────────────────────────────────────●───────> (Always deployable)
       \                                                         /
        \──[ feat/library-api-skeleton ]──(Pull Request + CI)───/
```

### Core Principles
1. **Protected `main` Branch:** Code on `main` must always compile, pass all automated tests, and be deployable. Direct commits to `main` for new features are strictly avoided.
2. **Short-Lived Branches:** Branches are created for specific user stories, tasks, or fixes, and merged quickly once acceptance criteria are met.
3. **Automated Quality Gates:** Every Pull Request triggers the automated GitHub Actions CI pipeline (`./gradlew test`). A branch is only merged when the CI build is green.
4. **Traceability (Agile Alignment):** Branch names and commit messages reference their corresponding User Story or Technical Task IDs from the project board.

---

## 2. Branch Naming Conventions

Branches must use standardized prefixes matching the task type and issue ID:

| Branch Type | Prefix / Pattern | Example | Purpose |
| :--- | :--- | :--- | :--- |
| **Feature / User Story** | `feat/<ID>-<description>` | `feat/library-api-skeleton`<br>`feat/US-09-add-game` | New functional capabilities or user stories |
| **DevOps & Containers** | `devops/<ID>-<description>` | `devops/docker-backend` | Dockerfiles, Docker Compose, container configs |
| **CI / Automation** | `ci/<ID>-<description>` | `ci/github-actions` | GitHub Actions workflows and build automation |
| **Testing & QA** | `test/<ID>-<description>` | `test/postman-and-mockmvc` | MockMvc tests, unit tests, Postman suites |
| **Bugfixes** | `fix/<description>` | `fix/cors-configuration` | Defect corrections on existing code |
| **Documentation** | `docs/<description>` | `docs/architecture-update` | Standalone documentation or research papers |

---

## 3. The 5-Step Development Workflow

### Step 1: Start from an Up-to-Date `main`
Always pull the latest changes before starting a new task:
```bash
git checkout main
git pull origin main
```
Create and switch to your new branch:
```bash
git checkout -b feat/your-feature-name
```

### Step 2: Develop and Commit Frequently
Make small, focused commits with meaningful messages:
```bash
git add .
git commit -m "[US-10] Implement GET /api/library endpoint"
```

### Step 3: Push Your Branch to GitHub
Push your local branch to the remote repository:
```bash
git push -u origin feat/your-feature-name
```

### Step 4: Open a Pull Request (PR)
1. Go to your GitHub repository: `https://github.com/MrBunn448/GameVault`
2. Click **"Compare & pull request"**.
3. Fill out the PR template:
   * **Title:** `[PREFIX-ID] Short description of change` (e.g. `[US-10] View Personal Game Library`)
   * **Description:** What was implemented and how to test it.
   * **Linked Issue:** Link the corresponding issue/user story on the board.
4. Wait for **GitHub Actions CI** to run automated tests. Verify that the build status badge is **green**.

### Step 5: Merge and Clean Up
1. Merge the Pull Request into `main` on GitHub (**Squash and Merge** or standard merge commit).
2. Switch back to `main` locally and pull the newly merged code:
   ```bash
   git checkout main
   git pull origin main
   ```
3. Delete the obsolete local feature branch:
   ```bash
   git branch -d feat/your-feature-name
   ```

---

## 4. Sprint 1 Active Branches (PR1 Baseline)

The following branches are established for the initial sprint:

| Branch Name | Scope / Deliverable | Target PR Date |
| :--- | :--- | :--- |
| **`main`** | Production-ready baseline and stable releases | Ongoing |
| **`feat/library-api-skeleton`** | Controller, Service, In-memory Repository for `US-09` & `US-10` | ~Sep 22, 2026 |
| **`devops/docker-backend`** | Multi-stage backend `Dockerfile` and `.dockerignore` | ~Sep 23, 2026 |
| **`ci/github-actions`** | Automated CI pipeline running tests on push/PR | ~Sep 24, 2026 |
| **`test/postman-and-mockmvc`** | MockMvc controller test suite & Postman collection export | ~Sep 24, 2026 |

---

## 5. Commit Message Conventions

Commit messages should be concise, written in the imperative mood, and prefixed with the issue ID where applicable:

```text
[US-10] Add LibraryController and GET /api/library endpoint
[US-09] Validate request payload for POST /api/library
[DEVOPS-01] Add multi-stage Dockerfile for backend build
[CI-01] Configure GitHub Actions CI workflow for automated testing
[QA-01] Add MockMvc tests for HTTP 200 and HTTP 400 responses
```

---

## 6. Portfolio Evidence Value (Fontys Rubric Alignment)

* **LO2 (Agile Development):** Demonstrates disciplined iterative delivery. Every Pull Request serves as concrete proof of traceable user story implementation.
* **LO3 (Process Automation):** Shows that quality gates and automated CI pipelines are actively enforced before merging code into the main codebase.
