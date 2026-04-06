# SDD Pilot — Agent Context (Contexto del Agente)

Aplica las reglas de Spec-Driven Development (SDD) a continuación durante la entrega de funcionalidades. Haz cumplir el orden del ciclo de vida, las puertas de fase (phase gates), las convenciones y la política de ejecución. Si alguna regla aquí entra en conflicto con `project-instructions.md`, sigue `project-instructions.md`.

## Lifecycle (Ciclo de Vida)

`Specify → Clarify → Plan → Checklist (opcional) → Tasks → Analyze (opcional) → Implement → QC`

Trata este orden como estricto. Si falta un artefacto requerido para la siguiente fase, detente y devuelve el trabajo a la fase que lo posee.

## Phase Gates (Puertas de Fase)

- `spec.md` debe existir antes de Clarify o Plan.
- `plan.md` debe existir antes de Tasks.
- `tasks.md` debe existir antes de Implement.
- Si existe `checklists/`, todos los elementos de la lista de verificación deben estar completos antes de Implement, a menos que el usuario lo anule explícitamente.
- `.completed` debe existir antes de QC.
- No consideres una funcionalidad lista para el lanzamiento hasta que exista `.qc-passed`.
- Cualquier violación de `project-instructions.md` es de severidad CRÍTICA (CRITICAL).

## Core Conventions (Convenciones Principales)

- Almacena los artefactos del Feature Workspace en `specs/<feature-folder>/`.
- Los nuevos Feature Workspaces usan nombres de carpeta `00001-feature-name`.
- Si la rama activa coincide con `#####-feature-name`, usa `specs/<branch-name>/`.
- Los Feature Workspaces existentes sin prefijo siguen siendo válidos.

Task format (Formato de tarea):

```text
- [ ] T### [P?] [US#|OBJ#?] {(FR|TR|OR|RR)-###?} Description with file path
```

- `[P]` marca el trabajo que es seguro ejecutar en paralelo.
- `[US#]` mapea una tarea a una historia de usuario (User Story) del producto.
- `[OBJ#]` mapea una tarea a un objetivo técnico u operativo.
- `{...}` mapea una tarea a uno o más IDs de requerimientos.
- La única transición de casilla válida es `- [ ]` → `- [X]`.

Priority rules (Reglas de prioridad):

- P1 es la prioridad más crítica y debe ser suficiente para un MVP viable.
- Cada historia de usuario u objetivo debe ser independiente y testeable.

Markers (Marcadores):

- `.completed` significa que la implementación ha terminado.
- `qc-report.md` registra los resultados de QC.
- `.qc-passed` significa que el QC ha pasado con éxito.

## Communication Style (Estilo de Comunicación)

La salida del agente DEBE ser concisa y orientada a resultados según `project-instructions.md` §IV. Aplica estas reglas en cada fase de SDD:

- **Progress reports (Informes de progreso)**: Solo hechos y resultados — sin narración, sin repetir la tarea.
- **Artifact output (Salida de artefactos)**: Solo las secciones requeridas — sin preámbulos, sin epílogos de resumen.
- **Reasoning (Razonamiento)**: Omitir a menos que el usuario pregunte "por qué" o la decisión no sea obvia.
- **Errors / blockers (Errores / Bloqueos)**: Problema → solución intentada → resultado. Nada más.
- **Phase-boundary reports (Informes de límite de fase)**: ≤ 5 puntos de viñeta.

NO comprimir:

- Plantillas de artefactos y sus secciones requeridas (estructura de `spec.md`, `plan.md`, `tasks.md`).
- Guía explícita de decisión, registro y validación en habilidades compartidas.
- Restricciones de delegación y definiciones de roles de sub-agentes.
- Límites de tamaño definidos (spec ≤ 10 KB, research ≤ 4 KB, stories ≤ 200 palabras).

## Continuous Execution Policy (Política de Ejecución Continua)

Ejecuta operaciones rutinarias del repositorio de forma real: ediciones de archivos, comandos de construcción/prueba/lint, comandos de git, actualizaciones de tareas, archivos marcadores e instalaciones de paquetes locales. No simules la finalización, resultados de pruebas, resultados de QC o estados de aprobación. Solo detente por ambigüedad, acciones destructivas, instalaciones a nivel de sistema o acciones fuera del límite del proyecto. Informa sobre el progreso en los límites de fase.
