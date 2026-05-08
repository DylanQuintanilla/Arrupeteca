package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
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
    public Page<AutorResumen> obtenerTodosActivos(Pageable pageable) {
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
    public Page<AutorResumen> obtenerTodos(Pageable pageable) {
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
    public Page<AutorResumen> busquedaGlobal(String palabra, Year anioInicio, Year anioFin, Pageable pageable) {
        log.debug("Iniciando búsqueda global: '{}', años: {} - {}", palabra, anioInicio, anioFin);
        Specification<Autor> spec = AutorSpecification.filtroBusquedaGlobal(palabra, anioInicio, anioFin);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    public Page<AutorResumen> busquedaMaestraAdmin(
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

        // Validación de País
        if (request.idPaisNacimiento() != null) {
            paisRepository.findById(request.idPaisNacimiento())
                    .ifPresentOrElse(autor::setPaisNacimiento,
                            () -> errores.add("El país con ID " + request.idPaisNacimiento() + " no existe."));
        } else {
            autor.setPaisNacimiento(null);
        }

        // Validación de Nacionalidad
        if (request.idNacionalidad() != null) {
            nacionalidadRepository.findById(request.idNacionalidad())
                    .ifPresentOrElse(autor::setNacionalidad,
                            () -> errores.add("La nacionalidad con ID " + request.idNacionalidad() + " no existe."));
        } else {
            autor.setNacionalidad(null);
        }

        // Validación de Género
        if (request.idGeneroPrincipal() != null) {
            generoRepository.findById(request.idGeneroPrincipal())
                    .ifPresentOrElse(autor::setGeneroPrincipal,
                            () -> errores.add("El género con ID " + request.idGeneroPrincipal() + " no existe."));
        } else {
            autor.setGeneroPrincipal(null);
        }

        // Validación de Obra Cumbre
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
    public List<AutorResumen> buscarDuplicados(String termino, Long idExcluir) {
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
