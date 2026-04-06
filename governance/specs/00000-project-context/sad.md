# System Architecture Document (SAD) - Arrupeteca

## 1. Executive Summary
Arrupeteca is a library management system built with Spring Boot, designed to manage books, authors, and physical copies (items) within a structured hierarchy (buildings, floors, rooms, furniture). It provides a robust REST API for managing the catalog and physical inventory.

## 2. Architectural Goals and Constraints
- **Maintainability**: Clear separation of concerns using a layered architecture (Controller-Service-Persistence).
- **Data Integrity**: Soft delete mechanism (borradoLogico) to prevent accidental data loss.
- **Auditability**: Automatic tracking of creation and modification metadata via an Auditable base class.
- **Consistency**: Standardized API responses using an ApiResponse wrapper and centralized error handling.
- **Scalability**: Stateless REST API design suitable for modern web environments.

## 3. Technology Stack
- **Language**: Java 21 (using Records for DTOs)
- **Framework**: Spring Boot 4.0.2
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: MySQL 8.x
- **Build Tool**: Gradle
- **Mapping**: MapStruct (Entity-DTO transformation, supporting Java Records)
- **Boilerplate Reduction**: Lombok (primarily for JPA Entities)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)

## 4. Architectural Overview
The system follows a classic **Layered Architecture**:

1. Web Layer (Controller): Versioned REST API (/api/v1) handling JSON communication via **Java Records as DTOs**.
2. Service Layer (Service): Interface-based business logic orchestration and transaction management.
3. Persistence Layer (Repository): Spring Data JPA repositories with custom projection support.
4. Domain Layer (Entity): JPA entities (using Lombok) with auditing and soft-delete capabilities.

## 5. Domain Model
Key entities and their relationships:

- Obra: The creative work (abstract).
- Libro: A specific published edition of an Obra.
- EjemplarLibro: A physical copy of a Libro, assigned to a Mueble.
- Autor: Linked to Obra via ObraAutor to support many-to-many relationships with roles (TipoAutoria).
- Location Hierarchy: Edificio -> Planta -> Salon -> Mueble.

### Core Class Diagram (Conceptual)
```text
Obra <1---*> Libro <1---*> EjemplarLibro
Obra <*---*> Autor (via ObraAutor)
EjemplarLibro ---*> Mueble ---*> Salon ---*> Planta ---*> Edificio
```

## 6. API Design and Communication
- RESTful Principles: Resource-based URLs and standard HTTP verbs.
- Response Structure: Every response is wrapped in an ApiResponse<T> containing success, message, and data fields.
- Efficient Retrieval: Uses JPA Projections (e.g., LibroResumen) to minimize data transfer for list views.

## 7. Cross-cutting Concerns
- Error Handling: Managed by GlobalExceptionHandler using @RestControllerAdvice.
- Auditing: Tracks fechaCreacion, fechaModificacion, usuarioCreacion, and usuarioModificacion.
- Soft Delete: Uses a borradoLogico boolean flag to hide records instead of deleting them.

## 8. Development and Deployment
- Local Dev: Gradle-based build; requires MySQL 8.x.
- Testing: TDD approach using JUnit 5 and Mockito.
