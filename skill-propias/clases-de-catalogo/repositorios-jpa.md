# Estándar de Desarrollo: Repositorios JPA para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de los Repositorios de datos en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Estructura y Herencia

Todo repositorio de catálogo debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona métodos CRUD básicos automáticos.
*   *(Opcional)*: `JpaSpecificationExecutor<Entity>` si se requieren filtros dinámicos o búsquedas avanzadas.

### Regla de Limpieza de Código (Clean Code):
*   **Prohibido**: Utilizar la anotación `@Repository` sobre la interfaz del repositorio. Spring Data JPA lo detecta y lo registra de forma automática.

---

## 2. Modificaciones en Base de Datos (`@Modifying`)

Para los métodos que realicen cambios lógicos directos sobre la entidad en base de datos (como la desactivación o activación por ID):

*   **Obligatorio**: Configurar la anotación `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
*   **Propósito**: Evita estados inconsistentes o lecturas sucias en la memoria de Hibernate (Contexto de Persistencia) tras ejecutar las modificaciones.

---

## 3. Gestión de Borrado Lógico y Compatibilidad MySQL (COUNT)

Para validar la existencia física real de un registro antes de modificar su estado, debemos realizar un conteo nativo seguro:

*   **Problema de Compatibilidad**: En MySQL, la función booleana nativa `SELECT COUNT(*) > 0` retorna un entero (`Integer`), lo cual genera un error de tipo al recibirse como `boolean` en Java.
*   **Solución Estándar**: Definir una consulta nativa que retorne un tipo numérico plano `long` (`countByIdIncludeDeleted`) y realizar la comprobación de existencia en la capa de servicio:
    ```java
    @Query(value = "SELECT COUNT(*) FROM categoria WHERE id_categoria = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);
    ```

---

## 4. Plantilla Base Recomendada (Ejemplo de CategoriaRepository)

```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Categoria> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Categoria> findAllProjectedBy(Pageable pageable);

    Optional<Categoria> findProjectedById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Categoria c SET c.borradoLogico = true WHERE c.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Categoria c SET c.borradoLogico = false WHERE c.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM categoria WHERE id_categoria = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 5. Lista de Autoverificación (Checklist)

1. **¿Hereda de las interfaces correctas?**
   * ¿Extiende de `JpaRepository<Entidad, Long>`?
2. **¿Está libre de la anotación `@Repository`?**
   * ¿Se removió la anotación redundante en la interfaz?
3. **¿Las consultas de modificación `@Modifying` son seguras?**
   * ¿Tienen configurado `clearAutomatically = true` y `flushAutomatically = true`?
4. **¿Los parámetros de `@Query` tienen `@Param`?**
   * ¿Todos los parámetros se mapean explícitamente con su nombre?
5. **¿El método de verificación física usa `long`?**
   * ¿La firma de `countByIdIncludeDeleted` retorna `long` para asegurar la compatibilidad con MySQL?
