package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial,Long> {

    // --- LECTURAS USUARIO ---
    List<EditorialResumen> findByBorradoLogicoFalse();

    Optional<EditorialResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    // --- LECTURAS ADMIN ---
    List<EditorialResumen> findAllProjectedBy();

    Optional<EditorialResumen> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Editorial e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Editorial e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByCorreoIgnoreCase(String correo);

}
