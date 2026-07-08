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

---

## 3. Estrategia de Caché Local

Para optimizar las lecturas frecuentes de un mismo registro por su identificador único:
*   **`@Cacheable(value = "nombre_cache", key = "#id")`**: Aplicar en las consultas de detalle individual por ID. Dado que la caché es local (en memoria de la aplicación/JVM), los DTOs de salida no requieren implementar `Serializable`.
*   **`@CacheEvict(value = "nombre_cache", key = "#id")`**: Aplicar obligatoriamente en métodos de actualización (`actualizar`) y cambio de estado (`cambiarEstado`) para invalidar las entradas obsoletas en memoria.

---

## 4. Validaciones de Negocio y Relaciones (Fail-Fast)

*   **Validación de Duplicados en Actualizaciones**: Al validar unicidad de nombres o códigos, se debe permitir que el registro conserve su valor actual si no ha cambiado, y rechazar la petición solo si colisiona con el registro de otra entidad distinta.
*   **Recolección de Errores (Multi-Validation)**: Al validar la existencia física de IDs relacionados como en asignarRelaciones (ej. llaves foráneas):
    *   No lanzar excepciones individuales secuenciales.
    *   Acumular todos los fallos detectados en una lista de cadenas `List<String> errores`.
    *   Si la lista no está vacía, lanzar una sola `ValidacionNegocioException(errores)`. Esto mejora significativamente la experiencia del frontend al retornar todos los errores en una sola respuesta.

---

## 5. Plantilla Maestra de Referencia (AutorServiceImpl)

```java
package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
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
import com.arrupeteca.persistence.specification.AutorSpecification;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        log.debug("Obteniendo todos los autores activos con paginación: {}", pageable);
        Specification<Autor> spec = AutorSpecification.esBorrado(false);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Cacheable(value = "autores", key = "#id")
    public AutorDetalleResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando autor activo con ID: {}", id);
        Specification<Autor> spec = Specification.where(AutorSpecification.tieneId(id))
                .and(AutorSpecification.esBorrado(false));

        return autorRepository.findOne(spec)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontró Autor activo con ID: {}", id);
                    return new ValidacionNegocioException("No se encontró Autor activo con ID: " + id);
                });
    }

    @Override
    public Page<AutorResumenResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los autores (incluyendo borrados) con paginación: {}", pageable);
        return autorRepository.findAll((Specification<Autor>) null, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Cacheable(value = "autores", key = "#id")
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
        log.debug("Iniciando búsqueda global: '{}', años: {} - {}", palabra, anioInicio, anioFin);
        Specification<Autor> spec = AutorSpecification.filtroBusquedaGlobal(palabra, anioInicio, anioFin);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    public Page<AutorResumenResponse> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero,
            String termino, Pageable pageable) {
        log.debug("Iniciando búsqueda maestra admin para término: '{}'", termino);
        Specification<Autor> spec = AutorSpecification.filtroMaestroAdmin(esBorrado, idNacionalidad, idPais, idGenero, termino);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional
    public AutorDetalleResponse crear(AutorRequest request) {
        log.info("Creando nuevo autor con nombre: {}", request.nombre());
        validarUnicidadNombre(request.nombre(), null);

        Autor autor = autorMapper.toEntity(request);
        asignarRelaciones(autor, request);
        
        Autor guardado = autorRepository.save(autor);
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

        validarUnicidadNombre(request.nombre(), autorExistente);

        autorMapper.updateEntity(request, autorExistente);
        asignarRelaciones(autorExistente, request);
        
        Autor actualizado = autorRepository.save(autorExistente);
        log.info("Autor con ID: {} actualizado exitosamente", id);
        return autorMapper.toDetalleResponse(actualizado);
    }

    private void validarUnicidadNombre(String nombreNuevo, Autor autorExistente) {
        if (nombreNuevo == null || nombreNuevo.isBlank()) {
            return;
        }

        boolean esNuevo = (autorExistente == null);
        boolean nombreCambio = !esNuevo && !autorExistente.getNombre().equalsIgnoreCase(nombreNuevo.trim());

        if ((esNuevo || nombreCambio) && autorRepository.existsByNombreIgnoreCase(nombreNuevo.trim())) {
            log.warn("Intento de duplicar nombre de autor: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un autor con el nombre: " + nombreNuevo);
        }
    }

    private void asignarRelaciones(Autor autor, AutorRequest request) {
        List<String> errores = new ArrayList<>();

        if (request.idPaisNacimiento() != null) {
            paisRepository.findById(request.idPaisNacimiento())
                    .ifPresentOrElse(autor::setPaisNacimiento,
                            () -> errores.add("El país con ID " + request.idPaisNacimiento() + " no existe."));
        } else {
            autor.setPaisNacimiento(null);
        }

        if (request.idNacionalidad() != null) {
            nacionalidadRepository.findById(request.idNacionalidad())
                    .ifPresentOrElse(autor::setNacionalidad,
                            () -> errores.add("La nacionalidad con ID " + request.idNacionalidad() + " no existe."));
        } else {
            autor.setNacionalidad(null);
        }

        if (request.idGeneroPrincipal() != null) {
            generoRepository.findById(request.idGeneroPrincipal())
                    .ifPresentOrElse(autor::setGeneroPrincipal,
                            () -> errores.add("El género con ID " + request.idGeneroPrincipal() + " no existe."));
        } else {
            autor.setGeneroPrincipal(null);
        }

        if (request.idObraCumbre() != null) {
            obraRepository.findById(request.idObraCumbre())
                    .ifPresentOrElse(autor::setObraCumbre,
                            () -> errores.add("La obra con ID " + request.idObraCumbre() + " no existe."));
        } else {
            autor.setObraCumbre(null);
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
        if (!autorRepository.existsByIdIncludeDeleted(id)) {
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
        return autorRepository.buscarPosiblesDuplicados(termino.trim(), idExcluir)
                .stream()
                .map(autorMapper::toResumen)
                .toList();
    }
}
```

---

## 6. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿Configuración transaccional correcta?**
   * ¿Tiene la clase `@Transactional(readOnly = true)`?
   * ¿Los métodos que modifican o graban datos tienen `@Transactional` explícito?
2. **¿Inyección por constructor correcta?**
   * ¿Están todos los colaboradores marcados como `private final` y se usa `@RequiredArgsConstructor`?
3. **¿Mapeo de caché seguro?**
   * ¿Se incluyó `@Cacheable` en los métodos de consulta por ID?
   * ¿Se incluyó `@CacheEvict` en los métodos de modificación correspondientes para limpiar la caché?
4. **¿Validaciones cruzadas correctas?**
   * ¿La validación de unicidad permite mantener el valor del propio objeto al actualizar?
5. **¿Recolección acumulativa de errores de relaciones?**
   * Al buscar entidades de claves foráneas, ¿se acumulan los errores en una `List<String>` y se lanzan todos juntos en una única excepción `ValidacionNegocioException(errores)`?
6. **¿Código libre de Javadoc inline innecesario?**
   * ¿Se removieron los comentarios Javadoc de las clases concretas para cumplir con las directrices de limpieza visual?

---
*Este estándar garantiza servicios robustos, limpios y transaccionales en todo el dominio de Arrupeteca.*
