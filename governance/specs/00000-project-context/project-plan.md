# Project Implementation Plan (Project Plan) - Arrupeteca (Plan de Implementación del Proyecto)

## 1. Project Overview (Descripción General)
Este plan detalla las fases para el desarrollo de Arrupeteca, desde la base actual de gestión de catálogo hasta funcionalidades avanzadas como gestión de usuarios y préstamos.

## 2. Execution Phases (Fases de Ejecución)

### Phase 1: Core Catalog and Infrastructure (Fase 1: Catálogo Base e Infraestructura) [EN PROGRESO]
- [X] Configuración inicial Spring Boot 4.0.2 / Java 21.
- [X] Implementación de entidades de catálogo (Obra, Libro, Autor).
- [X] Implementación de jerarquía de ubicación (Edificio, Planta, Salón, Mueble).
- [X] Auditoría automática y borrado lógico.
- [ ] Documentación técnica completa (DOD, SAD actualizado).

### Phase 2: Refinement and Quality (Fase 2: Refinamiento y Calidad)
- [ ] Implementación de validaciones de negocio adicionales.
- [ ] Mejora de la cobertura de pruebas al 100% (Unitarias e Integración).
- [ ] Optimización de proyecciones JPA para búsquedas rápidas.
- [ ] Documentación de API con Swagger/OpenAPI.

### Phase 3: Advanced Features (Fase 3: Funcionalidades Avanzadas)
- [ ] Módulo de Gestión de Usuarios (Autenticación JWT).
- [ ] Módulo de Préstamos y Devoluciones.
- [ ] Sistema de Reservas.
- [ ] Notificaciones automáticas.

## 3. Deployment and Operations Strategy (Estrategia de Despliegue y Operaciones)
- Despliegue local mediante Docker Compose para facilitar el entorno MySQL.
- Pipeline de CI/CD para ejecutar pruebas y linting automáticamente.

## 4. Key Milestones (Hitos Clave)
- **M-01**: Cierre de la infraestructura de catálogo y ubicación (Fase 1).
- **M-02**: Cobertura de pruebas completa y API documentada (Fase 2).
- **M-03**: Lanzamiento del sistema de préstamos (Fase 3).
