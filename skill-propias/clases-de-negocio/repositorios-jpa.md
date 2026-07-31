# Estándar de Desarrollo: Repositorios JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de acceso a datos (Repositorios) en Arrupeteca. Su objetivo es garantizar consultas altamente eficientes a la base de datos, evitar problemas de rendimiento N+1 y asegurar la consistencia del estado de las entidades en la caché de primer nivel de Hibernate.

---

## 1. Estructura y Herencia

Todo repositorio debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona métodos CRUD estándar de forma automática.
*   `JpaSpecificationExecutor<Entity>`: Permite ejecutar consultas dinámicas y avanzadas utilizando la API de Criterios de JPA (Specifications).

### Regla de Limpieza de Código (Clean Code):
*   **Prohibido**: Utilizar la anotación `@Repository` sobre la interfaz del repositorio. Dado que extiende de `JpaRepository`, Spring Data JPA lo detecta y lo registra de forma automática en el contexto de aplicación. Colocar la anotación es redundante.

---

## 2. Optimización con `@EntityGraph` (Prevenir el Problema N+1)

Hibernate utiliza carga perezosa (`FetchType.LAZY`) por defecto en las relaciones. Sin embargo, al consultar listados o detalles de entidades que serán mapeadas a DTOs de salida, acceder a atributos de relaciones puede generar decenas de consultas individuales adicionales en bucle (el problema N+1).

*   **Solución Obligatoria**: Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})` en métodos de consulta clave (`findAll`, `findById`, `findOne`, etc.).
*   **Propósito**: Fuerza a Hibernate a generar una única consulta SQL con sentencias `LEFT JOIN FETCH` para traer todos los objetos relacionados de un solo viaje, optimizando la base de datos y preparando los datos para ser cacheados sin lanzar excepciones `LazyInitializationException`.
*   **Restricción Crítica (Colecciones ToMany)**:
    *   **Prohibido**: Incluir colecciones (`@ManyToMany` o `@OneToMany`, ej. `categorias` u `obras`) dentro del `@EntityGraph` junto con otras colecciones o en consultas de listados paginados.
    *   *Razón*: Carga múltiples colecciones en paralelo provocando la excepción `MultipleBagFetchException` en Hibernate o generando un Producto Cartesiano en memoria que degrada drásticamente el rendimiento. Además, la paginación con colecciones obliga a Hibernate a paginar en memoria (JVM), lo cual es extremadamente ineficiente.
    *   *Solución*: Configurar `@BatchSize` a nivel de colección o globalmente en `application.properties` (`spring.jpa.properties.hibernate.default_batch_fetch_size=100`) para que las colecciones se carguen en lotes optimizados independientes.

---

## 3. Proyecciones por Constructor (`SELECT new`) para Búsquedas Maestras de Admin

Para las tablas de administración paginadas de alto tráfico y volumen (ej. `busquedaMaestraAdmin` en `Autor`, `Obra`, `Libro`, `EjemplarLibro`), las consultas no deben recuperar entidades completas ni pasar por MapStruct.

*   **Uso de `SELECT new`**: Definir consultas JPQL con la sintaxis `@Query("SELECT new com.arrupeteca.dto.response.summary.MiResumenResponse(...) FROM ...")`.
*   **Ventajas de Rendimiento**:
    *   **Bypassea el Persistence Context (Caché L1)**: Hibernate no crea proxies, snapshots de auditoría ni rastreo de cambios (*dirty checking*), reduciendo entre un 70% y 85% la memoria RAM consumida en la JVM.
    *   **Tráfico Mínimo de Red**: Lee únicamente de la base de datos las columnas necesarias para el DTO Resumen.
    *   **Sin MapStruct en Lecturas Paginadas**: La base de datos y Hibernate entregan directamente la lista de DTOs finales lista para serializarse a JSON.
*   **Filtrado de Colecciones `@ManyToMany` sin Producto Cartesiano**:
    *   Al filtrar por colecciones (como géneros, categorías o etiquetas), **evitar `JOIN` directo en el `WHERE`**. Usar el patrón `EXISTS (SELECT 1 FROM ...)`:
        ```jpql
        AND (:idGenero IS NULL OR EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero))
        ```
    *   *Propósito*: Evita la duplicación de filas en el ResultSet de la base de datos (producto cartesiano) sin necesidad de aplicar `query.distinct(true)`.

---

## 4. Modificaciones en Base de Datos (`@Modifying`)

Para consultas personalizadas de actualización o borrado directo (ej. actualizar estados o realizar borrado lógico masivo):

*   **Obligatorio**: Configurar la anotación `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
    ```java
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);
    ```
