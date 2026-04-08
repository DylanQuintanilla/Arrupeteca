package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.projection.EdificioResumen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EdificioRepository extends JpaRepository<Edificio, Long> {

    Page<EdificioResumen> findByBorradoLogicoFalse(Pageable pageable);

    Optional<EdificioResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<EdificioResumen> findAllProjectedBy(Pageable pageable);

    Optional<EdificioResumen> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Edificio e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Edificio e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
