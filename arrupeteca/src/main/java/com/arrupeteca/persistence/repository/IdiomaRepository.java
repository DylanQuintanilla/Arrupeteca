package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Idioma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IdiomaRepository extends JpaRepository<Idioma, Long> {

    Page<Idioma> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Idioma> findByIdAndBorradoLogicoFalse(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Idioma i SET i.borradoLogico = true WHERE i.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Idioma i SET i.borradoLogico = false WHERE i.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM idioma WHERE id_idioma = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
