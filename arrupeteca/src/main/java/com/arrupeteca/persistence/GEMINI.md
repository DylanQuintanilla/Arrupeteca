# Estándares de Persistencia (Arrupeteca)

Este archivo contiene instrucciones automatizadas para la generación y mantenimiento de la capa de persistencia. Deben seguirse estrictamente para cualquier nueva entidad, repositorio o especificación.

## 1. Entidades JPA
- **Herencia**: Siempre extender de `Auditable`.
- **Nomenclatura**: Atributos en `camelCase`, columnas en `snake_case`. Siempre usar `@Table` y `@Column` explícitos.
- **Lombok**:
    - `@Getter`, `@Setter`.
    - `@ToString(onlyExplicitlyIncluded = true)` y `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`.
    - `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor(access = AccessLevel.PRIVATE)`, `@Builder`.
- **Identificadores**: `@Id` con `GenerationType.IDENTITY` y `updatable = false`. Incluir en Equals/ToString.
- **Tipos**: `java.time.Year` para años, `@Lob` + `TEXT` para biografía/descripciones.
- **Relaciones**: Siempre `FetchType.LAZY`.
- **Borrado Lógico**: Usar `@SQLDelete` y el atributo `boolean borradoLogico` con `@Column(columnDefinition = "TINYINT DEFAULT 0")` y `@Builder.Default`.
- **Validación**: Usar `jakarta.validation` con mensajes en español. Usar `@AssertTrue` para lógica compleja.

## 2. Repositorios
- **Herencia**: Extender de `JpaRepository` y `JpaSpecificationExecutor`.
- **Rendimiento**: Usar `@EntityGraph(attributePaths = {...})` en todos los métodos de lectura para evitar N+1.
- **Retorno**: Usar `Optional<T>` para resultados únicos.
- **Consultas**: Preferir JPQL sobre Native Queries. Siempre usar `ORDER BY` y `@Param`.
- **Borrado Lógico**: Implementar métodos `activar(id)` y `desactivar(id)` usando `@Modifying` y JPQL.

## 3. Especificaciones
- **Estructura**: Clase de utilidad con constructor privado.
- **Métodos Base**: `tieneId`, `esBorrado`, `contieneTermino`, `tiene[Relacion]`.
- **Joins**: Usar `JoinType.LEFT` y `query.distinct(true)` en búsquedas globales para evitar pérdida de datos y duplicados.
- **Orquestadores**: `filtroMaestroAdmin` (permite ver borrados) y `filtroBusquedaGlobal` (solo activos).

---
*Referencia: skill-propias/entidades-jpa.md y skill-propias/repositorios-jpa.md*
