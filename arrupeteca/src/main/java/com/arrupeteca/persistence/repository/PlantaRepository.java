package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Planta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlantaRepository extends JpaRepository<Planta, Long> {

    @EntityGraph(attributePaths = {"edificio"})
    Page<Planta> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<Planta> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"edificio"})
    Page<Planta> findByEdificioIdAndBorradoLogicoFalse(Long idEdificio, Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Page<Planta> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"edificio"})
    Optional<Planta> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Planta p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Planta p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM planta WHERE id_planta = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCaseAndEdificioId(String nombre, Long idEdificio);
}
