package com.arrupeteca.security;

import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Endpoint no autenticado debe retornar 401 Unauthorized con formato JSON")
    void debeRetornar401SinToken() throws Exception {
        mockMvc.perform(get("/api/v1/autores"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("No autorizado. Token JWT inválido o ausente."));
    }

    @Test
    @DisplayName("Endpoint público Swagger UI debe ser accesible sin token")
    void debePermitirSwaggerSinToken() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint protegido con token JWT válido debe permitir acceso")
    void debePermitirAccesoConTokenValido() throws Exception {
        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo("admin@arrupeteca.com")
                .tipoUsuario(tipoAdmin)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("admin@arrupeteca.com"))
                .willReturn(java.util.Optional.of(usuario));

        String token = jwtService.generateToken(usuario);

        mockMvc.perform(get("/api/v1/autores")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint /admin con token de usuario con rol admin debe retornar 200 OK")
    void debePermitirAccesoAAdminConRolAdmin() throws Exception {
        TipoUsuario tipoAdmin = TipoUsuario.builder().id(1L).nombre("admin").build();
        Usuario usuarioAdmin = Usuario.builder()
                .id(1L)
                .correo("admin@arrupeteca.com")
                .tipoUsuario(tipoAdmin)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("admin@arrupeteca.com"))
                .willReturn(java.util.Optional.of(usuarioAdmin));

        String token = jwtService.generateToken(usuarioAdmin);

        mockMvc.perform(get("/api/v1/categorias/admin")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Endpoint /admin con token de usuario con rol alumno debe retornar 403 Forbidden")
    void debeDenegarAccesoAAdminConRolAlumno() throws Exception {
        TipoUsuario tipoAlumno = TipoUsuario.builder().id(2L).nombre("alumno").build();
        Usuario usuarioAlumno = Usuario.builder()
                .id(2L)
                .correo("alumno@arrupeteca.com")
                .tipoUsuario(tipoAlumno)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("alumno@arrupeteca.com"))
                .willReturn(java.util.Optional.of(usuarioAlumno));

        String token = jwtService.generateToken(usuarioAlumno);

        mockMvc.perform(get("/api/v1/categorias/admin")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
