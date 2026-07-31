# Estándar de Desarrollo: Repositorios JPA para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de los Repositorios de datos en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Ciclo`, `Disponibilidad`, `Edificio`, `Editorial`, `EstadoFisico`, `Etiqueta`, `Formato`, `Genero`, `Grado`, `Idioma`, `Mueble`, `Nacionalidad`, `Pais`, `Parentesco`, `Planta`, `Responsable`, `Salon`, `TipoAutoria`, `TipoUsuario`).

---

## 1. Estructura y Herencia

Todo repositorio de catálogo debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona métodos CRUD básicos automáticos (`save`, `findById`, `findAll`, `delete`, etc.).

### Regla de Limpieza de Código (Clean Code):
*   **Prohibido**: Utilizar la anotación `@Repository` sobre la interfaz del repositorio. Spring Data JPA lo detecta y lo registra de forma automática.

---

## 2. Modificaciones en Base de Datos (`@Modifying`)

Para los métodos que realicen cambios lógicos directos sobre la entidad en base de datos (como la desactivación o activación por ID):

*   **Obligatorio**: Configurar la anotación `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
*   **Propósito**: Evita estados inconsistentes o lecturas sucias en el Contexto de Persistencia de Hibernate tras ejecutar las modificaciones.

---

## 3. Consultas Específicas de Catálogos

1. **`List<Entity> findByBorradoLogicoFalseOrderByNombreAsc()`**: Devuelve la lista simple de entidades activas ordenadas alfabéticamente para su uso en selectores/dropdowns de la interfaz pública y administrativa.
2. **`@Query busquedaAdmin`**: Búsqueda paginada para la tabla de administración que acepta filtrado parcial por nombre y filtrado por estado `borradoLogico` (`null` = todos, `false` = activos, `true` = inactivos).
3. **`countByIdIncludeDeleted`**: Consulta nativa que retorna `long` para verificar la existencia física de un ID independientemente de si está borrado o no.
4. **`existsByNombreIgnoreCase`**: Validación de unicidad de nombre.

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

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByBorradoLogicoFalseOrderByNombreAsc();

    @Query("SELECT c FROM Categoria c WHERE " +
           "(:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR c.borradoLogico = :borradoLogico)")
    Page<Categoria> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

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

1. **¿Hereda de `JpaRepository<Entidad, Long>`?**
2. **¿Está libre de la anotación `@Repository`?**
3. **¿Las consultas `@Modifying` incluyen `clearAutomatically = true` y `flushAutomatically = true`?**
4. **¿Los métodos `busquedaAdmin` manejan filtros nulos correctamente?**
5. **¿El método de verificación física `countByIdIncludeDeleted` retorna `long`?**
