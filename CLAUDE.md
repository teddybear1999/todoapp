# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Running the Application
- **Start application**: `./mvnw spring-boot:run` or `mvn spring-boot:run`
- **Start database**: `docker-compose up -d` (starts MySQL container)
- **Stop database**: `docker-compose down`

### Testing and Quality
- **Run tests**: `./mvnw test` or `mvn test`
- **Build project**: `./mvnw clean package` or `mvn clean package`
- **Run single test**: `./mvnw test -Dtest=TodoappApplicationTests`

### Traffic Generation and Testing
- **Generate API traffic**: `./scripts.sh` (contains various curl commands for load testing)
- **Generate traffic script**: `./generatetraffic.sh`
- **Error testing script**: `./errorscript.sh`

## Architecture Overview

This is a Spring Boot REST API application for task management with the following structure:

### Core Components
- **Model**: `Task` entity with JPA annotations for database persistence
- **Repository**: `TaskRepository` extends JpaRepository for data access
- **Service**: `TaskService` contains business logic for task operations
- **Controller**: `TaskController` provides REST endpoints for task management

### Database Setup
- Uses MySQL 8.0 via Docker Compose
- Database name: `taskdb`
- Connection details in `application.yml`
- Initialization scripts in `queries.sql`

### API Endpoints
- `GET /tasks/{id}` - Get task by ID
- `GET /tasks/completed` - Get all completed tasks
- `GET /tasks/uncompleted` - Get all uncompleted tasks
- `POST /tasks` - Create new task
- `PUT /tasks/{id}/completion` - Update task completion status
- `PUT /tasks/{id}/description` - Update task description
- `DELETE /tasks/{id}` - Delete task
- `GET /tasks/randomBoolean` - Returns random boolean (utility endpoint)
- `GET /tasks/chinese` - Chinese language test endpoint with custom exceptions

### Package Structure
```
com.simplecrud.todoapp/
├── TodoappApplication.java (main class)
├── controller/TaskController.java
├── model/Task.java
├── repository/TaskRepository.java
├── service/TaskService.java
└── exceptions/ (custom exception classes including Chinese language exceptions)
```

### Technology Stack
- Spring Boot 2.7.14
- Java 11
- Spring Data JPA
- MySQL 8.0
- Maven build system

### Notable Features
- Custom Chinese language exception handling
- Load testing scripts with extensive API calls
- Docker-based database setup
- Hibernate timestamps for created/updated tracking