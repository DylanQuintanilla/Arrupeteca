package com.arrupeteca.security;

import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.service.CategoriaService;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private com.arrupeteca.service.AutorService autorService;

    @MockitoBean
    private com.arrupeteca.service.GeneroService generoService;

    @MockitoBean
    private com.arrupeteca.service.EditorialService editorialService;

    @MockitoBean
    private com.arrupeteca.service.LibroService libroService;

    @Test
    @DisplayName("Endpoint de usuarios sin token debe retornar 401 Unauthorized")
    void debeRetornar401SinToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("No autorizado. Token JWT inválido o ausente."));
    }

    @Test
    @DisplayName("GET /api/v1/libros sin token debe ser permitido públicamente (200 OK)")
    void debePermitirBusquedaLibrosSinToken() throws Exception {
        given(libroService.busquedaGlobal(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .willReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/autores, /generos, /categorias, /editoriales sin token deben ser públicos (200 OK)")
    void debePermitirCatalogosDropdownSinToken() throws Exception {
        given(autorService.busquedaGlobal(any(), any(), any(), any(), any(), any(), any(), any()))
                .willReturn(new PageImpl<>(List.of()));
        given(generoService.obtenerTodosActivos(any())).willReturn(List.of());
        given(categoriaService.obtenerTodosActivos(any())).willReturn(List.of());
        given(editorialService.obtenerTodosActivos(any())).willReturn(List.of());

        mockMvc.perform(get("/api/v1/autores")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/generos")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/categorias")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/editoriales")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} sin token debe ser público (200 OK)")
    void debePermitirDetalleLibroPorIdSinToken() throws Exception {
        given(libroService.busquedaGlobalPorId(1L)).willReturn(com.arrupeteca.dto.response.detail.LibroDetalleResponse.builder().id(1L).build());

        mockMvc.perform(get("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/autores/{id} sin token debe ser permitido públicamente (200 OK)")
    void debePermitirDetalleAutorPorIdSinToken() throws Exception {
        given(autorService.busquedaGlobalPorId(1L))
                .willReturn(com.arrupeteca.dto.response.detail.AutorDetalleResponse.builder().id(1L).nombre("Gabriel").build());

        mockMvc.perform(get("/api/v1/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
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

        given(categoriaService.obtenerTodos(any(), any(), any())).willReturn(new PageImpl<>(List.of()));
        String token = jwtService.generateToken(usuarioAdmin);

        mockMvc.perform(get("/api/v1/categorias/admin/all")
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

        mockMvc.perform(get("/api/v1/categorias/admin/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Endpoint administrativo de catálogo con rol bibliotecario debe retornar 403 Forbidden")
    void debeDenegarAccesoACatalogoAdminConRolBibliotecario() throws Exception {
        TipoUsuario tipoBibliotecario = TipoUsuario.builder().id(5L).nombre("bibliotecario").build();
        Usuario usuarioBibliotecario = Usuario.builder()
                .id(5L)
                .correo("bibliotecario@arrupeteca.com")
                .tipoUsuario(tipoBibliotecario)
                .build();

        given(usuarioRepository.findByCorreoAndBorradoLogicoFalse("bibliotecario@arrupeteca.com"))
                .willReturn(java.util.Optional.of(usuarioBibliotecario));

        String token = jwtService.generateToken(usuarioBibliotecario);

        mockMvc.perform(get("/api/v1/categorias/admin/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

}
