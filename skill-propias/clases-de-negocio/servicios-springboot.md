# Estándar de Desarrollo: Servicios Spring Boot (Arrupeteca)

Este documento establece las directrices, convenciones y mejores prácticas para el diseño e implementación de la capa de **Servicio** (Lógica de Negocio) en Arrupeteca.

---

## 1. Configuración de Clase y Dependencias

Toda clase de servicio debe estructurarse como una interfaz (`[Entidad]Service`) y su respectiva implementación (`[Entidad]ServiceImpl`). La implementación debe cumplir con:

*   **`@Service`**: Registro obligatorio en el contenedor de Spring.
*   **`@RequiredArgsConstructor`**: Inyección de dependencias por constructor generada de forma automática por Lombok (los atributos inyectados deben ser `private final`).
*   **`@Transactional(readOnly = true)`**: Configurado a nivel de clase para que todas las consultas de lectura sean rápidas y de solo lectura por defecto.
*   **`@Slf4j`**: Anotación de Lombok para habilitar logging de trazabilidad en depuraciones.

---

## 2. Gestión de Transacciones de Escritura

Los métodos que realicen modificaciones de datos (`crear`, `actualizar`, `cambiarEstado`, o borrados lógicos) deben sobrescribir la transaccionalidad de solo lectura utilizando la anotación `@Transactional` a nivel de método.

*   **Forzar actualización de versión**: En los métodos `crear()` y `actualizar()`, se debe utilizar **`saveAndFlush()`** en lugar del simple `save()` para sincronizar inmediatamente los cambios con la base de datos, asegurando que el DTO de respuesta contenga el número de versión actualizado para el control de bloqueo optimista del cliente.

---

## 3. Delegación de Proyecciones Directas (`SELECT new`) vs Vistas de Detalle

Para maximizar el rendimiento y la eficiencia de la memoria en la capa de servicio:

*   **Métodos de Listado Paginado de Resumen (`obtenerTodosActivos`, `obtenerTodos`, `busquedaGlobal`, `busquedaMaestraAdmin`, `buscarPorObraId`)**:
    *   **Delegación Directa**: Deben delegar directamente al método proyectado del repositorio (ej. `autorRepository.busquedaMaestraAdminProyectada(...)`).
    *   **Prohibido usar MapStruct en Lecturas Paginadas**: NO se debe invocar `.map(mapper::toResumen)`, ya que el resultado entregado por el repositorio ya es la página de DTOs finales (`Page<[Entidad]Resumen>`) instanciada por SQL.
*   **Métodos de Detalle por ID (`obtenerPorIdActivo`, `obtenerPorId`)**:
    *   **Carga Completa con `@EntityGraph`**: Consultan la entidad completa desde el repositorio (`findByIdAndBorradoLogicoFalse` / `findById`) y luego mapean el objeto con MapStruct (`.map(mapper::toDetalleResponse)`).

---

## 4. Estrategia de Caché Local

Para optimizar las lecturas frecuentes de un mismo registro por su identificador único:
*   **`@Cacheable(value = "nombre_cache", key = "#id")`**: Aplicar **únicamente en las consultas de detalle del catálogo público** (`obtenerPorIdActivo`).
*   **Evitar colisiones con consultas de administración**: Los métodos de detalle para administradores (`obtenerPorId`) **no deben ser cacheados**, ya que los administradores modifican datos y requieren ver la versión real en base de datos de manera inmediata.
*   **`@CacheEvict(value = "nombre_cache", key = "#id")`**: Aplicar obligatoriamente en métodos de modificación (`actualizar`) y cambio de estado (`cambiarEstado`) para invalidar de forma quirúrgica las entradas obsoletas en memoria.

---

## 5. Validaciones de Negocio y Relaciones (Fail-Fast)

*   **Validaciones de obligatoriedad según la acción**: Como el DTO es reutilizado para creación (POST) y actualización parcial (PUT/PATCH), las validaciones lógicas obligatorias de presencia deben validarse de manera dinámica en la capa de servicio:
    *   En `crear()`: Se verifica que todos los campos mandatorios estén provistos y no en blanco.
    *   En `actualizar()`: Se comprueban únicamente si se encuentran presentes en el `JsonNullable` del DTO, y tras hacer el merge se valida el estado final de la entidad.
