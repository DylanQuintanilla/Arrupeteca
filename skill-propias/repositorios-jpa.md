# Estándar de Desarrollo: Repositorios y Especificaciones JPA (Arrupeteca)

Este documento establece las reglas para la capa de acceso a datos, enfocándose en la eficiencia, el filtrado dinámico y el manejo de borrado lógico.

## 1. Repositorios (AutorRepository Pattern)

### 1.1. Estructura y Herencia
Todo repositorio debe extender de las siguientes interfaces:
*   `JpaRepository<Entity, ID>`: Para operaciones CRUD estándar.
*   `JpaSpecificationExecutor<Entity>`: Para permitir el uso de filtros dinámicos (Specifications).

### 1.2. Optimización con EntityGraph
Para evitar el problema de N+1 consultas, es obligatorio definir la carga de relaciones en los métodos de lectura:
*   Usar `@EntityGraph(attributePaths = {"relacion1", "relacion2"})`.
*   Aplicar tanto en `findById` como en los métodos de búsqueda personalizados.

### 1.3. Métodos de Búsqueda y Consultas (@Query)
*   **Paginación Obligatoria**: Todo método que devuelva múltiples registros (listados) debe usar `Pageable` y devolver `Page<Entity>`. Se prohibe el uso de `List<Entity>` para evitar problemas de memoria en tablas con crecimiento constante.
*   **Optional**: Siempre usar `Optional<Entity>` para métodos que devuelven un solo resultado por ID o atributo único.
*   **JPQL**: Preferido para lógica compleja que involucre múltiples campos o condiciones de negocio.
*   **Native Query**: Reservado para operaciones de alto rendimiento o que dependan de funciones específicas de MySQL (ej. actualizaciones masivas o conteos complejos).
*   **Ordenamiento**: Toda consulta que devuelva listas o páginas debe incluir un `ORDER BY` explícito para garantizar resultados predecibles.
*   **Parámetros**: Usar `@Param("nombre")` para claridad, especialmente en consultas multilínea.

### 1.4. Interfaz Limpia
*   No declarar métodos redundantes como `findByNombre` si ya se dispone de una `Specification` que cubra ese filtro. Esto mantiene el repositorio enfocado en la ejecución y no en la definición de filtros.

---

## 2. Especificaciones (AutorSpecification Pattern)

Las `Specifications` permiten construir consultas dinámicas de forma tipada y reutilizable.

### 2.1. Estructura de Clase
*   Debe ser una clase de utilidad en el paquete `.persistence.specification`.
*   Nombre: `[Entidad]Specification`.
*   Constructor privado para evitar instanciación.

### 2.2. Patrones de Filtrado Comunes
Cada método debe ser estático y devolver un `Specification<Entity>`:

*   **Borrado Lógico**: `esBorrado(Boolean)`. Si es `null`, filtrar por `false` por defecto.
*   **Relaciones (Joins)**: `tiene[Entidad](Long id)`. Usar `JoinType.LEFT` para evitar excluir registros si la relación es opcional.
*   **Búsqueda por Término**: `contieneTermino(String)`. Realizar `like` sobre los campos descriptivos (nombre, código, etc.).
*   **Rangos**: `[campo]Entre(Tipo inicio, Tipo fin)`. Manejar nulabilidad de ambos extremos.

### 2.3. Orquestadores (Filtros Maestros)
Es una buena práctica crear métodos que combinen múltiples filtros para casos de uso específicos:
*   `filtroMaestroAdmin`: Combina filtros de estado, relaciones y términos.
*   `filtroBusquedaPublica`: Fuerza filtros de seguridad (ej. solo no borrados).

---

## 3. Manejo de Borrado Lógico en el Repositorio

A falta de `@SQLRestriction` global, el repositorio debe gestionar el estado de forma explícita cuando sea necesario:
*   **Desactivar/Activar**: Usar métodos con `@Modifying` y `@Query` nativo para actualizar el campo `borrado_logico`.
*   **Existencia**: Crear `existsByIdIncludeDeleted` mediante consulta nativa si se requiere verificar la existencia física de un registro ignorando el filtro de borrado lógico.

---

## 4. Ejemplos de Plantillas Completas

### 4.1. Plantilla de Repositorio
```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Entidad;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EntidadRepository extends JpaRepository<Entidad, Long>, JpaSpecificationExecutor<Entidad> {

    @EntityGraph(attributePaths = {"relacion1", "relacion2"})
    Page<Entidad> findAll(Specification<Entidad> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"relacion1", "relacion2"})
    Optional<Entidad> findOne(Specification<Entidad> spec);

    @Modifying
    @Query("UPDATE Entidad e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM tabla WHERE id = :id", nativeQuery = true)
    boolean existsByIdIncludeDeleted(@Param("id") Long id);
}
```

### 4.2. Plantilla de Especificación
```java
package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Entidad;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class EntidadSpecification {
    private EntidadSpecification() {}

    public static Specification<Entidad> tieneId(Long id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Entidad> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> cb.equal(root.get("borradoLogico"), esBorrado != null ? esBorrado : false);
    }

    public static Specification<Entidad> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nombre")), pattern);
        };
    }

    public static Specification<Entidad> filtroBusqueda(String termino) {
        return Specification.where(esBorrado(false))
                .and(contieneTermino(termino));
    }
}
```

---
*Este estándar garantiza que la lógica de filtrado sea desacoplada de la base de datos y altamente reutilizable.*
rrado_logico`.
*   **Existencia**: Crear `existsByIdIncludeDeleted` mediante consulta nativa si se requiere verificar la existencia física de un registro ignorando el filtro de borrado lógico.

---

## 4. Ejemplos de Plantillas Completas

### 4.1. Plantilla de Repositorio
```java
package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Entidad;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EntidadRepository extends JpaRepository<Entidad, Long>, JpaSpecificationExecutor<Entidad> {

    @EntityGraph(attributePaths = {"relacion1", "relacion2"})
    Page<Entidad> findAll(Specification<Entidad> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"relacion1", "relacion2"})
    Optional<Entidad> findOne(Specification<Entidad> spec);

    @Modifying
    @Query("UPDATE Entidad e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM tabla WHERE id = :id", nativeQuery = true)
    boolean existsByIdIncludeDeleted(@Param("id") Long id);
}
```

### 4.2. Plantilla de Especificación
```java
package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Entidad;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class EntidadSpecification {
    private EntidadSpecification() {}

    public static Specification<Entidad> tieneId(Long id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<Entidad> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> cb.equal(root.get("borradoLogico"), esBorrado != null ? esBorrado : false);
    }

    public static Specification<Entidad> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nombre")), pattern);
        };
    }

    public static Specification<Entidad> filtroBusqueda(String termino) {
        return Specification.where(esBorrado(false))
                .and(contieneTermino(termino));
    }
}
```

---
*Este estándar garantiza que la lógica de filtrado sea desacoplada de la base de datos y altamente reutilizable.*
