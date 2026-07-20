# Estándar de Desarrollo: Request y Response DTOs para Catálogos (Arrupeteca)

Este documento establece las directrices para la creación y estructuración de los Objetos de Transferencia de Datos (DTOs) en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Clasificación y Estructura de DTOs de Catálogo

Dado que las tablas de catálogo son simples, sus datos de salida son idénticos tanto para listados como para consultas individuales. Por lo tanto, no se requiere la división de *Summary vs Detail*. Se centraliza la salida en un único DTO. Todos los DTOs se definen como **Java Records**:

| Categoría DTO | Convención de Nombre | Capa / Dirección | Propósito | Características |
| :--- | :--- | :--- | :--- | :--- |
| **Request** | `[Entidad]Request.java` | Entrada (Cliente $\rightarrow$ API) | Creación y actualización completa. | Validación sintáctica clásica (`@NotBlank`, `@Size`). |
| **Response** | `[Entidad]Response.java` | Salida (API $\rightarrow$ Cliente) | Representación unificada para listados, combos de selección y detalle individual. | Campos de identidad del catálogo (ID, nombre, borradoLogico). |

---

## 2. Validación Sintáctica en el Request DTO

Toda validación de formato, presencia y tamaño máximo debe residir en el Request DTO usando las anotaciones de validación estándar:
*   `@NotBlank(message = "...")`: Obligatorio para el nombre o código del catálogo.
*   `@Size(max = X, message = "...")`: Ajustado exactamente al tamaño de la columna en base de datos.

---

## 3. Plantillas de Ejemplo (Módulo Categoría)

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

## 4. Lista de Autoverificación (Checklist)

1. **¿Es un Java Record?**
   * ¿Todos los DTOs de catálogo usan `record` para garantizar inmutabilidad?
2. **¿Tiene configurado `@Builder` y `@Jacksonized`?**
   * ¿Se agregaron para habilitar el constructor fluido e integración con Jackson?
3. **¿El Request tiene validaciones sintácticas estrictas?**
   * ¿El campo de nombre tiene `@NotBlank` para asegurar que nunca viaje vacío?
4. **¿Los DTOs de salida están unificados?**
   * ¿Se utiliza un único `[Entidad]Response.java` para listados y detalles, evitando el sobrediseño de separar en resumen y detalle?
5. **¿El Request DTO está libre de `JsonNullable`?**
   * ¿Los tipos son planos/directos (`String`, `Long`) al no requerirse actualizaciones parciales en catálogos simples?
