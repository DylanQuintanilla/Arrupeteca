package com.arrupeteca.service;

import java.util.Optional;

import com.arrupeteca.dto.request.LoginRequest;
import com.arrupeteca.dto.request.RegisterRequest;
import com.arrupeteca.dto.response.AuthResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.TipoUsuarioRepository;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.security.JwtService;
import com.arrupeteca.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Debe registrar nuevo usuario exitosamente")
    void debeRegistrarUsuarioExitosamente() {
        RegisterRequest request = RegisterRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan@arrupeteca.com")
                .contrasena("pass123")
                .idTipo(1L)
                .build();

        TipoUsuario tipoAlumno = TipoUsuario.builder().id(2L).nombre("alumno").build();

        given(usuarioRepository.existsByCorreo("juan@arrupeteca.com")).willReturn(false);
        given(tipoUsuarioRepository.findByNombreAndBorradoLogicoFalse("alumno")).willReturn(Optional.of(tipoAlumno));
        given(passwordEncoder.encode("pass123")).willReturn("encodedPass");
        given(usuarioRepository.save(any(Usuario.class))).willAnswer(inv -> inv.getArgument(0));
        given(jwtService.generateToken(any(Usuario.class))).willReturn("token123");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("token123");
        assertThat(response.correo()).isEqualTo("juan@arrupeteca.com");
        assertThat(response.tipo()).isEqualTo("alumno");
    }

    @Test
    @DisplayName("Debe lanzar excepción al registrar con correo duplicado")
    void debeLanzarExcepcionCorreoDuplicado() {
        RegisterRequest request = RegisterRequest.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("duplicado@arrupeteca.com")
                .contrasena("pass123")
                .idTipo(1L)
                .build();

        given(usuarioRepository.existsByCorreo("duplicado@arrupeteca.com")).willReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Ya existe un usuario registrado con el correo");
    }

    @Test
    @DisplayName("Debe realizar login exitosamente")
    void debeRealizarLoginExitosamente() {
        LoginRequest request = LoginRequest.builder()
                .correo("user@arrupeteca.com")
                .contrasena("pass123")
                .build();

        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo("user@arrupeteca.com")
                .nombres("User")
                .apellidos("Test")
                .tipoUsuario(tipoAdmin)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("user@arrupeteca.com"))
                .willReturn(Optional.of(usuario));
        given(jwtService.generateToken(usuario)).willReturn("tokenJwt");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("tokenJwt");
        assertThat(response.correo()).isEqualTo("user@arrupeteca.com");
    }

}
