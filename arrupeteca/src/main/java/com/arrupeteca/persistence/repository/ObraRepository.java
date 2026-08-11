package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.persistence.entity.Obra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ObraRepository extends JpaRepository<Obra, Long> {

    @Query("SELECT new com.arrupeteca.dto.response.summary.ObraResumen(" +
           "o.id, o.titulo, o.anioCreacion, o.resumen, o.borradoLogico) " +
           "FROM Obra o " +
           "WHERE (:borradoLogico IS NULL OR o.borradoLogico = :borradoLogico) " +
           "AND (:idGenero IS NULL OR EXISTS (SELECT 1 FROM o.generos g WHERE g.id = :idGenero)) " +
           "AND (:idCategoria IS NULL OR EXISTS (SELECT 1 FROM o.categorias c WHERE c.id = :idCategoria)) " +
           "AND (:idEtiqueta IS NULL OR EXISTS (SELECT 1 FROM o.etiquetas e WHERE e.id = :idEtiqueta)) " +
           "AND (:termino IS NULL OR :termino = '' OR LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR o.anioCreacion >= :anioInicio) " +
           "AND (:anioFin IS NULL OR o.anioCreacion <= :anioFin)")
    Page<ObraResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("idGenero") Long idGenero,
            @Param("idCategoria") Long idCategoria,
            @Param("idEtiqueta") Long idEtiqueta,
            @Param("termino") String termino,
            @Param("anioInicio") Integer anioInicio,
            @Param("anioFin") Integer anioFin,
            Pageable pageable);

    @Query("SELECT o FROM Obra o WHERE o.id = :id AND (:borradoLogico IS NULL OR o.borradoLogico = :borradoLogico)")
    Optional<Obra> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    @Query("SELECT o FROM Obra o " +
           "WHERE LOWER(o.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "AND (:idExcluir IS NULL OR o.id != :idExcluir)")
    java.util.List<Obra> buscarDuplicados(@Param("termino") String termino, @Param("idExcluir") Long idExcluir, org.springframework.data.domain.Sort sort);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Obra o SET o.borradoLogico = true WHERE o.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Obra o SET o.borradoLogico = false WHERE o.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByTituloIgnoreCase(String titulo);
}

