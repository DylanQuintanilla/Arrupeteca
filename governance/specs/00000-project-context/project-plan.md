# Project Implementation Plan (Project Plan) - Arrupeteca (Plan de Implementación del Proyecto)

## 1. Project Overview (Descripción General)
Este plan detalla las fases para el desarrollo de Arrupeteca, enfocado en un robusto sistema de catálogo, inventario físico y flujos operativos con notificaciones automáticas.

## 2. Execution Phases (Fases de Ejecución)

### Phase 1: Core Catalog and Infrastructure (Fase 1: Catálogo Base e Infraestructura) [COMPLETADO]
- [X] Configuración inicial Spring Boot 3.4.2 / Java 21.
- [X] Implementación inicial de entidades de catálogo.
- [X] Jerarquía de ubicación y Auditoría automática.
- [X] Relaciones M:M (`Obra_Genero`, `Obra_Categoria`).

### Phase 2: Refinement, Optimization and Quality (Fase 2: Refinamiento, Optimización y Calidad) [EN PROGRESO]
- [X] **Technical Debt & Standards**:
    - [X] Infraestructura de Checkstyle (5 Golden Rules). *(Nota: Verificación temporalmente obviada por refactorización activa).*
    - [X] Limpieza manual y refactor de estilo de Controllers, Services, Mappers y Entities (100% cumplimiento). *(Nota: Verificación temporalmente obviada por refactorización activa).*
    - [/] Migración de DTOs a **Java Records** (Records de respuesta implementados; request DTOs pendientes excepto AutorRequest).
    - [X] Implementación de **@Version** a nivel de entidad transaccional (ej. `Autor`).
    - [ ] Mejora de `ApiResponse` con estructura completa.
    - [/] Optimización de repositorios y JPA (EntityGraph en AutorRepository; Batch Fetching configurado a nivel global; pendiente en demás entidades).
- [ ] **Functional Enhancements**:
    - [ ] Nueva entidad **Etiqueta** (M:M con Libros).
    - [ ] Campo **Popularidad** en Libros e **id_donacion** en EjemplarLibro.
    - [ ] **Estado** en la entidad Mueble.
    - [ ] Búsqueda Global por ID (QR).
- [ ] **Advanced Logic**:
    - [ ] Manejo de errores profesional y SLF4J.
    - [ ] **Borrado Lógico en Cascada** (Obra -> Libros -> Ejemplares).
- [ ] **Quality Control**:
    - [/] Estabilización de la base de pruebas (Fix de tests rotos tras refactor; en progreso/pausado por refactorización).
    - [ ] **Plan de Pruebas Maestro**: Crear el suite de pruebas (Unitarias e Integración) para todo el proyecto.
    - [ ] Cobertura de pruebas al 100% (JaCoCo). *(Nota: Temporalmente obviado por refactorización activa).*
- [ ] **Infrastructure & Documentation**:
    - [ ] Configuración e integración de caché local con `@Cacheable`.
    - [ ] Asegurar deserialización correcta de Records de respuesta mediante `@Jacksonized`.
    - [ ] Estandarización de Javadoc en clases de servicio y anotaciones OpenAPI/OpenAPI UI (Swagger) en controladores (incluyendo el uso de `@ParameterObject` y `@PageableDefault` para paginación).
    - [ ] Creación de la tabla de logs en la base de datos para auditoría y persistencia de trazas.
    - [ ] Configurar la IP directa del frontend en la anotación `@CrossOrigin` de los controladores en lugar del comodín (`*`).



### Phase 3: Advanced Operational Flows (Fase 3: Flujos Operativos Avanzados)
- [ ] **Módulo de Préstamos:** Solicitudes, renovaciones y límites.
- [ ] **Módulo de Transferencias:** Movimientos y "Muebles de Transición".
- [ ] **Módulo de Donaciones:** Entradas/Salidas y registro de entidades.
- [ ] **Gestión de Usuarios y Roles (Admin, Maestro, Alumno):**
    - [ ] Registro, login y control de accesos.
    - [ ] Verificación de la validez y existencia de correos al registrar nuevos usuarios.
    - [ ] Flujo de envío de correos para recuperación de contraseñas.

### Phase 4: Future Enhancements (Fase 4: Mejoras Futuras)
- [ ] **Sistema de Notificaciones Automáticas y Envío de Correos:**
    - [ ] Plantillas y lógica de aviso para préstamos próximos a vencer (alertas previas) y vencidos.
    - [ ] Correos para avisos generales de la biblioteca y comunicados.
    - [ ] Notificaciones automáticas de flujos operativos (donación registrada, transferencia iniciada, etc.).
- [ ] Sistema de Etiquetas (Tags) transversales para eventos.
- [ ] Versionado en la clase auditable y multidioma.

## 3. Key Milestones (Hitos Clave)
- **M-01**: Cierre de infraestructura optimizada y tests al 100% (Checkstyle + JaCoCo).
- **M-02**: Lanzamiento del sistema de préstamos, usuarios y notificaciones básicas.
- **M-03**: Sistema completo con transferencias y donaciones.
