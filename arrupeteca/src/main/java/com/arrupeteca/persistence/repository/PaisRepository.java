package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.PaisResumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    List<PaisResumen> findByBorradoLogicoFalse();

    Optional<PaisResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    List<PaisResumen> findAllProjectedBy();

    Optional<PaisResumen> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Pais g SET g.borradoLogico = true WHERE g.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Pais g SET g.borradoLogico = false WHERE g.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

}