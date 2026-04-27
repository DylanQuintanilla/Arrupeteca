package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Pais;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    Page<Pais> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Pais> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Pais> findAllProjectedBy(Pageable pageable);

    Optional<Pais> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Pais p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pais p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
