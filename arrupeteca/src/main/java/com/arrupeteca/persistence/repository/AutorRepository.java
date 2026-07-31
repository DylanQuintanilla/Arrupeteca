package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.persistence.entity.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT new com.arrupeteca.dto.response.summary.AutorResumenResponse(" +
           "a.id, a.nombre, a.seudonimo, a.anioNacimiento, a.anioFallecimiento, " +
           "a.borradoLogico, p.nombre, n.nombre, g.nombre, o.titulo, " +
           "a.biografia) " +
           "FROM Autor a " +
           "LEFT JOIN a.paisNacimiento p " +
           "LEFT JOIN a.nacionalidad n " +
           "LEFT JOIN a.generoPrincipal g " +
           "LEFT JOIN a.obraCumbre o " +
           "WHERE (:esBorrado IS NULL OR a.borradoLogico = :esBorrado) " +
           "AND (:idNacionalidad IS NULL OR n.id = :idNacionalidad) " +
           "AND (:idPais IS NULL OR p.id = :idPais) " +
           "AND (:idGenero IS NULL OR g.id = :idGenero) " +
           "AND (:termino IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:anioInicio IS NULL OR a.anioFallecimiento >= :anioInicio OR a.anioFallecimiento IS NULL) " +
           "AND (:anioFin IS NULL OR a.anioNacimiento <= :anioFin)")
    Page<AutorResumenResponse> busquedaMaestraAdminProyectada(
            @Param("esBorrado") Boolean esBorrado,
            @Param("idNacionalidad") Long idNacionalidad,
            @Param("idPais") Long idPais,
            @Param("idGenero") Long idGenero,
            @Param("anioInicio") Year anioInicio,
            @Param("anioFin") Year anioFin,
            @Param("termino") String termino,
            Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    @Query("SELECT a FROM Autor a " +
           "WHERE (LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "   OR LOWER(a.seudonimo) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:idExcluir IS NULL OR a.id != :idExcluir)")
    List<Autor> buscarDuplicados(@Param("termino") String termino, @Param("idExcluir") Long idExcluir, Sort sort);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = true WHERE a.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Autor a SET a.borradoLogico = false WHERE a.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM autor WHERE id_autor = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
