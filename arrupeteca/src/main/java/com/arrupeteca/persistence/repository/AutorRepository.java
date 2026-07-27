package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Autor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor> {

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Page<Autor> findAll(Specification<Autor> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    List<Autor> findAll(Specification<Autor> spec, Sort sort);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findById(Long id);

    @EntityGraph(attributePaths = {"paisNacimiento", "nacionalidad", "generoPrincipal", "obraCumbre"})
    Optional<Autor> findOne(Specification<Autor> spec);

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
