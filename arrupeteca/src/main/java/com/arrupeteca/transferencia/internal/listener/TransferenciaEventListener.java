package com.arrupeteca.transferencia.internal.listener;

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
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferenciaEventListener {

    private final SolicitudTransferenciaRepository solicitudRepository;
    private final EstadoSolicitudTransferenciaRepository estadoSolicitudRepository;

    @ApplicationModuleListener
    public void alCrearSolicitud(SolicitudTransferenciaCreadaEvent event) {
        log.info("SPRING MODULITH EVENT [SolicitudTransferenciaCreadaEvent]: Solicitud #{} creada por usuario #{} para salon #{} (Total libros: {})",
                event.idSolicitud(), event.idUsuarioSolicitante(), event.idSalonDestino(), event.totalLibrosSolicitados());
    }

    @ApplicationModuleListener
    public void alEvaluarSolicitud(SolicitudTransferenciaEvaluadaEvent event) {
        log.info("SPRING MODULITH EVENT [SolicitudTransferenciaEvaluadaEvent]: Solicitud #{} evaluada -> Aprobada: {}, Motivo Rechazo: '{}'",
                event.idSolicitud(), event.aprobada(), event.motivoRechazo());
    }

    @ApplicationModuleListener
    public void alDespacharTransferencia(TransferenciaDespachadaEvent event) {
        log.info("SPRING MODULITH EVENT [TransferenciaDespachadaEvent]: Transferencia #{} despachada desde mueble #{} hacia mueble #{} con {} ejemplares",
                event.idTransferencia(), event.idMuebleOrigen(), event.idMuebleDestino(), event.idsEjemplares().size());
    }

    @ApplicationModuleListener
    public void alConfirmarTransferencia(TransferenciaConfirmadaEvent event) {
        log.info("SPRING MODULITH EVENT [TransferenciaConfirmadaEvent]: Transferencia #{} confirmada en mueble #{} por usuario #{}",
                event.idTransferencia(), event.idMuebleDestino(), event.idUsuarioRecibe());

        if (event.idSolicitudOriginaria() != null) {
            solicitudRepository.findById(event.idSolicitudOriginaria()).ifPresent(sol -> {
                estadoSolicitudRepository.buscarActivosPorNombre("FINALIZADA").stream().findFirst().ifPresent(estadoFinalizada -> {
                    sol.setEstadoSolicitudTransferencia(estadoFinalizada);
                    solicitudRepository.save(sol);
                    log.info("Solicitud #{} actualizada a estado FINALIZADA automaticamente via evento de Modulith", sol.getId());
                });
            });
        }
    }
}
