# Est├índar de Desarrollo: Servicios Spring Boot (Arrupeteca)

Este documento establece las directrices, convenciones y mejores pr├ícticas para el dise├▒o e implementaci├│n de la capa de **Servicio** (L├│gica de Negocio) en Arrupeteca.

---

## 1. Configuraci├│n de Clase y Dependencias

Toda clase de servicio debe estructurarse como una interfaz (`[Entidad]Service`) y su respectiva implementaci├│n (`[Entidad]ServiceImpl`). La implementaci├│n debe cumplir con:

*   **`@Service`**: Registro obligatorio en el contenedor de Spring.
*   **`@RequiredArgsConstructor`**: Inyecci├│n de dependencias por constructor generada de forma autom├ítica por Lombok (los atributos inyectados deben ser `private final`).
*   **`@Transactional(readOnly = true)`**: Configurado a nivel de clase para que todas las consultas de lectura sean r├ípidas y de solo lectura por defecto.
*   **`@Slf4j`**: Anotaci├│n de Lombok para habilitar logging de trazabilidad en depuraciones.

---

## 2. Gesti├│n de Transacciones de Escritura

Los m├⌐todos que realicen modificaciones de datos (`crear`, `actualizar`, `cambiarEstado`, o borrados l├│gicos) deben sobrescribir la transaccionalidad de solo lectura utilizando la anotaci├│n `@Transactional` a nivel de m├⌐todo.

*   **Forzar actualizaci├│n de versi├│n**: En los m├⌐todos `crear()` y `actualizar()`, se debe utilizar **`saveAndFlush()`** en lugar del simple `save()` para sincronizar inmediatamente los cambios con la base de datos, asegurando que el DTO de respuesta contenga el n├║mero de versi├│n actualizado para el control de bloqueo optimista del cliente.

---

## 3. Delegaci├│n de Proyecciones Directas (`busqueda`) vs Vistas de Detalle (`busquedaPorId`)

Para maximizar el rendimiento y la eficiencia de la memoria en la capa de servicio:

*   **M├⌐todos de Listado Paginado (`busquedaGlobal`, `busquedaAdmin`)**:
    *   **Delegaci├│n Directa**: Deben delegar directamente al m├⌐todo unificado proyectado del repositorio (`autorRepository.busqueda(...)`).
    *   **`busquedaGlobal`**: Pasa `borradoLogico = false` a `busqueda(...)` para garantizar que los usuarios normales consulten ├║nicamente registros activos.
    *   **`busquedaAdmin`**: Pasa `borradoLogico` (el cual puede ser `null` para ver todos, `false` para activos o `true` para inactivos) seg├║n la selecci├│n del administrador.
    *   **Prohibido usar MapStruct en Lecturas Paginadas**: NO se debe invocar `.map(mapper::toResumen)`, ya que el resultado entregado por el repositorio ya es la p├ígina de DTOs finales (`Page<[Entidad]Resumen>`) instanciada por SQL.
*   **M├⌐todos de Detalle por ID (`busquedaGlobalPorId`, `busquedaAdminPorId`)**:
    *   **`busquedaGlobalPorId`**: Consulta la entidad desde el repositorio invocando `autorRepository.busquedaPorId(id, false)`.
    *   **`busquedaAdminPorId`**: Consulta la entidad invocando `autorRepository.busquedaPorId(id, null)`.
    *   Mapean el objeto obtenido con MapStruct (`.map(mapper::toDetalleResponse)`).

---

## 4. Estrategia de Cach├⌐ Local

Para optimizar las lecturas frecuentes de un mismo registro por su identificador ├║nico:
*   **`@Cacheable(value = "nombre_cache", key = "#id")`**: Aplicar **├║nicamente en las consultas de detalle del cat├ílogo p├║blico** (`busquedaGlobalPorId`).
*   **Evitar colisiones con consultas de administraci├│n**: Los m├⌐todos de detalle para administradores (`busquedaAdminPorId`) **no deben ser cacheados**, ya que los administradores modifican datos y requieren ver la versi├│n real en base de datos de manera inmediata.
*   **`@CacheEvict(value = "nombre_cache", key = "#id")`**: Aplicar obligatoriamente en m├⌐todos de modificaci├│n (`actualizar`) y cambio de estado (`cambiarEstado`) para invalidar de forma quir├║rgica las entradas obsoletas en memoria.

