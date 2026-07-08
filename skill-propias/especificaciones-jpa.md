# Estándar de Desarrollo: Especificaciones JPA (Arrupeteca)

Este documento establece las directrices y mejores prácticas para el uso de **JPA Specifications** (API de Criterios) en Arrupeteca. Su objetivo es centralizar la lógica de consultas dinámicas avanzadas, asegurar la reutilización de predicados SQL y mantener la legibilidad de la capa de datos.

---

## 1. ¿Por qué usar Specifications?

En lugar de crear métodos de consulta rígidos o consultas `@Query` gigantescas con condicionales complejos en los repositorios, las Specifications permiten construir consultas SQL dinámicamente en tiempo de ejecución combinando pequeños bloques (predicados) mediante operadores lógicos (`.and()`, `.or()`, `.not()`).

---

## 2. Reglas de Diseño de la Clase de Especificación

*   **Clase de Utilidad**:
    *   Debe definirse en el paquete `com.arrupeteca.persistence.specification`.
    *   Debe tener un constructor privado vacío para evitar que sea instanciada.
*   **Métodos Estáticos**: Cada criterio de filtrado debe definirse como un método estático que retorne un objeto `Specification<Entidad>`.
*   **Cumplimiento de Checkstyle**:
    *   **Obligatorio**: Todos los condicionales dentro de los predicados deben utilizar llaves `{}` (regla `NeedBraces`).
    *   *Ejemplo Incorrecto*: `if (id == null) return null;`
    *   *Ejemplo Correcto*:
        ```java
        if (id == null) {
            return null;
        }
        ```

---

## 3. Manejo de Joins y Evitar Registros Duplicados

Cuando un criterio requiere unir tablas relacionadas (ej. buscar autores por país o nacionalidad):

*   **Uso de Left Join**: Usar explícitamente `JoinType.LEFT` para evitar excluir registros que tengan dichos campos relacionados en valor nulo (`null`).
*   **Distinct Query**: Al realizar JOINs en consultas complejas, la base de datos puede duplicar registros en las filas de retorno. Para solucionarlo:
    *   Aplicar `query.distinct(true)` de manera condicionada.
    *   **Crucial**: Validar que el tipo de retorno no sea una consulta de conteo (`Long.class` o `long.class`) antes de aplicar distinct, para evitar errores en las consultas de paginación automática de Spring Data.
    ```java
    if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
        query.distinct(true);
    }
    ```

---

## 4. Plantilla Base Recomendada (Ejemplo de AutorSpecification)

```java
package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.entity.Genero;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class AutorSpecification {

    private AutorSpecification() {
        // Constructor privado para evitar instanciación
    }

    public static Specification<Autor> tieneId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get("id"), id);
        };
    }

    public static Specification<Autor> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> cb.equal(root.get("borradoLogico"), esBorrado != null ? esBorrado : false);
    }

    public static Specification<Autor> tieneNacionalidad(Long idNacionalidad) {
        return (root, query, cb) -> {
            if (idNacionalidad == null) {
                return null;
            }
            Join<Autor, Nacionalidad> join = root.join("nacionalidad", JoinType.LEFT);
            return cb.equal(join.get("id"), idNacionalidad);
        };
    }

    public static Specification<Autor> tienePais(Long idPais) {
        return (root, query, cb) -> {
            if (idPais == null) {
                return null;
            }
            Join<Autor, Pais> join = root.join("paisNacimiento", JoinType.LEFT);
            return cb.equal(join.get("id"), idPais);
        };
    }

    public static Specification<Autor> tieneGenero(Long idGenero) {
        return (root, query, cb) -> {
            if (idGenero == null) {
                return null;
            }
            Join<Autor, Genero> join = root.join("generoPrincipal", JoinType.LEFT);
            return cb.equal(join.get("id"), idGenero);
        };
    }

    public static Specification<Autor> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) {
                return null;
            }
            String pattern = "%" + termino.trim() + "%";
            return cb.or(
                    cb.like(root.get("nombre"), pattern),
                    cb.like(root.get("seudonimo"), pattern)
            );
        };
    }

    public static Specification<Autor> busquedaGlobal(String palabra) {
        return (root, query, cb) -> {
            if (palabra == null || palabra.isBlank()) {
                return null;
            }
            String pattern = "%" + palabra.trim() + "%";

            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            Join<Autor, Pais> joinPais = root.join("paisNacimiento", JoinType.LEFT);
            Join<Autor, Nacionalidad> joinNacionalidad = root.join("nacionalidad", JoinType.LEFT);

            return cb.or(
                    cb.like(root.get("nombre"), pattern),
                    cb.like(root.get("seudonimo"), pattern),
                    cb.like(joinPais.get("nombre"), pattern),
                    cb.like(joinNacionalidad.get("nombre"), pattern)
            );
        };
    }

    public static Specification<Autor> anioNacimientoEntre(Year inicio, Year fin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("anioNacimiento"), inicio));
            }
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("anioNacimiento"), fin));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Autor> filtroMaestroAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero, String termino) {
        return Specification.where(esBorrado(esBorrado))
                .and(tieneNacionalidad(idNacionalidad))
                .and(tienePais(idPais))
                .and(tieneGenero(idGenero))
                .and(contieneTermino(termino));
    }
}
```

---

## 5. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿Es una clase de utilidad cerrada?**
   * ¿Tiene la clase un constructor privado sin argumentos para evitar instanciaciones directas?
2. **¿Los condicionales `if` cumplen con Checkstyle?**
   * ¿Tienen todos los `if` sus respectivas llaves `{}` para envolver el bloque de código de retorno?
3. **¿Se manejan correctamente los valores nulos?**
   * ¿Retornan `null` los predicados si el parámetro de filtro recibido es nulo o vacío? (Spring Data JPA descarta los predicados nulos al combinar consultas).
4. **¿Los joins usan LEFT JOIN?**
   * ¿Se configuraron los joins de relaciones usando explícitamente `JoinType.LEFT` para no descartar registros con campos relacionados nulos?
5. **¿Se configuró distinct seguro para paginaciones?**
   * Al usar `query.distinct(true)` en joins, ¿se validó que el tipo de consulta no sea de conteo (`query.getResultType() != Long.class`)?

---
*Este estándar asegura que las búsquedas dinámicas sean modulares, seguras y altamente legibles.*
