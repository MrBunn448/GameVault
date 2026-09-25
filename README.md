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

## 5. Automated Testing

Run the automated test suite natively:
```bash
cd GameVaultAPI
./gradlew test
```

Or run Postman / Newman integration tests:
```bash
npx newman run ../../Documentation/Postman/GameVault_Sprint1_Collection.json --environment ../../Documentation/Postman/GameVault_Sprint1_Environment.json
```

---

## 6. Branching & Git Strategy

Development follows **GitHub Flow**. See [`GIT_WORKFLOW.md`](GIT_WORKFLOW.md) for full branch naming conventions and PR guidelines.
* Active development branch: `feat/library-api-skeleton`
* Base branch: `main`
