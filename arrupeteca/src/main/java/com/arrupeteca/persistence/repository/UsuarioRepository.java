package com.arrupeteca.persistence.repository;

import java.util.Optional;

import com.arrupeteca.persistence.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT new com.arrupeteca.dto.response.summary.UsuarioResumen(" +
           "u.id, u.nombres, u.apellidos, u.correo, u.carnet, u.bloqueadoPorMora, " +
           "u.fechaRegistro, u.ultimaFechaActividad, u.borradoLogico, tu.nombre) " +
           "FROM Usuario u " +
           "LEFT JOIN u.tipoUsuario tu " +
           "WHERE (:borradoLogico IS NULL OR u.borradoLogico = :borradoLogico) " +
           "AND (:bloqueadoPorMora IS NULL OR u.bloqueadoPorMora = :bloqueadoPorMora) " +
           "AND (:idTipoUsuario IS NULL OR tu.id = :idTipoUsuario) " +
           "AND (:termino IS NULL OR :termino = '' OR LOWER(u.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "     OR LOWER(u.carnet) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<com.arrupeteca.dto.response.summary.UsuarioResumen> busqueda(
            @Param("borradoLogico") Boolean borradoLogico,
            @Param("bloqueadoPorMora") Boolean bloqueadoPorMora,
            @Param("idTipoUsuario") Long idTipoUsuario,
            @Param("termino") String termino,
            Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"tipoUsuario"})
    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND (:borradoLogico IS NULL OR u.borradoLogico = :borradoLogico)")
    Optional<Usuario> busquedaPorId(@Param("id") Long id, @Param("borradoLogico") Boolean borradoLogico);

    Optional<Usuario> findByCorreoAndBorradoLogicoFalse(String correo);

    boolean existsByCorreo(String correo);

    Page<Usuario> findByBorradoLogicoFalse(Pageable pageable);

    Optional<Usuario> findByIdAndBorradoLogicoFalse(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.borradoLogico = true WHERE u.id = :id")
    void desactivar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.borradoLogico = false WHERE u.id = :id")
    void activar(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Usuario u SET u.bloqueadoPorMora = :bloqueado WHERE u.id = :id")
    void cambiarBloqueoMora(@Param("id") Long id, @Param("bloqueado") boolean bloqueado);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE id_usuario = :id", nativeQuery = true)
    long countByIdIncludeDeleted(@Param("id") Long id);
}
