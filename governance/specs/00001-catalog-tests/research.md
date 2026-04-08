# Research: Catalog Testing (Investigación: Pruebas de Catálogo)
> Feature | 2026-04-08 | Purpose: Definir arquitectura de pruebas y estrategia de refactorización.

## Pagination in Catalogs (Paginación en Catálogos)
- **Decision**: Estandarizar todos los catálogos para usar `org.springframework.data.domain.Page` en lugar de `List`.
- **Rationale**: Unifica el contrato de la API, facilita la integración con el frontend y permite manejar grandes volúmenes de datos.
- **Rejected**: Mantener `List` por ser más simple para datos pequeños (se rechaza por falta de consistencia con `Editorial`).
- **Pitfalls**: Olvidar actualizar los repositorios para que acepten `Pageable`.
- **Sources**: [Spring Data JPA Documentation], [Project SAD]

## Exception Handling (Manejo de Excepciones)
- **Decision**: Usar `ValidacionNegocioException` para todos los errores de validación de negocio (duplicados, no encontrados).
- **Rationale**: Permite al `GlobalExceptionHandler` capturar errores específicos y devolver el formato `ApiResponse` estandarizado.
- **Rejected**: `RuntimeException` genérico (se rechaza por ser poco descriptivo).
- **Pitfalls**: No actualizar los tests de servicio para verificar el tipo de excepción correcto.
- **Sources**: [Project SAD §5.2]

## Testing Infrastructure (Infraestructura de Tests)
- **Decision**: Uso de JUnit 5, Mockito y H2 Database en memoria.
- **Rationale**: Maximiza la velocidad de ejecución de los tests y garantiza aislamiento total.
- **Rejected**: Testcontainers con MySQL (se rechaza por la sobrecarga de tiempo de ejecución para CRUD simple).
- **Pitfalls**: No configurar correctamente el perfil de H2 en `application-test.properties`.
- **Sources**: [Spring Boot Testing Guide]

## Catalog Test Architecture (Arquitectura de Tests de Catálogo)
- **Decision**: Crear clases base `BaseServiceTest`, `BaseRepositoryTest` y `BaseControllerTest`.
- **Rationale**: Reduce drásticamente la duplicación de código para los 9 catálogos que comparten comportamiento idéntico.
- **Rejected**: Escribir tests individuales desde cero para cada catálogo (se rechaza por violar el principio de Simplicidad/DRY).
- **Pitfalls**: Hacer las clases base demasiado complejas para manejar las excepciones como `Editorial`.
- **Sources**: [JUnit 5 Hierarchy Patterns]

## Summary (Resumen)
| Topic | Decision | Rationale |
|-------|----------|-----------|
| Paginación | Page<T> | Consistencia con Editorial y escalabilidad. |
| Excepciones | ValidacionNegocioException | Semántica de errores y respuesta estandarizada. |
| Base de Datos | H2 | Rapidez y simplicidad en tests de persistencia. |
| Diseño de Tests | Clases Base | Minimiza duplicación para 9 catálogos idénticos. |

## Sources Index (Índice de Fuentes)
| URL | Topic | Fetched |
|-----|-------|---------|
| Project SAD | Arquitectura y Estándares | 2026-04-08 |
| Project Instructions | Gobernanza y Calidad | 2026-04-08 |
