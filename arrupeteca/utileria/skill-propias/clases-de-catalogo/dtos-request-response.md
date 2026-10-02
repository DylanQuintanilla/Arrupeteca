# Est├índar de Desarrollo: Request y Response DTOs para Cat├ílogos (Arrupeteca)

Este documento establece las directrices para la creaci├│n y estructuraci├│n de los Objetos de Transferencia de Datos (DTOs) en los m├│dulos de **Cat├ílogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Clasificaci├│n y Estructura de DTOs de Cat├ílogo

Dado que las tablas de cat├ílogo son simples, sus datos de salida son id├⌐nticos tanto para listados como para consultas individuales. Por lo tanto, no se requiere la divisi├│n de *Summary vs Detail*. Se centraliza la salida en un ├║nico DTO. Todos los DTOs se definen como **Java Records**:

| Categor├¡a DTO | Convenci├│n de Nombre | Capa / Direcci├│n | Prop├│sito | Caracter├¡sticas |
| :--- | :--- | :--- | :--- | :--- |
| **Request** | `[Entidad]Request.java` | Entrada (Cliente $\rightarrow$ API) | Creaci├│n y actualizaci├│n completa. | Validaci├│n sint├íctica cl├ísica (`@NotBlank`, `@Size`). |
| **Response** | `[Entidad]Response.java` | Salida (API $\rightarrow$ Cliente) | Representaci├│n unificada para listados, combos de selecci├│n y detalle individual. | Campos de identidad del cat├ílogo (ID, nombre, borradoLogico). |

---

## 2. Validaci├│n Sint├íctica en el Request DTO

Toda validaci├│n de formato, presencia y tama├▒o m├íximo debe residir en el Request DTO usando las anotaciones de validaci├│n est├índar:
*   `@NotBlank(message = "...")`: Obligatorio para el nombre o c├│digo del cat├ílogo.
*   `@Size(max = X, message = "...")`: Ajustado exactamente al tama├▒o de la columna en base de datos.

---

## 3. Plantillas de Ejemplo (M├│dulo Categor├¡a)

### A. Request DTO
```java
package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoriaRequest(
    @NotBlank(message = "El nombre de la categoria no puede estar vacio")
    @Size(max = 50, message = "El nombre de la categoria no puede exceder 50 caracteres")
    String nombre
) {
    public CategoriaRequest {}
}
```

### B. Response DTO Centralizado
```java
package com.arrupeteca.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record CategoriaResponse(
    @NotNull Long id,
    @NotBlank String nombre,
    boolean borradoLogico
) { }
```

---

## 4. Lista de Autoverificaci├│n (Checklist)

1. **┬┐Es un Java Record?**
   * ┬┐Todos los DTOs de cat├ílogo usan `record` para garantizar inmutabilidad?
2. **┬┐Tiene configurado `@Builder` y `@Jacksonized`?**
   * ┬┐Se agregaron para habilitar el constructor fluido e integraci├│n con Jackson?
3. **┬┐El Request tiene validaciones sint├ícticas estrictas?**
   * ┬┐El campo de nombre tiene `@NotBlank` para asegurar que nunca viaje vac├¡o?
4. **┬┐Los DTOs de salida est├ín unificados?**
   * ┬┐Se utiliza un ├║nico `[Entidad]Response.java` para listados y detalles, evitando el sobredise├▒o de separar en resumen y detalle?
5. **┬┐El Request DTO est├í libre de `JsonNullable`?**
   * ┬┐Los tipos son planos/directos (`String`, `Long`) al no requerirse actualizaciones parciales en cat├ílogos simples?
