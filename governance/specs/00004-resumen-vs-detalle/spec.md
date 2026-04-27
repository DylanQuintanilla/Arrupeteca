---
feature_branch: "00004-resumen-vs-detalle"
created: "2026-04-24"
input: "Implementar el patrón 'Resumen vs Detalle' y optimización JPA. Corregir EjemplarLibroResumen y otros campos en proyecciones. Clasificar entidades por complejidad para aplicar el patrón de forma selectiva."
spec_type: "technical"
spec_maturity: "clarified"
---

# Feature Specification: Summary vs Detail Pattern and JPA Optimization (Patrón Resumen vs Detalle y Optimización JPA)

**Feature Branch (Rama de Característica)**: `00004-resumen-vs-detalle`  
**Created (Creado)**: 2026-04-24  
**Status (Estado)**: Draft  
**Spec Type (Tipo de Spec)**: technical  
**Spec Maturity (Madurez de Spec)**: clarified

## Problem Statement (Declaración del Problema)

La arquitectura actual depende de proyecciones basadas en interfaces con SpEL (`@Value`), lo que genera una carga innecesaria durante la serialización y carece de seguridad de tipos. Además, existen errores en las proyecciones actuales (ej. `EjemplarLibroResumen` referencia `codigoInventario` inexistente) y no se diferencian correctamente los listados ligeros de las consultas de detalle completas, provocando ineficiencias N+1.

## Scope (Alcance)

### Included (Incluido)

- **Capa Dual Selectiva**: Aplicación del patrón Resumen vs Detalle a **Entidades Nucleares** (con colecciones o jerarquías complejas).
- **Capa de Resumen**: Proyecciones o Records ligeros optimizados para listados (sin colecciones).
- **Capa de Detalle**: DTOs completos (`DetalleResponse`) para consultas individuales, incluyendo relaciones OneToMany/ManyToMany.
- **Corrección de Proyecciones**: Auditoría y corrección de campos erróneos en proyecciones existentes.
- **Mapeo con MapStruct**: Centralización de la lógica de concatenación de ubicación física en los Mappers.
- **Optimización JPA**: Uso de `@EntityGraph` para listados y Batch Fetching para detalles.

### Excluded (Excluido)

- **Entidades Maestras Simples**: Entidades sin colecciones relacionadas o con muy pocos campos no requieren un DTO de "Detalle" separado si el Resumen ya es completo.
- **UI/Frontend**: La implementación de cambios en la interfaz queda fuera de este alcance técnico.

### Edge Cases & Boundaries (Casos Borde y Límites)

- **Relaciones Recursivas (Ciclos)**: Se deben evitar bucles infinitos en el JSON (ej. Obra -> Libro -> Obra) mediante el uso de `@JsonIgnoreProperties` o mapeo selectivo en MapStruct.
- **Nulidad en Ubicación**: La concatenación de 'Mueble - Salón - Planta' debe manejar de forma segura la ausencia de cualquiera de sus componentes.

## Clarifications (Aclaraciones)

### Session 2026-04-24
- **Q**: ¿Qué nivel de detalle deben tener los Records de 'Resumen'? -> **A**: Funcional Puro. Solo ID, nombre/título y relaciones X-to-One principales.
- **Q**: ¿Qué convención de nombres prefieres para los objetos de respuesta? -> **A**: Híbrido. `[Entidad]Resumen` para Records y `[Entidad]DetalleResponse` para clases.
- **Q**: ¿Cómo prefieres manejar las relaciones recursivas para evitar ciclos en el JSON? -> **A**: MapStruct Unidireccional. Ignorar la relación inversa en el mapeo.
- **Q**: ¿Dónde prefieres definir los EntityGraphs? -> **A**: Dinámico en Repositorio mediante `attributePaths`.
- **Q**: ¿Cómo organizar la lógica de mapeo? -> **A**: Mapper Centralizado por entidad.

## Stress-Test Findings (Hallazgos del Stress-Test)

### Session 2026-04-24
- **STF-001**: Tensión de rendimiento entre latencia y grafos de entidad. -> **Resolución**: Mantener restricción de 100ms. Se deben optimizar los `@EntityGraph` para que los joins sean eficientes y no degraden el tiempo de respuesta.
- **STF-002**: Ambigüedad en el alcance del Record `Resumen` para `Mueble`. -> **Resolución**: La ubicación concatenada se mostrará solo en el `DetalleResponse` para mantener los Records de `Resumen` lo más ligeros posible.

## Technical Objectives (Objetivos Técnicos)

### Objective 1 - Dual Layer Strategy (Estrategia de Capa Dual) (Priority: P1)

Diferenciar las respuestas de la API según la complejidad de la entidad y el tipo de consulta. Los resúmenes serán funcionales y ligeros.

**Why this priority**: Mejora el rendimiento eliminando el over-fetching y estandariza las respuestas.

**Rationale**: No todas las entidades necesitan dos DTOs; se debe aplicar donde el beneficio sea material.

