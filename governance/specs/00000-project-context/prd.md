# Product Requirements Document (PRD) - Arrupeteca (Documento de Requerimientos del Producto)

## 1. Vision and Goals (Visión y Objetivos)
Arrupeteca es un sistema integral de gestión de bibliotecas diseñado para digitalizar el catálogo y el inventario físico de una colección. El objetivo principal es proporcionar una plataforma centralizada que permita organizar obras literarias, sus ediciones impresas y la ubicación exacta de cada copia física.

## 2. Target Audience (Público Objetivo)
- Bibliotecarios y administradores de colecciones.
- Usuarios finales que necesiten consultar el catálogo disponible.

## 3. Core Features (Funcionalidades Principales)

### 3.1 Catalog Management (Gestión del Catálogo)
- Gestión de **Obras**: Registro de la obra intelectual (título, descripción, género).
- Gestión de **Libros**: Registro de ediciones específicas (editorial, año, idioma, formato).
- Gestión de **Autores**: Información detallada de autores, incluyendo nacionalidad y roles en cada obra.
- Taxonomías: Categorías, géneros e idiomas personalizables.

### 3.2 Inventory and Location Control (Control de Inventario y Ubicación)
- Gestión de **Ejemplares**: Control de copias físicas individuales (estado físico, disponibilidad).
- Jerarquía de Ubicación: Organización por Edificio -> Planta -> Salón -> Mueble (Estante).

### 3.3 Audit and Safety (Auditoría y Seguridad)
- Seguimiento de cambios: Registro de quién y cuándo creó o modificó cada registro.
- Borrado Suave: Prevención de pérdida de datos mediante desactivación lógica en lugar de borrado físico.

## 4. User Stories (Historias de Usuario)

### US-001: Gestión de Catálogo
Como bibliotecario, quiero registrar una nueva obra y vincular sus autores para que los usuarios puedan encontrarla en el sistema.

### US-002: Control de Inventario
Como administrador, quiero asignar un ejemplar físico a un mueble específico para saber exactamente dónde se encuentra cada libro.

### US-003: Consulta de Disponibilidad
Como usuario, quiero saber si un libro tiene copias disponibles y en qué estado se encuentran.

## 5. Non-Functional Requirements (Requerimientos No Funcionales)
- **R-NF-01**: Respuesta rápida de la API (menos de 200ms para consultas simples).
- **R-NF-02**: Persistencia robusta en MySQL.
- **R-NF-03**: Arquitectura modular fácil de extender (ej. para añadir sistema de préstamos en el futuro).

## 6. Success Metrics (Métricas de Éxito)
- 100% de los libros físicos registrados con una ubicación exacta.
- Cobertura de pruebas del 100% para la lógica de negocio.
- Cero pérdida de datos accidentales gracias al borrado lógico.
