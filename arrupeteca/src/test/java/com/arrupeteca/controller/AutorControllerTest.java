package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.service.AutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Year;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutorController.class)
class AutorControllerTest extends BaseControllerTest {

    @MockitoBean
    private AutorService autorService;

    @Test
    @DisplayName("GET /api/v1/autores - Should return active authors")
    void shouldReturnActive() throws Exception {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de autores activos"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/{id} - Should return author by ID")
    void shouldReturnById() throws Exception {
        AutorDetalleResponse detalle = AutorDetalleResponse.builder()
                .id(1L)
                .nombre("Autor Test")
                .biografia("Bio")
                .build();
        when(autorService.obtenerPorIdActivo(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Autor encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Autor Test"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/global - Should perform global search")
    void shouldPerformGlobalSearch() throws Exception {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorService.busquedaGlobal(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/autores/global")
                        .param("palabra", "test")
                        .param("inicio", "1900")
                        .param("fin", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados de búsqueda"));
    }

    @Test
    @DisplayName("POST /api/v1/autores - Should create author")
    void shouldCreate() throws Exception {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Nuevo"))
                .biografia(JsonNullable.of("Biografia de prueba"))
                .build();

        AutorDetalleResponse detalle = AutorDetalleResponse.builder()
                .id(1L)
                .nombre("Nuevo")
                .biografia("Biografia de prueba")
                .build();
        when(autorService.crear(any())).thenReturn(detalle);

        mockMvc.perform(post("/api/v1/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PATCH /api/v1/autores/{id} - Should update author")
    void shouldUpdate() throws Exception {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("Actualizado"))
                .biografia(JsonNullable.of("Nueva biografia"))
                .build();

        AutorDetalleResponse detalle = AutorDetalleResponse.builder()
                .id(1L)
                .nombre("Actualizado")
                .biografia("Nueva biografia")
                .build();
        when(autorService.actualizar(any(Long.class), any(AutorRequest.class))).thenReturn(detalle);

        mockMvc.perform(patch("/api/v1/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/autores/{id} - Should deactivate author")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Autor desactivado"));
    }

    @Test
    @DisplayName("PATCH /api/v1/autores/{id}/activar - Should activate author")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/autores/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Autor activado"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/admin - Should return all authors for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/autores/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/admin/busqueda-maestra - Should perform master search")
    void shouldPerformMasterSearch() throws Exception {
        Page<AutorResumenResponse> page = new PageImpl<>(List.of());
        when(autorService.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/autores/admin/busqueda-maestra")
                        .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/admin/{id} - Should return author by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        AutorDetalleResponse detalle = AutorDetalleResponse.builder()
                .id(1L)
                .nombre("Autor Test")
                .biografia("Bio")
                .build();
        when(autorService.obtenerPorId(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/autores/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Autor encontrado (admin)"))
                .andExpect(jsonPath("$.data.nombre").value("Autor Test"));
    }

    @Test
    @DisplayName("GET /api/v1/autores/verificar-duplicados - Should verify duplicates")
    void shouldVerifyDuplicates() throws Exception {
        when(autorService.buscarDuplicados(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/autores/verificar-duplicados")
                        .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Verificación completada"));
    }

    @Test
    @DisplayName("POST /api/v1/autores - Should return 400 Bad Request when request is invalid")
    void shouldReturnBadRequestWhenCreatingInvalid() throws Exception {
        AutorRequest request = AutorRequest.builder()
                .nombre(JsonNullable.of("A".repeat(101))) // Nombre demasiado largo
                .build();

        mockMvc.perform(post("/api/v1/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

    @Test
    @DisplayName("PATCH /api/v1/autores/{id} - Should return 400 Bad Request when request is invalid")
    void shouldReturnBadRequestWhenUpdatingInvalid() throws Exception {
        AutorRequest request = AutorRequest.builder()
                .anioNacimiento(JsonNullable.of(Year.now().plusYears(1))) // Año futuro
                .build();

        mockMvc.perform(patch("/api/v1/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
