package com.arrupeteca.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.arrupeteca.persistence.entity.Responsable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponsableRepository extends JpaRepository<Responsable, Long> {

    Page<Responsable> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Responsable> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Responsable> findAllProjectedBy(Pageable pageable);

    Optional<Responsable> findProjectedById(Long id);

    List<Responsable> findByUsuarioIdAndBorradoLogicoFalse(Long idUsuario);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Responsable r SET r.borradoLogico = true WHERE r.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Responsable r SET r.borradoLogico = false WHERE r.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM responsable WHERE id_responsable = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

}
