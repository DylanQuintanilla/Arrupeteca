# Estándar de Desarrollo: Repositorios JPA (Arrupeteca)

Este documento establece las reglas para la capa de acceso a datos, enfocándose en la eficiencia y la preparación para el almacenamiento en caché.

## 1. Estructura y Herencia
Todo repositorio debe extender de:
*   `JpaRepository<Entity, Long>`
*   `JpaSpecificationExecutor<Entity>`

## 2. Optimización con EntityGraph
Para evitar el problema de N+1 consultas (especialmente importante cuando los resultados se van a cachear):
*   Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})`.
*   Esto asegura que el objeto guardado en el caché esté "completo" y no lance `LazyInitializationException` al ser recuperado.

## 3. Manejo de Borrado Lógico
A falta de filtros globales, implementar métodos nativos para verificar existencia física si es necesario:
```java
@Query(value = "SELECT COUNT(*) > 0 FROM tabla WHERE id = :id", nativeQuery = true)
boolean existsByIdIncludeDeleted(@Param("id") Long id);
```

## 4. Ejemplo de Plantilla Completa

```java
public interface EntidadRepository extends JpaRepository<Entidad, Long>, JpaSpecificationExecutor<Entidad> {

    @EntityGraph(attributePaths = {"relacion"})
    Page<Entidad> findAll(Specification<Entidad> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"relacion"})
    Optional<Entidad> findById(Long id);

    @Modifying
    @Query("UPDATE Entidad e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);
}
```

---
*Este estándar garantiza que la base de datos sea consultada de forma eficiente antes de pasar los datos a la capa de servicio y caché.*
