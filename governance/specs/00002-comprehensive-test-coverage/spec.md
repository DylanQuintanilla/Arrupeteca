---
feature_branch: "Refactorizacion"
created: "2026-04-12"
input: "Por favor revisa los test. Actualmente hay test sin embargo faltan test de ciertas estrucuturas como de algunos mappers, dto, etc. En general quiero que revises todos los test y que vayas viendo si las clase main consta con su respectivo test en la carpeta test"
spec_type: "technical"
spec_maturity: "draft"
epic_id: "Phase 2 Quality Control"
spec_maturity: "clarified"
---

# Feature Specification: Comprehensive Test Coverage (Cobertura de Pruebas Integral)

**Feature Branch (Rama de Característica)**: `Refactorizacion`  
**Created (Creado)**: 2026-04-12  
**Status (Estado)**: Draft  
**Spec Type (Tipo de Espec)**: technical  
**Spec Maturity (Madurez de la Espec)**: clarified

**Epic ID (ID de Épica)**: Phase 2 Quality Control  
**Product Document (Documento del Producto)**: governance/specs/00000-project-context/prd.md

## Problem Statement (Planteamiento del Problema)

Actualmente, el proyecto Arrupeteca cuenta con una base sólida de pruebas para controladores, servicios y repositorios, pero presenta lagunas críticas en la cobertura de Mappers, DTOs y estructuras de soporte. La falta de pruebas en los Mappers (MapStruct) puede ocultar errores de transformación de datos, mientras que la ausencia de pruebas en los DTOs (Requests) pone en riesgo la estabilidad del contrato de la API y la validación de entrada. Sin una cobertura integral al 100%, no se puede garantizar la integridad del sistema ante futuras refactorizaciones.

## Scope (Alcance)

### Included (Incluido)

- **Pruebas de Mappers**: Implementación de pruebas unitarias para todos los mappers de MapStruct faltantes.
- **Pruebas de DTOs**: Implementación de pruebas de serialización/deserialización y validación para todas las clases `Request`.
- **Pruebas de Estructuras de Soporte**: Cobertura para `ApiResponse`, `JpaConfig` y la entidad `ObraAutor`.
- **Verificación de Paridad**: Asegurar que cada clase en `src/main` con lógica o estado tenga su correspondiente prueba.
- **Organización de Pruebas**: Los archivos de prueba se ubicarán en el directorio estándar de Gradle `src/test/java/`, manteniendo la paridad de paquetes con el código fuente.
- **Pruebas de Proyecciones**: Validar que las interfaces de proyección se comporten correctamente cuando se utilizan en mappers o repositorios.

### Excluded (Excluido)

- Refactorización de lógica de negocio en servicios o controladores (solo se añaden pruebas).
- Pruebas de integración E2E con bases de datos externas (se mantiene el uso de H2).

### Edge Cases & Boundaries (Casos de Borde y Límites)

- Mapeo de valores `null` en mappers.
- Deserialización de JSON con campos faltantes o tipos de datos incorrectos en DTOs.
- Comportamiento de `ApiResponse` con diferentes tipos de datos genéricos.
- Persistencia de relaciones complejas en `ObraAutor`.

## Technical Objectives (Objetivos Técnicos)

### OBJ1: Cobertura Completa de Mappers (P1)

Garantizar que todas las transformaciones entre Entidades, DTOs y Proyecciones sean precisas, incluyendo la lógica personalizada.

**Why this priority (Por qué esta prioridad)**: Los mappers son el puente entre capas; un error aquí corrompe los datos expuestos o persistidos.

**Rationale (Justificación)**: Uso de JUnit 5 para validar el mapeo campo a campo, expresiones de MapStruct (`@Mapping(expression = "...")`) y mapeos complejos.

**Deliverables (Entregables)**:
- Clases de prueba para: `EdificioMapper`, `LibroMapper`, `MuebleMapper`, `ObraMapper`, `PlantaMapper`, `SalonMapper`, `EjemplarLibroMapper`.

