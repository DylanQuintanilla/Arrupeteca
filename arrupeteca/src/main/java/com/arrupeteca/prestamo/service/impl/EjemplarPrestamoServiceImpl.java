package com.arrupeteca.prestamo.service.impl;

import com.arrupeteca.config.CatalogoEstadosConstants;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.DisponibilidadRepository;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.persistence.entity.EjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.Prestamo;
import com.arrupeteca.prestamo.mapper.EjemplarPrestamoMapper;
import com.arrupeteca.prestamo.persistence.repository.EjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.EstadoEjemplarPrestamoRepository;
import com.arrupeteca.prestamo.persistence.repository.PrestamoRepository;
import com.arrupeteca.prestamo.service.EjemplarPrestamoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EjemplarPrestamoServiceImpl implements EjemplarPrestamoService {

    private static final List<String> ESTADOS_CERRADOS = List.of(
            CatalogoEstadosConstants.EstadoEjemplarPrestamo.DEVUELTO,
            CatalogoEstadosConstants.EstadoEjemplarPrestamo.PERDIDO,
            CatalogoEstadosConstants.EstadoEjemplarPrestamo.REPUESTO
    );

    private final EjemplarPrestamoRepository ejemplarPrestamoRepository;
    private final PrestamoRepository prestamoRepository;
    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final EstadoEjemplarPrestamoRepository estadoEjemplarPrestamoRepository;
    private final EstadoFisicoRepository estadoFisicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjemplarPrestamoMapper ejemplarPrestamoMapper;

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

        validarEjemplarDisponibleParaPrestamo(ejemplar, idPrestamo);

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

        EjemplarPrestamo guardado = ejemplarPrestamoRepository.saveAndFlush(entity);
        if (CatalogoEstadosConstants.EstadoEjemplarPrestamo.EN_PRESTAMO.equalsIgnoreCase(estadoEjemplar.getNombre())) {
            sincronizarDisponibilidadEjemplar(ejemplar, CatalogoEstadosConstants.Disponibilidad.PRESTADO);
        }
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
            if (existente.getEjemplar() != null) {
                existente.getEjemplar().setEstadoFisico(estadoFisico);
            }
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

        EjemplarPrestamo actualizado = ejemplarPrestamoRepository.saveAndFlush(existente);
        sincronizarDisponibilidadAlActualizar(actualizado);
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
    public EjemplarPrestamoDetalleResponse pagarMora(Long id) {
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
        EjemplarPrestamo guardado = ejemplarPrestamoRepository.saveAndFlush(existente);
        log.info("Mora pagada exitosamente para ejemplar de préstamo ID: {}", id);
        return ejemplarPrestamoMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    public EjemplarPrestamoDetalleResponse exentarMora(Long id) {
        log.info("Exentando de mora al ejemplar de préstamo ID: {}", id);
        EjemplarPrestamo existente = ejemplarPrestamoRepository.busquedaPorId(id, null)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró el ejemplar de préstamo con ID: " + id));

        existente.setExentoMora(true);
        existente.setMoraCalculada(BigDecimal.ZERO);
        EjemplarPrestamo guardado = ejemplarPrestamoRepository.saveAndFlush(existente);
        log.info("Ejemplar de préstamo ID: {} marcado como exento de mora exitosamente", id);
        return ejemplarPrestamoMapper.toDetalleResponse(guardado);
    }

    private void validarEjemplarDisponibleParaPrestamo(EjemplarLibro ejemplar, Long idPrestamo) {
        String disponibilidadActual = ejemplar.getDisponibilidad() != null ? ejemplar.getDisponibilidad().getNombre() : null;
        if (!CatalogoEstadosConstants.Disponibilidad.DISPONIBLE.equalsIgnoreCase(disponibilidadActual)) {
            throw new ValidacionNegocioException(
                    "El ejemplar físico ID " + ejemplar.getId() + " no está disponible para préstamo (disponibilidad actual: "
                            + (disponibilidadActual != null ? disponibilidadActual : "sin asignar") + ").");
        }
        if (ejemplarPrestamoRepository.existsByPrestamoIdAndEjemplarIdAndBorradoLogicoFalse(idPrestamo, ejemplar.getId())) {
            throw new ValidacionNegocioException(
                    "El ejemplar físico ID " + ejemplar.getId() + " ya fue agregado a este préstamo.");
        }
        if (ejemplarPrestamoRepository.existePrestamoActivoParaEjemplar(ejemplar.getId(), ESTADOS_CERRADOS, null)) {
            throw new ValidacionNegocioException(
                    "El ejemplar físico ID " + ejemplar.getId() + " ya se encuentra asignado a otro préstamo activo.");
        }
    }

    private void sincronizarDisponibilidadAlActualizar(EjemplarPrestamo ejemplarPrestamo) {
        EjemplarLibro ejemplar = ejemplarPrestamo.getEjemplar();
        if (ejemplar == null) {
            return;
        }
        String estadoNombre = ejemplarPrestamo.getEstadoEjemplarPrestamo() != null
                ? ejemplarPrestamo.getEstadoEjemplarPrestamo().getNombre()
                : null;
        if (estadoNombre == null) {
            return;
        }
        if (CatalogoEstadosConstants.EstadoEjemplarPrestamo.DEVUELTO.equalsIgnoreCase(estadoNombre)
                || CatalogoEstadosConstants.EstadoEjemplarPrestamo.REPUESTO.equalsIgnoreCase(estadoNombre)) {
            sincronizarDisponibilidadEjemplar(ejemplar, CatalogoEstadosConstants.Disponibilidad.DISPONIBLE);
        } else if (CatalogoEstadosConstants.EstadoEjemplarPrestamo.PERDIDO.equalsIgnoreCase(estadoNombre)) {
            sincronizarDisponibilidadEjemplar(ejemplar, CatalogoEstadosConstants.Disponibilidad.PERDIDO);
        } else if (CatalogoEstadosConstants.EstadoEjemplarPrestamo.EN_PRESTAMO.equalsIgnoreCase(estadoNombre)
                && ejemplarPrestamo.getFechaDevolucionReal() == null) {
            if (ejemplarPrestamoRepository.existePrestamoActivoParaEjemplar(ejemplar.getId(), ESTADOS_CERRADOS, ejemplarPrestamo.getId())) {
                throw new ValidacionNegocioException(
                        "El ejemplar físico ID " + ejemplar.getId() + " ya se encuentra asignado a otro préstamo activo.");
            }
            sincronizarDisponibilidadEjemplar(ejemplar, CatalogoEstadosConstants.Disponibilidad.PRESTADO);
        }
    }

    private void sincronizarDisponibilidadEjemplar(EjemplarLibro ejemplar, String nombreDisponibilidad) {
        Disponibilidad disponibilidad = disponibilidadRepository
                .findFirstByNombreIgnoreCaseAndBorradoLogicoFalse(nombreDisponibilidad)
                .orElseThrow(() -> new ValidacionNegocioException(
                        "No se encontró la disponibilidad activa '" + nombreDisponibilidad + "' en el catálogo."));
        ejemplar.setDisponibilidad(disponibilidad);
        ejemplarLibroRepository.save(ejemplar);
    }
}
