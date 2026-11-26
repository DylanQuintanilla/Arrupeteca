package com.arrupeteca.transferencia.internal.listener;

import com.arrupeteca.config.CatalogoEstadosConstants;
import com.arrupeteca.transferencia.event.SolicitudTransferenciaCreadaEvent;
import com.arrupeteca.transferencia.event.SolicitudTransferenciaEvaluadaEvent;
import com.arrupeteca.transferencia.event.TransferenciaConfirmadaEvent;
import com.arrupeteca.transferencia.event.TransferenciaDespachadaEvent;
import com.arrupeteca.transferencia.internal.entity.EstadoSolicitudTransferencia;
import com.arrupeteca.transferencia.internal.entity.SolicitudTransferencia;
import com.arrupeteca.transferencia.internal.repository.EstadoSolicitudTransferenciaRepository;
import com.arrupeteca.transferencia.internal.repository.SolicitudTransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferenciaEventListener {

    private final SolicitudTransferenciaRepository solicitudRepository;
    private final EstadoSolicitudTransferenciaRepository estadoSolicitudRepository;

    @TransactionalEventListener
    public void alCrearSolicitud(SolicitudTransferenciaCreadaEvent event) {
        log.info("SPRING EVENT [SolicitudTransferenciaCreadaEvent]: Solicitud #{} creada por usuario #{} para salon #{} (Total libros: {})",
                event.idSolicitud(), event.idUsuarioSolicitante(), event.idSalonDestino(), event.totalLibrosSolicitados());
    }

    @TransactionalEventListener
    public void alEvaluarSolicitud(SolicitudTransferenciaEvaluadaEvent event) {
        log.info("SPRING EVENT [SolicitudTransferenciaEvaluadaEvent]: Solicitud #{} evaluada -> Aprobada: {}, Motivo Rechazo: '{}'",
                event.idSolicitud(), event.aprobada(), event.motivoRechazo());
    }

    @TransactionalEventListener
    public void alDespacharTransferencia(TransferenciaDespachadaEvent event) {
        log.info("SPRING EVENT [TransferenciaDespachadaEvent]: Transferencia #{} despachada desde mueble #{} hacia mueble #{} con {} ejemplares",
                event.idTransferencia(), event.idMuebleOrigen(), event.idMuebleDestino(), event.idsEjemplares().size());
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alConfirmarTransferencia(TransferenciaConfirmadaEvent event) {
        log.info("SPRING EVENT [TransferenciaConfirmadaEvent]: Transferencia #{} confirmada en mueble #{} por usuario #{}",
                event.idTransferencia(), event.idMuebleDestino(), event.idUsuarioRecibe());

        if (event.idSolicitudOriginaria() != null) {
            solicitudRepository.findById(event.idSolicitudOriginaria()).ifPresent(sol -> {
                estadoSolicitudRepository.buscarActivosPorNombre(CatalogoEstadosConstants.EstadoSolicitudTransferencia.FINALIZADA).stream().findFirst().ifPresent(estadoFinalizada -> {
                    sol.setEstadoSolicitudTransferencia(estadoFinalizada);
                    solicitudRepository.save(sol);
                    log.info("Solicitud #{} actualizada a estado FINALIZADA automaticamente via evento", sol.getId());
                });
            });
        }
    }
}
