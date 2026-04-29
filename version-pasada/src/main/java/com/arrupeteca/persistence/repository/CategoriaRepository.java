package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Page<Categoria> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Categoria> findProjectedByIdAndBorradoLogicoFalse(Long id);

    Page<Categoria> findAllProjectedBy(Pageable pageable);

    Optional<Categoria> findProjectedById(Long id);

    @Modifying
    @Query("UPDATE Categoria c SET c.borradoLogico = true WHERE c.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Categoria c SET c.borradoLogico = false WHERE c.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
