package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {

    @EntityGraph(attributePaths = {"edificio"})
    List<PlantaResumen> findByBorradoLogicoFalse();

    @EntityGraph(attributePaths = {"edificio"})
    Optional<PlantaResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    List<PlantaResumen> findProjectedByEdificio_IdAndBorradoLogicoFalse(Long idEdificio);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<PlantaResumen> findProjectedById(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    List<PlantaResumen> findAllProjectedBy();

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivarPlanta(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = false WHERE p.id = :id")
    void activarPlanta(@Param("id") Long id);

    boolean existsByNombreIgnoreCaseAndEdificio_Id(String nombre, Long idEdificio);

}
