# Estándar de Desarrollo: Request y Response DTOs (Arrupeteca)

Este documento es la guía definitiva para crear objetos de transferencia de datos (DTOs) desde cero. Define la estructura obligatoria para garantizar inmutabilidad, seguridad, validación automática y soporte para caché y concurrencia.

## 1. Estructura Base (Java Records)
Todas las transferencias de datos deben ser **Java Records** (Java 21+). No se permiten clases tradicionales para DTOs.

*   **Request**: `[NombreEntidad]Request.java`
*   **Resumen**: `[NombreEntidad]Resumen.java`
*   **Detalle**: `[NombreEntidad]DetalleResponse.java`

### Anotaciones de Clase Obligatorias:
*   `@Builder`: Permite la creación fluida de instancias.
*   `@Jacksonized`: Obligatorio para que Jackson use el Builder al deserializar el JSON.
*   `@JsonIgnoreProperties(ignoreUnknown = true)`: (Solo en Request) Evita errores por campos adicionales.

## 2. Definición de Campos y Validación
Los campos deben usar tipos semánticos y estar validados individualmente:

*   **Identificadores**: Usar `Long` con `@NotNull` y `@Positive`.
*   **Bloqueo Optimista (Concurrencia)**: 
    *   Incluir `Long version` en **Request** (obligatorio para actualizaciones) y **Detalle**.
*   **Fechas/Años**: Usar `java.time.Year` o `java.time.LocalDateTime`. Usar `@PastOrPresent`.
*   **Textos**: 
    *   `@NotBlank`: Para campos obligatorios.
    *   `@Size(max = X)`: Definir el límite máximo según el esquema de la DB.
*   **Soporte para Caché**: 
    *   Todos los DTOs de respuesta (`Resumen` y `Detalle`) deben implementar `java.io.Serializable` para compatibilidad con sistemas de caché externos (ej. Redis).

## 3. Validaciones de Integridad (@AssertTrue)
Para reglas que dependan de dos o más campos, se deben incluir métodos dentro del cuerpo del record:

```java
@AssertTrue(message = "El año de nacimiento no puede ser mayor al de fallecimiento")
public boolean isFechasValidas() {
    if (anioNacimiento == null || anioFallecimiento == null) return true;
    return !anioNacimiento.isAfter(anioFallecimiento);
}
```

## 4. Ejemplo de Plantilla Completa (Request)

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
    @Size(max = 100)
    String nombre,

    @Positive(message = "ID inválido")
    Long idRelacion,

    Long version, // Para bloqueo optimista

    @PastOrPresent(message = "No puede ser una fecha futura")
    Year anio
) {
    public EjemploRequest {}
}
```

## 5. Ejemplo de Plantilla Completa (Response Detalle)

```java
package com.arrupeteca.dto.response.detail;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EjemploDetalleResponse(
    @NotNull Long id,
    @NotBlank String nombre,
    Long version, // Crucial para que el front lo devuelva en el PUT
    @NotBlank String creadoPor,
    @NotNull LocalDateTime fechaCreacion,
    // ... otros campos
    RelacionResumen relacion
) implements Serializable { }
```

---
*Este estándar asegura que cada petición sea una "Fortaleza de Datos" y cada respuesta sea "Cacheable" y segura.*
