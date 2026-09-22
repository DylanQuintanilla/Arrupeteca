package com.arrupeteca.security;

import java.util.Optional;

import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Debe cargar usuario correctamente cuando existe por correo")
    void debeCargarUsuarioExistente() {
        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo("test@arrupeteca.com")
                .contrasena("hashedPass")
                .tipoUsuario(tipoAdmin)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("test@arrupeteca.com"))
                .willReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@arrupeteca.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@arrupeteca.com");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("Debe lanzar UsernameNotFoundException cuando el usuario no existe")
    void debeLanzarExcepcionUsuarioNoEncontrado() {
        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("noexiste@arrupeteca.com"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("noexiste@arrupeteca.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con correo: noexiste@arrupeteca.com");
    }

}
