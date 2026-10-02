package com.arrupeteca.prestamo.internal.service.impl;

import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.internal.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.internal.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.internal.entity.Prestamo;
import com.arrupeteca.prestamo.internal.mapper.EjemplarPrestamoMapper;
import com.arrupeteca.prestamo.internal.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.internal.repository.PrestamoRepository;
import com.arrupeteca.prestamo.internal.service.EjemplarPrestamoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arrupeteca.prestamo.event.EjemplarDevueltoEvent;
import com.arrupeteca.prestamo.event.EjemplarPerdidoORepuestoEvent;
import com.arrupeteca.prestamo.event.MoraPagadaEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EjemplarPrestamoServiceImpl implements EjemplarPrestamoService {

    private final EjemplarPrestamoRepository ejemplarPrestamoRepository;
    private final PrestamoRepository prestamoRepository;
    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;
    private final EstadoFisicoRepository estadoFisicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjemplarPrestamoMapper ejemplarPrestamoMapper;
    private final ApplicationEventPublisher events;

    @Override
    public List<EjemplarPrestamoDetalleResponse> obtenerPorPrestamo(Long idPrestamo) {
        log.debug("Obteniendo ejemplares para préstamo ID: {}", idPrestamo);
        return ejemplarPrestamoRepository.findByPrestamoIdAndBorradoLogicoFalse(idPrestamo).stream()
                .map(ejemplarPrestamoMapper::toDetalleResponse)
                .toList();
    }

    @Override
    public EjemplarPrestamoDetalleResponse obtenerPorId(Long id) {
        log.debug("Buscando ejemplar de préstamo por ID: {}", id);
        return ejemplarPrestamoRepository.busquedaPorId(id, null)
                .map(ejemplarPrestamoMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id));
    }

    @Override
    @Transactional
    public EjemplarPrestamoDetalleResponse agregarEjemplar(EjemplarPrestamoRequest request) {
        log.info("Agregando ejemplar a préstamo");

        if (request.idPrestamo() == null || !request.idPrestamo().isPresent() || request.idPrestamo().get() == null) {
            throw new ValidacionNegocioException("El ID del préstamo es obligatorio.");
        }
        if (request.idEjemplar() == null || !request.idEjemplar().isPresent() || request.idEjemplar().get() == null) {
            throw new ValidacionNegocioException("El ID del ejemplar físico es obligatorio.");
        }

        Long idPrestamo = request.idPrestamo().get();
        Long idEjemplar = request.idEjemplar().get();

        if (idPrestamo <= 0 || idEjemplar <= 0) {
            throw new ValidacionNegocioException("Los IDs del préstamo y del ejemplar físico deben ser números positivos mayores a cero.");
        }

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el préstamo con ID: " + idPrestamo));

        if (prestamo.isBorradoLogico()) {
            throw new ValidacionNegocioException("El préstamo se encuentra inactivo.");
        }

        EjemplarLibro ejemplar = ejemplarLibroRepository.findById(idEjemplar)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar físico con ID: " + idEjemplar));

        if (ejemplar.isBorradoLogico()) {
            throw new ValidacionNegocioException("El ejemplar físico seleccionado se encuentra inactivo.");
        }

        Long idEstadoEjemplar = (request.idEstadoEjemplarPrestamo() != null && request.idEstadoEjemplarPrestamo().isPresent() && request.idEstadoEjemplarPrestamo().get() != null)
                ? request.idEstadoEjemplarPrestamo().get()
                : 1L; // 1 = EN_PRESTAMO

        EstadoEjemplarPrestamo estadoEjemplar = estadoEjemplarPrestamoRepository.findById(idEstadoEjemplar)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado de ejemplar préstamo con ID: " + idEstadoEjemplar));

        EstadoFisico estadoFisicoEntrega = null;
        if (request.idEstadoFisicoEntrega() != null && request.idEstadoFisicoEntrega().isPresent() && request.idEstadoFisicoEntrega().get() != null) {
            Long idEf = request.idEstadoFisicoEntrega().get();
            estadoFisicoEntrega = estadoFisicoRepository.findById(idEf)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado físico con ID: " + idEf));
        }

        EjemplarPrestamo entity = ejemplarPrestamoMapper.toEntity(request);
        entity.setPrestamo(prestamo);
        entity.setEjemplar(ejemplar);
        entity.setEstadoEjemplarPrestamo(estadoEjemplar);
        entity.setEstadoFisicoEntrega(estadoFisicoEntrega);
        entity.setMoraCalculada(BigDecimal.ZERO);
        entity.setMoraPagada(false);
        entity.setExentoMora(request.exentoMora() != null && request.exentoMora().isPresent() ? request.exentoMora().get() : false);
        entity.setBorradoLogico(false);

        EjemplarPrestamo guardado = ejemplarPrestamoRepository.save(entity);
        log.info("Ejemplar de préstamo guardado exitosamente con ID: {}", guardado.getId());
        return ejemplarPrestamoMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public EjemplarPrestamoDetalleResponse actualizarEjemplar(Long id, EjemplarPrestamoRequest request) {
        log.info("Actualizando ejemplar de préstamo ID: {}", id);

        EjemplarPrestamo existente = ejemplarPrestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id));

        if (request.version() != null && !request.version().equals(existente.getVersion())) {
            throw new ValidacionNegocioException("La versión proporcionada no coincide con la versión actual (" + existente.getVersion() + ")");
        }

        if (request.idEstadoEjemplarPrestamo() != null && request.idEstadoEjemplarPrestamo().isPresent() && request.idEstadoEjemplarPrestamo().get() != null) {
            Long idEstado = request.idEstadoEjemplarPrestamo().get();
            if (idEstado <= 0) {
                throw new ValidacionNegocioException("El ID del estado de ejemplar debe ser un número positivo mayor a cero.");
            }
            EstadoEjemplarPrestamo estado = estadoEjemplarPrestamoRepository.findById(idEstado)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado de ejemplar con ID: " + idEstado));
            existente.setEstadoEjemplarPrestamo(estado);
        }

        if (request.idEstadoFisicoDevolucion() != null && request.idEstadoFisicoDevolucion().isPresent() && request.idEstadoFisicoDevolucion().get() != null) {
            Long idEf = request.idEstadoFisicoDevolucion().get();
            if (idEf <= 0) {
                throw new ValidacionNegocioException("El ID del estado físico debe ser un número positivo mayor a cero.");
            }
            EstadoFisico estadoFisico = estadoFisicoRepository.findById(idEf)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el estado físico con ID: " + idEf));
            existente.setEstadoFisicoDevolucion(estadoFisico);
        }

        if (request.fechaDevolucionReal() != null && request.fechaDevolucionReal().isPresent()) {
            LocalDateTime fechaDev = request.fechaDevolucionReal().get();
            if (fechaDev != null && fechaDev.isAfter(LocalDateTime.now().plusMinutes(5))) {
                throw new ValidacionNegocioException("La fecha de devolución real no puede ser posterior a la fecha y hora actual.");
            }
            if (fechaDev != null && existente.getPrestamo() != null && existente.getPrestamo().getFechaPrestamo() != null
                    && fechaDev.isBefore(existente.getPrestamo().getFechaPrestamo())) {
                throw new ValidacionNegocioException("La fecha de devolución no puede ser anterior a la fecha en que se entregó el préstamo.");
            }
            existente.setFechaDevolucionReal(fechaDev);
        }

        if (request.exentoMora() != null && request.exentoMora().isPresent()) {
            boolean exento = request.exentoMora().get();
            existente.setExentoMora(exento);
            if (exento) {
                existente.setMoraCalculada(BigDecimal.ZERO);
            }
        }

        if (request.moraCalculada() != null && request.moraCalculada().isPresent() && !existente.isExentoMora()) {
            BigDecimal mora = request.moraCalculada().get();
            if (mora != null && mora.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidacionNegocioException("El monto de la mora calculada no puede ser negativo.");
            }
            existente.setMoraCalculada(mora);
        }

        if (request.moraPagada() != null && request.moraPagada().isPresent()) {
            existente.setMoraPagada(request.moraPagada().get());
        }

        if (request.recibidoPor() != null && request.recibidoPor().isPresent() && request.recibidoPor().get() != null) {
            Long idRecibido = request.recibidoPor().get();
            if (idRecibido <= 0) {
                throw new ValidacionNegocioException("El ID del usuario receptor debe ser un número positivo mayor a cero.");
            }
            Usuario recibidoPor = usuarioRepository.findById(idRecibido)
                    .orElseThrow(() -> new ValidacionNegocioException("No se encontró el usuario receptor con ID: " + idRecibido));
            existente.setRecibidoPor(recibidoPor);
        }

        EjemplarPrestamo actualizado = ejemplarPrestamoRepository.save(existente);
        log.info("Ejemplar de préstamo ID: {} actualizado exitosamente", id);
        return ejemplarPrestamoMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del ejemplar de préstamo ID: {} a {}", id, !estado);
        if (!ejemplarPrestamoRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id);
        }
        if (estado) {
            ejemplarPrestamoRepository.activar(id);
        } else {
            ejemplarPrestamoRepository.desactivar(id);
        }
    }

    @Override
    @Transactional
    public void pagarMora(Long id) {
        log.info("Registrando pago de mora para ejemplar de préstamo ID: {}", id);
        EjemplarPrestamo existente = ejemplarPrestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id));

        if (existente.isExentoMora()) {
            throw new ValidacionNegocioException("Este ejemplar ha sido marcado como exento de mora.");
        }

        if (existente.getMoraCalculada() == null || existente.getMoraCalculada().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionNegocioException("Este ejemplar no registra ninguna mora pendiente de pago.");
        }

        if (existente.isMoraPagada()) {
            throw new ValidacionNegocioException("La mora de este ejemplar ya ha sido pagada previamente.");
        }

        existente.setMoraPagada(true);
        EjemplarPrestamo guardado = ejemplarPrestamoRepository.save(existente);
        events.publishEvent(new MoraPagadaEvent(
                guardado.getId(),
                guardado.getPrestamo() != null && guardado.getPrestamo().getUsuario() != null ? guardado.getPrestamo().getUsuario().getId() : null,
                guardado.getMoraCalculada(),
                LocalDateTime.now()
        ));
        log.info("Mora pagada exitosamente para ejemplar de préstamo ID: {}", id);
    }

    @Override
    @Transactional
    public void exentarMora(Long id) {
        log.info("Exentando de mora al ejemplar de préstamo ID: {}", id);
        EjemplarPrestamo existente = ejemplarPrestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id));

        existente.setExentoMora(true);
        existente.setMoraCalculada(BigDecimal.ZERO);
        ejemplarPrestamoRepository.save(existente);
        log.info("Ejemplar de préstamo ID: {} marcado como exento de mora exitosamente", id);
    }
}
