# Product Requirements Document (PRD) - Arrupeteca (Documento de Requerimientos del Producto)

## 1. Vision and Goals (Visión y Objetivos)
Arrupeteca es un sistema integral de gestión de bibliotecas diseñado para digitalizar el catálogo y el control físico de una colección. El objetivo principal es proporcionar una plataforma centralizada que permita organizar obras literarias, sus ediciones impresas y la ubicación exacta de cada copia física.

A futuro, el sistema tiene como objetivo gestionar flujos operativos completos, incluyendo:
- **Préstamos (Loans):** Gestión de salidas y devoluciones de ejemplares a usuarios.
- **Transferencias (Transfers):** Movimiento controlado de ejemplares entre diferentes ubicaciones (salones, edificios).
- **Donaciones (Donations):** Registro de libros que ingresan al sistema como donativos, manteniendo la trazabilidad de su origen.
El sistema permitirá la creación y aprobación de solicitudes formales tanto para préstamos como para transferencias.

## 2. Target Audience (Público Objetivo)
El sistema está diseñado para tres perfiles principales:
- **Administradores (Admins / Bibliotecarios):** Tienen control total sobre el catálogo, inventario, aprobaciones de solicitudes, donaciones, préstamos y transferencias.
- **Maestros (Teachers):** Pueden consultar el catálogo y tienen privilegios para realizar **solicitudes de préstamos** y **solicitudes de transferencias** de material para sus clases o salones.
- **Alumnos (Students / Usuarios Finales):** Pueden consultar el catálogo disponible y realizar únicamente **solicitudes de préstamos** (reservas) de los libros que necesiten.

## 3. Core Features (Funcionalidades Principales)

### 3.1 Catalog Management (Gestión del Catálogo)
- Gestión de **Obras**: Registro de la obra intelectual (título, resumen, año). Incluye la clasificación a través de múltiples **Categorías** y **Géneros**.
- Gestión de **Libros**: Registro de ediciones específicas (editorial, idioma, formato físico/digital). Incluye la clasificación por **Grado** y **Ciclo** educativo, indicando a qué nivel de estudiantes va dirigido el material.
- Gestión de **Autores**: Información detallada de los creadores. Cada autor está conectado a un **Género Principal** y se relaciona con las obras mediante tipos de autoría específicos (autor, coautor, ilustrador, etc.).

### 3.2 Inventory and Location Control (Control de Inventario y Ubicación)
- Gestión de **Ejemplares**: Control de copias físicas individuales, rastreando su estado físico y disponibilidad (Disponible, Prestado, Reservado, Mantenimiento, Perdido).
- Jerarquía de Ubicación: Organización estructurada por Edificio -> Planta -> Salón -> Mueble (Estante).
- **Muebles de Transición:** Capacidad de asignar ejemplares a "muebles comodín" de forma temporal cuando llegan de una transferencia y aún no se les ha asignado su estante definitivo.

### 3.3 Operational Flows (Flujos Operativos - Futuro)
- **Préstamos y Reservas:** Creación de solicitudes y aprobaciones. Incluye la aplicación de reglas de negocio restrictivas (ej. los Alumnos solo pueden tener 1 copia de un mismo ejemplar y un máximo de 3 préstamos activos en total).
- **Renovaciones:** Creación de solicitudes de extensión (Solicitud_Renovacion) para prolongar el tiempo de un préstamo activo, sujetas a la aprobación del administrador.
- **Transferencias:** Solicitudes para mover libros de un salón/mueble a otro, manteniendo el historial del movimiento y el estado de la solicitud (Aprobado, En tránsito, etc.).
- **Donaciones (Entradas y Salidas):** Registro de movimientos de donación, clasificándolos como *Entradas* (libros recibidos para el inventario) o *Salidas* (libros del inventario donados a terceros). Incluye el registro de la **Entidad** asociada (empresa, colegio, persona) a la que se le dona o de quien se recibe.

### 3.4 Audit and Safety (Auditoría y Seguridad)
- Seguimiento de cambios: Registro automático de quién y cuándo creó o modificó cada registro (Auditoría). A futuro, la clase auditable integrará un sistema de **versionado**.
- Borrado Suave: Prevención de pérdida de datos mediante desactivación lógica (borrado_logico) en lugar de borrado físico en base de datos.

## 4. User Stories (Historias de Usuario)

### US-001: Gestión Administrativa (Admin)
Como administrador, quiero gestionar el catálogo completo (obras, libros, donaciones) y poder **aprobar o rechazar solicitudes de préstamos y transferencias** creadas por alumnos o maestros, para mantener el control total del inventario.

### US-002: Gestión de Materiales (Maestro)
Como maestro, quiero consultar el catálogo y poder **solicitar el préstamo de libros** para mi uso, así como **solicitar la transferencia de ejemplares** a un salón específico para utilizarlos en mis clases.

### US-003: Consulta y Reserva (Alumno)
Como alumno, quiero buscar libros en el catálogo de la biblioteca, identificar en qué **formato** se encuentran (físico o digital), y si existe una copia física disponible, poder **apartarla/reservarla** a través de una solicitud de préstamo.

## 5. Non-Functional Requirements (Requerimientos No Funcionales)
- **R-NF-01**: Respuesta rápida de la API (menos de 200ms para consultas simples).
- **R-NF-02**: Persistencia robusta y relacional en MySQL.
- **R-NF-03**: Arquitectura modular fácil de extender para soportar los flujos de préstamos y transferencias sin afectar el catálogo base.

## 6. Success Metrics (Métricas de Éxito)
- 100% de los libros físicos registrados con una ubicación exacta.
- Cobertura de pruebas del 100% para la lógica de negocio.
- Cero pérdida de datos accidentales gracias al borrado lógico.

## Project Context Baseline Updates (Actualizaciones de la Línea Base del Contexto del Proyecto)

### 2026-04-12 | Cobertura de Pruebas Integral
- **Estabilidad de Datos**: Se establece el requisito de cobertura del 100% no solo para la lógica de negocio, sino para todas las estructuras de datos (DTOs y Mappers) para garantizar la integridad de los contratos de la API y las transformaciones de datos.
- **Validación de Contratos**: Todas las solicitudes de entrada (Requests) deben ser validadas mediante pruebas de serialización y validación de restricciones (Bean Validation) para prevenir cambios accidentales en la interfaz pública y asegurar la integridad de los datos de entrada.

