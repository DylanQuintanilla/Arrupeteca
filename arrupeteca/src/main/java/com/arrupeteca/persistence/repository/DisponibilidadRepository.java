package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Disponibilidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    Page<Disponibilidad> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Disponibilidad> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Disponibilidad> findAllProjectedBy(Pageable pageable);

    Optional<Disponibilidad> findProjectedById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Disponibilidad d SET d.borradoLogico = true WHERE d.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Disponibilidad d SET d.borradoLogico = false WHERE d.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM disponibilidad WHERE id_disponibilidad = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
