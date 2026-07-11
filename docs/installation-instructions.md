# Installation Instructions

## Prerequisites

Before running the project, ensure the following software is installed:

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Git

## Clone the Repository

```bash
git clone <repository-url>
cd <project-directory>
```

## Configure Environment Variables

Create a `.env` file in the project root and provide the required environment variables.

See [ENVIRONMENT_VARIABLES.md](environment-variables.md) for details.

## Start the Database

```bash
docker compose up -d
```

## Run the Application

```bash
mvn spring-boot:run
```

The application will start at:

```
http://localhost:8080
```

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```