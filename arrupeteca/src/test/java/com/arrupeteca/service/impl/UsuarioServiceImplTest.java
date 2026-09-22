package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ActualizarPerfilRequest;
import com.arrupeteca.dto.request.UsuarioAdminRequest;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.UsuarioMapper;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    @DisplayName("obtenerPerfilActual - Should return profile detail")
    void obtenerPerfilActual() {
        Usuario usuario = Usuario.builder().id(1L).correo("test@test.com").build();
        UsuarioDetalleResponse response = mock(UsuarioDetalleResponse.class);

        when(usuarioRepository.findByCorreoAndBorradoLogicoFalse("test@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDetalleResponse(usuario)).thenReturn(response);

        UsuarioDetalleResponse result = usuarioService.obtenerPerfilActual("test@test.com");

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("actualizarPerfilActual - Success")
    void actualizarPerfilActual() {
        Usuario usuario = Usuario.builder().id(1L).correo("test@test.com").contrasena("hashed_pass").build();
        UsuarioDetalleResponse response = mock(UsuarioDetalleResponse.class);
        ActualizarPerfilRequest request = ActualizarPerfilRequest.builder()
                .nombres(JsonNullable.of("Nuevo"))
                .build();

        when(usuarioRepository.findByCorreoAndBorradoLogicoFalse("test@test.com")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any())).thenReturn(usuario);
        when(usuarioMapper.toDetalleResponse(any())).thenReturn(response);

        UsuarioDetalleResponse result = usuarioService.actualizarPerfilActual("test@test.com", request);

        assertThat(result).isEqualTo(response);
        verify(usuarioMapper).updatePerfil(request, usuario);
    }

    @Test
    @DisplayName("busquedaAdmin - Should return page of Resumen")
    void busquedaAdmin() {
        UsuarioResumen resumen = mock(UsuarioResumen.class);
        Page<UsuarioResumen> page = new PageImpl<>(List.of(resumen));
        when(usuarioRepository.busqueda(eq(false), any(), any(), any(), any())).thenReturn(page);

        Page<UsuarioResumen> result = usuarioService.busquedaAdmin(false, null, null, "term", Pageable.unpaged());

        assertThat(result.getContent()).containsExactly(resumen);
    }

    @Test
    @DisplayName("crearAdmin - Success")
    void crearAdmin() {
        UsuarioAdminRequest request = UsuarioAdminRequest.builder()
                .nombres(JsonNullable.of("Juan"))
                .apellidos(JsonNullable.of("Perez"))
                .correo(JsonNullable.of("juan@test.com"))
                .contrasena(JsonNullable.of("pass123"))
                .idTipoUsuario(JsonNullable.of(1L))
                .build();

        TipoUsuario tipo = TipoUsuario.builder().id(1L).nombre("ADMIN").build();
        Usuario entity = Usuario.builder().id(1L).correo("juan@test.com").build();
        UsuarioDetalleResponse response = mock(UsuarioDetalleResponse.class);

        when(usuarioRepository.existsByCorreo("juan@test.com")).thenReturn(false);
        when(tipoUsuarioRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(usuarioMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(usuarioRepository.saveAndFlush(any())).thenReturn(entity);
        when(usuarioMapper.toDetalleResponse(entity)).thenReturn(response);

        UsuarioDetalleResponse result = usuarioService.crearAdmin(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("crearAdmin - Should throw exception when email exists")
    void crearAdminEmailExists() {
        UsuarioAdminRequest request = UsuarioAdminRequest.builder()
                .nombres(JsonNullable.of("Juan"))
                .apellidos(JsonNullable.of("Perez"))
                .correo(JsonNullable.of("juan@test.com"))
                .contrasena(JsonNullable.of("pass123"))
                .idTipoUsuario(JsonNullable.of(1L))
                .build();

        when(usuarioRepository.existsByCorreo("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearAdmin(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un usuario registrado con el correo");
    }

    @Test
    @DisplayName("cambiarEstado - Success")
    void cambiarEstado() {
        when(usuarioRepository.countByIdIncludeDeleted(1L)).thenReturn(1L);

        usuarioService.cambiarEstado(1L, false);

        verify(usuarioRepository).desactivar(1L);
    }
}
