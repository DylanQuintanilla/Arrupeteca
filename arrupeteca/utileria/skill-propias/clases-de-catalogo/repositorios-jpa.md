# Est├índar de Desarrollo: Repositorios JPA para Cat├ílogos (Arrupeteca)

Este documento establece las directrices para el dise├▒o e implementaci├│n de los Repositorios de datos en los m├│dulos de **Cat├ílogo** (tablas maestras de referencia, ej. `Categoria`, `Ciclo`, `Disponibilidad`, `Edificio`, `Editorial`, `EstadoFisico`, `Etiqueta`, `Formato`, `Genero`, `Grado`, `Idioma`, `Mueble`, `Nacionalidad`, `Pais`, `Parentesco`, `Planta`, `Responsable`, `Salon`, `TipoAutoria`, `TipoUsuario`).

---

## 1. Estructura y Herencia

Todo repositorio de cat├ílogo debe definirse como una interfaz y extender de:
*   `JpaRepository<Entity, Long>`: Proporciona m├⌐todos CRUD b├ísicos autom├íticos (`save`, `findById`, `findAll`, `delete`, etc.).

### Regla de Limpieza de C├│digo (Clean Code):
*   **Prohibido**: Utilizar la anotaci├│n `@Repository` sobre la interfaz del repositorio. Spring Data JPA lo detecta y lo registra de forma autom├ítica.

---

## 2. Modificaciones en Base de Datos (`@Modifying`)

Para los m├⌐todos que realicen cambios l├│gicos directos sobre la entidad en base de datos (como la desactivaci├│n o activaci├│n por ID):

*   **Obligatorio**: Configurar la anotaci├│n `@Modifying` con las propiedades `clearAutomatically = true` y `flushAutomatically = true`.
*   **Prop├│sito**: Evita estados inconsistentes o lecturas sucias en el Contexto de Persistencia de Hibernate tras ejecutar las modificaciones.

---

## 3. Consultas Espec├¡ficas de Cat├ílogos

1. **`List<Entity> buscarActivosPorNombre(@Param("nombre") String nombre)`**: Devuelve la lista de entidades activas ordenadas alfab├⌐ticamente para su uso en selectores/dropdowns y autocompletado (con soporte a filtro opcional por `nombre`).
2. **`@Query busquedaAdmin`**: B├║squeda paginada para la tabla de administraci├│n que acepta filtrado parcial por nombre, filtro opcional por entidad padre (si aplica) y filtrado por estado `borradoLogico` (`null` = todos, `false` = activos, `true` = inactivos).
3. **`existsById(id)`**: M├⌐todo nativo heredado de `JpaRepository` para verificar la existencia f├¡sica de un ID independientemente de si est├í borrado l├│gicamente o no.
4. **`existsByNombreIgnoreCase`** / **`existsByNombreIgnoreCaseAnd[Padre]Id`**: Validaci├│n de unicidad de nombre (global o dentro del ├ímbito del padre).

---

## 4. Carga de Relaciones Jer├írquicas (`@EntityGraph`)

Cuando una entidad de cat├ílogo pertenece a una jerarqu├¡a o contiene relaciones `@ManyToOne` perezosas (`FetchType.LAZY`) que son requeridas en la respuesta de los DTOs (ejemplos: `Planta -> Edificio`, `Salon -> Planta -> Edificio`, `Mueble -> Salon -> Planta -> Edificio`):

*   **Obligatorio**: Colocar la anotaci├│n `@EntityGraph(attributePaths = {...})` sobre las consultas del repositorio (`busquedaAdmin` y `buscarActivosPorNombre`).
*   **Prop├│sito**: Realiza un `LEFT JOIN FETCH` en la base de datos dentro de una sola consulta SQL, evitando el problema de **consultas N+1** al mapear la entidad y sus relaciones padres en el Mapper/DTO.

### Ejemplo de Configuraci├│n en Cat├ílogos Anidados (Ej. `MuebleRepository`):

```java
@EntityGraph(attributePaths = {"salon", "salon.planta", "salon.planta.edificio"})
@Query("""
    SELECT m FROM Mueble m
    WHERE
        (:borradoLogico IS NULL OR m.borradoLogico = :borradoLogico)
        AND (:idSalon IS NULL OR m.salon.id = :idSalon)
        AND (:nombre IS NULL OR :nombre = '' OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
""")
Page<Mueble> busquedaAdmin(
        @Param("idSalon") Long idSalon,
        @Param("nombre") String nombre,
        @Param("borradoLogico") Boolean borradoLogico,
        Pageable pageable);
```

---

## 5. Plantilla Base Recomendada (Ejemplo de CategoriaRepository)

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

    @Query("SELECT c FROM Categoria c WHERE c.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY c.nombre ASC")
    List<Categoria> buscarActivosPorNombre(@Param("nombre") String nombre);

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

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 6. Lista de Autoverificaci├│n (Checklist)

1. **┬┐Hereda de `JpaRepository<Entidad, Long>`?**
2. **┬┐Est├í libre de la anotaci├│n `@Repository`?**
3. **┬┐Las consultas `@Modifying` incluyen `clearAutomatically = true` y `flushAutomatically = true`?**
4. **┬┐Los m├⌐todos `busquedaAdmin` manejan filtros nulos correctamente?**
5. **┬┐Se utiliza `@EntityGraph` en las consultas de cat├ílogos con relaciones padres para evitar N+1?**
6. **┬┐Se utiliza `existsById(id)` heredado de `JpaRepository` para la verificaci├│n f├¡sica de existencia?**
