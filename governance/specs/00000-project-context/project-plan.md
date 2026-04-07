# Project Implementation Plan (Project Plan) - Arrupeteca (Plan de Implementación del Proyecto)

## 1. Project Overview (Descripción General)
Este plan detalla las fases para el desarrollo de Arrupeteca, enfocado en un robusto sistema de catálogo, inventario físico y flujos operativos con notificaciones automáticas.

## 2. Execution Phases (Fases de Ejecución)

### Phase 1: Core Catalog and Infrastructure (Fase 1: Catálogo Base e Infraestructura) [COMPLETADO]
- [X] Configuración inicial Spring Boot 4.0.2 / Java 21.
- [X] Implementación inicial de entidades de catálogo.
- [X] Jerarquía de ubicación y Auditoría automática.
- [X] Relaciones M:M (`Obra_Genero`, `Obra_Categoria`).

### Phase 2: Refinement, Optimization and Quality (Fase 2: Refinamiento, Optimización y Calidad) [EN PROGRESO]
- [ ] **Technical Debt & Standards**:
    - [ ] Migración de DTOs a **Java Records**.
    - [ ] Implementación de **@Version** en `Auditable.java`.
    - [ ] Mejora de `ApiResponse` con estructura completa.
    - [ ] Optimización de repositorios y JPA (EntityGraph, Batch Fetching).
- [ ] **Functional Enhancements**:
    - [ ] Nueva entidad **Etiqueta** (M:M con Libros).
    - [ ] Campo **Popularidad** en Libros e **id_donacion** en EjemplarLibro.
    - [ ] **Estado** en la entidad Mueble.
    - [ ] Búsqueda Global por ID (QR).
- [ ] **Advanced Logic**:
    - [ ] Manejo de errores profesional y SLF4J.
    - [ ] **Borrado Lógico en Cascada** (Obra -> Libros -> Ejemplares).
- [ ] **Quality Control**:
    - [ ] **Plan de Pruebas Maestro**: Crear el suite de pruebas (Unitarias e Integración) para todo el proyecto.
    - [ ] Cobertura de pruebas al 100%.

### Phase 3: Advanced Operational Flows (Fase 3: Flujos Operativos Avanzados)
- [ ] **Módulo de Préstamos:** Solicitudes, renovaciones y límites.
- [ ] **Módulo de Transferencias:** Movimientos y "Muebles de Transición".
- [ ] **Módulo de Donaciones:** Entradas/Salidas y registro de entidades.
- [ ] Gestión de Usuarios y Roles (Admin, Maestro, Alumno).

### Phase 4: Future Enhancements (Fase 4: Mejoras Futuras)
- [ ] **Notificaciones Automáticas vía Correo** (Préstamos vencidos, solicitudes aprobadas).
- [ ] Sistema de Etiquetas (Tags) transversales para eventos.
- [ ] Versionado en la clase auditable y multidioma.

## 3. Key Milestones (Hitos Clave)
- **M-01**: Cierre de infraestructura optimizada y tests al 100%.
- **M-02**: Lanzamiento del sistema de préstamos, usuarios y notificaciones básicas.
- **M-03**: Sistema completo con transferencias y donaciones.
