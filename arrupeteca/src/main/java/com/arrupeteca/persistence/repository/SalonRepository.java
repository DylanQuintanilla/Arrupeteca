package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Salon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {

    @EntityGraph(attributePaths = {"planta.edificio"})
    Page<Salon> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Optional<Salon> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Page<Salon> findByPlantaIdAndBorradoLogicoFalse(Long idPlanta, Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Page<Salon> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Optional<Salon> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Salon s SET s.borradoLogico = true WHERE s.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Salon s SET s.borradoLogico = false WHERE s.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreAndPlantaId(String nombre, Long plantaId);
}