*   **Bloqueo Optimista Manual**: Al actualizar, se debe comprobar la versión del DTO. Si no coincide con la versión existente en la entidad, lanzar una excepción de negocio de inmediato.
*   **Recolección de Errores (Multi-Validation)**: Al validar la existencia de relaciones, acumular todos los fallos detectados en una lista de cadenas `List<String> errores` y lanzar una sola `ValidacionNegocioException(errores)`.

---

## 6. Plantilla Maestra de Referencia (AutorServiceImpl)

```java
package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;
    private final PaisRepository paisRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final GeneroRepository generoRepository;
    private final ObraRepository obraRepository;
    private final AutorMapper autorMapper;

    @Override
    public Page<AutorResumenResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todos los autores activos proyectados con paginación: {}", pageable);
        return autorRepository.busquedaMaestraAdminProyectada(false, null, null, null, null, null, null, pageable);
    }

    @Override
    @Cacheable(value = "autores", key = "#id")
    public AutorDetalleResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando autor activo con ID: {}", id);
        return autorRepository.findByIdAndBorradoLogicoFalse(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró Autor activo con ID: {}", id);
                    return new ValidacionNegocioException("No se encontró Autor activo con ID: " + id);
                });
    }

    @Override
    public Page<AutorResumenResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los autores proyectados (incluyendo borrados) con paginación: {}", pageable);
        return autorRepository.busquedaMaestraAdminProyectada(null, null, null, null, null, null, null, pageable);
    }

    @Override
    public AutorDetalleResponse obtenerPorId(Long id) {
        log.debug("Buscando autor con ID (incluyendo borrados): {}", id);
        return autorRepository.findById(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró Autor con ID: {}", id);
                    return new ValidacionNegocioException("No se encontró Autor con ID: " + id);
                });
    }

    @Override
    public Page<AutorResumenResponse> busquedaGlobal(String palabra, Year anioInicio, Year anioFin, Pageable pageable) {
        log.debug("Iniciando búsqueda global proyectada: '{}', años: {} - {}", palabra, anioInicio, anioFin);
        return autorRepository.busquedaMaestraAdminProyectada(false, null, null, null, anioInicio, anioFin, palabra, pageable);
    }

    @Override
    public Page<AutorResumenResponse> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero, Long idObraCumbre,
            Year anioInicio, Year anioFin, String termino, Pageable pageable) {
        log.debug("Iniciando búsqueda maestra admin proyectada para término: '{}', años: {} - {}", termino, anioInicio, anioFin);
        return autorRepository.busquedaMaestraAdminProyectada(esBorrado, idNacionalidad, idPais, idGenero, idObraCumbre, anioInicio, anioFin, termino, pageable);
    }

    @Override
    @Transactional
    public AutorDetalleResponse crear(AutorRequest request) {
        log.info("Creando nuevo autor con nombre: {}", request.nombre());
        validarDatosCreacion(request);
        validarUnicidadNombre(request.nombre(), null);

        Autor autor = autorMapper.toEntity(request);
        asignarRelaciones(autor, request);
        
        Autor guardado = autorRepository.saveAndFlush(autor);
        log.info("Autor creado exitosamente con ID: {}", guardado.getId());
        return autorMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "autores", key = "#id")
    public AutorDetalleResponse actualizar(Long id, AutorRequest request) {
        log.info("Actualizando autor con ID: {}", id);
        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));

        validarDatosActualizacion(request, autorExistente);
        validarUnicidadNombre(request.nombre(), autorExistente);

        autorMapper.updateEntity(request, autorExistente);
        asignarRelaciones(autorExistente, request);

        // Validar fechas cruzadas post-merge
        if (autorExistente.getAnioNacimiento() != null && autorExistente.getAnioFallecimiento() != null) {
            if (autorExistente.getAnioNacimiento().isAfter(autorExistente.getAnioFallecimiento())) {
                throw new ValidacionNegocioException("El año de nacimiento no puede ser mayor al año de fallecimiento (" + autorExistente.getAnioFallecimiento() + ")");
            }
        }
        
        Autor actualizado = autorRepository.saveAndFlush(autorExistente);
        log.info("Autor con ID: {} actualizado exitosamente", id);
        return autorMapper.toDetalleResponse(actualizado);
    }

    private void validarDatosCreacion(AutorRequest request) {
        boolean tieneNombre = (request.nombre() != null && request.nombre().isPresent() && request.nombre().get() != null && !request.nombre().get().isBlank());
        boolean tieneSeudonimo = (request.seudonimo() != null && request.seudonimo().isPresent() && request.seudonimo().get() != null && !request.seudonimo().get().isBlank());
        if (!tieneNombre && !tieneSeudonimo) {
            throw new ValidacionNegocioException("El autor debe tener al menos un Nombre o un Seudonimo");
        }
        
        boolean tieneBiografia = (request.biografia() != null && request.biografia().isPresent() && request.biografia().get() != null && !request.biografia().get().isBlank());
        if (!tieneBiografia) {
            throw new ValidacionNegocioException("La biografia no puede estar vacía");
        }
    }

    private void validarDatosActualizacion(AutorRequest request, Autor autorExistente) {
        // Validación manual de Bloqueo Optimista
        Long vExistente = autorExistente.getVersion() != null ? autorExistente.getVersion() : 0L;
        if (request.version() == null || !request.version().equals(vExistente)) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue la página.");
        }

        // Si se envían en el JSON, no deben estar vacíos
        if (request.nombre() != null && request.nombre().isPresent()) {
            if (request.nombre().get() == null || request.nombre().get().isBlank()) {
                throw new ValidacionNegocioException("El nombre no puede estar vacío");
            }
        }
        if (request.seudonimo() != null && request.seudonimo().isPresent()) {
            if (request.seudonimo().get() == null || request.seudonimo().get().isBlank()) {
                throw new ValidacionNegocioException("El seudonimo no puede estar vacío");
            }
        }
        if (request.biografia() != null && request.biografia().isPresent()) {
            if (request.biografia().get() == null || request.biografia().get().isBlank()) {
                throw new ValidacionNegocioException("La biografia no puede estar vacía");
            }
        }

        // Post-merge, verificar que aún quede al menos un nombre o seudónimo válido
        String nombreFinal = (request.nombre() != null && request.nombre().isPresent())
                ? (request.nombre().get() != null ? request.nombre().get().trim() : null)
                : autorExistente.getNombre();
        String seudonimoFinal = (request.seudonimo() != null && request.seudonimo().isPresent())
                ? (request.seudonimo().get() != null ? request.seudonimo().get().trim() : null)
                : autorExistente.getSeudonimo();

        boolean tieneNombre = (nombreFinal != null && !nombreFinal.isBlank());
        boolean tieneSeudonimo = (seudonimoFinal != null && !seudonimoFinal.isBlank());
        if (!tieneNombre && !tieneSeudonimo) {
            throw new ValidacionNegocioException("El autor debe tener al menos un Nombre o un Seudonimo");
        }
    }

    private void validarUnicidadNombre(JsonNullable<String> nombreNuevo, Autor autorExistente) {
        if (nombreNuevo == null || !nombreNuevo.isPresent() || nombreNuevo.get() == null || nombreNuevo.get().isBlank()) {
            return;
        }

        String nombreVal = nombreNuevo.get().trim();
        boolean esNuevo = (autorExistente == null);
        boolean nombreCambio = !esNuevo && !autorExistente.getNombre().equalsIgnoreCase(nombreVal);

        if ((esNuevo || nombreCambio) && autorRepository.existsByNombreIgnoreCase(nombreVal)) {
            log.warn("Intento de duplicar nombre de autor: {}", nombreVal);
            throw new ValidacionNegocioException("Ya existe un autor con el nombre: " + nombreVal);
        }
    }

    private void asignarRelaciones(Autor autor, AutorRequest request) {
        List<String> errores = new ArrayList<>();

        if (request.idPaisNacimiento() != null && request.idPaisNacimiento().isPresent()) {
            Long idPais = request.idPaisNacimiento().orElse(null);
            if (idPais != null) {
                paisRepository.findById(idPais)
                        .ifPresentOrElse(autor::setPaisNacimiento,
                                () -> errores.add("El país con ID " + idPais + " no existe."));
            } else {
                autor.setPaisNacimiento(null);
            }
        }

        if (request.idNacionalidad() != null && request.idNacionalidad().isPresent()) {
            Long idNac = request.idNacionalidad().orElse(null);
            if (idNac != null) {
                nacionalidadRepository.findById(idNac)
                        .ifPresentOrElse(autor::setNacionalidad,
                                () -> errores.add("La nacionalidad con ID " + idNac + " no existe."));
            } else {
                autor.setNacionalidad(null);
            }
        }

        if (request.idGeneroPrincipal() != null && request.idGeneroPrincipal().isPresent()) {
            Long idGen = request.idGeneroPrincipal().orElse(null);
            if (idGen != null) {
                generoRepository.findById(idGen)
                        .ifPresentOrElse(autor::setGeneroPrincipal,
                                () -> errores.add("El género con ID " + idGen + " no existe."));
            } else {
                autor.setGeneroPrincipal(null);
            }
        }

        if (request.idObraCumbre() != null && request.idObraCumbre().isPresent()) {
            Long idObra = request.idObraCumbre().orElse(null);
            if (idObra != null) {
                obraRepository.findById(idObra)
                        .ifPresentOrElse(autor::setObraCumbre,
                                () -> errores.add("La obra con ID " + idObra + " no existe."));
            } else {
                autor.setObraCumbre(null);
            }
        }

        if (!errores.isEmpty()) {
            log.warn("Errores al asignar relaciones para autor: {}", errores);
            throw new ValidacionNegocioException(errores);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "autores", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del autor ID: {} a {}", id, !estado);
        if (!autorRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Autor con ID: " + id);
        }
        if (estado) {
            autorRepository.activar(id);
        } else {
            autorRepository.desactivar(id);
        }
    }

    @Override
    public List<AutorResumenResponse> buscarDuplicados(String termino, Long idExcluir) {
        if (termino == null || termino.isBlank()) {
            return List.of();
        }
        log.debug("Buscando duplicados para el término: '{}'", termino);
        return autorRepository.buscarDuplicados(termino.trim(), idExcluir, Sort.by("nombre"))
                .stream()
                .map(autorMapper::toResumen)
                .toList();
    }
}
```