**Validation Criteria (Criterios de Validación)**:
1. **Given** una Entidad con datos completos y complejos, **When** se mapea a DTO, **Then** todos los campos (incluyendo expresiones personalizadas) deben coincidir.
2. **Given** un DTO, **When** se mapea a Entidad, **Then** la identidad y los campos básicos deben preservarse correctamente.

### OBJ2: Cobertura de DTOs y Contratos API (P1)

Validar la serialización Jackson y las anotaciones de validación (Bean Validation) en las solicitudes.

**Why this priority (Por qué esta prioridad)**: Define el contrato externo de la aplicación y garantiza la integridad de la entrada de datos.

**Rationale (Justificación)**: Uso de `@JsonTest` para la serialización y un `Validator` para verificar anotaciones como `@NotBlank`, `@Size`, etc.

**Deliverables (Entregables)**:
- Clases de prueba para todas las clases `*Request`.
- Clase de prueba para `ApiResponse`.

**Validation Criteria (Criterios de Validación)**:
1. **Given** un objeto Request, **When** se serializa a JSON, **Then** las claves deben coincidir con la especificación.
2. **Given** un JSON válido, **When** se deserializa, **Then** el objeto resultante debe tener los valores correctos.
3. **Given** un objeto Request con datos inválidos, **When** se valida, **Then** debe detectar las violaciones de restricciones esperadas.

### OBJ3: Cierre de Brechas en Entidades y Configuración (P2)

Completar las pruebas para clases aisladas como `ObraAutor` y `JpaConfig`.

**Why this priority (Por qué esta prioridad)**: Alcanzar el objetivo de cobertura del 100% definido en el plan maestro.

**Rationale (Justificación)**: Asegurar que incluso las clases de configuración y entidades de relación tengan validación.

**Deliverables (Entregables)**:
- `ObraAutorTest`.
- `JpaConfigTest`.

**Validation Criteria (Criterios de Validación)**:
1. **Given** una instancia de `ObraAutor`, **When** se validan sus campos, **Then** debe cumplir con las restricciones de JPA.
2. **Given** el contexto de Spring, **When** se carga `JpaConfig`, **Then** los beans de auditoría deben estar presentes.

## Technical Constraints (Restricciones Técnicas)

- **Tecnología**: JUnit 5, Mockito, AssertJ, `@JsonTest`, `@DataJpaTest`.
- **Rendimiento**: Las pruebas de mappers y DTOs deben ser unitarias (sin cargar el contexto completo de Spring si es posible) para mantener la velocidad de la suite.
- **Layout**: Los archivos de prueba deben organizarse en el directorio estándar de Gradle `src/test/java/`.

## Integration Points (Puntos de Integración)

- **IP-001**: Los mappers dependen de las implementaciones generadas por MapStruct durante la compilación.
- **IP-002**: Las pruebas de DTOs dependen de la configuración global de Jackson en `application.properties`.

## Requirements (Requerimientos)

### Technical Requirements (Requerimientos Técnicos)

- **TR-001**: El sistema DEBE tener una clase de prueba para cada Mapper de MapStruct.
- **TR-002**: El sistema DEBE tener una clase de prueba para cada DTO de solicitud (`*Request`) que valide la serialización y las restricciones de Bean Validation.
- **TR-003**: El sistema DEBE incluir pruebas para la estructura genérica `ApiResponse`.
- **TR-004**: Todas las pruebas nuevas DEBEN seguir el patrón de nombrado `[ClassName]Test`.
- **TR-005**: Las pruebas de persistencia faltantes DEBEN usar `@DataJpaTest` y H2.
- **TR-006**: Los archivos de prueba DEBEN ubicarse en el directorio estándar de Gradle `src/test/java/`.

## Key Entities (Entidades Clave)

- **ObraAutor**: Representa la relación muchos a muchos entre Obras y Autores, incluyendo el tipo de autoría.

## Assumptions & Risks (Supuestos y Riesgos)

### Assumptions (Supuestos)

