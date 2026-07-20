# Estándar de Desarrollo: Request y Response DTOs (Arrupeteca)

Este documento establece las directrices definitivas para la creación y estructuración de los Objetos de Transferencia de Datos (DTOs) en Arrupeteca. Los DTOs actúan como la barrera de validación sintáctica (API Gateway) y control de contratos de comunicación entre el frontend y el backend.

---

## 1. Clasificación y Propósito de los DTOs

Para cumplir con el estándar de rendimiento **Summary vs Detail**, cada módulo de negocio debe estructurar sus DTOs en tres variantes basadas en **Java Records** (inmutables por defecto, reduciendo el boilerplate):

| Categoría DTO | Convención de Nombre | Capa / Dirección | Propósito | Características de Validación |
| :--- | :--- | :--- | :--- | :--- |
| **Request** | `[Entidad]Request.java` | Entrada (Cliente $\rightarrow$ API) | Creación y actualización de datos. | **Validación Sintáctica** (Formatos, límites de caracteres) y soporte a edición parcial mediante **`JsonNullable<T>`**. |
| **Resumen** | `[Entidad]ResumenResponse.java` | Salida (API $\rightarrow$ Cliente) | Listados paginados rápidos. Evita traer relaciones pesadas o colecciones. | Sin validación de negocio. Anotaciones de formato para OpenAPI. |
| **Detalle** | `[Entidad]DetalleResponse.java` | Salida (API $\rightarrow$ Cliente) | Consulta individual por ID. Contiene relaciones anidadas completas y auditoría. | Incluye auditoría, versión y objetos `Resumen` de entidades relacionadas. |

---

## 2. Separación de Responsabilidades y Validación

*   **Validación Sintáctica (En el DTO)**: Toda validación estática de longitud, formato o valores permitidos debe residir en el Request DTO mediante anotaciones estándar de Java Validation (ej: `@Size`, `@Positive`, `@PastOrPresent`).
*   **Validación Lógica/Negocio (En el Servicio)**: Las comprobaciones de obligatoriedad según la acción (crear vs. actualizar), unicidad o lógica condicional deben residir **exclusivamente en la capa de Servicio**. Esto evita colisiones de validación en peticiones parciales (`PUT`/`PATCH`).
*   **Anotaciones Comunes en DTOs**:
    *   `@Size(max = X, message = "...")`: Límite superior de caracteres mapeado idénticamente al tamaño máximo de la columna en la BD.
    *   `@Positive(message = "...")` / `@PositiveOrZero`: Para IDs de relaciones y montos.
    *   `@PastOrPresent(message = "...")`: Para años o fechas históricas.

### Lógica de Fechas Cruzadas (`@AssertTrue`)
Para validaciones estáticas entre atributos (como verificar que el año de nacimiento no supere al de fallecimiento):
*   Se declaran como métodos públicos que retornan `boolean` dentro del cuerpo del Record.
*   Al validar campos `JsonNullable`, se debe extraer su valor de forma segura si están presentes:
    ```java
    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        Year nacimiento = (anioNacimiento != null && anioNacimiento.isPresent()) ? anioNacimiento.get() : null;
        Year fallecimiento = (anioFallecimiento != null && anioFallecimiento.isPresent()) ? anioFallecimiento.get() : null;
        if (nacimiento == null || fallecimiento == null) {
            return true;
        }
        return !nacimiento.isAfter(fallecimiento);
    }
    ```

---

## 3. Actualización Parcial con `JsonNullable`

Para dar soporte a peticiones de actualización parcial (`PATCH`/`PUT` parcial) donde los campos del DTO pueden ser omitidos, enviados con valor, o enviados explícitamente como nulos (borrado de datos), debemos envolver los tipos de datos opcionales en **`JsonNullable<T>`**:

*   **Presente con valor**: `"seudonimo": "Gabo"` $\rightarrow$ `JsonNullable.of("Gabo")`
*   **Presente con nulo (borrado)**: `"seudonimo": null` $\rightarrow$ `JsonNullable.of(null)`
*   **Ausente (no actualizar)**: Se omite del JSON $\rightarrow$ `JsonNullable.undefined()`

El campo `version` (bloqueo optimista) no debe envolverse en `JsonNullable`, sino declararse como `Long version` simple (puede llegar como nulo en la creación).

---

## 4. Deserialización de Jackson y OpenAPI