---

## 5. Validaciones de Negocio y Relaciones (Fail-Fast)

*   **Validaciones de obligatoriedad seg├║n la acci├│n**: Como el DTO es reutilizado para creaci├│n (POST) y actualizaci├│n parcial (PUT/PATCH), las validaciones l├│gicas obligatorias de presencia deben validarse de manera din├ímica en la capa de servicio:
    *   En `crear()`: Se verifica que todos los campos mandatorios est├⌐n provistos y no en blanco.
    *   En `actualizar()`: Se comprueban ├║nicamente si se encuentran presentes en el `JsonNullable` del DTO, y tras hacer el merge se valida el estado final de la entidad.
*   **Bloqueo Optimista Manual**: Al actualizar, se debe comprobar la versi├│n del DTO. Si no coincide con la versi├│n existente en la entidad, lanzar una excepci├│n de negocio de inmediato.
*   **Recolecci├│n de Errores (Multi-Validation)**: Al validar la existencia de relaciones, acumular todos los fallos detectados en una lista de cadenas `List<String> errores` y lanzar una sola `ValidacionNegocioException(errores)`.

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
    public Page<AutorResumenResponse> busquedaGlobal(
            Long idNacionalidad, Long idPais, Long idGenero, Long idObraCumbre,
            Year anioInicio, Year anioFin, String termino, Pageable pageable) {
        log.debug("Iniciando b├║squeda global de autores activos con t├⌐rmino: '{}'", termino);
        String term = (termino != null && !termino.isBlank()) ? termino.trim() : null;
        return autorRepository.busqueda(false, idNacionalidad, idPais, idGenero, idObraCumbre, anioInicio, anioFin, term, pageable);
    }

    @Override
    @Cacheable(value = "autores", key = "#id")
    public AutorDetalleResponse busquedaGlobalPorId(Long id) {
        log.debug("Buscando autor activo con ID: {}", id);
        return autorRepository.busquedaPorId(id, false)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontr├│ Autor activo con ID: {}", id);
                    return new ValidacionNegocioException("No se encontr├│ Autor activo con ID: " + id);
                });
    }

    @Override
    public Page<AutorResumenResponse> busquedaAdmin(
            Boolean borradoLogico, Long idNacionalidad, Long idPais, Long idGenero, Long idObraCumbre,
            Year anioInicio, Year anioFin, String termino, Pageable pageable) {
        log.debug("Iniciando b├║squeda admin de autores para t├⌐rmino: '{}', borrado: {}", termino, borradoLogico);
        String term = (termino != null && !termino.isBlank()) ? termino.trim() : null;
        return autorRepository.busqueda(borradoLogico, idNacionalidad, idPais, idGenero, idObraCumbre, anioInicio, anioFin, term, pageable);
    }

    @Override
    public AutorDetalleResponse busquedaAdminPorId(Long id) {
        log.debug("Buscando autor con ID (admin): {}", id);
        return autorRepository.busquedaPorId(id, null)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> {
                    log.warn("No se encontr├│ Autor con ID: {}", id);
                    return new ValidacionNegocioException("No se encontr├│ Autor con ID: " + id);
                });
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
        Autor autorExistente = autorRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontr├│ Autor con ID: " + id));

        validarDatosActualizacion(request, autorExistente);
        validarUnicidadNombre(request.nombre(), autorExistente);

        autorMapper.updateEntity(request, autorExistente);
        asignarRelaciones(autorExistente, request);

        // Validar fechas cruzadas post-merge
        if (autorExistente.getAnioNacimiento() != null && autorExistente.getAnioFallecimiento() != null) {
            if (autorExistente.getAnioNacimiento().isAfter(autorExistente.getAnioFallecimiento())) {
                throw new ValidacionNegocioException("El a├▒o de nacimiento no puede ser mayor al a├▒o de fallecimiento (" + autorExistente.getAnioFallecimiento() + ")");
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
            throw new ValidacionNegocioException("La biografia no puede estar vac├¡a");
        }
    }

    private void validarDatosActualizacion(AutorRequest request, Autor autorExistente) {
        // Validaci├│n manual de Bloqueo Optimista
        Long vExistente = autorExistente.getVersion() != null ? autorExistente.getVersion() : 0L;
        if (request.version() == null || !request.version().equals(vExistente)) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue la p├ígina.");
        }

        // Si se env├¡an en el JSON, no deben estar vac├¡os
        if (request.nombre() != null && request.nombre().isPresent()) {
            if (request.nombre().get() == null || request.nombre().get().isBlank()) {
                throw new ValidacionNegocioException("El nombre no puede estar vac├¡o");
            }
        }
        if (request.seudonimo() != null && request.seudonimo().isPresent()) {
            if (request.seudonimo().get() == null || request.seudonimo().get().isBlank()) {
                throw new ValidacionNegocioException("El seudonimo no puede estar vac├¡o");
            }
        }
        if (request.biografia() != null && request.biografia().isPresent()) {
            if (request.biografia().get() == null || request.biografia().get().isBlank()) {
                throw new ValidacionNegocioException("La biografia no puede estar vac├¡a");
            }
        }

        // Post-merge, verificar que a├║n quede al menos un nombre o seud├│nimo v├ílido
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
                                () -> errores.add("El pa├¡s con ID " + idPais + " no existe."));
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
                                () -> errores.add("El g├⌐nero con ID " + idGen + " no existe."));
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
            throw new ValidacionNegocioException("No se encontr├│ Autor con ID: " + id);
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
        log.debug("Buscando duplicados para el t├⌐rmino: '{}'", termino);
        return autorRepository.buscarDuplicados(termino.trim(), idExcluir, Sort.by("nombre"))
                .stream()
                .map(autorMapper::toResumen)
                .toList();
    }
}
```

---

## 7. Lista de Autoverificaci├│n (Checklist del Desarrollador)

1. **┬┐Configuraci├│n transaccional correcta?**
   * ┬┐Tiene la clase `@Transactional(readOnly = true)`?
   * ┬┐Los m├⌐todos que modifican o graban datos tienen `@Transactional` expl├¡cito?
   * ┬┐Se utiliza `saveAndFlush()` para forzar el incremento de la versi├│n del bloqueo optimista en creaciones y actualizaciones?
2. **┬┐Inyecci├│n por constructor correcta?**
   * ┬┐Est├ín todos los colaboradores marcados como `private final` y se usa `@RequiredArgsConstructor`?
3. **┬┐Mapeo de cach├⌐ seguro?**
   * ┬┐Se incluy├│ `@Cacheable` solo en las consultas p├║blicas de lectura por ID (`busquedaGlobalPorId`)?
   * ┬┐Se excluy├│ del cach├⌐ el m├⌐todo de lectura del administrador (`busquedaAdminPorId`) para evitar colisiones y datos fantasmas?
   * ┬┐Se incluy├│ `@CacheEvict` en los m├⌐todos de modificaci├│n correspondientes para limpiar la cach├⌐?
4. **┬┐Validaciones cruzadas correctas?**
   * ┬┐La validaci├│n de unicidad de nombres desenvuelve el `JsonNullable` y permite mantener el valor original al actualizar?
5. **┬┐Recolecci├│n acumulativa de errores de relaciones?**
   * Al buscar entidades de claves for├íneas, ┬┐se acumulan los errores en una `List<String>` y se lanzan todos juntos en una ├║nica excepci├│n `ValidacionNegocioException(errores)`?
