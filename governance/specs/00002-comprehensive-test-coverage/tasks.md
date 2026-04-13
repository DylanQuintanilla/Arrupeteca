# Tasks: Comprehensive Test Coverage (Cobertura de Pruebas Integral)

## Project Mode
Brownfield

## Epic / Capability Map
- **Phase 1: Setup** — Configuración de Gradle y JaCoCo.
- **Phase 2: OBJ1 - Mappers Delivery** — Pruebas unitarias para mappers de MapStruct.
- **Phase 3: OBJ2 - DTOs Delivery** — Pruebas de serialización y validación para DTOs.
- **Phase 4: OBJ3 - Entity & Config Delivery** — Pruebas para ObraAutor y JpaConfig.
- **Phase 5: Polish** — Verificación final de cobertura y limpieza de build.gradle.

## Phase 1: Setup (Repository / Workspace Delta)
- [ ] T001 [P] Configurar MapStruct para añadir anotación @Generated en arrupeteca/build.gradle
- [ ] T002 [P] Configurar JaCoCo para excluir clases anotadas con @Generated en arrupeteca/build.gradle

## Phase 2: OBJ1 - Mappers Delivery 🎯 MVP
- [ ] T003 [P] [OBJ1] {TR-001} Implementar EdificioMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/EdificioMapperTest.java
- [ ] T004 [P] [OBJ1] {TR-001} Implementar LibroMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/LibroMapperTest.java
- [ ] T005 [P] [OBJ1] {TR-001} Implementar MuebleMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/MuebleMapperTest.java
- [ ] T006 [P] [OBJ1] {TR-001} Implementar ObraMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/ObraMapperTest.java
- [ ] T007 [P] [OBJ1] {TR-001} Implementar PlantaMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/PlantaMapperTest.java
- [ ] T008 [P] [OBJ1] {TR-001} Implementar SalonMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/SalonMapperTest.java
- [ ] T009 [P] [OBJ1] {TR-001} Implementar EjemplarLibroMapperTest en arrupeteca/src/test/java/com/arrupeteca/mapper/EjemplarLibroMapperTest.java

## Phase 3: OBJ2 - DTOs Delivery 🎯 MVP
- [ ] T010 [P] [OBJ2] {TR-003} Implementar ApiResponseTest en arrupeteca/src/test/java/com/arrupeteca/dto/response/ApiResponseTest.java
- [ ] T011 [P] [OBJ2] {TR-002} Implementar AutorRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/AutorRequestTest.java
- [ ] T012 [P] [OBJ2] {TR-002} Implementar CategoriaRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/CategoriaRequestTest.java
- [ ] T013 [P] [OBJ2] {TR-002} Implementar EdificioRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/EdificioRequestTest.java
- [ ] T014 [P] [OBJ2] {TR-002} Implementar EditorialRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/EditorialRequestTest.java
- [ ] T015 [P] [OBJ2] {TR-002} Implementar EjemplarLibroRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/EjemplarLibroRequestTest.java
- [ ] T016 [P] [OBJ2] {TR-002} Implementar FormatoRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/FormatoRequestTest.java
- [ ] T017 [P] [OBJ2] {TR-002} Implementar GeneroRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/GeneroRequestTest.java
- [ ] T018 [P] [OBJ2] {TR-002} Implementar IdiomaRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/IdiomaRequestTest.java
- [ ] T019 [P] [OBJ2] {TR-002} Implementar LibroRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/LibroRequestTest.java
- [ ] T020 [P] [OBJ2] {TR-002} Implementar MuebleRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/MuebleRequestTest.java
- [ ] T021 [P] [OBJ2] {TR-002} Implementar NacionalidadRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/NacionalidadRequestTest.java
- [ ] T022 [P] [OBJ2] {TR-002} Implementar ObraAutorRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/ObraAutorRequestTest.java
- [ ] T023 [P] [OBJ2] {TR-002} Implementar ObraRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/ObraRequestTest.java
- [ ] T024 [P] [OBJ2] {TR-002} Implementar PaisRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/PaisRequestTest.java
- [ ] T025 [P] [OBJ2] {TR-002} Implementar PlantaRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/PlantaRequestTest.java
- [ ] T026 [P] [OBJ2] {TR-002} Implementar SalonRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/SalonRequestTest.java
- [ ] T027 [P] [OBJ2] {TR-002} Implementar TipoAutoriaRequestTest en arrupeteca/src/test/java/com/arrupeteca/dto/request/TipoAutoriaRequestTest.java

## Phase 4: OBJ3 - Entity & Config Delivery
- [ ] T028 [P] [OBJ3] {TR-005} Implementar ObraAutorTest en arrupeteca/src/test/java/com/arrupeteca/persistence/entity/ObraAutorTest.java
- [ ] T029 [P] [OBJ3] Implementar JpaConfigTest en arrupeteca/src/test/java/com/arrupeteca/config/JpaConfigTest.java

## Phase 5: Polish & Cross-Cutting Concerns
- [ ] T030 Eliminar exclusiones obsoletas del bloque jacocoTestCoverageVerification en arrupeteca/build.gradle
- [ ] T031 Ejecutar `./gradlew clean build jacocoTestReport` y verificar reporte de cobertura al 100%