*   **Deserialización Segura**: Decorar los Records con `@Builder` y la anotación **`@Jacksonized`**. Esto indica a Jackson que utilice el patrón Builder generado por Lombok.
*   **Ignorar propiedades desconocidas**: Usar `@JsonIgnoreProperties(ignoreUnknown = true)` en los Request DTOs.
*   **Documentación en Respuestas**: Las anotaciones de validación (`@NotNull`, `@NotBlank`) en los DTOs de salida (`Resumen` y `Detalle`) no se validan en ejecución, sino que actúan como metadatos para **Springdoc OpenAPI (Swagger UI)**.

---

## 5. Plantillas de Ejemplo (Módulo Autor)

### A. Request DTO
```java
package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorRequest(
    JsonNullable<String> nombre,

    JsonNullable<String> seudonimo,

    JsonNullable<Year> anioNacimiento,

    JsonNullable<Year> anioFallecimiento,

    JsonNullable<String> biografia,

    Long version,

    JsonNullable<Long> idPaisNacimiento,

    JsonNullable<Long> idNacionalidad,

    JsonNullable<Long> idGeneroPrincipal,

    JsonNullable<Long> idObraCumbre
) {
    public AutorRequest {}

    @AssertTrue(message = "El nombre no puede exceder 100 caracteres")
    public boolean isNombreLongitudValida() {
        if (nombre == null || !nombre.isPresent() || nombre.get() == null) {
            return true;
        }
        return nombre.get().length() <= 100;
    }

    @AssertTrue(message = "El seudonimo no puede exceder 50 caracteres")
    public boolean isSeudonimoValido() {
        if (seudonimo == null || !seudonimo.isPresent() || seudonimo.get() == null) {
            return true;
        }
        return seudonimo.get().length() <= 50;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        Year nacimiento = (anioNacimiento != null && anioNacimiento.isPresent()) ? anioNacimiento.get() : null;
        Year fallecimiento = (anioFallecimiento != null && anioFallecimiento.isPresent()) ? anioFallecimiento.get() : null;
        if (nacimiento == null || fallecimiento == null) {
            return true;
        }
        return !nacimiento.isAfter(fallecimiento);
    }
}
```

### B. Response Resumen DTO
```java
package com.arrupeteca.dto.response.summary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record AutorResumenResponse(
    @NotNull Long id,
    @NotBlank String nombre,
    String seudonimo,
    Year anioNacimiento,
    Year anioFallecimiento,
    boolean borradoLogico,
    String nombrePais,
    String nombreNacionalidad,
    String nombreGeneroPrincipal,
    String nombreObraCumbre,
    String biografiaResumida
) { }
```

### C. Response Detalle DTO
```java
package com.arrupeteca.dto.response.detail;

import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.dto.response.summary.PaisResumen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record AutorDetalleResponse(
    @NotNull Long id,
    @NotBlank String nombre,
    String seudonimo,
    Year anioNacimiento,
    Year anioFallecimiento,
    @NotBlank String biografia,
    Long version,
    boolean borradoLogico,
    @NotBlank String creadoPor,
    @NotNull LocalDateTime fechaCreacion,
    String actualizadoPor,
    LocalDateTime fechaActualizacion,
    PaisResumen paisNacimiento,
    NacionalidadResumen nacionalidad,
    GeneroResumen generoPrincipal,
    ObraResumen obraCumbre
) { }
```

---

## 6. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿El DTO está libre de lógica de negocio condicional?**
   * ¿Se removieron validaciones de obligatoriedad condicional como `@NotBlank` o `@NotNull` para dar soporte a actualizaciones parciales?
2. **¿Los campos opcionales están envueltos en `JsonNullable<T>`?**
   * ¿Todos los campos mutables opcionales usan `JsonNullable` para permitir actualizar a nulo o ignorar si se omiten?
3. **¿La versión es un `Long` simple?**
   * ¿El atributo `version` es un `Long` de referencia para admitir valores nulos en creaciones?
4. **¿Tiene las anotaciones `@Builder` y `@Jacksonized`?**
   * ¿Se agregaron para permitir la deserialización de Jackson basada en Lombok en los records?
5. **¿Las validaciones en `@AssertTrue` controlan nulos de `JsonNullable`?**
   * ¿Se verifica `isPresent()` y si el valor es nulo antes de ejecutar aserciones lógicas?
