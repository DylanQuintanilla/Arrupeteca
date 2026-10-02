# Est├índar de Desarrollo: Mappers con MapStruct (Arrupeteca)

Este documento define las reglas para la transformaci├│n de datos entre Entidades y DTOs, con especial ├⌐nfasis en el soporte para el Bloqueo Optimista, compatibilidad con actualizaciones parciales y consistencia con Lombok.

---

## 1. Configuraci├│n Centralizada

Todos los mappers deben usar: `config = CentralMapperConfig.class`. Esto asegura pol├¡ticas uniformes como la inyecci├│n en Spring y el descarte autom├ítico de campos no mapeados.

---

## 2. Reglas de Mapeo de Versi├│n (Bloqueo Optimista y Edici├│n Parcial)

Para evitar sobrescribir accidentalmente la versi├│n de base de datos con `null` cuando un cliente hace una actualizaci├│n parcial (y no env├¡a el campo `version` en su JSON):

*   **Creaci├│n (`toEntity`)**:
    *   Debe ignorar el campo `version` de la entidad: `@Mapping(target = "version", ignore = true)`.
    *   Hibernate inicializar├í la versi├│n en la base de datos autom├íticamente.
*   **Actualizaci├│n (`updateEntity`)**:
    *   **Debe ignorar el campo `version` de la entidad**: `@Mapping(target = "version", ignore = true)`.
    *   **Raz├│n**: Si no se ignora, cuando el cliente omita la versi├│n en un `PUT`/`PATCH` parcial, MapStruct mapear├í `null` sobre el campo `version` de la entidad en base de datos. Esto corrompe el control de concurrencia de Hibernate.
    *   **Validaci├│n de Concurrencia**: La comparaci├│n del bloqueo optimista se traslada a la capa de Servicio de forma manual:
        ```java
        if (request.version() != null && !request.version().equals(entidadExistente.getVersion())) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario...");
        }
        ```

---

## 3. Patrones de Mapeo y Seguridad de Auditor├¡a

*   **Campos de Auditor├¡a T├⌐cnicos**:
    *   Los campos heredados de `Auditable` (`creadoPor`, `fechaCreacion`, `actualizadoPor`, `fechaActualizacion`) **no deben ser mapeados ni declarados como ignorados (`ignore = true`)** en los m├⌐todos de mapeo como `toEntity`.
    *   **Raz├│n t├⌐cnica**: Las entidades utilizan `@Builder` de Lombok, el cual no incluye campos de la clase padre en el constructor del builder del hijo. Intentar declarar ignores expl├¡citos para estos campos causar├í un error de compilaci├│n de MapStruct (*Unknown property*).
    *   **Raz├│n de arquitectura**: La auditor├¡a debe ser inyectada exclusivamente por Spring Data JPA a trav├⌐s de sus Listeners (`@CreatedBy`, `@LastModifiedBy`, etc.). MapStruct los omitir├í de forma limpia y segura gracias a la pol├¡tica global `unmappedTargetPolicy = ReportingPolicy.IGNORE` configurada en `CentralMapperConfig`.
*   **Herencia de Configuraci├│n**: Usar `@InheritConfiguration` en `updateEntity` para reutilizar las reglas de ignorar campos t├⌐cnicos que s├¡ pertenezcan a la entidad (como `id`, `version` o `borradoLogico`).

*   **Evitar Dependencias Circulares en `uses`**:
    *   **Peligro**: Si el mapper A se relaciona con B y el mapper B se relaciona con A, poner a ambos en la propiedad `uses` del otro (`uses = { BMapper.class }` y `uses = { AMapper.class }`) causar├í una **dependencia circular de Beans en Spring** que impedir├í compilar o arrancar el sistema.
    *   **Soluci├│n**: Para romper el ciclo, **no** registres el mapper conflictivo en `uses`. En su lugar, escribe un m├⌐todo de mapeo local por defecto (`default`) o anotado con `@Named` dentro de la misma interfaz (ej: el m├⌐todo `obraToResumen` en `AutorMapper`), realizando la transformaci├│n de forma directa e independiente.
    *   **Tres Reglas de Oro para decidir qu├⌐ Mapper rompe el ciclo**:
        1.  **Jerarqu├¡a (Fuerte vs. D├⌐bil)**: El mapper de la entidad de negocio (fuerte) registra al de cat├ílogo (d├⌐bil) en su `uses`. El de cat├ílogo rompe el ciclo mapeando la entidad fuerte con un m├⌐todo `default` local.
        2.  **Direcci├│n (Padre vs. Hijo)**: El mapper principal (el que contiene la relaci├│n de colecci├│n m├ís importante, ej. `Obra` que contiene muchos autores) usa `uses` para inyectar al hijo. El hijo usa un m├⌐todo `default` local para mapear su relaci├│n simple (ej. `Autor` mapeando su `obraCumbre`).
        3.  **Simplicidad del DTO**: Se escribe el m├⌐todo `default` manual en el mapper que necesite transformar hacia el DTO **m├ís simple** (el que tiene menos campos, ej. `ObraResumen` de 5 campos vs. `AutorResumenResponse` de 12 campos), haciendo el mantenimiento del c├│digo mucho m├ís sencillo.

---

## 4. Ejemplo de Plantilla Completa

```java
@Mapper(config = CentralMapperConfig.class, uses = { RelacionMapper.class })
public interface EjemploMapper {

    // 1. Mapeo para Creaci├│n (toEntity)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)       // Ignorar versi├│n en creaci├│n
    @Mapping(target = "borradoLogico", ignore = true)  // Ignorar borrado l├│gico inicial
    // Nota: Los campos de auditor├¡a heredados de Auditable se omiten aqu├¡, MapStruct los ignora impl├¡citamente
    Ejemplo toEntity(EjemploRequest request);

    // 2. Mapeo para Actualizaci├│n (updateEntity)
    @InheritConfiguration(name = "toEntity")
    // Al heredar 'toEntity', ya se ignora autom├íticamente 'id', 'version' y 'borradoLogico'
    void updateEntity(EjemploRequest request, @MappingTarget Ejemplo ejemplo);

    // 3. Mapeo para Respuestas de Detalle (toDetalleResponse)
    @Mapping(target = "relacion", source = "relacion")
    EjemploDetalleResponse toDetalleResponse(Ejemplo entidad);
}
```

---
*Este est├índar asegura que las transformaciones de datos respeten el ciclo de vida de las entidades, la seguridad de la auditor├¡a y el versionamiento ├│ptimo del sistema sin corromper la concurrencia.*