---

## 7. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿Configuración transaccional correcta?**
   * ¿Tiene la clase `@Transactional(readOnly = true)`?
   * ¿Los métodos que modifican o graban datos tienen `@Transactional` explícito?
   * ¿Se utiliza `saveAndFlush()` para forzar el incremento de la versión del bloqueo optimista en creaciones y actualizaciones?
2. **¿Inyección por constructor correcta?**
   * ¿Están todos los colaboradores marcados como `private final` y se usa `@RequiredArgsConstructor`?
3. **¿Mapeo de caché seguro?**
   * ¿Se incluyó `@Cacheable` solo en las consultas públicas de lectura por ID (`obtenerPorIdActivo`)?
   * ¿Se excluyó del caché el método de lectura del administrador (`obtenerPorId`) para evitar colisiones y datos fantasmas?
   * ¿Se incluyó `@CacheEvict` en los métodos de modificación correspondientes para limpiar la caché?
4. **¿Validaciones cruzadas correctas?**
   * ¿La validación de unicidad de nombres desenvuelve el `JsonNullable` y permite mantener el valor original al actualizar?
5. **¿Recolección acumulativa de errores de relaciones?**
   * Al buscar entidades de claves foráneas, ¿se acumulan los errores en una `List<String>` y se lanzan todos juntos en una única excepción `ValidacionNegocioException(errores)`?
