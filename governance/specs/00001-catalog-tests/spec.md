---
spec_type: technical
epic_id: 00001
spec_maturity: clarified
---

# Technical Specification (Especificación Técnica) - Catalog Testing (Pruebas de Catálogo)

## Problem Statement (Planteamiento del Problema)
Actualmente, las clases de catálogo (`Categoria`, `Edificio`, `Editorial`, `Formato`, `Genero`, `Idioma`, `Nacionalidad`, `Pais`, `TipoAutoria`) han sido implementadas sin una suite de pruebas automatizadas y con inconsistencias en su diseño (algunas con paginación y otras con listas). Esto viola la política de "Pruebas Primero" (TDD) y pone en riesgo la integridad de los datos. Sin estas pruebas, no es posible garantizar que las validaciones de negocio, consultas JPA, lógica de servicios y contratos de API funcionen correctamente.

## Scope (Alcance)

### Included (Incluido)
- Suite de pruebas unitarias para Entidades (validación de campos).
- Suite de pruebas de persistencia para Repositorios (consultas personalizadas y proyecciones).
- Suite de pruebas unitarias para Servicios (lógica de negocio y manejo de excepciones).
- Suite de pruebas de integración web para Controladores (contratos de API y respuestas estandarizadas).
- **Estandarización de Paginación**: Refactorización de todos los catálogos para usar `Page` en lugar de `List`.
- **Refactorización de Excepciones**: Uso de `ValidacionNegocioException` en lugar de `RuntimeException`.
- Clases base de prueba para centralizar la lógica común de las entidades de catálogo.

### Excluded (Excluido)
- Pruebas de integración de extremo a extremo (E2E) que requieran una base de datos MySQL real (se usará H2).
- Pruebas para flujos operativos fuera del catálogo (Préstamos, Transferencias).

### Edge Cases & Boundaries (Casos de Borde y Límites)
- Intentar crear un registro con un nombre que ya existe (ignorar mayúsculas/minúsculas).
- Intentar obtener un registro inexistente o marcado con borrado lógico.
- Validaciones de tamaño máximo de caracteres en el campo `nombre`.
- Comportamiento de las proyecciones cuando faltan datos.
- Validación de formatos específicos en `Editorial` (Email, Teléfono).

## Technical Objectives (Objetivos Técnicos)

### OBJ1: Implementación de Pruebas de Entidad (P1)
**Why this priority**: Es la base de la integridad de datos y las validaciones de entrada.
**Rationale**: Garantizar que las anotaciones de Jakarta Bean Validation funcionen como se espera.
**Deliverables**: Clases de prueba en `arrupeteca/src/test/java/com/arrupeteca/persistence/entity/` para cada entidad.
**Validation Criteria**: El validador debe detectar campos nulos, vacíos o que excedan la longitud permitida.

### OBJ2: Implementación de Pruebas de Repositorio (P1)
**Why this priority**: Garantiza que las consultas personalizadas y las proyecciones recuperen los datos correctos.
**Rationale**: Uso de `@DataJpaTest` y H2 para validar métodos como `findByBorradoLogicoFalse`.
**Deliverables**: Repository Test classes en `arrupeteca/src/test/java/com/arrupeteca/persistence/repository/`.
**Validation Criteria**: Los métodos deben devolver solo registros activos y las proyecciones deben mapear correctamente.

### OBJ3: Implementación de Pruebas de Servicio y Refactorización (P1)
**Why this priority**: Aquí reside la lógica de negocio y la estandarización técnica.
**Rationale**: Uso de Mockito para aislar el servicio. Se debe refactorizar para usar `ValidacionNegocioException` y estandarizar la paginación.
**Deliverables**: Service Implementation Test classes en `arrupeteca/src/test/java/com/arrupeteca/service/impl/`.
**Validation Criteria**: Debe lanzar `ValidacionNegocioException` en violaciones de reglas y retornar `Page<T>` consistentemente.

