# Estándar de Desarrollo: Mappers con MapStruct (Arrupeteca)

Este documento define las reglas para la transformación de datos entre Entidades y DTOs, con especial énfasis en el soporte para el Bloqueo Optimista.

## 1. Configuración Centralizada
Todos los mappers deben usar: `config = CentralMapperConfig.class`.

## 2. Reglas de Mapeo de Versión (Bloqueo Optimista)
Para que el sistema detecte conflictos de edición, la versión debe fluir correctamente:

*   **Creación (`toEntity`)**: 
    *   Debe ignorar el campo `version` de la entidad: `@Mapping(target = "version", ignore = true)`. 
    *   Hibernate inicializará la versión en 0 o 1 automáticamente.
*   **Actualización (`updateEntity`)**: 
    *   Debe mapear explícitamente la versión desde el DTO: `@Mapping(target = "version", source = "version")`. 
    *   Esto permite que JPA compare la versión enviada por el cliente con la de la base de datos.

## 3. Patrones de Mapeo
*   **Ignorar Auditoría**: Siempre ignorar `creadoPor`, `fechaCreacion`, etc., en `toEntity`.
*   **Herencia de Configuración**: Usar `@InheritConfiguration` en `updateEntity` para reutilizar las reglas de ignorar campos técnicos.

## 4. Ejemplo de Plantilla Completa

```java
@Mapper(config = CentralMapperConfig.class, uses = { RelacionMapper.class })
public interface EjemploMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true) // Ignorar en creación
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "actualizadoPor", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    Ejemplo toEntity(EjemploRequest request);

    @InheritConfiguration(name = "toEntity")
    @Mapping(target = "version", source = "version") // Mapear en actualización
    void updateEntity(EjemploRequest request, @MappingTarget Ejemplo ejemplo);

    @Mapping(target = "relacion", source = "relacion")
    EjemploDetalleResponse toDetalleResponse(Ejemplo entidad);
}
```

---
*Este estándar asegura que las transformaciones de datos respeten el ciclo de vida de las entidades y su versionamiento.*
