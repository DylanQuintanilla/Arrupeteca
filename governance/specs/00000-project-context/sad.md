# System Architecture Document (SAD) - Arrupeteca (Documento de Arquitectura del Sistema)

## 1. Executive Summary (Resumen Ejecutivo)
Arrupeteca es un sistema de gestión de bibliotecas construido con Spring Boot, diseñado para gestionar libros, autores y copias físicas (items) dentro de una jerarquía estructurada (edificios, plantas, salones, muebles). Proporciona una API REST robusta para gestionar el catálogo y el inventario físico.

## 2. Architectural Goals and Constraints (Objetivos y Restricciones Arquitectónicas)
- **Maintainability (Mantenibilidad)**: Clara separación de responsabilidades mediante una arquitectura por capas (Controller-Service-Persistence).
- **Data Integrity (Integridad de Datos)**: Mecanismo de borrado suave (borradoLogico) para evitar la pérdida accidental de datos.
- **Auditability (Auditabilidad)**: Seguimiento automático de metadatos de creación y modificación a través de una clase base Auditable.
- **Consistency (Consistencia)**: Respuestas de API estandarizadas mediante un envoltorio ApiResponse y manejo de errores centralizado.
- **Scalability (Escalabilidad)**: Diseño de API REST sin estado (stateless) adecuado para entornos web modernos.

## 3. Technology Stack (Stack Tecnológico)
- **Language (Lenguaje)**: Java 21 (usando Records para DTOs)
- **Framework**: Spring Boot 4.0.2
- **Persistence (Persistencia)**: Spring Data JPA / Hibernate
- **Database (Base de Datos)**: MySQL 8.x
- **Build Tool (Herramienta de Construcción)**: Gradle
- **Mapping (Mapeo)**: MapStruct (transformación Entity-DTO, soportando Java Records)
- **Boilerplate Reduction (Reducción de Código Repetitivo)**: Lombok (principalmente para Entidades JPA)
- **Validation (Validación)**: Jakarta Bean Validation (Hibernate Validator)

## 4. Architectural Overview (Visión General de la Arquitectura)
El sistema sigue una **Layered Architecture (Arquitectura por Capas)** clásica:

1. Web Layer (Controller): API REST versionada (/api/v1) que maneja la comunicación JSON a través de **Java Records como DTOs**.
2. Service Layer (Service): Orquestación de lógica de negocio basada en interfaces y gestión de transacciones.
3. Persistence Layer (Repository): Repositorios Spring Data JPA con soporte para proyecciones personalizadas.
4. Domain Layer (Entity): Entidades JPA (usando Lombok) con capacidades de auditoría y borrado suave.

## 5. Domain Model (Modelo de Dominio)
Entidades clave y sus relaciones:

- Obra: El trabajo creativo (abstracto).
- Libro: Una edición publicada específica de una Obra.
- EjemplarLibro: Una copia física de un Libro, asignada a un Mueble.
- Autor: Vinculado a Obra a través de ObraAutor para soportar relaciones de muchos-a-muchos con roles (TipoAutoria).
- Location Hierarchy (Jerarquía de Ubicación): Edificio -> Planta -> Salon -> Mueble.

### Core Class Diagram (Conceptual) (Diagrama de Clases Principal)
```text
Obra <1---*> Libro <1---*> EjemplarLibro
Obra <*---*> Autor (via ObraAutor)
EjemplarLibro ---*> Mueble ---*> Salon ---*> Planta ---*> Edificio
```

## 6. API Design and Communication (Diseño de API y Comunicación)
- RESTful Principles: URLs basadas en recursos y verbos HTTP estándar.
- Response Structure (Estructura de Respuesta): Cada respuesta se envuelve en un ApiResponse<T> que contiene los campos success, message y data.
- Efficient Retrieval (Recuperación Eficiente): Utiliza proyecciones JPA (ej. LibroResumen) para minimizar la transferencia de datos en las vistas de lista.

## 7. Cross-cutting Concerns (Aspectos Transversales)
- Error Handling (Manejo de Errores): Gestionado por GlobalExceptionHandler usando @RestControllerAdvice.
- Auditing (Auditoría): Rastrea fechaCreacion, fechaModificacion, usuarioCreacion y usuarioModificacion.
- Soft Delete (Borrado Suave): Utiliza una bandera booleana borradoLogico para ocultar registros en lugar de eliminarlos.

## 8. Development and Deployment (Desarrollo y Despliegue)
- Local Dev: Construcción basada en Gradle; requiere MySQL 8.x.
- Testing (Pruebas): Enfoque TDD usando JUnit 5 y Mockito.
