# Estándar de Desarrollo: Request DTOs (Arrupeteca)

Este documento es la guía definitiva para crear objetos de petición (Request DTOs) desde cero. Define la estructura obligatoria para garantizar inmutabilidad, seguridad y validación automática.

## 1. Estructura Base (Java Records)
Todas las peticiones deben ser **Java Records** (Java 21+). No se permiten clases tradicionales para peticiones de entrada.

*   **Archivo**: `[NombreEntidad]Request.java`
*   **Anotaciones de Clase**:
    *   `@Builder`: Permite la creación fluida de instancias (especialmente útil en Tests).
    *   `@Jacksonized`: Obligatorio para que Jackson use el Builder al deserializar el JSON.
    *   `@JsonIgnoreProperties(ignoreUnknown = true)`: Evita errores si el cliente envía campos adicionales no definidos.

## 2. Definición de Campos (Header del Record)
Los campos deben usar tipos semánticos y estar validados individualmente:

*   **Identificadores**: Usar `Long` con `@Positive`.
*   **Fechas/Años**: Usar `java.time.Year` o `java.time.LocalDate` según corresponda. Usar `@PastOrPresent` para fechas históricas.
*   **Textos**: 
    *   `@NotBlank`: Para campos obligatorios (evita cadenas vacías o solo con espacios).
    *   `@Size(max = X)`: Siempre definir el límite máximo según el esquema de la DB.
*   **Números**: Usar `@Positive`, `@Min`, `@Max` para restringir rangos de negocio.

## 3. Validaciones de Integridad (@AssertTrue)
Para reglas que dependan de dos o más campos, se deben incluir métodos dentro del cuerpo del record:

```java
@AssertTrue(message = "La fecha de fin debe ser posterior a la de inicio")
public boolean isRangoValido() {
    if (inicio == null || fin == null) return true;
    return !inicio.isAfter(fin);
}
```

## 4. Fail-Fast (Constructor Compacto)
Se recomienda incluir un constructor compacto para asegurar que el objeto sea consistente incluso si se instancia manualmente fuera de Spring.

```java
public [NombreEntidad]Request {
    // Espacio para validaciones defensivas manuales o normalización de datos
}
```

## 5. Ejemplo de Plantilla Completa
Copia este código como base para cualquier nuevo Request:

```java
package com.arrupeteca.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.time.Year;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public record EjemploRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    String nombre,

    @Positive(message = "ID de relación inválido")
    Long idRelacion,

    @PastOrPresent(message = "No puede ser una fecha futura")
    Year anio
) {
    public EjemploRequest {
        // Constructor compacto
    }

    @AssertTrue(message = "Mensaje de error lógico")
    public boolean isEstadoValido() {
        return true; // Lógica aquí
    }
}
```

---
*Este estándar asegura que cada petición que entra al sistema sea una "Fortaleza de Datos" antes de llegar al Servicio.*
