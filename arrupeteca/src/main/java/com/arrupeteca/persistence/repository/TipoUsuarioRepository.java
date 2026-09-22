package com.arrupeteca.persistence.repository;

import java.util.Optional;

import com.arrupeteca.persistence.entity.TipoUsuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TipoUsuario t SET t.borradoLogico = true WHERE t.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TipoUsuario t SET t.borradoLogico = false WHERE t.id = :id")
    void activar(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    Optional<TipoUsuario> findByNombreAndBorradoLogicoFalse(String nombre);


    @Query("SELECT e FROM TipoUsuario e WHERE " +
           "(:nombre IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:borradoLogico IS NULL OR e.borradoLogico = :borradoLogico)")
    Page<TipoUsuario> busquedaAdmin(
            @Param("nombre") String nombre,
            @Param("borradoLogico") Boolean borradoLogico,
            Pageable pageable);

    @Query("SELECT t FROM TipoUsuario t WHERE t.borradoLogico = false AND " +
           "(:nombre IS NULL OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "ORDER BY t.nombre ASC")
    List<TipoUsuario> buscarActivosPorNombre(@Param("nombre") String nombre);
}
