package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.projection.PlantaResumen;
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
public interface PlantaRepository extends JpaRepository<Planta, Long> {

    @EntityGraph(attributePaths = {"edificio"})
    Page<PlantaResumen> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<PlantaResumen> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    @Query("SELECT p FROM Planta p WHERE p.edificio.id = :idEdificio AND p.borradoLogico = false")
    Page<PlantaResumen> qBuscarPorEdificioIdYActivo(@Param("idEdificio") Long idEdificio, Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<PlantaResumen> findProjectedById(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    Page<PlantaResumen> findAllProjectedBy(Pageable pageable);

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivarPlanta(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = false WHERE p.id = :id")
    void activarPlanta(@Param("id") Long id);

    @Query("SELECT COUNT(p) > 0 FROM Planta p WHERE LOWER(p.nombre) = LOWER(:nombre) AND p.edificio.id = :idEdificio")
    boolean qExistePorNombreYEdificioId(@Param("nombre") String nombre, @Param("idEdificio") Long idEdificio);

}
