package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.EjemplarLibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EjemplarLibroServiceImpl implements EjemplarLibroService {

    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EjemplarLibroMapper ejemplarLibroMapper;
    private final LibroRepository libroRepository;
    private final MuebleRepository muebleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> obtenerTodosActivos(Pageable pageable) {
        return ejemplarLibroRepository.findByBorradoLogicoFalse(pageable)
                .map(ejemplarLibroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroDetalleResponse obtenerPorIdActivo(Long id) {
        return ejemplarLibroRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(ejemplarLibroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return ejemplarLibroRepository.busquedaGlobal(palabra, pageable)
                .map(ejemplarLibroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> obtenerTodos(Pageable pageable) {
        return ejemplarLibroRepository.findAllProjectedBy(pageable)
                .map(ejemplarLibroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroDetalleResponse obtenerPorId(Long id) {
        return ejemplarLibroRepository.findProjectedById(id)
                .map(ejemplarLibroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idMueble, Object estadoFisicoStr,
            Object disponibilidadStr, String termino, Pageable pageable) {

        EstadoFisico estadoFisico = (estadoFisicoStr instanceof String s && !s.isEmpty())
                ? EstadoFisico.valueOf(s) : (estadoFisicoStr instanceof EstadoFisico ef ? ef : null);

        Disponibilidad disponibilidad = (disponibilidadStr instanceof String s && !s.isEmpty())
                ? Disponibilidad.valueOf(s) : (disponibilidadStr instanceof Disponibilidad d ? d : null);

        return ejemplarLibroRepository.busquedaMaestraAdmin(esBorrado, idLibro, idMueble, estadoFisico,
                        disponibilidad, termino, pageable)
                .map(ejemplarLibroMapper::toResumen);
    }

    @Override
    @Transactional
    public EjemplarLibroDetalleResponse crear(EjemplarLibroRequest request) {
        EjemplarLibro ejemplar = ejemplarLibroMapper.toEntity(request);
        asignarRelaciones(ejemplar, request);
        return ejemplarLibroMapper.toDetalleResponse(ejemplarLibroRepository.save(ejemplar));
    }

    @Override
    @Transactional
    public EjemplarLibroDetalleResponse actualizar(Long id, EjemplarLibroRequest request) {
        EjemplarLibro ejemplarExistente = ejemplarLibroRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id));

        ejemplarLibroMapper.updateEntity(request, ejemplarExistente);
        asignarRelaciones(ejemplarExistente, request);
        return ejemplarLibroMapper.toDetalleResponse(ejemplarLibroRepository.save(ejemplarExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!ejemplarLibroRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Ejemplar con ID: " + id);
        }
        if (estado) {
            ejemplarLibroRepository.activar(id);
        } else {
            ejemplarLibroRepository.desactivar(id);
        }
    }

    private void asignarRelaciones(EjemplarLibro ejemplar, EjemplarLibroRequest request) {
        if (request.getIdLibro() != null) {
            ejemplar.setLibro(libroRepository.findById(request.getIdLibro())
                    .orElseThrow(() -> new ValidacionNegocioException("El Libro indicado no existe")));
        }
        if (request.getIdMueble() != null) {
            ejemplar.setMueble(muebleRepository.findById(request.getIdMueble())
                    .orElseThrow(() -> new ValidacionNegocioException("El Mueble indicado no existe")));
        }
    }
}
