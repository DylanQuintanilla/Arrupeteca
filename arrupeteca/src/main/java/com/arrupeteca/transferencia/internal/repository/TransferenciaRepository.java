package com.arrupeteca.transferencia.internal.repository;

import com.arrupeteca.transferencia.dto.response.TransferenciaResumenResponse;
import com.arrupeteca.transferencia.internal.entity.Transferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    @Query("""
        SELECT new com.arrupeteca.transferencia.dto.response.TransferenciaResumenResponse(
            t.id,
            st.id,
            ue.id,
            ue.nombres,
            ur.id,
            ur.nombres,
            mo.id,
            mo.nombre,
            md.id,
            md.nombre,
            et.id,
            et.nombre,
            t.fechaEnvio,
            t.fechaRecepcion,
            t.confirmada,
            CAST(COUNT(ej.id) AS integer),
            t.borradoLogico,
            t.fechaCreacion
        )
        FROM Transferencia t
        LEFT JOIN t.solicitudTransferencia st
        JOIN t.usuarioEnvia ue
        LEFT JOIN t.usuarioRecibe ur
        JOIN t.muebleOrigen mo
        JOIN t.muebleDestino md
        JOIN t.estadoTransferencia et
        LEFT JOIN t.ejemplares ej
        WHERE
            (:borradoLogico IS NULL OR t.borradoLogico = :borradoLogico)
            AND (:idUsuarioEnvia IS NULL OR ue.id = :idUsuarioEnvia)
            AND (:idUsuarioRecibe IS NULL OR ur.id = :idUsuarioRecibe)
            AND (:idMuebleOrigen IS NULL OR mo.id = :idMuebleOrigen)
            AND (:idMuebleDestino IS NULL OR md.id = :idMuebleDestino)
            AND (:idEstadoTransferencia IS NULL OR et.id = :idEstadoTransferencia)
            AND (:confirmada IS NULL OR t.confirmada = :confirmada)
        GROUP BY t.id, st.id, ue.id, ue.nombres, ur.id, ur.nombres, mo.id, mo.nombre, md.id, md.nombre, et.id, et.nombre, t.fechaEnvio, t.fechaRecepcion, t.confirmada, t.borradoLogico, t.fechaCreacion
    """)
    Page<TransferenciaResumenResponse> busqueda(
            @Param("idUsuarioEnvia") Long idUsuarioEnvia,
            @Param("idUsuarioRecibe") Long idUsuarioRecibe,
            @Param("idMuebleOrigen") Long idMuebleOrigen,
            @Param("idMuebleDestino") Long idMuebleDestino,
            @Param("idEstadoTransferencia") Long idEstadoTransferencia,
            @Param("confirmada") Boolean confirmada,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @EntityGraph(attributePaths = {"solicitudTransferencia", "usuarioEnvia", "usuarioRecibe", "muebleOrigen", "muebleDestino", "estadoTransferencia", "ejemplares", "ejemplares.ejemplarLibro", "ejemplares.ejemplarLibro.libro", "ejemplares.ejemplarLibro.libro.obra", "ejemplares.estadoFisicoAlMomento"})
    @Query("""
        SELECT t FROM Transferencia t
        WHERE t.id = :id
        AND (:borradoLogico IS NULL OR t.borradoLogico = :borradoLogico)
    """)
    Optional<Transferencia> busquedaPorId(
            @Param("id") Long id,
            @Param("borradoLogico") Boolean borradoLogico);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Transferencia t SET t.borradoLogico = true WHERE t.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Transferencia t SET t.borradoLogico = false WHERE t.id = :id")
    void activar(@Param("id") Long id);
}
