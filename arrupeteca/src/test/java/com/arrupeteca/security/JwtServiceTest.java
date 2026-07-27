package com.arrupeteca.security;

import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        // 256-bit key in Base64
        jwtConfig.setSecret("dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtYXQtbGVhc3QtMjU2LWJpdHMtbG9uZy1mb3ItaG1hYy1zaGEyNTY=");
        jwtConfig.setAccessTokenExpiration(3600000L); // 1 hour
        jwtConfig.setIssuer("arrupeteca-test");

        jwtService = new JwtService(jwtConfig);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido para un UserDetails")
    void debeGenerarTokenValido() {
        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo("admin@arrupeteca.com")
                .contrasena("password123")
                .nombres("Admin")
                .apellidos("User")
                .tipoUsuario(tipoAdmin)
                .build();

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@arrupeteca.com");
        assertThat(jwtService.isTokenValid(token, usuario)).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar token cuando el usuario no coincide")
    void debeRechazarTokenUsuarioInvalido() {
        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario1 = Usuario.builder().id(1L).correo("u1@arrupeteca.com").tipoUsuario(tipoAdmin).build();
        Usuario usuario2 = Usuario.builder().id(2L).correo("u2@arrupeteca.com").tipoUsuario(tipoAdmin).build();

        String token = jwtService.generateToken(usuario1);

        assertThat(jwtService.isTokenValid(token, usuario2)).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar token cuando está expirado")
    void debeRechazarTokenExpirado() {
        jwtConfig.setAccessTokenExpiration(-1000L); // Expirado inmediatamente
        jwtService = new JwtService(jwtConfig);

        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario = Usuario.builder().id(1L).correo("expired@arrupeteca.com").tipoUsuario(tipoAdmin).build();

        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isTokenValid(token, usuario)).isFalse();
    }

}