- **Asunción 1**: La mayoría de los DTOs son POJOs simples donde el foco principal es la serialización JSON.
- **Asunción 2**: MapStruct genera código predecible, pero se requiere validación para campos personalizados o expresiones.

### Risks (Riesgos)

- **[Risk 1]** *(likelihood: low, impact: medium)*: Algunas proyecciones pueden ser difíciles de instanciar manualmente para pruebas de mappers sin usar proxies de Spring.

## Implementation Signals (Señales de Implementación)

- `NEW-ENTITY` — Cobertura para `ObraAutor`.
- `NEW-CONFIG` — Cobertura para `JpaConfig`.

## Success Criteria (Criterios de Éxito)

### Measurable Outcomes (Resultados Medibles)

- **SC-001** [OBJ1]: Cobertura de líneas del 100% en el paquete `com.arrupeteca.mapper` (excluyendo implementaciones generadas automáticamente como `*MapperImpl.java`).
- **SC-002** [OBJ2]: Cobertura de líneas del 100% en el paquete `com.arrupeteca.dto`.
- **SC-003** [OBJ3]: Todas las entidades en `com.arrupeteca.persistence.entity` cuentan con su respectivo test.
- **SC-004** [OBJ1-OBJ3]: El build de Gradle finaliza exitosamente con todos los tests pasando.

## Glossary (Glosario)

| Term (Término) | Definition (Definición) |
|------|------------|
| Mapper | Componente encargado de transformar datos entre Entidades (DB) y DTOs (API). |
| DTO | Data Transfer Object; objeto usado para transferir datos entre procesos. |
| @JsonTest | Anotación de Spring Boot para probar específicamente la serialización/deserialización JSON. |

## Clarifications (Aclaraciones)

### Session 2026-04-12
- Q: La política de gobernanza §VI indica ENFORCE_SRC_ROOT y que las pruebas están co-localizadas en directorios __tests__/. En un proyecto Java/Gradle, el estándar es src/test/java/. ¿Cómo debemos proceder? -> A: Opción B: Estándar de Gradle en `src/test/java/...`. Se mantiene la estructura convencional de Java/Spring Boot para compatibilidad con el ecosistema Gradle.
- Q: Para las pruebas de DTOs, ¿qué profundidad deben tener? -> A: Opción A: Jackson + Bean Validation. Se validará la serialización JSON y que las anotaciones de validación (@NotBlank, @Size, etc.) funcionen correctamente.
- Q: En las pruebas de mappers, ¿qué alcance debe tener la validación? -> A: Opción A: Paridad de Campos + Expresiones Personalizadas. Se validará que MapStruct mapee correctamente tanto los campos directos como los configurados mediante expresiones o lógica compleja.
- Q: Para el objetivo de cobertura del 100%, ¿debe incluirse el código generado por MapStruct? -> A: Opción A: Solo Código de Fuente (Interfaces/DTOs). Se excluirán las implementaciones generadas (`*MapperImpl.java`) del reporte de cobertura para enfocarse en la lógica definida manualmente.

## Compliance Check (Control de Cumplimiento)

### Instructions Check Report (Reporte de Verificación de Instrucciones)
**Target (Objetivo)**: spec.md
**Status (Estado)**: PASS

| Principle (Principio) | Verdict (Veredicto) | Notes (Notas) |
|-----------|---------|-------|
| Test-First | PASS | Define objetivos técnicos específicos para pruebas de mappers, DTOs y entidades. |
| Simplicity | PASS | Objetivos claros y sin sobre-ingeniería; se enfoca en completar la cobertura. |
| Agent Output Style | PASS | Salida concisa, sin preámbulos y siguiendo la estructura de la plantilla. |
| Language Policy | PASS | Encabezados bilingües (Inglés/Español) con contenido en Español según la política. |
| Governance | PASS | El archivo reside en `governance/specs/` y sigue la convención de nomenclatura. |
| Tech Stack | PASS | Alineado con Java 21 y Spring Boot 4.0.2. |
