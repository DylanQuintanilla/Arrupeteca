# Est├índar de Desarrollo: Mappers con MapStruct para Cat├ílogos (Arrupeteca)

Este documento define las reglas para la transformaci├│n de datos entre Entidades y DTOs en los m├│dulos de **Cat├ílogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Configuraci├│n Centralizada

Todos los mappers deben usar: `config = CentralMapperConfig.class`. Esto asegura pol├¡ticas uniformes como la inyecci├│n en Spring, el descarte autom├ítico de campos no mapeados y el uso de utilitarios globales (como el limpiador de textos `StringTrimmer`).

---

## 2. Reglas de Mapeo de Identidad y Borrado

Al mapear desde DTOs a Entidades, se deben ignorar los campos t├⌐cnicos autogenerados o de estado l├│gico que no deben sobrescribirse en la creaci├│n:

*   **Creaci├│n (`toEntity`)**:
    *   Debe ignorar el campo `id`: `@Mapping(target = "id", ignore = true)`.
    *   Debe ignorar el campo `borradoLogico`: `@Mapping(target = "borradoLogico", ignore = true)`.
*   **Actualizaci├│n (`updateEntity`)**:
    *   Usar `@InheritConfiguration(name = "toEntity")` para heredar las exclusiones de `id` y `borradoLogico` autom├íticamente al modificar un registro existente.

*   **Evitar Dependencias Circulares en `uses`**:
    *   **Peligro**: Si un cat├ílogo se relaciona con una entidad de negocio (ej. `Categoria` tiene obras) y la de negocio se relaciona con la de cat├ílogo, registrar mappers mutuamente en `uses` generar├í una **dependencia circular de Beans en Spring** que romper├í la compilaci├│n.
    *   **Soluci├│n**: Los cat├ílogos **nunca** deben incluir mappers de negocio en su propiedad `uses`. Para mapear colecciones inversas de forma limpia, se debe escribir un m├⌐todo de mapeo local por defecto (`default`) dentro de la interfaz (ej: `mapObraToObraResumen` en `CategoriaMapper`).
    *   **Tres Reglas de Oro para decidir qu├⌐ Mapper rompe el ciclo**:
        1.  **Jerarqu├¡a (Fuerte vs. D├⌐bil)**: El mapper de la entidad de negocio (fuerte) registra al de cat├ílogo (d├⌐bil) en su `uses`. El de cat├ílogo rompe el ciclo mapeando la entidad fuerte con un m├⌐todo `default` local.
        2.  **Direcci├│n (Padre vs. Hijo)**: El mapper principal (el que contiene la relaci├│n de colecci├│n m├ís importante) usa `uses` para inyectar al hijo. El hijo usa un m├⌐todo `default` local para mapear su relaci├│n simple.
        3.  **Simplicidad del DTO**: Se escribe el m├⌐todo `default` manual en el mapper que necesite transformar hacia el DTO **m├ís simple** (el que tiene menos campos, ej. `ObraResumen` en el mapper de categor├¡as), reduciendo el boilerplate.

---

## 3. Ejemplo de Plantilla Completa (CategoriaMapper)

```java
package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.persistence.entity.Categoria;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface CategoriaMapper {

    // 1. Mapeo para Creaci├│n
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "obras", ignore = true) // Ignorar relaciones complejas
    Categoria toEntity(CategoriaRequest request);

    // 2. Mapeo para Actualizaci├│n
    @InheritConfiguration(name = "toEntity")
    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

    // 3. Mapeo para Respuesta Unificada (Listado y Detalle)
    CategoriaResponse toResponse(Categoria categoria);
}
```

---

## 4. Lista de Autoverificaci├│n (Checklist)

1. **┬┐Tiene configuradas las directrices de la anotaci├│n `@Mapper`?**
   * ┬┐Tiene `componentModel = "spring"` y `unmappedTargetPolicy = ReportingPolicy.IGNORE`?
2. **┬┐Los m├⌐todos de creaci├│n ignoran ID y borrado l├│gico?**
   * ┬┐Se incluyeron los mappings correspondientes con `ignore = true`?
3. **┬┐El m├⌐todo de actualizaci├│n hereda la configuraci├│n de creaci├│n?**
   * ┬┐Tiene la anotaci├│n `@InheritConfiguration(name = "toEntity")`?
4. **┬┐La salida est├í centralizada?**
   * ┬┐Se reemplazaron `toResumen` y `toDetalleResponse` por un ├║nico m├⌐todo `toResponse`?
