package com.arrupeteca.transferencia.internal.repository;

import com.arrupeteca.transferencia.internal.entity.DetalleSolicitudTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleSolicitudTransferenciaRepository extends JpaRepository<DetalleSolicitudTransferencia, Long> {

    @Query("SELECT d FROM DetalleSolicitudTransferencia d JOIN FETCH d.libro WHERE d.solicitudTransferencia.id = :idSolicitud")
    List<DetalleSolicitudTransferencia> findBySolicitudTransferenciaId(@Param("idSolicitud") Long idSolicitud);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM DetalleSolicitudTransferencia d WHERE d.solicitudTransferencia.id = :idSolicitud")
    void deleteBySolicitudTransferenciaId(@Param("idSolicitud") Long idSolicitud);
}
