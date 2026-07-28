package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Ciclo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CicloRepository extends JpaRepository<Ciclo, Long> {

    Page<Ciclo> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Ciclo> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Ciclo> findAllProjectedBy(Pageable pageable);

    Optional<Ciclo> findProjectedById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Ciclo c SET c.borradoLogico = true WHERE c.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Ciclo c SET c.borradoLogico = false WHERE c.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM ciclo WHERE id_ciclo = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
