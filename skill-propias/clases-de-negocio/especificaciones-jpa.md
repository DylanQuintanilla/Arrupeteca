# Estándar de Desarrollo: Especificaciones JPA (Arrupeteca)

Este documento establece las directrices y mejores prácticas para el uso de **JPA Specifications** (API de Criterios) en Arrupeteca. Su objetivo es centralizar la lógica de consultas dinámicas avanzadas, asegurar la reutilización de predicados SQL y mantener la legibilidad de la capa de datos.

---

## 1. ¿Por qué usar Specifications?

En lugar de crear métodos de consulta rígidos o consultas `@Query` gigantescas con condicionales complejos en los repositorios, las Specifications permiten construir consultas SQL dinámicamente en tiempo de ejecución combinando pequeños bloques (predicados) mediante operadores lógicos (`.and()`, `.or()`, `.not()`).

---

## 2. Anatomía de una Clase Specification (Qué lleva y Cuándo se usan)

Toda clase `[Entidad]Specification` se divide funcionalmente en **dos capas de métodos**:

### A. Predicados Atómicos (Filtros Individuales)
Cada método atómico representa **una sola regla de negocio** o criterio de filtrado. Se usan de forma independiente o como bloques de construcción.

1. **`tieneId(Long id)`**: Filtro exacto por clave primaria.
2. **`esBorrado(Boolean esBorrado)`**: Filtro por estado lógico (`null` = sin filtro para admin; `false` = activo; `true` = eliminado).
3. **`tiene[Relacion](Long idRelacion)`**: Filtro por clave foránea usando `JoinType.LEFT` (ej: `tieneNacionalidad`, `tieneEditorial`, `tieneMueble`).
4. **`contieneTermino(String termino)`**: Búsqueda por coincidencia de texto parcial (`LIKE %termino%`) en campos principales como `nombre`, `titulo`, `isbn`, `codigoBarras` o `seudonimo`.
5. **`vivoEnPeriodo / coincideRango`**: Filtro por intersección de rangos numéricos o de fechas.

### B. Orquestadores de Búsqueda (Especificaciones Compuestas)
Métodos de alto nivel que combinan múltiples predicados atómicos usando `.and()`. Se invocan directamente desde los Servicios:

1. **`filtroMaestroAdmin(...)`**:
   - **Cuándo se usa**: En la ruta `GET /api/v1/[recurso]/admin/busqueda-maestra`.
   - **Qué lleva**: Todos los filtros posibles del sistema (incluyendo estado borrado, rangos, categorización y término).
2. **`filtroBusquedaGlobal(...)`**:
   - **Cuándo se usa**: En la ruta pública `GET /api/v1/[recurso]` de búsqueda para alumnos.
   - **Qué lleva**: Fuerza `esBorrado(false)` + búsqueda textual amplia en múltiples campos y tablas asociadas.

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

## 4. Estado de Borrado Lógico en Specifications

Para que la búsqueda maestra de administración sea consistente, el método de filtrado de borrado lógico no debe imponer filtros si el parámetro es nulo:

*   **Si `esBorrado` es `null`**: Debe retornar `null` para que no se filtre por este estado (permitiendo al admin ver registros borrados y activos juntos).
*   **Si `esBorrado` tiene valor (`true`/`false`)**: Filtra estrictamente por el estado enviado.
*   En las consultas del catálogo público, el servicio debe llamar de forma explícita a `esBorrado(false)` para ocultar los eliminados a los usuarios comunes.

```java
public static Specification<Autor> esBorrado(Boolean esBorrado) {
    return (root, query, cb) -> {
        if (esBorrado == null) {
            return null;
        }
        return cb.equal(root.get("borradoLogico"), esBorrado);
    };
}
```

---

## 5. Lógica de Intersección de Fechas

Al realizar búsquedas por periodos históricos (ej: buscar autores activos en un siglo o rango de años), la especificación debe implementar la **Lógica de Intersección de Periodos** en lugar de filtrar solo la fecha de nacimiento.
*   **Lógica**: El autor debe haber nacido antes del fin del filtro Y (haber fallecido después del inicio del filtro O seguir vivo hoy).
*   **SQL**: `birth_year <= fin AND (death_year >= inicio OR death_year IS NULL)`

```java
public static Specification<Autor> vivoEnPeriodo(Year inicio, Year fin) {
    return (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        if (fin != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("anioNacimiento"), fin));
        }
        if (inicio != null) {
            Predicate murioDespues = cb.greaterThanOrEqualTo(root.get("anioFallecimiento"), inicio);
            Predicate sigueVivo = cb.isNull(root.get("anioFallecimiento"));
            predicates.add(cb.or(murioDespues, sigueVivo));
        }
        return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
    };
}
```

---

## 6. Plantilla Base Recomendada (Ejemplo de AutorSpecification)

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
        return (root, query, cb) -> {
            if (esBorrado == null) {
                return null;
            }
            return cb.equal(root.get("borradoLogico"), esBorrado);
        };
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

    public static Specification<Autor> vivoEnPeriodo(Year inicio, Year fin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("anioNacimiento"), fin));
            }
            if (inicio != null) {
                Predicate murioDespues = cb.greaterThanOrEqualTo(root.get("anioFallecimiento"), inicio);
                Predicate sigueVivo = cb.isNull(root.get("anioFallecimiento"));
                predicates.add(cb.or(murioDespues, sigueVivo));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Autor> filtroMaestroAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero,
            Year anioInicio, Year anioFin, String termino) {
        return Specification.where(esBorrado(esBorrado))
                .and(tieneNacionalidad(idNacionalidad))
                .and(tienePais(idPais))
                .and(tieneGenero(idGenero))
                .and(vivoEnPeriodo(anioInicio, anioFin))
                .and(contieneTermino(termino));
    }

    public static Specification<Autor> filtroBusquedaGlobal(String palabra, Year inicio, Year fin) {
        return Specification.where(esBorrado(false))
                .and(busquedaGlobal(palabra))
                .and(vivoEnPeriodo(inicio, fin));
    }
}
```

---

## 7. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿Es una clase de utilidad cerrada?**
   * ¿Tiene la clase un constructor privado sin argumentos para evitar instanciaciones directas?
2. **¿Los condicionales `if` cumplen con Checkstyle?**
   * ¿Tienen todos los `if` sus respectivas llaves `{}` para verificar la sintaxis?
3. **¿Se manejan correctamente los valores nulos?**
   * ¿Retornan `null` los predicados si el parámetro de filtro recibido es nulo o vacío?
   * ¿El método `esBorrado()` retorna `null` si el parámetro es nulo para que el admin pueda buscar sin filtro de estado?
4. **¿Se usa la lógica de intersección en fechas?**
   * ¿Las búsquedas por año emplean la lógica `vivoEnPeriodo()` (nacido antes del fin Y (muerto después del inicio o vivo)) para una precisión histórica correcta?
5. **¿Los joins usan LEFT JOIN?**
   * ¿Se configuraron los joins de relaciones usando explícitamente `JoinType.LEFT`?
6. **¿Se configuró distinct seguro para paginaciones?**
   * Al usar `query.distinct(true)` en joins, ¿se validó que el tipo de consulta no sea de conteo (`query.getResultType() != Long.class`)?
