package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EstadoFisico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstadoFisicoRepository extends JpaRepository<EstadoFisico, Long> {

    Page<EstadoFisico> findByBorradoLogicoFalse(Pageable pageable);

    Optional<EstadoFisico> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<EstadoFisico> findAllProjectedBy(Pageable pageable);

    Optional<EstadoFisico> findProjectedById(Long id);

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