*   **Por qué es crítico**:
    *   `flushAutomatically = true`: Envía a la base de datos cualquier cambio pendiente almacenado en memoria antes de que se ejecute la consulta SQL de modificación.
    *   `clearAutomatically = true`: Vacía el contexto de persistencia (Caché L1) de Hibernate inmediatamente después de ejecutar la consulta. Esto evita lecturas obsoletas/sucias, obligando a Hibernate a recargar la entidad actualizada desde la base de datos cuando se vuelva a solicitar.

---

## 4. Gestión de Borrado Lógico y Compatibilidad MySQL (COUNT)

Dado que el borrado lógico preserva la fila en la tabla, necesitamos chequear si un ID existe físicamente (incluso si está borrado lógicamente) antes de activarlo o desactivarlo. 

*   **Problema de Compatibilidad**: En MySQL, la función booleana nativa `SELECT COUNT(*) > 0` retorna un entero (`Integer`), lo cual lanza un error `ClassCastException` si intentamos recibirlo en Java como `boolean`.
*   **Solución Estándar**: Para validar existencia física de manera compatible con cualquier motor de base de datos (H2, MySQL, PostgreSQL, etc.), se debe realizar un conteo numérico (`long`) y comprobar en el servicio si el valor es mayor a cero:
    ```java
    @Query(value = "SELECT COUNT(*) FROM autor WHERE id_autor = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);
    ```
*   En la capa de servicio:
    ```java
    if (autorRepository.countByIdIncludeDeleted(id) == 0) {
        throw new ValidacionNegocioException("No se encontró el registro...");
    }
    ```

---

## 6. Plantilla Base Recomendada (Ejemplo de AutorRepository)

```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.persistence.entity.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor> {

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
           "AND (:termino IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR a.anioFallecimiento >= :anioInicio OR a.anioFallecimiento IS NULL) " +
           "AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)")
    Page<AutorResumenResponse> busquedaMaestraAdminProyectada(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("anioInicio") Year anioInicio,
            @Param("anioFin") Year anioFin,
            @Param("termino") String termino,
            Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Page<Autor> findAll(Specification<Autor> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    List<Autor> findAll(Specification<Autor> spec, Sort sort);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findById(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findOne(Specification<Autor> spec);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM autor WHERE id_autor = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 7. Lista de Autoverificación (Checklist del Desarrollador)

Antes de considerar listo un repositorio, verifique el cumplimiento de los siguientes puntos:

1. **¿Hereda de las interfaces correctas?**
   * ¿Extiende de `JpaRepository<Entidad, Long>` y de `JpaSpecificationExecutor<Entidad>`?
2. **¿Evita anotaciones redundantes?**
   * ¿Está la interfaz libre de la anotación `@Repository`?
3. **¿Utiliza Proyección `SELECT new` para la Búsqueda Maestra de Admin?**
   * ¿Tiene definido un método `busquedaMaestraAdminProyectada(...)` con la consulta `@Query("SELECT new ...")` que construye directamente el DTO Resumen?
   * Al filtrar por colecciones `@ManyToMany`, ¿utiliza el patrón `EXISTS (SELECT 1 FROM ...)` para evitar duplicación de filas (producto cartesiano)?
4. **¿Tiene optimización de consultas N+1 con `@EntityGraph` para vistas de detalle?**
   * ¿Los métodos de lectura de entidades (`findById`, `findOne`, `findAll`) tienen anotado `@EntityGraph` con relaciones `@ManyToOne`/`@OneToOne` únicamente?
5. **¿Los métodos `@Modifying` son seguros para el contexto de Hibernate?**
   * ¿Tienen todos los métodos `@Modifying` configurado `clearAutomatically = true` y `flushAutomatically = true`?
6. **¿Se utiliza `@Param` explícito?**
   * ¿Todos los parámetros en los métodos `@Query` tienen su correspondiente `@Param("nombre")`?
7. **¿El método de verificación de existencia física es compatible con MySQL?**
   * Si la entidad maneja borrado lógico, ¿existe el método native query `countByIdIncludeDeleted` retornando un `long` en lugar de una comparación booleana directa?

---
*Este estándar garantiza que la capa de persistencia sea consultada de forma eficiente y proteja el estado transaccional del sistema.*
*Este estándar garantiza que la capa de persistencia sea consultada de forma eficiente y proteja el estado transaccional del sistema.*
