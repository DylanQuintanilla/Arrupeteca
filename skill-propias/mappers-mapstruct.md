# Estándar de Desarrollo: Mappers con MapStruct (Arrupeteca)

Este documento define la estructura y las mejores prácticas para la capa de transformación de datos (Mappers). Se basa en el "Gold Standard" de `AutorMapper` para garantizar consistencia, seguridad y limpieza de datos.

## 1. Configuración Centralizada
Para mantener un comportamiento uniforme, todos los mappers deben usar la configuración global:

*   **Atributo**: `config = CentralMapperConfig.class`
*   **Ubicación**: `com.arrupeteca.config.CentralMapperConfig`
*   **Estrategias Incluidas**: 
    *   Inyección por constructor (Spring).
    *   Ignorar nulos en actualizaciones (Estrategia PATCH).
    *   Política de reporte de errores/ignorados centralizada.

## 2. Simetría de Nombres (Entidad ↔ DTO)
Para maximizar la automatización de MapStruct, los nombres de los campos deben ser **idénticos** entre la Entidad y el DTO de respuesta, especialmente en los campos de auditoría:
*   Usar `actualizadoPor` y `fechaActualizacion` en los DTOs de Detalle.

## 3. Patrones de Mapeo Obligatorios

### 3.1. DTO Request → Entidad (Creación y Actualización)
*   **`toEntity`**: Debe ignorar campos técnicos que son competencia exclusiva de la DB o JPA (`id`, `borradoLogico`, `creadoPor`, etc.).
*   **`updateEntity`**: Debe heredar la configuración de `toEntity` usando `@InheritConfiguration`. Gracias a la configuración centralizada, ignorará automáticamente los valores nulos que vengan del DTO.

### 3.2. Entidad → DTO Resumen (Listados)
*   Usar "dot notation" (ej. `source = "relacion.nombre"`) para extraer campos simples de entidades relacionadas.
*   Incluir campos calculados si es necesario (ej. `biografiaResumida`).

### 3.3. Entidad → DTO Detalle (Vista Completa)
*   Mapear objetos relacionados completos (usualmente otros DTOs de tipo Resumen).
*   No requiere mapeos explícitos para auditoría si se sigue la regla de simetría.

## 4. Técnicas Avanzadas

### 4.1. Transformaciones Reutilizables (@Named)
Usar para normalizar datos sin forzar formatos agresivos. Ejemplo: `trimString`.
```java
@Named("trimString")
default String trimString(String value) {
    return (value == null) ? null : value.trim();
}
```

### 4.2. Cálculos Complejos (@AfterMapping)
Utilizar para llenar campos que no existen en la entidad o requieren lógica condicional. 
*   **Nota**: Requiere que el DTO de respuesta tenga la anotación `@Builder` de Lombok.

### 4.3. Prevención de Ciclos Infinitos (CycleAvoidingMappingContext)
Si una relación es circular (ej. Autor ↔ Obra) y se requiere mapear ambas entidades de forma recursiva sin agotar la memoria (StackOverflow), se debe utilizar un contexto de mapeo.

#### Implementación del Contexto:
El archivo debe existir en `com.arrupeteca.config.mapper.CycleAvoidingMappingContext`.

#### Uso en el Mapper:
Se debe pasar como un parámetro anotado con `@Context` en los métodos de mapeo:

```java
@Mapper(config = CentralMapperConfig.class, uses = { ... })
public interface EjemploMapper {

    // En el método principal, se añade el Context
    EjemploDetalleResponse toDetalleResponse(Ejemplo entidad, @Context CycleAvoidingMappingContext context);

    // Los métodos internos que MapStruct genere usarán automáticamente este context
}
```

## 5. Ejemplo de Plantilla Completa (Gold Standard)

```java
package com.arrupeteca.mapper;

import com.arrupeteca.config.CentralMapperConfig;
import com.arrupeteca.dto.request.EjemploRequest;
import com.arrupeteca.dto.response.detail.EjemploDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemploResumen;
import com.arrupeteca.persistence.entity.Ejemplo;
import org.mapstruct.*;

@Mapper(
    config = CentralMapperConfig.class,
    uses = { RelacionMapper.class }
)
public interface EjemploMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    // Ignorar relaciones que se asignan manualmente en el servicio
    @Mapping(target = "relacion", ignore = true)
    Ejemplo toEntity(EjemploRequest request);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(EjemploRequest request, @MappingTarget Ejemplo ejemplo);

    @Mapping(target = "nombreRelacion", source = "relacion.nombre")
    @Mapping(target = "nombre", source = "nombre", qualifiedByName = "trimString")
    @Mapping(target = "campoCalculado", ignore = true)
    EjemploResumen toResumen(Ejemplo entidad);

    @Mapping(target = "relacion", source = "relacion")
    @Mapping(target = "nombre", source = "nombre", qualifiedByName = "trimString")
    EjemploDetalleResponse toDetalleResponse(Ejemplo entidad);

    // --- HERRAMIENTAS ---

    @Named("trimString")
    default String trimString(String value) {
        return (value == null) ? null : value.trim();
    }

    @AfterMapping
    default void finalizeResumen(@MappingTarget EjemploResumen.EjemploResumenBuilder resumen, Ejemplo entidad) {
        // Lógica para campoCalculado aquí
    }
}
```

---
*Este estándar garantiza que la transformación de datos sea segura, predecible y de alto rendimiento.*
