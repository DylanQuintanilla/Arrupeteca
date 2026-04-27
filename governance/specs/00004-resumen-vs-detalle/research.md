# Research: Summary vs Detail Pattern & JPA Optimization
> Feature 00004 | 2026-04-24 | Purpose: Optimize performance & prevent N+1 queries.

## Summary vs Detail Pattern (Patrón Resumen vs Detalle)
- **Decision**: Use lightweight Java Records for List views and complete Records for Detail views.
- **Rationale**: Decoupling API responses from JPA entities minimizes payload size and database overhead.
- **Rejected**: Returning full JPA entities directly (security & performance risk).
- **Pitfalls**: Including collections in summary records triggers unnecessary queries.
- **Sources**: [Baeldung - Projections](https://www.baeldung.com/spring-data-jpa-projections), [Thorben Janssen - Best Practices](https://thorben-janssen.com/spring-data-jpa-projections-best-practices/)

## JPA @EntityGraph Optimization (Optimización con JPA @EntityGraph)
- **Decision**: Define dynamic EntityGraphs in Repositories using `attributePaths`.
- **Rationale**: Forcing a `LEFT JOIN FETCH` resolves N+1 problems for known detail paths surgically.
- **Rejected**: NamedEntityGraph in Entity classes (clutters domain model).
- **Pitfalls**: Over-fetching many collections at once can lead to Cartesian product issues.
- **Sources**: [Spring Data JPA - Entity Graphs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-graph)

## MapStruct for Complex Mappings (MapStruct para Mapeos Complejos)
- **Decision**: Use MapStruct with `default` methods for location formatting ('Mueble - Salón - Planta').
- **Rationale**: MapStruct generates type-safe code during compilation, avoiding runtime reflection overhead.
- **Rejected**: implementing concatenation logic in Service/Controller layers.
- **Pitfalls**: Using complex SpEL expressions inside MapStruct (harder to debug).
- **Sources**: [MapStruct - Defining Mappers](https://mapstruct.org/documentation/stable/reference/html/#defining-a-mapper)

## Hibernate Batch Fetching (Carga por Lotes de Hibernate)
- **Decision**: Rely on `default_batch_fetch_size=100` for remaining collections in Detail views.
- **Rationale**: Provides a global performance safety net for lazy-loaded sibling entities using `IN` clauses.
- **Rejected**: Relying solely on batch fetching for critical performance paths.
- **Pitfalls**: Setting the value too high leads to excessive memory usage.
- **Sources**: [Vlad Mihalcea - Collection Fetching](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)

## Summary
| Topic | Decision | Rationale |
|-------|----------|-----------|
| Pattern | Records | Performance & immutability. |
| JPA | @EntityGraph | Surgical N+1 prevention. |
| Mapping | MapStruct | Type-safe, fast conversion. |
| Batch | size=100 | Lazy-load safety net. |

## Sources Index
| URL | Topic | Fetched |
|-----|-------|---------|
| https://www.baeldung.com/spring-data-jpa-projections | Pattern | 2026-04-24 |
| https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-graph | JPA | 2026-04-24 |
| https://mapstruct.org/documentation/stable/reference/html/ | Mapping | 2026-04-24 |
| https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/ | Batch | 2026-04-24 |
