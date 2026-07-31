package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
import com.arrupeteca.service.EjemplarLibroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EjemplarLibroServiceImpl implements EjemplarLibroService {

    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EjemplarLibroMapper ejemplarLibroMapper;
    private final LibroRepository libroRepository;
    private final MuebleRepository muebleRepository;
    private final EstadoFisicoRepository estadoFisicoRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    @Override
    public Page<EjemplarLibroResumen> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo ejemplares activos proyectados paginados: {}", pageable);
        return ejemplarLibroRepository.busquedaMaestraAdminProyectada(
                false, null, null, null, null, null, null, null, null, pageable);
    }

    @Override
    @Cacheable(value = "ejemplares", key = "#id")
    public EjemplarLibroDetalleResponse obtenerPorIdActivo(Long id) {
        log.debug("Obteniendo ejemplar activo por ID: {}", id);
        return ejemplarLibroRepository.findByIdAndBorradoLogicoFalse(id)
                .map(ejemplarLibroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar activo con ID: " + id));
    }

    @Override
    public Page<EjemplarLibroResumen> busquedaGlobal(String palabra, Long idGenero, Long idCategoria, Pageable pageable) {
        log.debug("Búsqueda global proyectada de ejemplares por '{}', género: {}, categoría: {} paginados: {}", palabra, idGenero, idCategoria, pageable);
        return ejemplarLibroRepository.busquedaMaestraAdminProyectada(
                false, null, idGenero, idCategoria, null, null, null, null, palabra, pageable);
    }

    @Override
    public Page<EjemplarLibroResumen> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los ejemplares proyectados paginados: {}", pageable);
        return ejemplarLibroRepository.busquedaMaestraAdminProyectada(
                null, null, null, null, null, null, null, null, null, pageable);
    }

    @Override
    public EjemplarLibroDetalleResponse obtenerPorId(Long id) {
        log.debug("Obteniendo ejemplar por ID: {}", id);
        return ejemplarLibroRepository.findById(id)
                .map(ejemplarLibroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id));
    }

    @Override
    public Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idGenero, Long idCategoria, Long idEtiqueta,
            Long idMueble, Long idEstadoFisico, Long idDisponibilidad,
            String termino, Pageable pageable) {

        log.debug("Búsqueda maestra de ejemplares para admin proyectada: esBorrado={}, idLibro={}, idEtiqueta={}", esBorrado, idLibro, idEtiqueta);

        return ejemplarLibroRepository.busquedaMaestraAdminProyectada(
                esBorrado, idLibro, idGenero, idCategoria, idEtiqueta,
                idMueble, idEstadoFisico, idDisponibilidad, termino, pageable);
    }

    @Override
    @Transactional
    public EjemplarLibroDetalleResponse crear(EjemplarLibroRequest request) {
        log.info("Creando nuevo ejemplar de libro");
        validarCreacion(request);

        EjemplarLibro ejemplar = ejemplarLibroMapper.toEntity(request);
        asignarRelaciones(ejemplar, request);

        EjemplarLibro guardado = ejemplarLibroRepository.save(ejemplar);
        log.info("Ejemplar de libro creado exitosamente con ID: {}", guardado.getId());
        return ejemplarLibroMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "ejemplares", key = "#id")
    public EjemplarLibroDetalleResponse actualizar(Long id, EjemplarLibroRequest request) {
        log.info("Actualizando ejemplar de libro con ID: {}", id);
        EjemplarLibro ejemplarExistente = ejemplarLibroRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id));

        validarActualizacion(request, ejemplarExistente);

        ejemplarLibroMapper.updateEntity(request, ejemplarExistente);
        asignarRelaciones(ejemplarExistente, request);

        EjemplarLibro actualizado = ejemplarLibroRepository.save(ejemplarExistente);
        log.info("Ejemplar de libro con ID: {} actualizado exitosamente", id);
        return ejemplarLibroMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "ejemplares", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del ejemplar ID: {} a {}", id, !estado);
        if (ejemplarLibroRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id);
        }
        if (estado) {
            ejemplarLibroRepository.activar(id);
        } else {
            ejemplarLibroRepository.desactivar(id);
        }
    }

    private void validarCreacion(EjemplarLibroRequest request) {
        List<String> errores = new ArrayList<>();
        if (request.idLibro() == null || !request.idLibro().isPresent() || request.idLibro().get() == null) {
            errores.add("Se debe elegir un libro");
        }
        if (request.idMueble() == null || !request.idMueble().isPresent() || request.idMueble().get() == null) {
            errores.add("Por favor ingresar donde se econtrara el ejemplar");
        }
        if (request.idEstadoFisico() == null || !request.idEstadoFisico().isPresent() || request.idEstadoFisico().get() == null) {
            errores.add("El estado físico no puede estar vacío");
        }
        if (request.idDisponibilidad() == null || !request.idDisponibilidad().isPresent() || request.idDisponibilidad().get() == null) {
            errores.add("La disponibilidad no puede estar vacía");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void validarActualizacion(EjemplarLibroRequest request, EjemplarLibro existente) {
        // Bloqueo optimista
        Long vExistente = existente.getVersion() != null ? existente.getVersion() : 0L;
        if (request.version() == null || !request.version().equals(vExistente)) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue la página.");
        }

        List<String> errores = new ArrayList<>();
        if (request.idLibro() != null && request.idLibro().isPresent() && request.idLibro().get() == null) {
            errores.add("Se debe elegir un libro");
        }
        if (request.idMueble() != null && request.idMueble().isPresent() && request.idMueble().get() == null) {
            errores.add("Por favor ingresar donde se econtrara el ejemplar");
        }
        if (request.idEstadoFisico() != null && request.idEstadoFisico().isPresent() && request.idEstadoFisico().get() == null) {
            errores.add("El estado físico no puede estar vacío");
        }
        if (request.idDisponibilidad() != null && request.idDisponibilidad().isPresent() && request.idDisponibilidad().get() == null) {
            errores.add("La disponibilidad no puede estar vacía");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void asignarRelaciones(EjemplarLibro ejemplar, EjemplarLibroRequest request) {
        List<String> errores = new ArrayList<>();

        if (request.idLibro() != null && request.idLibro().isPresent()) {
            Long idLibro = request.idLibro().get();
            if (idLibro != null) {
                libroRepository.findById(idLibro)
                        .ifPresentOrElse(ejemplar::setLibro,
                                () -> errores.add("El Libro con ID " + idLibro + " no existe."));
            }
        }

        if (request.idMueble() != null && request.idMueble().isPresent()) {
            Long idMueble = request.idMueble().get();
            if (idMueble != null) {
                muebleRepository.findById(idMueble)
                        .ifPresentOrElse(ejemplar::setMueble,
                                () -> errores.add("El Mueble con ID " + idMueble + " no existe."));
            }
        }

        if (request.idEstadoFisico() != null && request.idEstadoFisico().isPresent()) {
            Long idEF = request.idEstadoFisico().get();
            if (idEF != null) {
                estadoFisicoRepository.findById(idEF)
                        .ifPresentOrElse(ejemplar::setEstadoFisico,
                                () -> errores.add("El estado físico con ID " + idEF + " no existe."));
            }
        }

        if (request.idDisponibilidad() != null && request.idDisponibilidad().isPresent()) {
            Long idD = request.idDisponibilidad().get();
            if (idD != null) {
                disponibilidadRepository.findById(idD)
                        .ifPresentOrElse(ejemplar::setDisponibilidad,
                                () -> errores.add("La disponibilidad con ID " + idD + " no existe."));
            }
        }

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }
}
