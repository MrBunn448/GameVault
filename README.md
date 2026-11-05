# GameVault – Backend REST API

**Project:** GameVault (Semester 3 Full-Stack Individual Project)  
**Primary Learning Outcome:** LO1 (Full-Stack Application Development)  
**Secondary Learning Outcome:** LO3 (Process Automation & CI/CD) & LO4 (Quality Assurance)  
**Version:** 1.0 (Sprint 1 – PR1 Baseline)

---

## 1. Overview

The **GameVault Backend** is a Spring Boot REST API responsible for managing game collections, tracking user library statuses, calculating statistics, and serving data to the client frontend.

---

## 2. Technology Stack

* **Framework:** Spring Boot 3.5+
* **Language & Runtime:** Java 25 (Eclipse Temurin)
* **Build Automation:** Gradle 8.14 (Gradle Wrapper)
* **Containerization:** Docker (Multi-stage build with JRE runtime & health checks)
* **Testing:** JUnit 5, MockMvc, AssertJ

---

## 3. Getting Started & Startup Instructions

The backend API can be run either **natively using Gradle** or as an **isolated Docker container**. Ensure port `8080` is available on your machine.

### Option A: Run Natively (Gradle)

#### Prerequisites
* Java JDK 25 installed (`java -version`)

#### Steps
```bash
# 1. Navigate to the API directory
cd GameVaultAPI

# 2. Start the Spring Boot application
./gradlew bootRun
```
* **API Base URL:** `http://localhost:8080/api/library`
* To stop the server: Press `Ctrl + C`.

---

### Option B: Run via Docker Container

Running inside Docker requires no host Java installation—the multi-stage `Dockerfile` compiles the JAR inside a JDK 25 builder image and packages it into a hardened, lightweight JRE 25 runtime image.

For a detailed explanation of image building, running, health checks, logs, and cleanup, see [`GameVaultAPI/DOCKER.md`](GameVaultAPI/DOCKER.md).

#### Prerequisites
* Docker installed and daemon running (`docker --version`)

#### Steps
```bash
# 1. Navigate to the API directory
cd GameVaultAPI

# 2. Build the Docker container image
docker build -t gamevault-api:latest .

# 3. Run the container in the background (mapping port 8080)
docker run -d --name gamevault-backend -p 8080:8080 gamevault-api:latest

# 4. View container status & health checks
docker ps

# 5. Follow live application logs
docker logs -f gamevault-backend
```

#### Stopping and Cleaning Up
```bash
docker stop gamevault-backend && docker rm gamevault-backend
```

---

## 4. API Verification

Once started (either natively or via Docker), verify the seeded library endpoint:

```bash
curl -X GET http://localhost:8080/api/library
```

Expected response: HTTP 200 with initial seeded games (*Hades*, *Elden Ring*, *Hollow Knight*).

---

## 5. Automated Testing & Static Code Analysis (LO4)

All testing and quality gates can be executed locally before pushing to CI/CD:

```bash
cd GameVaultAPI
```

### 1. Static Code Analysis with Checkstyle
Validates Java syntax, formatting, clean imports, and naming standards against `config/checkstyle/checkstyle.xml`:
```bash
./gradlew checkstyleMain checkstyleTest
```
* HTML report: `build/reports/checkstyle/main.html`

### 2. Unit & Integration Tests (JUnit 5, Mockito & MockMvc)
Executes isolated service unit tests and Spring Boot full-slice integration tests ("from repository to valid JSON"):
```bash
./gradlew test
```
* HTML report: `build/reports/tests/test/index.html`

### 3. JaCoCo Test Coverage Report
Generates branch and line test coverage data:
```bash
./gradlew jacocoTestReport
```
* Open in browser: `build/reports/jacoco/test/html/index.html`
* XML report for Sonar: `build/reports/jacoco/test/jacocoTestReport.xml`

### 4. SonarCloud Static Code Analysis
Performs deep semantic analysis, vulnerability scanning, code smell checks, and Quality Gate evaluation:
```bash
./gradlew sonar -Dsonar.token=<YOUR_SONAR_TOKEN>
```
* Cloud Dashboard: [https://sonarcloud.io/dashboard?id=MrBunn448_GameVault](https://sonarcloud.io/dashboard?id=MrBunn448_GameVault)

### 5. Run Everything in One Command:
```bash
./gradlew checkstyleMain checkstyleTest test jacocoTestReport
```

---

## 6. Branching & Git Strategy

Development follows **GitHub Flow**. See [`GIT_WORKFLOW.md`](GIT_WORKFLOW.md) for full branch naming conventions and PR guidelines.
* Active development branch: `feat/library-api-skeleton`
* Base branch: `main`
