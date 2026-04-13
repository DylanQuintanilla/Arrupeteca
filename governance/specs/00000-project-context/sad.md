# System Architecture Document (SAD) - Arrupeteca (Documento de Arquitectura del Sistema)

## 1. Executive Summary (Resumen Ejecutivo)
Arrupeteca es un sistema de gestión de bibliotecas construido con Spring Boot 4.0.2 y Java 21. El sistema utiliza una arquitectura por capas para gestionar el catálogo intelectual, el inventario físico y flujos operativos con altos estándares de calidad y optimización de datos.

## 2. Architectural Goals and Constraints (Objetivos y Restricciones Arquitectónicas)
- **Maintainability (Mantenibilidad)**: Uso de **Java Records** para DTOs y mapeo con MapStruct.
- **Data Integrity (Integridad de Datos)**: Borrado lógico en cascada y bloqueo optimista mediante `@Version`.
- **Optimization (Optimización)**: Gestión eficiente de relaciones N+1 mediante Batch Fetching y consultas optimizadas con `@EntityGraph`.
- **Reliability (Fiabilidad)**: Manejo de errores centralizado con SLF4J logging y excepciones de negocio personalizadas.

## 3. Technology Stack (Stack Tecnológico)
- **Language (Lenguaje)**: Java 21 (Records para DTOs como estándar).
- **Framework**: Spring Boot 4.0.2.
- **Persistence (Persistencia)**: Spring Data JPA / Hibernate con **HikariCP** y **Batch Fetching (size=100)**.
- **Database (Base de Datos)**: MySQL 8.x.
- **Build Tool (Herramienta de Construcción)**: Gradle.
- **Mapping (Mapeo)**: MapStruct (Transformación Entity <-> Record).
- **Boilerplate Reduction**: Lombok (Principalmente para Entidades JPA).
- **Validation (Validación)**: Jakarta Bean Validation (Hibernate Validator).
- **Logging**: SLF4J (Lombok `@Slf4j`).
- **Messaging (Notificaciones)**: Spring Boot Starter Mail (SMTP) para notificaciones por correo.

## 4. Domain Model and Database Schema (Modelo de Dominio y Esquema de BD)

### 4.1 Catalog Entities (Entidades del Catálogo)
- **Obra:** Obra intelectual. Relacionada M:M con **Género** (`Obra_Genero`) y **Categoría** (`Obra_Categoria`).
- **Autor:** Vinculado a un **GeneroPrincipal**. Relación M:M con Obra (Obra_Autor).
- **Libro:** Edición de una Obra. Clasificado por Grado, Ciclo, Idioma, Editorial y Formato.
- **Etiqueta (Tag):** Entidad para agrupamiento transversal (ej. "Tecnología"). Relación M:M con Libro.
- **Popularidad:** Atributo incremental en Libros basado en la frecuencia de préstamos.

### 4.2 Inventory and Location (Inventario y Ubicación)
- **EjemplarLibro:** Copia física única. Rastreable por **ID Propio** (Búsqueda Global para QR) y vinculado a un **Mueble**.
- **Mueble:** Estante físico. Incluye un campo **Estado** para registrar su condición física.

### 4.3 Operational Flow Entities (Entidades de Flujos Operativos)
- **Prestamos, Transferencias y Donaciones:** Entidades para la gestión de solicitudes y movimientos operativos (Fase 3).

## 5. Key Technical Standards (Estándares Técnicos Clave)

### 5.1 API Response Structure (Estructura de Respuesta de API)
```json
{
  "timestamp": "ISO-8601-Date",
  "status": 200,
  "error": null,
  "message": "Mensaje informativo",
  "path": "/api/v1/recurso",
  "data": { ... }
}
```

### 5.2 Error Handling and Logging (Manejo de Errores y Logs)
- **GlobalExceptionHandler**: Captura excepciones de negocio y errores graves (500).
- **Business Exceptions**: Uso de `ReglaNegocioException`.
- **Logging**: Registro de errores graves con StackTrace mediante `@Slf4j`.

### 5.3 Data Persistence Strategy (Estrategia de Persistencia)
- **Soft Delete in Cascade**: Propagación del borrado lógico de Obra a sus Libros y Ejemplares.
- **Optimistic Locking**: Campo `@Version Integer version` en `Auditable.java`.
- **Query Optimization**: Uso de `@EntityGraph` para relaciones ToOne y Batch Fetching para colecciones.

## 6. Development and Quality Policy (Política de Desarrollo y Calidad)
- **TDD Mandatory**: Cobertura del 100% mediante JUnit 5 y Mockito.
- **Clean Code**: Eliminar anotaciones redundantes como `@Repository`.

## Project Context Baseline Updates (Actualizaciones de la Línea Base del Contexto del Proyecto)

### 2026-04-12 | Estrategia de Pruebas de Mappers y DTOs
- **MapStruct Testing**: Se adopta el estándar de probar los mappers generados por MapStruct mediante pruebas unitarias de JUnit 5 sin cargar el contexto de Spring, validando el mapeo bidireccional y el manejo de nulos. Se recomienda usar inyección manual para mappers con dependencias.
- **DTO Serialization & Validation**: Se estandariza el uso de `@JsonTest` de Spring Boot junto con `@Import(ValidationAutoConfiguration.class)` para validar la serialización de Jackson y las restricciones de Bean Validation en todas las clases `Request`.
- **JaCoCo Exclusion**: Se establece la política de excluir código generado automáticamente (como los mappers de MapStruct) del reporte de cobertura mediante la configuración del procesador de MapStruct (`addGeneratedAnnotation=true`) para mantener el foco en la lógica escrita a mano.

