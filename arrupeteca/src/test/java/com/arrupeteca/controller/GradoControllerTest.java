package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.service.GradoService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GradoController.class)
class GradoControllerTest extends BaseControllerTest {

    @MockitoBean
    private GradoService gradoService;

    @Test
    @DisplayName("GET /api/v1/grados - Should return active grados list")
    void shouldReturnActiveGrados() throws Exception {
        when(gradoService.obtenerTodosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/grados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de grados activos"));
    }

    @Test
    @DisplayName("GET /api/v1/grados/all - Should return grados table")
    void shouldReturnAllGrados() throws Exception {
        Page<GradoResponse> page = new PageImpl<>(List.of());
        when(gradoService.obtenerTodos(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/grados/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de grados"));
    }

    @Test
    @DisplayName("GET /api/v1/grados/{id} - Should return grado by ID")
    void shouldReturnGradoById() throws Exception {
        GradoResponse response = new GradoResponse(1L, "Bachillerato General", false);
        when(gradoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/grados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Grado encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Bachillerato General"));
    }

    @Test
    @DisplayName("POST /api/v1/grados - Should create grado")
    void shouldCreateGrado() throws Exception {
        GradoRequest request = GradoRequest.builder().nombre("Nuevo").build();

        GradoResponse response = new GradoResponse(1L, "Nuevo", false);
        when(gradoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/grados")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/grados/{id} - Should update grado")
    void shouldUpdateGrado() throws Exception {
        GradoRequest request = GradoRequest.builder().nombre("Actualizado").build();

        GradoResponse response = new GradoResponse(1L, "Actualizado", false);
        when(gradoService.actualizar(any(Long.class), any(GradoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/grados/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Grado actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/grados/{id} - Should deactivate grado")
    void shouldDeactivateGrado() throws Exception {
        mockMvc.perform(delete("/api/v1/grados/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Grado desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/grados/{id}/activar - Should activate grado")
    void shouldActivateGrado() throws Exception {
        mockMvc.perform(patch("/api/v1/grados/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Grado activado correctamente"));
    }
}
