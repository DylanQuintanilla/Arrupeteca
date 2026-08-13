package com.arrupeteca.persistence.repository;

import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and find usuario by email")
    void shouldSaveAndFind() {
        TipoUsuario tipo = tipoUsuarioRepository.save(TipoUsuario.builder().nombre("ALUMNO_" + System.currentTimeMillis()).build());
        Usuario usuario = Usuario.builder()
                .nombres("Juan").apellidos("Pérez").correo("juan@test.com").contrasena("pass123")
                .tipoUsuario(tipo).build();
        usuarioRepository.save(usuario);

        assertThat(usuarioRepository.existsByCorreo("juan@test.com")).isTrue();
        assertThat(usuarioRepository.findByCorreoAndBorradoLogicoFalse("juan@test.com")).isPresent();
    }

    @Test
    @DisplayName("Should search usuarios with busqueda query")
    void shouldSearchWithBusqueda() {
        TipoUsuario tipo = tipoUsuarioRepository.save(TipoUsuario.builder().nombre("DOCENTE_" + System.currentTimeMillis()).build());
        Usuario usuario = Usuario.builder()
                .nombres("Carlos").apellidos("Gómez").correo("carlos@test.com").contrasena("pass123")
                .tipoUsuario(tipo).borradoLogico(false).bloqueadoPorMora(false).build();
        usuarioRepository.save(usuario);

        Page<UsuarioResumen> page = usuarioRepository.busqueda(
                false, false, tipo.getId(), "Carlos", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isGreaterThan(0);
        assertThat(page.getContent().get(0).nombres()).isEqualTo("Carlos");
    }

    @Test
    @DisplayName("Should find usuario by ID with busquedaPorId")
    void shouldFindByIdWithBusquedaPorId() {
        TipoUsuario tipo = tipoUsuarioRepository.save(TipoUsuario.builder().nombre("BIBLIOTECARIO_" + System.currentTimeMillis()).build());
        Usuario usuario = Usuario.builder()
                .nombres("Maria").apellidos("Lopez").correo("maria@test.com").contrasena("pass123")
                .tipoUsuario(tipo).borradoLogico(false).build();
        usuarioRepository.saveAndFlush(usuario);

        Optional<Usuario> foundActive = usuarioRepository.busquedaPorId(usuario.getId(), false);
        assertThat(foundActive).isPresent();
    }

    @Test
    @DisplayName("Should change logical status and bloqueadoPorMora")
    void shouldChangeStatusAndBloqueadoPorMora() {
        TipoUsuario tipo = tipoUsuarioRepository.save(TipoUsuario.builder().nombre("ADMIN_" + System.currentTimeMillis()).build());
        Usuario usuario = Usuario.builder()
                .nombres("Pedro").apellidos("Ramirez").correo("pedro@test.com").contrasena("pass123")
                .tipoUsuario(tipo).borradoLogico(false).bloqueadoPorMora(false).build();
        usuarioRepository.saveAndFlush(usuario);

        // Desactivar
        usuarioRepository.desactivar(usuario.getId());
        entityManager.clear();
        Usuario deactivated = usuarioRepository.busquedaPorId(usuario.getId(), null).orElseThrow();
        assertThat(deactivated.isBorradoLogico()).isTrue();

        // Activar
        usuarioRepository.activar(usuario.getId());
        entityManager.clear();
        Usuario activated = usuarioRepository.busquedaPorId(usuario.getId(), null).orElseThrow();
        assertThat(activated.isBorradoLogico()).isFalse();

        // Cambiar bloqueo por mora
        usuarioRepository.cambiarBloqueoMora(usuario.getId(), true);
        entityManager.clear();
        Usuario blocked = usuarioRepository.busquedaPorId(usuario.getId(), null).orElseThrow();
        assertThat(blocked.isBloqueadoPorMora()).isTrue();
    }
}
