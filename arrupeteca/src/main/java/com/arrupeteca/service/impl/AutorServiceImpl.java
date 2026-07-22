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
            Year anioInicio, Year anioFin, String termino, Pageable pageable) {
        log.debug("Iniciando búsqueda maestra admin para término: '{}', años: {} - {}", termino, anioInicio, anioFin);
        Specification<Autor> spec = AutorSpecification.filtroMaestroAdmin(esBorrado, idNacionalidad, idPais, idGenero, anioInicio, anioFin, termino);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
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

        // Validación de País
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

        // Validación de Nacionalidad
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

        // Validación de Género
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

        // Validación de Obra Cumbre
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
        if (autorRepository.countByIdIncludeDeleted(id) == 0) {
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
