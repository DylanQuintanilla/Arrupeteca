package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ObraRepository extends JpaRepository<Obra, Long>, JpaSpecificationExecutor<Obra> {

    @Query("SELECT new com.arrupeteca.dto.response.summary.ObraResumen(" +
           "o.id, o.titulo, o.anioCreacion, o.resumen, o.borradoLogico) " +
           "FROM Obra o " +
           "WHERE (:esBorrado IS NULL OR o.borradoLogico = :esBorrado) " +
           "AND (:termino IS NULL OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(o.resumen) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR o.anioCreacion >= :anioInicio) " +
           "AND (:anioFin IS NULL OR o.anioCreacion <= :anioFin) " +
           "AND (:idGenero IS NULL OR EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)) " +
           "AND (:idCategoria IS NULL OR EXISTS (SELECT 1 FROM o.categorias c WHERE c.id = :idCategoria)) " +
           "AND (:idEtiqueta IS NULL OR EXISTS (SELECT 1 FROM o.etiquetas e WHERE e.id = :idEtiqueta))")
    Page<ObraResumen> busquedaMaestraAdminProyectada(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idEtiqueta") Long idEtiqueta,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable);

    @EntityGraph(attributePaths = {"generos", "categorias"})
    Page<Obra> findAll(Specification<Obra> spec, Pageable pageable);

    Page<Obra> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Obra> findByIdAndBorradoLogicoFalse(Long id);

    Page<Obra> findAll(Pageable pageable);

    Optional<Obra> findById(Long id);

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
