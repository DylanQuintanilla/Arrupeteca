package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Libro;
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

public interface LibroRepository extends JpaRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<Libro> findAll(Specification<Libro> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<Libro> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<Libro> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Page<Libro> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    Optional<Libro> findById(Long id);

    @EntityGraph(attributePaths = {"obra", "editorial", "formato"})
    @Query("SELECT l FROM Libro l WHERE l.obra.id = :obraId")
    Page<Libro> buscarPorObraId(@Param("obraId") Long obraId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = true WHERE l.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.borradoLogico = false WHERE l.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM libro WHERE id_libro = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByIsbn(String isbn);
}
