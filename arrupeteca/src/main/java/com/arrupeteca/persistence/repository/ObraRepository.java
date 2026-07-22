package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ObraRepository extends JpaRepository<Obra, Long> {

    Page<Obra> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Obra> findByIdAndBorradoLogicoFalse(Long id);

    @Query(value = "SELECT o FROM Obra o WHERE o.borradoLogico = false " +
            "AND (:palabra IS NULL OR :palabra = '' OR " +
            "LOWER(o.titulo) LIKE LOWER(CONCAT('%', :palabra, '%')) OR " +
            "LOWER(o.resumen) LIKE LOWER(CONCAT('%', :palabra, '%')))")
    Page<Obra> busquedaGlobal(@Param("palabra") String palabra, Pageable pageable);

    Page<Obra> findAll(Pageable pageable);

    Optional<Obra> findById(Long id);

    @Query(value = "SELECT DISTINCT o FROM Obra o " +
            "LEFT JOIN o.generos g " +
            "LEFT JOIN o.categorias c " +
            "WHERE (:esBorrado IS NULL OR o.borradoLogico = :esBorrado) " +
            "AND (:idGenero IS NULL OR g.id = :idGenero) " +
            "AND (:idCategoria IS NULL OR c.id = :idCategoria) " +
            "AND (:termino IS NULL OR :termino = '' OR " +
            "(LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(o.resumen) LIKE LOWER(CONCAT('%', :termino, '%')))) " +
            "AND (:anioInicio IS NULL OR o.anioCreacion >= :anioInicio) " +
            "AND (:anioFin IS NULL OR o.anioCreacion <= :anioFin)")
    Page<Obra> busquedaMaestraAdmin(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Obra o SET o.borradoLogico = true WHERE o.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Obra o SET o.borradoLogico = false WHERE o.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM obra WHERE id_obra = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByTituloIgnoreCase(String titulo);

    Page<Obra> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}
