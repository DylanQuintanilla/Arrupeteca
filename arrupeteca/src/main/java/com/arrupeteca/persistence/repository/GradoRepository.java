package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Grado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradoRepository extends JpaRepository<Grado, Long> {

    Page<Grado> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Grado> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Grado> findAllProjectedBy(Pageable pageable);

    Optional<Grado> findProjectedById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Grado g SET g.borradoLogico = true WHERE g.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Grado g SET g.borradoLogico = false WHERE g.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM grado WHERE id_grado = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
