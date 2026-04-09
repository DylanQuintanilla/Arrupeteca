# Tasks (Tareas) - Catalog Testing & Standardization (Pruebas de Catálogo y Estandarización)

**Epic**: 00001 | **Feature**: 00001-catalog-tests | **Branch**: Refactorizacion

## Project Mode (Modo del Proyecto)
Brownfield - Refactorización y Estabilización

## Status (Estado)
- [X] T001 [OBJ1] {RR-001} Downgrade Spring Boot a 3.4.2 estable
- [X] T002 [OBJ1] {RR-002} Hacer opcional el escaneo de OWASP en build.gradle
- [X] T003 [OBJ4] {RR-003} Corregir NeedBraces (llaves en if/else) en todos los servicios
- [X] T004 [OBJ4] {RR-004} Expandir AvoidStarImport (quitar .*) en todo el proyecto
- [X] T005 [OBJ2] {TR-001} Estandarizar nombres de métodos (obtenerPorIdActivo)
- [X] T006 [OBJ2] {TR-002} Implementar tests para Categoria, Autor, Editorial
- [X] T007 [OBJ2] {TR-002} Implementar tests para Pais, Nacionalidad, Genero
- [X] T008 [OBJ2] {TR-002} Implementar tests para Idioma, Formato, TipoAutoria
- [X] T009 [OBJ3] {TR-003} Implementar tests para Edificio, Planta, Salon
- [X] T010 [OBJ3] {TR-003} Implementar tests para Mueble, Obra, Libro, EjemplarLibro
- [X] T011 [OBJ4] {RR-005} Corregir PropertyReferenceException en Editorial (email -> correo)
- [X] T012 [OBJ4] {RR-006} Corregir nombres de métodos de repositorio para evitar parsing de Spring Data JPA
- [X] T013 [OBJ1, OBJ2, OBJ3, OBJ4] Ejecutar suite completa de pruebas y verificar cobertura del 100%
- [X] T014 [OBJ4] Generar archivo .qc-passed

## Final Verification (Verificación Final)
- Build: SUCCESSFUL
- Tests: 227 passed / 0 failed
- Checkstyle: 0 warnings
- Jacoco: 100% coverage verification rule met
