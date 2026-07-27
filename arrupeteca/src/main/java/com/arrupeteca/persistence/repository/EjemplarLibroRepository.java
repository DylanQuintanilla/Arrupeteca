package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.EjemplarLibro;
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

public interface EjemplarLibroRepository extends JpaRepository<EjemplarLibro, Long>, JpaSpecificationExecutor<EjemplarLibro> {

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Page<EjemplarLibro> findAll(Specification<EjemplarLibro> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Page<EjemplarLibro> findByBorradoLogicoFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findByIdAndBorradoLogicoFalse(Long id);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Page<EjemplarLibro> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"libro.obra", "libro.editorial", "mueble.salon.planta.edificio"})
    Optional<EjemplarLibro> findById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = true WHERE e.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EjemplarLibro e SET e.borradoLogico = false WHERE e.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM ejemplar_libro WHERE id_ejemplar_libro = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);
}
