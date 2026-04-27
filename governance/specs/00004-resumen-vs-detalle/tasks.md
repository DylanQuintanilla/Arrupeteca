# Tasks: Patrón Resumen vs Detalle y Optimización JPA

**Input**: Design documents from `specs/00004-resumen-vs-detalle/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`

**Tests**: Include TDD tasks for Mappers and Controller responses as per project instructions.

## Project Mode

`Brownfield`

## Epic / Capability Map

- `[OBJ1]` → Capa Dual Record/DTO (Resumen + Detalle)
- `[OBJ2]` → MapStruct & Formato de Ubicación
- `[OBJ3]` → Corrección de Proyecciones y Optimización JPA

## Brownfield Notes

- Existing flows touched: `com.arrupeteca.persistence.repository.*`, `com.arrupeteca.service.impl.*`, `com.arrupeteca.controller.*`
- Compatibility or migration concerns: Eliminación de campos SpEL en favor de Records. Eliminación de `codigoInventario` en `EjemplarLibroResumen`.
- Regression focus: Listados paginados y consultas por ID de todas las entidades.

## Phase 1: Foundational (Cross-Work-Item Blockers)

- [X] T001 [P] {TR-002} Create Summary Records package in arrupeteca/src/main/java/com/arrupeteca/dto/response/summary
- [X] T002 [P] {TR-001} Create Detail Records package in arrupeteca/src/main/java/com/arrupeteca/dto/response/detail

---

## Phase 2: OBJ1 - Dual Layer & Record Migration (Priority: P1) 🎯 MVP

- [X] T003 [P] [OBJ1] {TR-002} Create [Entidad]Resumen Records for Core Entities in com.arrupeteca.dto.response.summary
- [X] T004 [P] [OBJ1] {TR-001} Create [Entidad]DetalleResponse Records for Core Entities in com.arrupeteca.dto.response.detail
- [X] T005 [P] [OBJ1] {TR-002} [COMPLETES TR-002] Create unique DTOs for Master Entities in com.arrupeteca.dto.response.summary
- [X] T006 [OBJ1] {TR-001,TR-002} [COMPLETES TR-001] Unit tests for Records serialization in arrupeteca/src/test/java/com/arrupeteca/dto/response

---

## Phase 3: OBJ2 - MapStruct & Location Logic (Priority: P1) 🎯 MVP

- [X] T007 [P] [OBJ2] {TR-003} Update [Entidad]Mapper to support Summary and Detail mapping in com.arrupeteca.mapper
- [X] T008 [OBJ2] {TR-003} Implement location concatenation in MuebleMapper.java (Detail only)
- [X] T009 [OBJ2] {TR-003} [COMPLETES TR-003] Implement unidirectionality in mappers to prevent recursion cycles in com.arrupeteca.mapper
- [X] T010 [OBJ2] {TR-003} Unit tests for Mappers in arrupeteca/src/test/java/com/arrupeteca/mapper

---

## Phase 4: OBJ3 - JPA Optimization & Refactoring (Priority: P1) 🎯 MVP

- [X] T011 [OBJ3] {TR-004} Update Repositories with dynamic @EntityGraph in com.arrupeteca.persistence.repository
- [X] T012 [OBJ3] {TR-005} Fix EjemplarLibroResumen: replace codigoInventario with comentario in summary Record
- [X] T013 [OBJ3] {TR-001,TR-002} Update Service implementation methods to use new Records in com.arrupeteca.service.impl
- [X] T014 [OBJ3] {TR-001,TR-002} Update Controllers to return Summary for list and Detail for ID in com.arrupeteca.controller
- [X] T015 [OBJ3] {TR-004,TR-005} [COMPLETES TR-004,TR-005] Integration tests for endpoints in arrupeteca/src/test/java/com/arrupeteca/controller

---

## Phase 5: Polish & Cross-Cutting Concerns

- [X] T016 [P] Remove legacy interface projections from com.arrupeteca.persistence.projection
- [X] T017 [P] Run Checkstyle and JaCoCo to verify compliance and coverage

---

## Dependencies

Foundational → Phase 2 (OBJ1) → Phase 3 (OBJ2) → Phase 4 (OBJ3) → Polish

- Tasks marked `[P]` can run in parallel within their phase.
- Delivery depends on DTOs and Mappers being ready before refactoring Services/Controllers.
- Polish phase ensures cleanup of deprecated artifacts.
