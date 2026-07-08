# System Architecture Document (SAD) - Arrupeteca (Documento de Arquitectura del Sistema)

## 1. Executive Summary (Resumen Ejecutivo)
Arrupeteca es un sistema de gestión de bibliotecas construido con Spring Boot 3.4.2 y Java 21. El sistema utiliza una arquitectura por capas para gestionar el catálogo intelectual, el inventario físico y flujos operativos con altos estándares de calidad y optimización de datos.

## 2. Architectural Goals and Constraints (Objetivos y Restricciones Arquitectónicas)
- **Maintainability (Mantenibilidad)**: Uso de **Java Records** para DTOs y mapeo con MapStruct.
- **Data Integrity (Integridad de Datos)**: Borrado lógico en cascada y bloqueo optimista mediante `@Version`.
- **Entity Integrity (Integridad de Entidades)**: Uso obligatorio de `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` incluyendo únicamente el campo `id` para evitar errores de recursión y problemas de circularidad en Hibernate/JPA.
- **Optimization (Optimización)**: Gestión eficiente de relaciones N+1 mediante Batch Fetching y consultas optimizadas con `@EntityGraph`.
- **Reliability (Fiabilidad)**: Manejo de errores centralizado con SLF4J logging y excepciones de negocio personalizadas.
- **Quality Standards (Estándares de Calidad)**: Cumplimiento estricto de reglas de estilo Checkstyle y cobertura de pruebas del 100% (JaCoCo).

## 3. Technology Stack (Stack Tecnológico)
- **Language (Lenguaje)**: Java 21 (Records para DTOs de respuesta y algunos de request; el resto de DTOs de request se migrarán en la Fase 2).
- **Framework**: Spring Boot 3.4.2.
- **Persistence (Persistencia)**: Spring Data JPA / Hibernate con **HikariCP** y **Batch Fetching (size=100)**.
- **Database (Base de Datos)**: MySQL 8.x.
- **Build Tool (Herramienta de Construcción)**: Gradle.
- **Quality & Style (Calidad y Estilo)**: 
    - **Checkstyle**: Validación de reglas de estilo (5 Golden Rules).
    - **JaCoCo**: Verificación de cobertura de pruebas (Target: 100%).
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
- **Etiqueta (Tag) (A futuro):** Entidad para agrupamiento transversal (ej. "Tecnología"). Relación M:M con Libro (Pendiente de implementar en Fase 2).
- **Popularidad (A futuro):** Atributo incremental en Libros basado en la frecuencia de préstamos (Pendiente de implementar en Fase 2).

### 4.2 Inventory and Location (Inventario y Ubicación)
- **EjemplarLibro:** Copia física única. Rastreable por **ID Propio** (Búsqueda Global para QR) y vinculado a un **Mueble** (y a una Donación a futuro).
- **Mueble:** Estante físico. Incluye un campo **Estado** para registrar su condición física (Pendiente de implementar en Fase 2).

### 4.3 Operational Flow Entities (Entidades de Flujos Operativos)
- **Prestamos, Transferencias y Donaciones:** Entidades para la gestión de solicitudes y movimientos operativos (Fase 3).

## 5. Key Technical Standards (Estándares Técnicos Clave)

### 5.1 Style Standards (Estándares de Estilo - Checkstyle)
Las reglas de estilo están definidas y validadas mediante `config/checkstyle/checkstyle.xml`:
1. **Line Length (Longitud de Línea)**: Máximo 140 caracteres, ignorando sentencias de package, import y URLs.
2. **Imports**:
   * Prohibido el uso de comodines (`*`) en importaciones mediante `AvoidStarImport`.
   * Prohibido tener importaciones no utilizadas mediante `UnusedImports`.
3. **Control Structures**: Uso obligatorio de llaves `{}` para bloques de control (`if`, `else`, `for`, `while`, `do`) mediante `NeedBraces`.
4. **Members Separator**: Uso de `EmptyLineSeparator` para exigir una línea en blanco entre miembros de la clase (métodos, constructores, etc.), aunque se permite omitir la línea en blanco consecutiva entre atributos/campos.
5. **One Statement Per Line**: Una sola instrucción o sentencia por línea de código.
6. **Integrity checks**: Regla `EqualsHashCode` que obliga a que si se sobrescribe `equals()`, también se sobrescriba `hashCode()`.
7. **Nomenclatura y Llaves**: Verificación estricta de nombres (`PackageName`, `TypeName`, `MemberName`, etc.) y formato de llaves (`LeftCurly`, `RightCurly`, `WhitespaceAround`).

### 5.2 API Response Structure (Estructura de Respuesta de API)

Actualmente se utiliza una estructura simplificada de respuesta:
```json
{
  "exito": true,
  "mensaje": "Mensaje informativo",
  "data": { ... }
}
```

La estructura planificada para implementarse al final de la Fase 2 es:
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

### 5.3 Error Handling and Logging (Manejo de Errores y Logs)
- **GlobalExceptionHandler**: Captura excepciones de negocio y errores graves (500).
- **Business Exceptions**: Uso de `ValidacionNegocioException`.
- **Logging**: Registro de errores graves con StackTrace mediante `@Slf4j`.

### 5.4 Data Persistence Strategy (Estrategia de Persistencia)
- **Soft Delete in Cascade (A futuro)**: Propagación del borrado lógico de Obra a sus Libros y Ejemplares (Pendiente de implementar en Fase 2).
- **Optimistic Locking**: Campo `@Version` (ej. `@Version private Long version`) definido a nivel de entidad en las clases transaccionales que lo requieran (ej. `Autor`), en lugar de la clase base `Auditable`, para evitar imponerlo en catálogos simples como `Edificio` o `Pais`.
- **Query Optimization**: Uso de `@EntityGraph` para relaciones ToOne y Batch Fetching para colecciones.

