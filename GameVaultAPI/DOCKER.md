# Running the GameVault API with Docker

This guide explains how to build and run the Spring Boot API using the `Dockerfile` in this directory. Docker packages the application and the Java runtime into an image, then runs that image in a container. You do not need Java or Gradle installed on your computer, but Docker must be installed and its daemon must be running.

## Prerequisites

- Docker Engine or Docker Desktop installed and running.
- Host port `8080` available, unless you choose another host port when starting the container.

Check that Docker is available:

```bash
docker --version
docker info
```

## 1. Open the API directory

Run the following from the backend repository root:

```bash
cd GameVaultAPI
```

The Dockerfile, Gradle wrapper, build configuration, and source code are all in this directory. Docker uses the current directory as its build context in the next step.

## 2. Build the image

```bash
docker build -t gamevault-api:latest .
```

The final `.` means “use the current directory as the build context.” The `-t` option assigns the image the name `gamevault-api` and tag `latest`.

The Dockerfile has two stages:

1. A JDK 25 builder stage copies the project files and runs Gradle to create the Spring Boot executable JAR.
2. A JRE 25 runtime stage copies only that JAR into a fresh image and runs the application.

The Docker build runs `bootJar` with `-x test`, so it does not execute the test suite. Run tests separately before building when needed:

```bash
./gradlew test
```

The `.dockerignore` file excludes local build output, Gradle caches, IDE files, and Git data from the build context. Docker may reuse cached build layers on later builds; use `docker build --no-cache -t gamevault-api:latest .` to rebuild without cache.

To confirm that the image exists:

```bash
docker image ls gamevault-api
```

## 3. Start a container

```bash
docker run -d --name gamevault-backend -p 8080:8080 gamevault-api:latest
```

This starts a container in the background:

- `--name gamevault-backend` assigns it a recognizable name.
- `-d` runs it detached from the terminal.
- `-p 8080:8080` forwards host port `8080` to container port `8080`.
- `gamevault-api:latest` identifies the image to run.

`EXPOSE 8080` in the Dockerfile documents the API's container port, but it does not publish that port on your computer. The `-p` option performs the host-to-container port mapping.

You can then access the API at `http://localhost:8080`. For example:

```bash
curl http://localhost:8080/api/library
```

The initial response should contain the seeded games Hades, Elden Ring, and Hollow Knight. To add a game:

```bash
curl -X POST http://localhost:8080/api/library \
  -H "Content-Type: application/json" \
  -d '{"title":"Celeste","status":"PLAYING"}'
```

### If host port 8080 is already in use

Choose another host port, while leaving the container port as `8080`:

```bash
docker run -d --name gamevault-backend -p 8081:8080 gamevault-api:latest
```

In this case, send requests to `http://localhost:8081`.

## 4. Check status and logs

List running containers:

```bash
docker ps
```

View application logs:

```bash
docker logs gamevault-backend
```

Follow new log output as it appears:

```bash
docker logs -f gamevault-backend
```

Press `Ctrl+C` to stop following the logs; this does not stop the container.

If the container exits or the endpoint is unavailable, inspect its logs:

```bash
docker ps -a
docker logs gamevault-backend
```

Common causes include a build or startup error, or port `8080` already being occupied on the host. If the container name is already in use, stop and remove the old container before creating another one with that name.

## 5. Stop and remove the container

```bash
docker stop gamevault-backend
docker rm gamevault-backend
```

Stopping halts the running application. Removing deletes the stopped container. The image remains available locally, so you can start a new container from it without rebuilding.

To remove the image as well, after removing its containers:

```bash
docker rmi gamevault-api:latest
```

## Notes

This simplified Docker setup is intentionally minimal so it is easy to understand and works reliably for a first project. It includes the basic two-stage build and runtime setup, but it does not include extra features like a health check, a non-root user, or curl installation. Those are useful production improvements, but they are not required just to run the application in Docker.
