# Estándar de Desarrollo: Mappers con MapStruct (Arrupeteca)

Este documento define las reglas para la transformación de datos entre Entidades y DTOs, con especial énfasis en el soporte para el Bloqueo Optimista, compatibilidad con actualizaciones parciales y consistencia con Lombok.

---

## 1. Configuración Centralizada

Todos los mappers deben usar: `config = CentralMapperConfig.class`. Esto asegura políticas uniformes como la inyección en Spring y el descarte automático de campos no mapeados.

---

## 2. Reglas de Mapeo de Versión (Bloqueo Optimista y Edición Parcial)

Para evitar sobrescribir accidentalmente la versión de base de datos con `null` cuando un cliente hace una actualización parcial (y no envía el campo `version` en su JSON):

*   **Creación (`toEntity`)**:
    *   Debe ignorar el campo `version` de la entidad: `@Mapping(target = "version", ignore = true)`.
    *   Hibernate inicializará la versión en la base de datos automáticamente.
*   **Actualización (`updateEntity`)**:
    *   **Debe ignorar el campo `version` de la entidad**: `@Mapping(target = "version", ignore = true)`.
    *   **Razón**: Si no se ignora, cuando el cliente omita la versión en un `PUT`/`PATCH` parcial, MapStruct mapeará `null` sobre el campo `version` de la entidad en base de datos. Esto corrompe el control de concurrencia de Hibernate.
    *   **Validación de Concurrencia**: La comparación del bloqueo optimista se traslada a la capa de Servicio de forma manual:
        ```java
        if (request.version() != null && !request.version().equals(entidadExistente.getVersion())) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario...");
        }
        ```

---

## 3. Patrones de Mapeo y Seguridad de Auditoría

*   **Campos de Auditoría Técnicos**:
    *   Los campos heredados de `Auditable` (`creadoPor`, `fechaCreacion`, `actualizadoPor`, `fechaActualizacion`) **no deben ser mapeados ni declarados como ignorados (`ignore = true`)** en los métodos de mapeo como `toEntity`.
    *   **Razón técnica**: Las entidades utilizan `@Builder` de Lombok, el cual no incluye campos de la clase padre en el constructor del builder del hijo. Intentar declarar ignores explícitos para estos campos causará un error de compilación de MapStruct (*Unknown property*).
    *   **Razón de arquitectura**: La auditoría debe ser inyectada exclusivamente por Spring Data JPA a través de sus Listeners (`@CreatedBy`, `@LastModifiedBy`, etc.). MapStruct los omitirá de forma limpia y segura gracias a la política global `unmappedTargetPolicy = ReportingPolicy.IGNORE` configurada en `CentralMapperConfig`.
*   **Herencia de Configuración**: Usar `@InheritConfiguration` en `updateEntity` para reutilizar las reglas de ignorar campos técnicos que sí pertenezcan a la entidad (como `id`, `version` o `borradoLogico`).

---

## 4. Ejemplo de Plantilla Completa

```java
@Mapper(config = CentralMapperConfig.class, uses = { RelacionMapper.class })
public interface EjemploMapper {

    // 1. Mapeo para Creación (toEntity)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)       // Ignorar versión en creación
    @Mapping(target = "borradoLogico", ignore = true)  // Ignorar borrado lógico inicial
    // Nota: Los campos de auditoría heredados de Auditable se omiten aquí, MapStruct los ignora implícitamente
    Ejemplo toEntity(EjemploRequest request);

    // 2. Mapeo para Actualización (updateEntity)
    @InheritConfiguration(name = "toEntity")
    // Al heredar 'toEntity', ya se ignora automáticamente 'id', 'version' y 'borradoLogico'
    void updateEntity(EjemploRequest request, @MappingTarget Ejemplo ejemplo);

    // 3. Mapeo para Respuestas de Detalle (toDetalleResponse)
    @Mapping(target = "relacion", source = "relacion")
    EjemploDetalleResponse toDetalleResponse(Ejemplo entidad);
}
```

---
*Este estándar asegura que las transformaciones de datos respeten el ciclo de vida de las entidades, la seguridad de la auditoría y el versionamiento óptimo del sistema sin corromper la concurrencia.*