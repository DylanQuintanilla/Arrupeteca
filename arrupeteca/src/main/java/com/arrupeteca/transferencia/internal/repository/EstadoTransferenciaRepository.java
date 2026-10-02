package com.arrupeteca.transferencia.internal.repository;

import com.arrupeteca.transferencia.internal.entity.EstadoTransferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadoTransferenciaRepository extends JpaRepository<EstadoTransferencia, Long> {

    @Query("SELECT e FROM EstadoTransferencia e WHERE e.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY e.nombre ASC")
    List<EstadoTransferencia> buscarActivosPorNombre(@Param("nombre") String nombre);

    @Query("SELECT e FROM EstadoTransferencia e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<EstadoTransferencia> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoTransferencia e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoTransferencia e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
