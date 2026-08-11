# Estándar de Desarrollo: Repositorios JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de acceso a datos (Repositorios) en Arrupeteca. Su objetivo es garantizar consultas altamente eficientes a la base de datos, evitar problemas de rendimiento N+1 y asegurar la consistencia del estado de las entidades en la caché de primer nivel de Hibernate.

---

## 1. Estructura y Herencia

Todo repositorio debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona métodos CRUD estándar de forma automática.

### Regla de Limpieza de Código (Clean Code):
*   **Prohibido**: Utilizar la anotación `@Repository` sobre la interfaz del repositorio. Dado que extiende de `JpaRepository`, Spring Data JPA lo detecta y lo registra de forma automática en el contexto de aplicación. Colocar la anotación es redundante.
*   **Prohibido**: Extender de `JpaSpecificationExecutor<Entity>`. Todas las búsquedas paginadas y filtrados complejos del sistema se gestionan a través de métodos JPQL unificados proyectados (`busqueda(...)`), manteniendo los repositorios limpios y sin código muerto de especificaciones.

---

## 2. Optimización con `@EntityGraph` (Prevenir el Problema N+1)

Hibernate utiliza carga perezosa (`FetchType.LAZY`) por defecto en las relaciones. Sin embargo, al consultar detalles por ID que serán mapeados a DTOs de salida, acceder a atributos de relaciones puede generar decenas de consultas individuales adicionales en bucle (el problema N+1).

*   **Solución Obligatoria**: Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})` en métodos de consulta de detalle por ID (`busquedaPorId`).
*   **Propósito**: Fuerza a Hibernate a generar una única consulta SQL con sentencias `LEFT JOIN FETCH` para traer todos los objetos relacionados de un solo viaje, optimizando la base de datos y preparando los datos para ser cacheados sin lanzar excepciones `LazyInitializationException`.
*   **Restricción Crítica (Colecciones ToMany)**:
    *   **Prohibido**: Incluir colecciones (`@ManyToMany` o `@OneToMany`, ej. `categorias` u `obras`) dentro del `@EntityGraph` junto con otras colecciones o en consultas de listados paginados.
    *   *Razón*: Carga múltiples colecciones en paralelo provocando la excepción `MultipleBagFetchException` en Hibernate o generando un Producto Cartesiano en memoria que degrada drásticamente el rendimiento. Además, la paginación con colecciones obliga a Hibernate a paginar en memoria (JVM), lo cual es extremadamente ineficiente.

---

## 3. Proyecciones por Constructor (`SELECT new`) para Listados Paginados (Regla de Oro)

En Arrupeteca se establece la **Regla de Oro** para la capa de persistencia en clases de negocio:

*   **1. Todos los métodos que retornen un Listado Paginado de Resumen (`Page<Resumen>`)**:
    *   **Obligatorio**: Deben ejecutarse mediante **`SELECT new`** en consultas `@Query` de JPQL (ej. `@Query("SELECT new com.arrupeteca.dto.response.summary.AutorResumenResponse(...) FROM ...")`).
    *   **Ventajas de Rendimiento**: Bypassean la caché L1 de Hibernate (Persistence Context), reducen la RAM en un 80%, consumen el mínimo ancho de banda SQL y eliminan la necesidad de transformación MapStruct en lectura.
    *   **Filtrado `@ManyToMany` sin Producto Cartesiano**: Usar `EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)` en lugar de `JOIN` explícito en el `WHERE`.
*   **2. Todos los métodos que retornen un Detalle por ID (`DetalleResponse`)**:
    *   **Obligatorio**: Deben usar **`@EntityGraph`** para cargar la entidad completa con todas sus relaciones para visualización detallada o edición.

### 📌 Patrón de Diseño: Consultas Unificadas Parametrizadas (`busqueda` y `busquedaPorId`)

La arquitectura de Arrupeteca consolida las consultas en **2 métodos universales parametrizados** en el repositorio:

1. **`busqueda(...)`**: Maneja tanto la búsqueda pública (enviando `borradoLogico = false`) como la de administración (enviando `borradoLogico = null` para ver todos o `borradoLogico = true` para ver inactivos).
2. **`busquedaPorId(...)`**: Retorna una entidad por ID aplicando `@EntityGraph` y el parámetro `@Param("borradoLogico") Boolean borradoLogico` (`false` para público, `null` para admin).

| Método en Repository | Parámetro `borradoLogico` | Uso Principal |
|---|---|---|
| `busqueda(...)` | `false` | Búsqueda Global Pública y Autocompletes de activos |
| `busqueda(...)` | `null` / `true` | Búsqueda Completa de Administración (sin importar estado)|
| `busquedaPorId(...)` | `false` | Detalle público de registro activo |
| `busquedaPorId(...)` | `null` | Detalle administrativo por ID (sin importar estado) |

---

## 4. Modificaciones en Base de Datos (`@Modifying`)

Para consultas personalizadas de actualización o borrado directo (ej. actualizar estados o realizar borrado lógico masivo):

*   **Obligatorio**: Configurar la anotación `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
    ```java
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);
    ```

