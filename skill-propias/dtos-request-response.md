# Estándar de Desarrollo: Request y Response DTOs (Arrupeteca)

Este documento establece las directrices definitivas para la creación y estructuración de los Objetos de Transferencia de Datos (DTOs) en Arrupeteca. Los DTOs actúan como la barrera de validación (API Gateway) y control de contratos de comunicación entre el frontend y el backend.

---

## 1. Clasificación y Propósito de los DTOs

Para cumplir con el estándar de rendimiento **Summary vs Detail**, cada módulo de negocio debe estructurar sus DTOs en tres variantes basadas en **Java Records** (inmutables por defecto, reduciendo el boilerplate):

| Categoría DTO | Convención de Nombre | Capa / Dirección | Propósito | Características de Validación |
| :--- | :--- | :--- | :--- | :--- |
| **Request** | `[Entidad]Request.java` | Entrada (Cliente $\rightarrow$ API) | Creación y actualización de datos. | **Validación Rigurosa** (`@NotBlank`, `@Size`, `@AssertTrue`, etc.). |
| **Resumen** | `[Entidad]ResumenResponse.java` | Salida (API $\rightarrow$ Cliente) | Listados paginados rápidos. Evita traer relaciones pesadas o colecciones. | Sin validación de negocio. Anotaciones de formato para OpenAPI. |
| **Detalle** | `[Entidad]DetalleResponse.java` | Salida (API $\rightarrow$ Cliente) | Consulta individual por ID. Contiene relaciones anidadas completas y auditoría. | Incluye auditoría, versión y objetos `Resumen` de entidades relacionadas. |

---

## 2. Separación de Responsabilidades y Validación

*   **Validaciones en DTOs (Request)**: Toda validación de formato, obligatoriedad, tamaño y lógica cruzada de atributos debe residir **exclusivamente** en los Request DTOs.
*   **Anotaciones Comunes**:
    *   `@NotBlank(message = "...")`: Obligatorio para textos. Previene cadenas vacías o espacios.
    *   `@NotNull(message = "...")`: Obligatorio para IDs, números y tipos como `Year` o `LocalDateTime`.
    *   `@Size(max = X, message = "...")`: Límite superior de caracteres mapeado idénticamente al tamaño máximo de la columna en la BD.
    *   `@Positive(message = "...")` / `@PositiveOrZero`: Para IDs y montos monetarios.
    *   `@PastOrPresent(message = "...")`: Para años de nacimiento o publicación.

### Validaciones Cruzadas (`@AssertTrue`)
Para validaciones lógicas que dependan de dos o más atributos (ej. verificar que se envíe al menos un nombre o seudónimo, o que el año de nacimiento no supere al de fallecimiento):
*   Se declaran como métodos públicos que retornan `boolean` dentro del cuerpo del Record.
*   Deben cumplir estrictamente con Checkstyle, utilizando llaves `{}` para envolver el código de retorno.
    ```java
    @AssertTrue(message = "El año de nacimiento no puede ser mayor al de fallecimiento")
    public boolean isFechasValidas() {
        if (anioNacimiento == null || anioFallecimiento == null) {
            return true;
        }
        return !anioNacimiento.isAfter(anioFallecimiento);
    }
    ```

---

## 3. Caché y Deserialización de Jackson

*   **Uso de Caché Local**: Dado que las respuestas de detalle se almacenan en caché local mediante Spring (RAM interna), **no es obligatorio** que los Records de salida implementen `Serializable` para la serialización. Esto simplifica su diseño.
*   **Deserialización Segura**: Los Java Records carecen de constructores sin argumentos. Para que Jackson pueda deserializar el JSON de entrada de forma fluida:
    *   Decorar los Records con `@Builder` y la anotación **`@Jacksonized`**. Esto indica a Jackson que utilice el patrón Builder generado por Lombok.
    *   Usar `@JsonIgnoreProperties(ignoreUnknown = true)` en los Request DTOs para evitar caídas del servicio si el frontend envía campos no mapeados.

---

## 4. Anotaciones de Validación en DTOs de Respuesta

*   Las anotaciones `@NotNull` y `@NotBlank` presentes en `Resumen` y `DetalleResponse` no son validadas por Spring en el flujo de salida.
*   **Propósito**: Se mantienen de forma intencional para servir como metadatos de documentación para **Springdoc OpenAPI (Swagger UI)**. Permiten que los desarrolladores de frontend identifiquen de forma interactiva qué campos están garantizados a nunca retornar nulos en el JSON.

---

## 5. Plantillas de Ejemplo (Módulo Autor)

### A. Request DTO
```java
package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorRequest(
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    String nombre,

    @Size(max = 50, message = "El seudonimo no puede exceder 50 caracteres")
    String seudonimo,

    @PastOrPresent(message = "El año de nacimiento no puede ser una fecha futura")
    Year anioNacimiento,

    @PastOrPresent(message = "El año de fallecimiento no puede ser una fecha futura")
    Year anioFallecimiento,

    @NotBlank(message = "La biografia no puede estar vacía")
    @Size(max = 5000, message = "La biografia es demasiado larga")
    String biografia,

    Long version,

    @Positive(message = "ID de país inválido")
    Long idPaisNacimiento,

    @Positive(message = "ID de nacionalidad inválido")
    Long idNacionalidad,

    @Positive(message = "ID de género inválido")
    Long idGeneroPrincipal,

    @Positive(message = "ID de obra inválido")
    Long idObraCumbre
) {
    public AutorRequest {}

    @AssertTrue(message = "El autor debe tener al menos un Nombre o un Seudonimo")
    public boolean isIdentidadValida() {
        boolean tieneNombre = (nombre != null && !nombre.isBlank());
        boolean tieneSeudonimo = (seudonimo != null && !seudonimo.isBlank());
        return tieneNombre || tieneSeudonimo;
    }

    @AssertTrue(message = "El año de nacimiento no puede ser mayor al año de fallecimiento")
    public boolean isFechasValidas() {
        if (anioNacimiento == null || anioFallecimiento == null) {
            return true;
        }
        return !anioNacimiento.isAfter(anioFallecimiento);
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

1. **¿Es un Java Record?**
   * ¿Todos los DTOs están definidos como `record` para garantizar inmutabilidad?
2. **¿Tiene las anotaciones de deserialización Jackson?**
   * ¿Todos los DTOs tienen `@Builder` y `@Jacksonized`?
   * En los Request DTOs, ¿se incluyó `@JsonIgnoreProperties(ignoreUnknown = true)`?
3. **¿La validación cruzada cumple con Checkstyle?**
   * En métodos `@AssertTrue`, ¿se envolvieron todos los bloques `if` en llaves `{}`?
4. **¿Los campos numéricos/IDs de relaciones están validados?**
   * ¿Los atributos `idRelacion` o similares tienen la anotación `@Positive` para evitar envíos con valores cero o negativos?
5. **¿Las longitudes de textos coinciden con la BD?**
   * ¿Los límites establecidos en `@Size(max = X)` de los Request DTOs se corresponden de manera exacta con el tamaño físico configurado en la entidad JPA / Base de Datos?

---
*Este estándar garantiza DTOs consistentes, autodeclarativos y seguros en toda la API de Arrupeteca.*
