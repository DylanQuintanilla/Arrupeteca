# Estándar de Desarrollo: Mappers con MapStruct para Catálogos (Arrupeteca)

Este documento define las reglas para la transformación de datos entre Entidades y DTOs en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Configuración Centralizada

Todos los mappers deben usar: `config = CentralMapperConfig.class`. Esto asegura políticas uniformes como la inyección en Spring, el descarte automático de campos no mapeados y el uso de utilitarios globales (como el limpiador de textos `StringTrimmer`).

---

## 2. Reglas de Mapeo de Identidad y Borrado

Al mapear desde DTOs a Entidades, se deben ignorar los campos técnicos autogenerados o de estado lógico que no deben sobrescribirse en la creación:

*   **Creación (`toEntity`)**:
    *   Debe ignorar el campo `id`: `@Mapping(target = "id", ignore = true)`.
    *   Debe ignorar el campo `borradoLogico`: `@Mapping(target = "borradoLogico", ignore = true)`.
*   **Actualización (`updateEntity`)**:
    *   Usar `@InheritConfiguration(name = "toEntity")` para heredar las exclusiones de `id` y `borradoLogico` automáticamente al modificar un registro existente.

*   **Evitar Dependencias Circulares en `uses`**:
    *   **Peligro**: Si un catálogo se relaciona con una entidad de negocio (ej. `Categoria` tiene obras) y la de negocio se relaciona con la de catálogo, registrar mappers mutuamente en `uses` generará una **dependencia circular de Beans en Spring** que romperá la compilación.
    *   **Solución**: Los catálogos **nunca** deben incluir mappers de negocio en su propiedad `uses`. Para mapear colecciones inversas de forma limpia, se debe escribir un método de mapeo local por defecto (`default`) dentro de la interfaz (ej: `mapObraToObraResumen` en `CategoriaMapper`).
    *   **Tres Reglas de Oro para decidir qué Mapper rompe el ciclo**:
        1.  **Jerarquía (Fuerte vs. Débil)**: El mapper de la entidad de negocio (fuerte) registra al de catálogo (débil) en su `uses`. El de catálogo rompe el ciclo mapeando la entidad fuerte con un método `default` local.
        2.  **Dirección (Padre vs. Hijo)**: El mapper principal (el que contiene la relación de colección más importante) usa `uses` para inyectar al hijo. El hijo usa un método `default` local para mapear su relación simple.
        3.  **Simplicidad del DTO**: Se escribe el método `default` manual en el mapper que necesite transformar hacia el DTO **más simple** (el que tiene menos campos, ej. `ObraResumen` en el mapper de categorías), reduciendo el boilerplate.

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

    // 1. Mapeo para Creación
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borradoLogico", ignore = true)
    @Mapping(target = "obras", ignore = true) // Ignorar relaciones complejas
    Categoria toEntity(CategoriaRequest request);

    // 2. Mapeo para Actualización
    @InheritConfiguration(name = "toEntity")
    void updateEntity(CategoriaRequest request, @MappingTarget Categoria categoria);

    // 3. Mapeo para Respuesta Unificada (Listado y Detalle)
    CategoriaResponse toResponse(Categoria categoria);
}
```

---

## 4. Lista de Autoverificación (Checklist)

1. **¿Tiene configuradas las directrices de la anotación `@Mapper`?**
   * ¿Tiene `componentModel = "spring"` y `unmappedTargetPolicy = ReportingPolicy.IGNORE`?
2. **¿Los métodos de creación ignoran ID y borrado lógico?**
   * ¿Se incluyeron los mappings correspondientes con `ignore = true`?
3. **¿El método de actualización hereda la configuración de creación?**
   * ¿Tiene la anotación `@InheritConfiguration(name = "toEntity")`?
4. **¿La salida está centralizada?**
   * ¿Se reemplazaron `toResumen` y `toDetalleResponse` por un único método `toResponse`?
