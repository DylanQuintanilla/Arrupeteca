package com.arrupeteca.persistence.repository;

import com.arrupeteca.persistence.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByBorradoLogicoFalseOrderByNombreAsc();

    @Query("SELECT c FROM Categoria c WHERE " +
           "(:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR c.borradoLogico = :borradoLogico)")
    Page<Categoria> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Categoria c SET c.borradoLogico = true WHERE c.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Categoria c SET c.borradoLogico = false WHERE c.id = :id")
    void activar(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM categoria WHERE id_categoria = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);

    boolean existsByIdAndBorradoLogicoFalse(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
