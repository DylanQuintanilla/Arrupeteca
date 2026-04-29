package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
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
    Page<Planta> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<Planta> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    Page<Planta> findByEdificioIdAndBorradoLogicoFalse(Long idEdificio, Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<Planta> findProjectedById(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    Page<Planta> findAllProjectedBy(Pageable pageable);

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Planta p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreAndEdificioId(String nombre, Long edificioId);
}
