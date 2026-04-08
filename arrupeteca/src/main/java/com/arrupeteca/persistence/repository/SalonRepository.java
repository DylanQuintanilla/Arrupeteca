package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.projection.SalonResumen;
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
    Page<SalonResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Optional<SalonResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Page<SalonResumen> findProjectedByPlanta_IdAndBorradoLogicoFalse(Long idPlanta, Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Page<SalonResumen> findAllProjectedBy(Pageable pageable);

    @EntityGraph(attributePaths = {"planta.edificio"})
    Optional<SalonResumen> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Salon s SET s.borradoLogico = true WHERE s.id = :id")
    void desactivarSalon(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Salon s SET s.borradoLogico = false WHERE s.id = :id")
    void activarSalon(@Param("id") Long id);

    boolean existsByNombreIgnoreCaseAndPlanta_Id(String nombre, Long idPlanta);

}
