package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.service.FormatoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

@WebMvcTest(FormatoController.class)
class FormatoControllerTest extends BaseControllerTest {

    @MockitoBean
    private FormatoService formatoService;

    @Test
    @DisplayName("GET /api/v1/formatos - Should return active formats")
    void shouldReturnActive() throws Exception {
        Page<FormatoResponse> page = new PageImpl<>(List.of());
        when(formatoService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/formatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/formatos/{id} - Should return format by ID")
    void shouldReturnById() throws Exception {
        FormatoResponse response = new FormatoResponse(1L, "Test", false);
        when(formatoService.obtenerPorIdActivo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/formatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/formatos - Should create format")
    void shouldCreate() throws Exception {
        FormatoRequest request = FormatoRequest.builder().nombre("Nuevo").build();
        FormatoResponse response = new FormatoResponse(1L, "Nuevo", false);

        when(formatoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/formatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/formatos/{id} - Should update format")
    void shouldUpdate() throws Exception {
        FormatoRequest request = FormatoRequest.builder().nombre("Actualizado").build();
        FormatoResponse response = new FormatoResponse(1L, "Actualizado", false);

        when(formatoService.actualizar(any(Long.class), any(FormatoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/formatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/formatos/admin - Should return all formats for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<FormatoResponse> page = new PageImpl<>(List.of());
        when(formatoService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/formatos/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de formatos"));
    }

    @Test
    @DisplayName("GET /api/v1/formatos/admin/{id} - Should return format by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        FormatoResponse response = new FormatoResponse(1L, "Test", false);
        when(formatoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/formatos/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato encontrado (admin)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/formatos/{id} - Should deactivate format")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/formatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/formatos/{id}/activar - Should activate format")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/formatos/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato activado correctamente"));
    }
}
