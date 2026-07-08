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

## 3. Modificaciones en Base de Datos (`@Modifying`)

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

## 4. Gestión de Borrado Lógico y Consultas Nativa

Dado que el borrado lógico preserva la fila en la tabla, el método por defecto `existsById` de Spring Data JPA podría retornar `true` aunque el registro esté borrado lógicamente (dependiendo de la configuración del filtro global).

*   **Solución Estándar**: Para validar existencia física real sin interferencias lógicas, se debe declarar una consulta nativa explícita:
    ```java
    @Query(value = "SELECT COUNT(*) > 0 FROM autor WHERE id_autor = :id", nativeQuery = true)
    boolean existsByIdIncludeDeleted(@Param("id") Long id);
    ```

---

## 5. Plantilla Base Recomendada (Ejemplo de AutorRepository)

```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor> {

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Page<Autor> findAll(Specification<Autor> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findById(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findOne(Specification<Autor> spec);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("""
        SELECT a FROM Autor a
        WHERE (
            a.nombre LIKE CONCAT('%', :termino, '%')
            OR (a.seudonimo IS NOT NULL AND a.seudonimo LIKE CONCAT('%', :termino, '%'))
        )
        AND (:idExcluir IS NULL OR a.id != :idExcluir)
        ORDER BY a.nombre ASC
    """)
    List<Autor> buscarPosiblesDuplicados(
            @Param("termino") String termino,
            @Param("idExcluir") Long idExcluir
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM autor WHERE id_autor = :id", nativeQuery = true)
    boolean existsByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 6. Lista de Autoverificación (Checklist del Desarrollador)

Antes de considerar listo un repositorio, verifique el cumplimiento de los siguientes puntos:

1. **¿Hereda de las interfaces correctas?**
   * ¿Extiende de `JpaRepository<Entidad, Long>` y de `JpaSpecificationExecutor<Entidad>`?
2. **¿Evita anotaciones redundantes?**
   * ¿Está la interfaz libre de la anotación `@Repository`?
3. **¿Tiene optimización de consultas N+1?**
   * ¿Los métodos de listado (`findAll`) y búsqueda de detalle tienen anotado `@EntityGraph` con relaciones `@ManyToOne`/`@OneToOne` únicamente, evitando incluir colecciones `@ManyToMany`/`@OneToMany` para prevenir el producto cartesiano o paginación en memoria?
4. **¿Los métodos `@Modifying` son seguros para el contexto de Hibernate?**
   * ¿Tienen todos los métodos `@Modifying` configurado `clearAutomatically = true` y `flushAutomatically = true`?
5. **¿Se utiliza `@Param` explícito?**
   * ¿Todos los parámetros en los métodos `@Query` tienen su correspondiente `@Param("nombre")` para evitar problemas de resolución de parámetros en tiempo de compilación?
6. **¿El método de verificación de existencia física está presente?**
   * Si la entidad maneja borrado lógico, ¿existe el método native query `existsByIdIncludeDeleted` para chequear existencia física real?

---
*Este estándar garantiza que la capa de persistencia sea consultada de forma eficiente y proteja el estado transaccional del sistema.*
