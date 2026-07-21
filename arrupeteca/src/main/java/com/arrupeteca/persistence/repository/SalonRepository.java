package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Salon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalonRepository extends JpaRepository<Salon, Long> {

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    Page<Salon> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    Optional<Salon> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    Page<Salon> findByPlantaIdAndBorradoLogicoFalse(Long idPlanta, Pageable pageable);

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    Page<Salon> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"planta", "planta.edificio"})
    Optional<Salon> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Salon s SET s.borradoLogico = true WHERE s.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Salon s SET s.borradoLogico = false WHERE s.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM salon WHERE id_salon = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCaseAndPlantaId(String nombre, Long idPlanta);
}
