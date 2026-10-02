# Est├índar de Desarrollo: Repositorios JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de acceso a datos (Repositorios) en Arrupeteca. Su objetivo es garantizar consultas altamente eficientes a la base de datos, evitar problemas de rendimiento N+1 y asegurar la consistencia del estado de las entidades en la cach├⌐ de primer nivel de Hibernate.

---

## 1. Estructura y Herencia

Todo repositorio debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona m├⌐todos CRUD est├índar de forma autom├ítica.

### Regla de Limpieza de C├│digo (Clean Code):
*   **Prohibido**: Utilizar la anotaci├│n `@Repository` sobre la interfaz del repositorio. Dado que extiende de `JpaRepository`, Spring Data JPA lo detecta y lo registra de forma autom├ítica en el contexto de aplicaci├│n. Colocar la anotaci├│n es redundante.
*   **Prohibido**: Extender de `JpaSpecificationExecutor<Entity>`. Todas las b├║squedas paginadas y filtrados complejos del sistema se gestionan a trav├⌐s de m├⌐todos JPQL unificados proyectados (`busqueda(...)`), manteniendo los repositorios limpios y sin c├│digo muerto de especificaciones.

---

## 2. Optimizaci├│n con `@EntityGraph` (Prevenir el Problema N+1)

Hibernate utiliza carga perezosa (`FetchType.LAZY`) por defecto en las relaciones. Sin embargo, al consultar detalles por ID que ser├ín mapeados a DTOs de salida, acceder a atributos de relaciones puede generar decenas de consultas individuales adicionales en bucle (el problema N+1).

*   **Soluci├│n Obligatoria**: Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})` en m├⌐todos de consulta de detalle por ID (`busquedaPorId`).
*   **Prop├│sito**: Fuerza a Hibernate a generar una ├║nica consulta SQL con sentencias `LEFT JOIN FETCH` para traer todos los objetos relacionados de un solo viaje, optimizando la base de datos y preparando los datos para ser cacheados sin lanzar excepciones `LazyInitializationException`.
*   **Restricci├│n Cr├¡tica (Colecciones ToMany)**:
    *   **Prohibido**: Incluir colecciones (`@ManyToMany` o `@OneToMany`, ej. `categorias` u `obras`) dentro del `@EntityGraph` junto con otras colecciones o en consultas de listados paginados.
    *   *Raz├│n*: Carga m├║ltiples colecciones en paralelo provocando la excepci├│n `MultipleBagFetchException` en Hibernate o generando un Producto Cartesiano en memoria que degrada dr├ísticamente el rendimiento. Adem├ís, la paginaci├│n con colecciones obliga a Hibernate a paginar en memoria (JVM), lo cual es extremadamente ineficiente.

---

## 3. Proyecciones por Constructor (`SELECT new`) para Listados Paginados (Regla de Oro)

En Arrupeteca se establece la **Regla de Oro** para la capa de persistencia en clases de negocio:

*   **1. Todos los m├⌐todos que retornen un Listado Paginado de Resumen (`Page<Resumen>`)**:
    *   **Obligatorio**: Deben ejecutarse mediante **`SELECT new`** en consultas `@Query` de JPQL (ej. `@Query("SELECT new com.arrupeteca.dto.response.summary.AutorResumenResponse(...) FROM ...")`).
    *   **Ventajas de Rendimiento**: Bypassean la cach├⌐ L1 de Hibernate (Persistence Context), reducen la RAM en un 80%, consumen el m├¡nimo ancho de banda SQL y eliminan la necesidad de transformaci├│n MapStruct en lectura.
    *   **Filtrado `@ManyToMany` sin Producto Cartesiano**: Usar `EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)` en lugar de `JOIN` expl├¡cito en el `WHERE`.
*   **2. Todos los m├⌐todos que retornen un Detalle por ID (`DetalleResponse`)**:
    *   **Obligatorio**: Deben usar **`@EntityGraph`** para cargar la entidad completa con todas sus relaciones para visualizaci├│n detallada o edici├│n.

### ≡ƒôî Patr├│n de Dise├▒o: Consultas Unificadas Parametrizadas (`busqueda` y `busquedaPorId`)

La arquitectura de Arrupeteca consolida las consultas en **2 m├⌐todos universales parametrizados** en el repositorio:

1. **`busqueda(...)`**: Maneja tanto la b├║squeda p├║blica (enviando `borradoLogico = false`) como la de administraci├│n (enviando `borradoLogico = null` para ver todos o `borradoLogico = true` para ver inactivos).
2. **`busquedaPorId(...)`**: Retorna una entidad por ID aplicando `@EntityGraph` y el par├ímetro `@Param("borradoLogico") Boolean borradoLogico` (`false` para p├║blico, `null` para admin).

| M├⌐todo en Repository | Par├ímetro `borradoLogico` | Uso Principal |
|---|---|---|
| `busqueda(...)` | `false` | B├║squeda Global P├║blica y Autocompletes de activos |
| `busqueda(...)` | `null` / `true` | B├║squeda Completa de Administraci├│n (sin importar estado)|
| `busquedaPorId(...)` | `false` | Detalle p├║blico de registro activo |
| `busquedaPorId(...)` | `null` | Detalle administrativo por ID (sin importar estado) |

---

## 4. Modificaciones en Base de Datos (`@Modifying`)

Para consultas personalizadas de actualizaci├│n o borrado directo (ej. actualizar estados o realizar borrado l├│gico masivo):

*   **Obligatorio**: Configurar la anotaci├│n `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
    ```java
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);
    ```

---

## 5. Gesti├│n de Borrado L├│gico y Verificaci├│n F├¡sica

*   **Verificaci├│n de Existencia F├¡sica**: Se utiliza el m├⌐todo nativo `existsById(id)` heredado de `JpaRepository`:
    ```java
    // Nativo de Spring Data JPA (no requiere declaraci├│n manual en la interfaz):
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

## 7. Lista de Autoverificaci├│n (Checklist del Desarrollador)

1. **┬┐Hereda de las interfaces correctas?**
   * ┬┐Extiende de `JpaRepository<Entidad, Long>` ├║nicamente sin incluir `JpaSpecificationExecutor`?
2. **┬┐Evita anotaciones redundantes?**
   * ┬┐Est├í la interfaz libre de la anotaci├│n `@Repository`?
3. **┬┐Utiliza Proyecci├│n `SELECT new` unificada en el m├⌐todo `busqueda`?**
   * ┬┐Tiene definido el m├⌐todo `busqueda(...)` con la consulta `@Query("SELECT new ...")` que construye directamente el DTO Resumen aceptando `@Param("borradoLogico") Boolean borradoLogico`?
4. **┬┐Tiene optimizaci├│n de consultas N+1 con `@EntityGraph` para vistas de detalle?**
   * ┬┐El m├⌐todo `busquedaPorId(id, borradoLogico)` utiliza `@EntityGraph` con las relaciones `@ManyToOne`?
5. **┬┐Los m├⌐todos `@Modifying` son seguros para el contexto de Hibernate?**
   * ┬┐Tienen todos los m├⌐todos `@Modifying` configurado `clearAutomatically = true` y `flushAutomatically = true`?
