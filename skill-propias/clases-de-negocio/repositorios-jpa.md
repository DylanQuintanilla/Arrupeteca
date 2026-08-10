# Estándar de Desarrollo: Repositorios JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de acceso a datos (Repositorios) en Arrupeteca. Su objetivo es garantizar consultas altamente eficientes a la base de datos, evitar problemas de rendimiento N+1 y asegurar la consistencia del estado de las entidades en la caché de primer nivel de Hibernate.

---

## 1. Estructura y Herencia

Todo repositorio debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona métodos CRUD estándar de forma automática.

### Regla de Limpieza de Código (Clean Code):
*   **Prohibido**: Utilizar la anotación `@Repository` sobre la interfaz del repositorio. Dado que extiende de `JpaRepository`, Spring Data JPA lo detecta y lo registra de forma automática en el contexto de aplicación. Colocar la anotación es redundante.
*   **Prohibido**: Extender de `JpaSpecificationExecutor<Entity>`. Todas las búsquedas paginadas y filtrados complejos del sistema se gestionan a través del método proyectado `busquedaMaestraAdminProyectada(...)`, manteniendo los repositorios limpios y sin código muerto de especificaciones.

---

## 2. Optimización con `@EntityGraph` (Prevenir el Problema N+1)

Hibernate utiliza carga perezosa (`FetchType.LAZY`) por defecto en las relaciones. Sin embargo, al consultar detalles por ID que serán mapeados a DTOs de salida, acceder a atributos de relaciones puede generar decenas de consultas individuales adicionales en bucle (el problema N+1).

*   **Solución Obligatoria**: Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})` en métodos de consulta de detalle por ID (`findById`, `findByIdAndBorradoLogicoFalse`).
*   **Propósito**: Fuerza a Hibernate a generar una única consulta SQL con sentencias `LEFT JOIN FETCH` para traer todos los objetos relacionados de un solo viaje, optimizando la base de datos y preparando los datos para ser cacheados sin lanzar excepciones `LazyInitializationException`.
*   **Restricción Crítica (Colecciones ToMany)**:
    *   **Prohibido**: Incluir colecciones (`@ManyToMany` o `@OneToMany`, ej. `categorias` u `obras`) dentro del `@EntityGraph` junto con otras colecciones o en consultas de listados paginados.
    *   *Razón*: Carga múltiples colecciones en paralelo provocando la excepción `MultipleBagFetchException` en Hibernate o generando un Producto Cartesiano en memoria que degrada drásticamente el rendimiento. Además, la paginación con colecciones obliga a Hibernate a paginar en memoria (JVM), lo cual es extremadamente ineficiente.

---

## 3. Proyecciones por Constructor (`SELECT new`) para Listados Paginados (Regla de Oro)

En Arrupeteca se establece la **Regla de Oro** para la capa de persistencia en clases de negocio:

*   **1. Todos los métodos que retornen un Listado Paginado de Resumen (`Page<Resumen>`)** (tales como `obtenerTodosActivos`, `obtenerTodos`, `busquedaGlobal`, `busquedaMaestraAdmin`, `buscarPorObraId`):
    *   **Obligatorio**: Deben ejecutarse mediante **`SELECT new`** en consultas `@Query` de JPQL (ej. `@Query("SELECT new com.arrupeteca.dto.response.summary.AutorResumenResponse(...) FROM ...")`).
    *   **Ventajas de Rendimiento**: Bypassean la caché L1 de Hibernate (Persistence Context), reducen la RAM en un 80%, consumen el mínimo ancho de banda SQL y eliminan la necesidad de transformación MapStruct en lectura.
    *   **Filtrado `@ManyToMany` sin Producto Cartesiano**: Usar `EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)` en lugar de `JOIN` explícito en el `WHERE`.
*   **2. Todos los métodos que retornen un Detalle por ID (`DetalleResponse`)** (tales como `obtenerPorIdActivo`, `obtenerPorId`):
    *   **Obligatorio**: Deben usar **`@EntityGraph`** para cargar la entidad completa con todas sus relaciones para visualización detallada o edición.

### 📌 Patrón de Diseño: 1 Consulta Proyectada Maestra para 4 Endpoints

En lugar de duplicar la sintaxis del constructor `SELECT new [Entidad]Resumen(...)` 4 veces distintas en el repositorio, la arquitectura de Arrupeteca implementa **1 sola Consulta Proyectada Maestra Parametrizada (`busquedaMaestraAdminProyectada`)**.

Esta única consulta atiende los **4 endpoints de listado paginado** pasando parámetros opcionales nulos desde el Servicio:

| Endpoint en Controller | Método en Service | Parámetros enviados a `busquedaMaestraAdminProyectada` |
|---|---|---|
| `GET /api/v1/[recurso]` | `obtenerTodosActivos` | `esBorrado = false`, demás filtros `null` |
| `GET /api/v1/[recurso]/global` | `busquedaGlobal` | `esBorrado = false`, `termino = palabra`, demás `null` |
| `GET /api/v1/[recurso]/admin` | `obtenerTodos` | `esBorrado = null` (trae activos y borrados), demás `null` |
| `GET /api/v1/[recurso]/admin/busqueda-maestra` | `busquedaMaestraAdmin` | Filtros dinámicos seleccionados por el administrador |

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
           "WHERE (:esBorrado IS NULL OR a.borradoLogico = :esBorrado) " +
           "AND (:idNacionalidad IS NULL OR n.id = :idNacionalidad) " +
           "AND (:idPais IS NULL OR p.id = :idPais) " +
           "AND (:idGenero IS NULL OR g.id = :idGenero) " +
           "AND (:idObraCumbre IS NULL OR o.id = :idObraCumbre) " +
           "AND (:termino IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR a.anioFallecimiento >= :anioInicio OR a.anioFallecimiento IS NULL) " +
           "AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)")
    Page<AutorResumenResponse> busquedaMaestraAdminProyectada(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("idObraCumbre") Long idObraCumbre,
            @Param("anioInicio") Year anioInicio,
            @Param("anioFin") Year anioFin,
            @Param("termino") String termino,
            Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("SELECT a FROM Autor a " +
           "WHERE (LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "   OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:idExcluir IS NULL OR a.id != :idExcluir)")
    List<Autor> buscarDuplicados(@Param("termino") String termino, @Param("idExcluir") Long idExcluir, Sort sort);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findById(Long id);

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
3. **¿Utiliza Proyección `SELECT new` para la Búsqueda Maestra de Admin?**
   * ¿Tiene definido un método `busquedaMaestraAdminProyectada(...)` con la consulta `@Query("SELECT new ...")` que construye directamente el DTO Resumen?
4. **¿Tiene optimización de consultas N+1 con `@EntityGraph` para vistas de detalle?**
   * ¿Los métodos de detalle por ID (`findById`, `findByIdAndBorradoLogicoFalse`) tienen anotado `@EntityGraph` con las relaciones `@ManyToOne`?
5. **¿Los métodos `@Modifying` son seguros para el contexto de Hibernate?**
   * ¿Tienen todos los métodos `@Modifying` configurado `clearAutomatically = true` y `flushAutomatically = true`?