### 5.5 Validation Strategy (Estrategia de Validación)
Para mantener una arquitectura limpia y de alto rendimiento (Separación de Responsabilidades), se adopta el estándar de dividir las validaciones:
* **API Layer (Request DTOs)**: Las validaciones de formato de entrada, obligatoriedad y límites de negocio (`@NotBlank`, `@Size`, `@AssertTrue`, etc.) residen exclusivamente en los Request DTOs (ej. `AutorRequest.java`). Esto asegura el filtrado temprano (fail-fast) a nivel de controlador.
* **Database Layer (JPA Entities)**: Las entidades de persistencia no contienen anotaciones de Bean Validation de Java. En su lugar, se definen restricciones físicas utilizando las propiedades de la columna en JPA (ej. `@Column(nullable = false, length = 100)`).
* **Beneficios**:
  * **Rendimiento**: Evita que Hibernate ejecute validaciones redundantes antes de persistir o actualizar datos en base de datos.
  * **Mantenibilidad**: Los tests unitarios y de integración de repositorios no se ven entorpecidos por validaciones lógicas restrictivas de Java.

### 5.6 Cache Strategy (Estrategia de Caché)
Para optimizar las consultas repetitivas de alta frecuencia y reducir la carga de peticiones a la base de datos:
* **Uso de `@Cacheable`**: Se aplica de forma selectiva en la capa de servicios para endpoints de lectura cuyos datos no cambian constantemente (ej. detalles de autores u obras) utilizando la memoria de la aplicación (Caché local).
* **Deserialización con Jackson**: Uso de las anotaciones `@Jacksonized` junto con `@Builder` en DTOs/Records para permitir que Jackson realice la deserialización de manera fluida sin requerir constructores por defecto.

## 6. Development and Quality Policy (Política de Desarrollo y Calidad)
- **TDD Mandatory**: Cobertura del 100% mediante JUnit 5 y Mockito.
- **Checkstyle Enforcement**: El build fallará (severidad warning/error) si hay violaciones de estilo.
- **Clean Code (En progreso)**: Eliminar anotaciones redundantes como `@Repository`.

## Project Context Baseline Updates (Actualizaciones de la Línea Base del Contexto del Proyecto)

### 2026-04-12 | Estrategia de Pruebas de Mappers y DTOs
- **MapStruct Testing**: Se adopta el estándar de probar los mappers generados por MapStruct mediante pruebas unitarias de JUnit 5 sin cargar el contexto de Spring, validando el mapeo bidireccional y el manejo de nulos. Se recomienda usar inyección manual para mappers con dependencias.
- **DTO Serialization & Validation**: Se estandariza el uso de `@JsonTest` de Spring Boot junto con `@Import(ValidationAutoConfiguration.class)` para validar la serialización de Jackson y las restricciones de Bean Validation en todas las clases `Request`.
- **JaCoCo Exclusion**: Se establece la política de excluir código generado automáticamente (como los mappers de MapStruct) del reporte de cobertura mediante la configuración del procesador de MapStruct (`addGeneratedAnnotation=true`) para mantener el foco en la lógica escrita a mano.

### 2026-04-24 | Estándar de Respuesta Summary vs Detail y Ubicación Física
- **Patrón Summary vs Detail**: Se establece el uso obligatorio de `[Entidad]Resumen` (Proyección o Record ligero) para listados y `[Entidad]DetalleResponse` (DTO completo) para consultas por ID. Los listados no deben incluir colecciones para optimizar el payload.
- **Concatenación de Ubicación**: La lógica de presentación de la ubicación física (ej. 'Mueble - Salón - Planta') debe residir exclusivamente en los Mappers de MapStruct mediante métodos `default`, manteniendo las Entidades y DTOs limpios de lógica de formato.
- **Optimización JPA Proactiva**: Los Repositorios deben usar `@EntityGraph` para cargar relaciones ManyToOne/OneToOne en listados (`Resumen`), mientras que las colecciones en vistas de detalle deben confiar en el `batch_fetch_size` configurado para evitar el problema N+1.

## 7. Documentation Policy (Política de Documentación)
Para mantener la legibilidad y facilitar la integración con otras aplicaciones:
* **Autodocumentación de API (OpenAPI)**: Los controladores (`Controller`) deben estar anotados con las anotaciones de Springdoc OpenAPI (`@Operation`, `@ApiResponse`, `@Parameter`) para detallar los códigos de estado HTTP y contratos de entrada/salida. Específicamente en listados paginados, se debe utilizar obligatoriamente `@ParameterObject` junto a `@PageableDefault(size = 20, sort = "nombre")` sobre el parámetro `Pageable pageable` para que Swagger UI exponga correctamente los parámetros de consulta individuales de la paginación.

* **Documentación Externa**: Las guías y estándares arquitectónicos, de diseño de clases, y del dominio del sistema deben documentarse de forma externa en archivos markdown (dentro de `skill-propias/`) para evitar ruido visual de comentarios en el código fuente.
* **Gobernanza**: Cualquier cambio de arquitectura, base de datos o del stack tecnológico debe sincronizarse inmediatamente en este documento (`sad.md`) y en el `prd.md` tras obtener la aprobación correspondiente.

