# Estándar de Desarrollo: Servicios Spring Boot para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de la capa de **Servicio** (Lógica de Negocio) en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Configuración de Clase y Dependencias

La clase de implementación del servicio (`[Entidad]ServiceImpl`) debe configurarse de la siguiente manera:

*   **`@Service`**: Registro en el contenedor de Spring.
*   **`@RequiredArgsConstructor`**: Inyección de colaboradores de manera automática por constructor (los atributos inyectados deben ser `private final`).
*   **`@Transactional(readOnly = true)`**: Configurado a nivel de clase para que todas las lecturas por defecto sean rápidas y de solo lectura.
*   **`@Slf4j`**: Logging para depuración en producción.

---

## 2. Gestión de Transacciones de Escritura

Los métodos que realicen modificaciones de datos (`crear`, `actualizar`, `cambiarEstado`) deben sobrescribir la transaccionalidad de solo lectura utilizando la anotación `@Transactional` a nivel de método.

Al no existir el campo `@Version` en las entidades de catálogo, no es necesario el uso de `saveAndFlush()`; un `save()` ordinario es suficiente.

---

## 3. Estrategia de Caché de Catálogos (Agresiva)

*   **Caché del listado completo**: En lugar de cachear elementos por ID individuales, es más eficiente cachear la página o lista completa de elementos activos:
    ```java
    @Cacheable(value = "categorias-activas")
    public Page<CategoriaResponse> obtenerTodosActivos(Pageable pageable) { ... }
    ```
*   **Desalojo total (`allEntries = true`)**: En los métodos que alteren el catálogo (`crear`, `actualizar`, `cambiarEstado`), se debe desalojar la caché completa de listados para asegurar que el frontend vea los cambios de forma inmediata:
    ```java
    @CacheEvict(value = "categorias-activas", allEntries = true)
    public CategoriaResponse crear(CategoriaRequest request) { ... }
    ```

---

## 4. Validaciones de Negocio e Integridad

*   **Validación de Unicidad**: Antes de guardar, verificar que el nombre o código no exista en la base de datos (ignorar mayúsculas/minúsculas y aplicar `.trim()` al texto).
*   **Existencia Física**: En `cambiarEstado()`, usar `countByIdIncludeDeleted(id) == 0` para lanzar una excepción si el elemento no existe físicamente en el sistema antes de ejecutar la desactivación o activación.

---

## 5. Plantilla Maestra de Referencia (CategoriaServiceImpl)

```java
package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.CategoriaMapper;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Cacheable(value = "categorias-activas")
    public Page<CategoriaResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todas las categorías activas con paginación: {}", pageable);
        return categoriaRepository.findByBorradoLogicoFalse(pageable)
                .map(categoriaMapper::toResponse);
    }

    @Override
    public CategoriaResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando categoría activa con ID: {}", id);
        return categoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría activa con ID: " + id));
    }

    @Override
    public Page<CategoriaResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todas las categorías (incluyendo borrados) con paginación: {}", pageable);
        return categoriaRepository.findAllProjectedBy(pageable)
                .map(categoriaMapper::toResponse);
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        log.debug("Buscando categoría con ID: {}", id);
        return categoriaRepository.findProjectedById(id)
                .map(categoriaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "categorias-activas", allEntries = true)
    public CategoriaResponse crear(CategoriaRequest request) {
        log.info("Creando nueva categoría con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setNombre(nombreTrimmed);
        
        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada exitosamente con ID: {}", guardada.getId());
        return categoriaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categorias-activas", allEntries = true)
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        log.info("Actualizando categoría con ID: {}", id);
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una categoría con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, categoriaExistente);

        categoriaMapper.updateEntity(request, categoriaExistente);
        categoriaExistente.setNombre(nombreTrimmed);
        
        Categoria actualizada = categoriaRepository.save(categoriaExistente);
        log.info("Categoría con ID: {} actualizada exitosamente", id);
        return categoriaMapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, Categoria categoriaExistente) {
        boolean esNuevo = (categoriaExistente == null);
        boolean nombreCambio = !esNuevo && !categoriaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && categoriaRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar categoría: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe una categoría con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "categorias-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de categoría ID: {} a {}", id, !estado);
        if (categoriaRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró una categoría con ID: " + id);
        }
        if (estado) {
            categoriaRepository.activar(id);
        } else {
            categoriaRepository.desactivar(id);
        }
    }
}
```

---

## 6. Lista de Autoverificación (Checklist)

1. **¿Configuración transaccional correcta?**
   * ¿Tiene la clase `@Transactional(readOnly = true)`?
   * ¿Los métodos modificadores de estado tienen `@Transactional` explícito?
2. **¿Mapeo de caché optimizado para catálogos?**
   * ¿Se incluyó `@Cacheable` en `obtenerTodosActivos`?
   * ¿Se incluyó `@CacheEvict(..., allEntries = true)` en los métodos de modificación para refrescar el listado completo?
3. **¿Validaciones de unicidad seguras?**
   * ¿Se aplica `.trim()` a los textos del DTO antes de validar unicidad o guardar?
4. **¿Se usa `countByIdIncludeDeleted`?**
   * En `cambiarEstado()`, ¿se realiza la verificación física de existencia con `countByIdIncludeDeleted`?
5. **¿La salida de servicio está centralizada?**
   * ¿Los métodos retornan `[Entidad]Response` en lugar de diferenciar entre resumen y detalle?