---

## 5. Gestión de Borrado Lógico y Verificación Física

*   **Verificación de Existencia Física**: Se utiliza el método nativo `existsById(id)` heredado de `JpaRepository`:
    ```java
    // Nativo de Spring Data JPA (no requiere declaración manual en la interfaz):
    boolean existsById(Long id);
    ```

---

## 6. Plantilla Maestra de Referencia (AutorRepository)

```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.persistence.entity.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT new com.arrupeteca.dto.response.summary.AutorResumenResponse(" +
           "a.id, a.nombre, a.seudonimo, a.anioNacimiento, a.anioFallecimiento, " +
           "a.borradoLogico, p.nombre, n.nombre, g.nombre, o.titulo, " +
           "a.biografia) " +
           "FROM Autor a " +
           "LEFT JOIN a.paisNacimiento p " +
           "LEFT JOIN a.nacionalidad n " +
           "LEFT JOIN a.generoPrincipal g " +
           "LEFT JOIN a.obraCumbre o " +
           "WHERE (:borradoLogico IS NULL OR a.borradoLogico = :borradoLogico) " +
           "AND (:idNacionalidad IS NULL OR n.id = :idNacionalidad) " +
           "AND (:idPais IS NULL OR p.id = :idPais) " +
           "AND (:idGenero IS NULL OR g.id = :idGenero) " +
           "AND (:idObraCumbre IS NULL OR o.id = :idObraCumbre) " +
           "AND (:termino IS NULL OR :termino = '' OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR a.anioFallecimiento >= :anioInicio OR a.anioFallecimiento IS NULL) " +
           "AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)")
    Page<AutorResumenResponse> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("idObraCumbre") Long idObraCumbre,
            @Param("anioInicio") Year anioInicio,
            @Param("anioFin") Year anioFin,
            @Param("termino") String termino,
            Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("SELECT a FROM Autor a WHERE a.id = :id AND (:borradoLogico IS NULL OR a.borradoLogico = :borradoLogico)")
    Optional<Autor> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("SELECT a FROM Autor a " +
           "WHERE (LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "   OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:idExcluir IS NULL OR a.id != :idExcluir)")
    List<Autor> buscarDuplicados(@Param("termino") String termino, @Param("idExcluir") Long idExcluir, Sort sort);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 7. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿Hereda de las interfaces correctas?**
   * ¿Extiende de `JpaRepository<Entidad, Long>` únicamente sin incluir `JpaSpecificationExecutor`?
2. **¿Evita anotaciones redundantes?**
   * ¿Está la interfaz libre de la anotación `@Repository`?
3. **¿Utiliza Proyección `SELECT new` unificada en el método `busqueda`?**
   * ¿Tiene definido el método `busqueda(...)` con la consulta `@Query("SELECT new ...")` que construye directamente el DTO Resumen aceptando `@Param("borradoLogico") Boolean borradoLogico`?
4. **¿Tiene optimización de consultas N+1 con `@EntityGraph` para vistas de detalle?**
   * ¿El método `busquedaPorId(id, borradoLogico)` utiliza `@EntityGraph` con las relaciones `@ManyToOne`?
5. **¿Los métodos `@Modifying` son seguros para el contexto de Hibernate?**
   * ¿Tienen todos los métodos `@Modifying` configurado `clearAutomatically = true` y `flushAutomatically = true`?
