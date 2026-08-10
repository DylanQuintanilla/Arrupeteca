# Estándar de Desarrollo: Servicios Spring Boot para Catálogos (Arrupeteca)

---

## 1. Configuración de Clase y Dependencias

La clase de implementación del servicio (`[Entidad]ServiceImpl`) debe configurarse de la siguiente manera:

*   **`@Service`**: Registro en el contenedor de Spring.
*   **`@RequiredArgsConstructor`**: Inyección de colaboradores de manera automática por constructor (los atributos inyectados deben ser `private final`).
*   **`@Transactional(readOnly = true)`**: Configurado a nivel de clase para que todas las lecturas por defecto sean de solo lectura.
*   **`@Slf4j`**: Logging para depuración en producción.

---

## 2. Estrategia de Caché de Catálogos

*   **Caché de listado activo dinámico**: Cachear la lista de elementos activos con soporte a clave dinámica (`nombre`):
    ```java
    @Cacheable(value = "categorias-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<CategoriaResponse> obtenerTodosActivos(String nombre) { ... }
    ```
*   **Desalojo total (`allEntries = true`)**: En los métodos que alteren el catálogo (`crear`, `actualizar`, `cambiarEstado`), se debe desalojar la caché completa de listados:
    ```java
    @CacheEvict(value = "categorias-activas", allEntries = true)
    public CategoriaResponse crear(CategoriaRequest request) { ... }
    ```

---

## 3. Plantilla Maestra de Referencia (CategoriaServiceImpl)

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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Cacheable(value = "categorias-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<CategoriaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo categorías activas (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return categoriaRepository.buscarActivosPorNombre(filtro).stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<CategoriaResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de categorías con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return categoriaRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(categoriaMapper::toResponse);
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        log.debug("Buscando categoría por ID: {}", id);
        return categoriaRepository.findById(id)
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
        if (!categoriaRepository.existsById(id)) {
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
