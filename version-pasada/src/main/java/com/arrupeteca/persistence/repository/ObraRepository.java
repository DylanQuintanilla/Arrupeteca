package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    Page<Obra> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Obra> findProjectedByIdAndBorradoLogicoFalse(Long id);

    @Query(value = "SELECT o FROM Obra o WHERE o.borradoLogico = false " +
            "AND (:palabra IS NULL OR :palabra = '' OR " +
            "LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR " +
            "LOWER(o.resumen) LIKE LOWER(CONCAT('%', :palabra, '%')))")
    Page<Obra> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    Page<Obra> findAllProjectedBy(Pageable pageable);

    Optional<Obra> findProjectedById(Long id);

    @Query(value = "SELECT o FROM Obra o WHERE " +
            "(:esBorrado IS NULL OR o.borradoLogico = :esBorrado) " +
            "AND (:termino IS NULL OR :termino = '' OR " +
            "(LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(o.resumen) LIKE LOWER(CONCAT('%', :termino, '%')))) " +
            "AND (:anioInicio IS NULL OR o.anioCreacion >= :anioInicio) " +
            "AND (:anioFin IS NULL OR o.anioCreacion <= :anioFin)")
    Page<Obra> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Obra o SET o.borradoLogico = true WHERE o.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Obra o SET o.borradoLogico = false WHERE o.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByTituloIgnoreCase(String titulo);

    Page<Obra> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}
