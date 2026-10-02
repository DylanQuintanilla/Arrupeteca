package com.arrupeteca.prestamo.internal.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamo;
import com.arrupeteca.prestamo.internal.entity.SolicitudPrestamoDetalle;
import com.arrupeteca.prestamo.internal.mapper.SolicitudPrestamoDetalleMapper;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoDetalleRepository;
import com.arrupeteca.prestamo.internal.repository.SolicitudPrestamoRepository;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoDetalleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolicitudPrestamoDetalleServiceImpl implements SolicitudPrestamoDetalleService {

    private final SolicitudPrestamoDetalleRepository detalleRepository;
    private final SolicitudPrestamoRepository solicitudRepository;
    private final LibroRepository libroRepository;
    private final SolicitudPrestamoDetalleMapper detalleMapper;

    @Override
    public List<SolicitudPrestamoDetalleItemResponse> obtenerPorSolicitud(Long idSolicitud) {
        log.debug("Obteniendo detalles para solicitud ID: {}", idSolicitud);
        return detalleRepository.findBySolicitudPrestamoIdAndBorradoLogicoFalse(idSolicitud).stream()
                .map(detalleMapper::toResponse)
                .toList();
    }

    @Override
    public SolicitudPrestamoDetalleItemResponse obtenerPorId(Long id) {
        log.debug("Buscando detalle de solicitud por ID: {}", id);
        return detalleRepository.busquedaPorId(id, null)
                .map(detalleMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el detalle de solicitud con ID: " + id));
    }

    @Override
    @Transactional
    public SolicitudPrestamoDetalleItemResponse agregarDetalle(SolicitudPrestamoDetalleRequest request) {
        log.info("Agregando nuevo detalle a solicitud");

        if (request.idSolicitud() == null || !request.idSolicitud().isPresent() || request.idSolicitud().get() == null) {
            throw new ValidacionNegocioException("El ID de la solicitud es obligatorio para agregar un detalle.");
        }
        if (request.idLibro() == null || !request.idLibro().isPresent() || request.idLibro().get() == null) {
            throw new ValidacionNegocioException("El ID del libro es obligatorio para agregar un detalle.");
        }

        Long idSolicitud = request.idSolicitud().get();
        Long idLibro = request.idLibro().get();

        if (idSolicitud <= 0 || idLibro <= 0) {
            throw new ValidacionNegocioException("Los IDs de la solicitud y del libro deben ser números positivos mayores a cero.");
        }

        SolicitudPrestamo solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una solicitud de préstamo con ID: " + idSolicitud));

        if (solicitud.isBorradoLogico()) {
            throw new ValidacionNegocioException("La solicitud de préstamo se encuentra inactiva.");
        }

        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un libro con ID: " + idLibro));

        if (libro.isBorradoLogico()) {
            throw new ValidacionNegocioException("El libro seleccionado se encuentra inactivo.");
        }

        Integer cantidad = (request.cantidadSolicitada() != null && request.cantidadSolicitada().isPresent() && request.cantidadSolicitada().get() != null)
                ? request.cantidadSolicitada().get()
                : 1;

        if (cantidad < 1) {
            throw new ValidacionNegocioException("La cantidad solicitada debe ser al menos 1.");
        }

        SolicitudPrestamoDetalle detalle = detalleMapper.toEntity(request);
        detalle.setSolicitudPrestamo(solicitud);
        detalle.setLibro(libro);
        detalle.setCantidadSolicitada(cantidad);
        detalle.setBorradoLogico(false);

        SolicitudPrestamoDetalle guardado = detalleRepository.save(detalle);
        log.info("Detalle de solicitud agregado exitosamente con ID: {}", guardado.getId());
        return detalleMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public SolicitudPrestamoDetalleItemResponse actualizarDetalle(Long id, SolicitudPrestamoDetalleRequest request) {
        log.info("Actualizando detalle de solicitud ID: {}", id);

        SolicitudPrestamoDetalle existente = detalleRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el detalle de solicitud con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada no coincide con la versión actual del detalle (" + existente.getVersion() + ")");
        }

        if (request.idLibro() != null && request.idLibro().isPresent() && request.idLibro().get() != null) {
            Long newIdLibro = request.idLibro().get();
            if (newIdLibro <= 0) {
                throw new ValidacionNegocioException("El ID del libro debe ser un número positivo mayor a cero.");
            }
            Libro libro = libroRepository.findById(newIdLibro)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró un libro con ID: " + newIdLibro));
            existente.setLibro(libro);
        }

        if (request.cantidadSolicitada() != null && request.cantidadSolicitada().isPresent() && request.cantidadSolicitada().get() != null) {
            Integer nuevaCantidad = request.cantidadSolicitada().get();
            if (nuevaCantidad < 1) {
                throw new ValidacionNegocioException("La cantidad solicitada debe ser al menos 1.");
            }
            existente.setCantidadSolicitada(nuevaCantidad);
        }

        SolicitudPrestamoDetalle actualizado = detalleRepository.save(existente);
        log.info("Detalle de solicitud ID: {} actualizado exitosamente", id);
        return detalleMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del detalle ID: {} a {}", id, !estado);
        if (!detalleRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró el detalle de solicitud con ID: " + id);
        }
        if (estado) {
            detalleRepository.activar(id);
        } else {
            detalleRepository.desactivar(id);
        }
    }
}
