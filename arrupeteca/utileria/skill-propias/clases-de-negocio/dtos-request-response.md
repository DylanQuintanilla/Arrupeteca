# Est├índar de Desarrollo: Request y Response DTOs (Arrupeteca)

Este documento establece las directrices definitivas para la creaci├│n y estructuraci├│n de los Objetos de Transferencia de Datos (DTOs) en Arrupeteca. Los DTOs act├║an como la barrera de validaci├│n sint├íctica (API Gateway) y control de contratos de comunicaci├│n entre el frontend y el backend.

---

## 1. Clasificaci├│n y Prop├│sito de los DTOs

Para cumplir con el est├índar de rendimiento **Summary vs Detail**, cada m├│dulo de negocio debe estructurar sus DTOs en tres variantes basadas en **Java Records** (inmutables por defecto, reduciendo el boilerplate):

| Categor├¡a DTO | Convenci├│n de Nombre | Capa / Direcci├│n | Prop├│sito | Caracter├¡sticas de Validaci├│n |
| :--- | :--- | :--- | :--- | :--- |
| **Request** | `[Entidad]Request.java` | Entrada (Cliente $\rightarrow$ API) | Creaci├│n y actualizaci├│n de datos. | **Validaci├│n Sint├íctica** (Formatos, l├¡mites de caracteres) y soporte a edici├│n parcial mediante **`JsonNullable<T>`**. |
| **Resumen** | `[Entidad]ResumenResponse.java` | Salida (API $\rightarrow$ Cliente) | Listados paginados r├ípidos. Evita traer relaciones pesadas o colecciones. | Sin validaci├│n de negocio. Anotaciones de formato para OpenAPI. |
| **Detalle** | `[Entidad]DetalleResponse.java` | Salida (API $\rightarrow$ Cliente) | Consulta individual por ID. Contiene relaciones anidadas completas y auditor├¡a. | Incluye auditor├¡a, versi├│n y objetos `Resumen` de entidades relacionadas. |

---

## 2. Separaci├│n de Responsabilidades y Validaci├│n

*   **Validaci├│n Sint├íctica (En el DTO)**: Toda validaci├│n est├ítica de longitud, formato o valores permitidos debe residir en el Request DTO mediante anotaciones est├índar de Java Validation (ej: `@Size`, `@Positive`, `@PastOrPresent`).
*   **Validaci├│n L├│gica/Negocio (En el Servicio)**: Las comprobaciones de obligatoriedad seg├║n la acci├│n (crear vs. actualizar), unicidad o l├│gica condicional deben residir **exclusivamente en la capa de Servicio**. Esto evita colisiones de validaci├│n en peticiones parciales (`PUT`/`PATCH`).
*   **Anotaciones Comunes en DTOs**:
    *   `@Size(max = X, message = "...")`: L├¡mite superior de caracteres mapeado id├⌐nticamente al tama├▒o m├íximo de la columna en la BD.
    *   `@Positive(message = "...")` / `@PositiveOrZero`: Para IDs de relaciones y montos.
    *   `@PastOrPresent(message = "...")`: Para a├▒os o fechas hist├│ricas.

### L├│gica de Fechas Cruzadas (`@AssertTrue`)
Para validaciones est├íticas entre atributos (como verificar que el a├▒o de nacimiento no supere al de fallecimiento):
*   Se declaran como m├⌐todos p├║blicos que retornan `boolean` dentro del cuerpo del Record.
*   Al validar campos `JsonNullable`, se debe extraer su valor de forma segura si est├ín presentes:
    ```java
    @AssertTrue(message = "El a├▒o de nacimiento no puede ser mayor al a├▒o de fallecimiento")
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

## 3. Actualizaci├│n Parcial con `JsonNullable`

Para dar soporte a peticiones de actualizaci├│n parcial (`PATCH`/`PUT` parcial) donde los campos del DTO pueden ser omitidos, enviados con valor, o enviados expl├¡citamente como nulos (borrado de datos), debemos envolver los tipos de datos opcionales en **`JsonNullable<T>`**:

*   **Presente con valor**: `"seudonimo": "Gabo"` $\rightarrow$ `JsonNullable.of("Gabo")`
*   **Presente con nulo (borrado)**: `"seudonimo": null` $\rightarrow$ `JsonNullable.of(null)`
*   **Ausente (no actualizar)**: Se omite del JSON $\rightarrow$ `JsonNullable.undefined()`

El campo `version` (bloqueo optimista) no debe envolverse en `JsonNullable`, sino declararse como `Long version` simple (puede llegar como nulo en la creaci├│n).

---

## 4. Deserializaci├│n de Jackson y OpenAPI

*   **Deserializaci├│n Segura**: Decorar los Records con `@Builder` y la anotaci├│n **`@Jacksonized`**. Esto indica a Jackson que utilice el patr├│n Builder generado por Lombok.
*   **Ignorar propiedades desconocidas**: Usar `@JsonIgnoreProperties(ignoreUnknown = true)` en los Request DTOs.
*   **Documentaci├│n en Respuestas**: Las anotaciones de validaci├│n (`@NotNull`, `@NotBlank`) en los DTOs de salida (`Resumen` y `Detalle`) no se validan en ejecuci├│n, sino que act├║an como metadatos para **Springdoc OpenAPI (Swagger UI)**.

---

## 5. Plantillas de Ejemplo (M├│dulo Autor)

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

    @AssertTrue(message = "El a├▒o de nacimiento no puede ser mayor al a├▒o de fallecimiento")
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

## 6. Lista de Autoverificaci├│n (Checklist del Desarrollador)

1. **┬┐El DTO est├í libre de l├│gica de negocio condicional?**
   * ┬┐Se removieron validaciones de obligatoriedad condicional como `@NotBlank` o `@NotNull` para dar soporte a actualizaciones parciales?
2. **┬┐Los campos opcionales est├ín envueltos en `JsonNullable<T>`?**
   * ┬┐Todos los campos mutables opcionales usan `JsonNullable` para permitir actualizar a nulo o ignorar si se omiten?
3. **┬┐La versi├│n es un `Long` simple?**
   * ┬┐El atributo `version` es un `Long` de referencia para admitir valores nulos en creaciones?
4. **┬┐Tiene las anotaciones `@Builder` y `@Jacksonized`?**
   * ┬┐Se agregaron para permitir la deserializaci├│n de Jackson basada en Lombok en los records?
5. **┬┐Las validaciones en `@AssertTrue` controlan nulos de `JsonNullable`?**
   * ┬┐Se verifica `isPresent()` y si el valor es nulo antes de ejecutar aserciones l├│gicas?
