package com.arrupeteca.persistence.repository;

import java.util.Optional;

import com.arrupeteca.persistence.entity.Parentesco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParentescoRepository extends JpaRepository<Parentesco, Long> {

    Page<Parentesco> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Parentesco> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Parentesco> findAllProjectedBy(Pageable pageable);

    Optional<Parentesco> findProjectedById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Parentesco p SET p.borradoLogico = true WHERE p.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Parentesco p SET p.borradoLogico = false WHERE p.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM parentesco WHERE id_parentesco = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

}
