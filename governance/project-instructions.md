<!-- template-version: 2 -->
# Proyectos Project Instructions (Instrucciones del Proyecto)

## Core Principles (Principios Fundamentales)

### I. Test-First (Pruebas Primero)

La implementación DEBE comenzar con pruebas. Un ciclo estricto de red-green-refactor es obligatorio para todas las nuevas funcionalidades. — Asegura la corrección funcional, previene regresiones y obliga al diseño para la testabilidad desde el inicio.

### II. Simplicity (Simplicidad)

Priorizar soluciones simples, legibles y mantenibles sobre abstracciones complejas (YAGNI). Evitar la ingeniería "por si acaso". — Minimiza la deuda técnica, reduce el coste de mantenimiento y acelera la incorporación de nuevos colaboradores.

### III. Type Safety (Seguridad de Tipos)

Usar tipado fuerte y estático para todo el código del proyecto donde el lenguaje lo soporte. Evitar "any" o estructuras de datos no tipadas en rutas críticas. — Previene clases enteras de errores en tiempo de ejecución y mejora la experiencia del desarrollador mediante soporte mejorado del IDE y código autodocumentado.

### IV. Agent Output Style (Estilo de Salida del Agente)

Toda la salida del agente DEBE ser concisa y orientada a resultados. Este principio prevalece sobre cualquier valor por defecto verboso.

- **Progress reports (Informes de progreso)**: Solo hechos y resultados — sin narración, sin repetir la tarea.
- **Artifacts (Artefactos)**: Emitir solo las secciones requeridas — sin párrafos de preámbulo, sin epílogos de resumen.
- **Reasoning (Razonamiento)**: Omitir a menos que el usuario pregunte "por qué" o la decisión no sea obvia.
- **Errors / blockers (Errores / Bloqueos)**: Indicar el problema, la solución intentada y el resultado — nada más.
- **Phase-boundary reports (Informes de límite de fase)**: ≤ 5 puntos de viñeta.
- **Preserve without compressing (Preservar sin comprimir)**: Estructura de plantillas de artefactos y secciones requeridas; guía explícita de decisión / registro / validación en habilidades compartidas; restricciones de delegación y definiciones de roles de sub-agentes; límites de tamaño existentes (spec ≤ 10 KB, research ≤ 4 KB, stories ≤ 200 palabras).

## Technology Stack (Stack Tecnológico)

- **Language/Runtime (Lenguaje)**: Java 21
- **Frameworks**: Spring Boot 4.0.2
- **Storage (Almacenamiento)**: MySQL 8.x
- **Infrastructure (Infraestructura)**: Local (Gradle)

## Testing & Quality Policy (Política de Pruebas y Calidad)

- **Coverage Target (Objetivo de Cobertura)**: 100%
- **Required QC Categories (Categorías QC Requeridas)**: linting, security scanning, coverage
- **Test Strategy (Estrategia de Pruebas)**: Unit + integration; TDD mandatory
- **Linting / Formatting (Formateo)**: Checkstyle

## Source Code Layout (Disposición del Código Fuente)

- **Policy (Política)**: ENFORCE_SRC_ROOT
- **Convention (Convención)**: El código fuente reside bajo arrupeteca/src; las pruebas están co-localizadas en __tests__/; configuración en la raíz del repositorio

## Development Workflow (Flujo de Desarrollo)

- **Branching (Ramas)**: Ramas de características (feature branches) desde main, squash merge
- **Commit Convention (Convención de Commits)**: Conventional Commits
- **CI Requirements (Requisitos de CI)**: Todas las pruebas pasan, lint limpio, sin errores de tipo antes de fusionar

## Governance (Gobernanza)

- Las instrucciones del proyecto prevalecen sobre toda otra documentación y prácticas.
- Las enmiendas requieren un aumento de versión con una entrada de registro de cambios con fecha ISO.
- Todas las implementaciones DEBEN pasar la puerta de enlace Instructions Check durante la planificación.
- La complejidad más allá de estos principios DEBE ser justificada y documentada.

**Version**: 1.0.0 | **Last Amended (Última Enmienda)**: 2026-03-31
