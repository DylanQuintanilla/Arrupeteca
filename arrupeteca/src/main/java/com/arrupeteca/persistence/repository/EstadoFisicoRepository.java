package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EstadoFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstadoFisicoRepository extends JpaRepository<EstadoFisico, Long> {

    List<EstadoFisico> findByBorradoLogicoFalseOrderByNombreAsc();

    @Query("SELECT e FROM EstadoFisico e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<EstadoFisico> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoFisico e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EstadoFisico e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM estado_fisico WHERE id_estado_fisico = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
