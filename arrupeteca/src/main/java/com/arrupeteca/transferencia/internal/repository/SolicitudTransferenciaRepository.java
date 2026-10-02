package com.arrupeteca.transferencia.internal.repository;

import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaResumenResponse;
import com.arrupeteca.transferencia.internal.entity.SolicitudTransferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolicitudTransferenciaRepository extends JpaRepository<SolicitudTransferencia, Long> {

    @Query("""
        SELECT new com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaResumenResponse(
            st.id,
            u.id,
            CONCAT(u.nombres, ' ', u.apellidos),
            s.id,
            s.nombre,
            est.id,
            est.nombre,
            st.motivo,
            st.motivoRechazo,
            CAST(COALESCE(SUM(d.cantidad), 0) AS integer),
            st.borradoLogico,
            st.fechaCreacion
        )
        FROM SolicitudTransferencia st
        JOIN st.usuario u
        JOIN st.salonDestino s
        JOIN st.estadoSolicitudTransferencia est
        LEFT JOIN st.detalles d
        WHERE
            (:borradoLogico IS NULL OR st.borradoLogico = :borradoLogico)
            AND (:idUsuario IS NULL OR u.id = :idUsuario)
            AND (:idSalonDestino IS NULL OR s.id = :idSalonDestino)
            AND (:idEstadoSolicitud IS NULL OR est.id = :idEstadoSolicitud)
            AND (:motivo IS NULL OR :motivo = '' OR LOWER(st.motivo) LIKE LOWER(CONCAT('%', :motivo, '%')))
        GROUP BY st.id, u.id, u.nombres, u.apellidos, s.id, s.nombre, est.id, est.nombre, st.motivo, st.motivoRechazo, st.borradoLogico, st.fechaCreacion
    """)
    Page<SolicitudTransferenciaResumenResponse> busqueda(
            @Param("idUsuario") Long idUsuario,
            @Param("idSalonDestino") Long idSalonDestino,
            @Param("idEstadoSolicitud") Long idEstadoSolicitud,
            @Param("motivo") String motivo,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @EntityGraph(attributePaths = {"usuario", "salonDestino", "estadoSolicitudTransferencia", "detalles", "detalles.libro", "detalles.libro.obra"})
    @Query("""
        SELECT st FROM SolicitudTransferencia st
        WHERE st.id = :id
        AND (:borradoLogico IS NULL OR st.borradoLogico = :borradoLogico)
    """)
    Optional<SolicitudTransferencia> busquedaPorId(
            @Param("id") Long id,
            @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudTransferencia st SET st.borradoLogico = true WHERE st.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SolicitudTransferencia st SET st.borradoLogico = false WHERE st.id = :id")
    void activar(@Param("id") Long id);
}