### OBJ4: Implementación de Pruebas de Controlador (P1)
**Why this priority**: Asegura que el contrato de la API (JSON) se mantenga estable.
**Rationale**: Uso de `@WebMvcTest` y `MockMvc` para validar códigos de estado HTTP y la estructura `ApiResponse`.
**Deliverables**: Controller Test classes en `arrupeteca/src/test/java/com/arrupeteca/controller/`.
**Validation Criteria**: Debe responder con 200/201 en éxito y los errores deben seguir el formato estandarizado.

## Integration Points (Puntos de Integración)
- **MapStruct**: Los servicios dependen de mappers para transformar DTOs a Entidades y Proyecciones.
- **H2 Database**: Base de datos en memoria para pruebas de persistencia agiles.
- **GlobalExceptionHandler**: Los controladores interactúan con el manejador para formatear `ValidacionNegocioException`.

## Requirements (Requerimientos)
- `TR-001`: El sistema DEBE tener pruebas de unidad para todas las entidades del catálogo.
- `TR-002`: El sistema DEBE tener pruebas de persistencia para todos los repositorios usando `@DataJpaTest` y H2.
- `TR-003`: El sistema DEBE tener pruebas unitarias para servicios usando Mockito y lanzar `ValidacionNegocioException`.
- `TR-004`: El sistema DEBE estandarizar el uso de `Page` para todos los métodos de consulta masiva en catálogos.
- `TR-005`: Las pruebas DEBEN ubicarse en el directorio estándar `src/test/java/com/arrupeteca/...`.
- `TR-006`: Los controladores DEBEN validar que las respuestas sigan la estructura `ApiResponse`.

## Assumptions & Risks (Supuestos y Riesgos)
- **Asunción 1**: Todas las entidades de catálogo comparten la misma estructura básica, excepto `Editorial` que requiere validaciones extra.
- **Asunción 2**: H2 se comportará de manera compatible con MySQL para las consultas JPA básicas.
- **Riesgo 1**: La estandarización de paginación en catálogos simples podría requerir cambios menores en los controladores actuales (Probabilidad: Alta, Impacto: Bajo).

## Implementation Signals (Señales de Implementación)
- `NEW-CONFIG`: Configuración de base de datos H2 para perfiles de prueba.
- `MIGRATION`: Refactorización de `RuntimeException` a `ValidacionNegocioException`.
- `BREAKING-CHANGE`: Cambio de `List` a `Page` en el contrato de API de catálogos.

## Success Criteria (Criterios de Éxito)
- `SC-001 [OBJ1-OBJ4]`: Cobertura del 100% en las clases de catálogo.
- `SC-002 [OBJ3]`: Todos los catálogos implementan paginación en sus servicios.
- `SC-003 [OBJ3]`: Las excepciones de validación lanzan `ValidacionNegocioException`.
- `SC-004 [OBJ4]`: Todas las respuestas de la API cumplen con el formato `ApiResponse`.

## Clarifications (Aclaraciones)

### Session 2026-04-08
- Q: ¿Deberíamos estandarizar todos los catálogos para que soporten paginación? -> A: Estandarizar todos a Paginación.
- Q: ¿Dónde prefieres que se generen los tests? -> A: `arrupeteca/src/test/java/com/arrupeteca/...` (Estándar de Gradle).
- Q: ¿Deberíamos usar la excepción personalizada `ValidacionNegocioException`? -> A: Sí, usar la excepción personalizada.
- Q: ¿Qué motor prefieres para ejecutar las pruebas de persistencia? -> A: H2 Database (en memoria).

## Compliance Check (Control de Cumplimiento)
- [ ] TDD Mandatory: Verificado en OBJ1-OBJ4.
- [ ] Language Policy: Headers en Inglés, Contenido en Español.
- [ ] Tech Stack: Java 21, Spring Boot 4.0.2, H2.