**Deliverables**:
- **Entidades Nucleares (Resumen + Detalle)**: Obra, Libro, EjemplarLibro, Autor, Editorial, Mueble, Salon, Planta, Edificio, Categoria, Genero, Idioma, Formato.
- **Entidades Maestras (Solo Resumen/DTO único)**: Nacionalidad, Pais, TipoAutoria.
- **Ubicación de DTOs**: 
    - `com.arrupeteca.dto.response.summary`: Java Records funcionales para listados ligeros.
    - `com.arrupeteca.dto.response.detail`: Java Records completos para consultas de detalle.

**Validation Criteria**:
1. El listado de `Obra` utiliza el Record de la carpeta `summary` y no incluye colecciones ni campos de auditoría.
2. La búsqueda por ID de `Obra` devuelve el objeto `ObraDetalleResponse` de la carpeta `detail` sin ciclos de recursión.

### Objective 2 - MapStruct & Location Formatting (MapStruct y Formato de Ubicación) (Priority: P1)

Migrar la lógica de proyecciones SpEL (`@Value`) a Mappers de MapStruct para ganar rendimiento y seguridad.

**Why this priority**: `@Value` usa reflexión en runtime; MapStruct genera código en compilación, siendo mucho más rápido.

**Deliverables**:
- Mappers actualizados con métodos `default` para concatenar 'Mueble - Salón - Planta'.

**Validation Criteria**:
1. Los DTOs no contienen anotaciones `@Value`.
2. El campo `ubicacion` se genera correctamente mediante el Mapper.

### Objective 3 - Projection Correction (Corrección de Proyecciones) (Priority: P1)

Corregir errores de mapeo detectados en las proyecciones actuales.

**Why this priority**: Evita errores 500 al intentar acceder a campos inexistentes en las entidades.

**Deliverables**:
- `EjemplarLibroResumen`: Cambiar `codigoInventario` por `comentario`.
- `MuebleResumen`/`SalonResumen`/`PlantaResumen`: Eliminar `@Value` y usar getters directos o DTOs.

**Validation Criteria**:
1. El endpoint de listado de `EjemplarLibro` funciona correctamente y muestra el comentario.

## Technical Constraints (Restricciones Técnicas)

- **No SpEL**: Queda prohibido el uso de `@Value` en las nuevas capas de respuesta.
- **Batch Fetching**: Se debe verificar que `hibernate.default_batch_fetch_size=100` esté activo para las colecciones de la vista de detalle.

## Integration Points (Puntos de Integración)

- **IP-001**: Los Controladores deben inyectar el Mapper correspondiente para transformar las entidades a la respuesta adecuada (Resumen vs Detalle).

## Requirements (Requerimientos)

### Technical Requirements (Requerimientos Técnicos)

- **TR-001**: El sistema DEBE implementar `[Entidad]DetalleResponse` para todas las Entidades Nucleares.
- **TR-002**: El sistema DEBE eliminar el uso de `@Value` en todas las proyecciones/DTOs de respuesta.
- **TR-003**: El sistema DEBE concatenar la ubicación en el Mapper siguiendo el patrón: 'Mueble - Salón - Planta' únicamente para la vista `DetalleResponse`.
- **TR-004**: Los Repositorios DEBEN usar `@EntityGraph` para las relaciones `ManyToOne` en las consultas de listado.
- **TR-005**: `EjemplarLibroResumen` DEBE corregirse: eliminar `codigoInventario` y añadir `comentario`.

### Key Entities (Entidades Clave)

- **Entidades Nucleares**: Aquellas con lógica de negocio compleja y múltiples relaciones.
- **Entidades Maestras**: Tablas de referencia simples (Lookups).

## Assumptions & Risks (Supuestos y Riesgos)

### Assumptions (Supuestos)

- El uso de Records de Java 21 es preferible para los DTOs de Resumen por su inmutabilidad y concisión.

### Risks (Riesgos)

- **[Recursion]** *(likelihood: low, impact: high)*: Ciclos infinitos en la serialización de detalles anidados.

## Implementation Signals (Señales de Implementación)

- `MIGRATION`: Reemplazo de proyecciones basadas en interfaces por Records o DTOs tipados.
- `BREAKING-CHANGE`: Cambio en la estructura de la respuesta JSON para listados (eliminación de colecciones).

## Success Criteria (Criterios de Éxito)

### Measurable Outcomes (Resultados Medibles)

- **SC-001** [OBJ1]: Las Entidades Nucleares cuentan con respuestas diferenciadas de Resumen y Detalle.
- **SC-002** [OBJ2]: Se ha eliminado el 100% de las anotaciones `@Value` en las capas de respuesta.
- **SC-003** [OBJ3]: El número de consultas SQL en un listado paginado es constante independientemente del número de registros (O(1)).

## Compliance Check (Control de Cumplimiento)

- **TDD Mandatory**: PASSED.
- **Language Policy**: PASSED.
- **Size Budget**: PASSED (8.2 KB).
