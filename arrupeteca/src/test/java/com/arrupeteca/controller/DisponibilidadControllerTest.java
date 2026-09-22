package com.arrupeteca.controller;

import com.arrupeteca.dto.request.DisponibilidadRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.service.DisponibilidadService;
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

@WebMvcTest(DisponibilidadController.class)
class DisponibilidadControllerTest extends BaseControllerTest {

    @MockitoBean
    private DisponibilidadService disponibilidadService;

    @Test
    @DisplayName("GET /api/v1/disponibilidades - Should return active disponibilidades list")
    void shouldReturnActiveDisponibilidades() throws Exception {
        when(disponibilidadService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/disponibilidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de estados de disponibilidad de ejemplares activos(as)"));
    }

    @Test
    @DisplayName("GET /api/v1/disponibilidades/all - Should return disponibilidades table")
    void shouldReturnAllDisponibilidades() throws Exception {
        Page<DisponibilidadResponse> page = new PageImpl<>(List.of());
        when(disponibilidadService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/disponibilidades/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de estados de disponibilidad de ejemplares"));
    }

    @Test
    @DisplayName("GET /api/v1/disponibilidades/{id} - Should return disponibilidad by ID")
    void shouldReturnDisponibilidadById() throws Exception {
        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Disponible", false);
        when(disponibilidadService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/disponibilidades/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad encontrado(a)"))
                .andExpect(jsonPath("$.data.nombre").value("Disponible"));
    }

    @Test
    @DisplayName("POST /api/v1/disponibilidades - Should create disponibilidad")
    void shouldCreateDisponibilidad() throws Exception {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Nuevo").build();

        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Nuevo", false);
        when(disponibilidadService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/disponibilidades/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/disponibilidades/{id} - Should update disponibilidad")
    void shouldUpdateDisponibilidad() throws Exception {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Actualizada").build();

        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Actualizada", false);
        when(disponibilidadService.actualizar(any(Long.class), any(DisponibilidadRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/disponibilidades/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad actualizado(a) correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    @DisplayName("DELETE /api/v1/disponibilidades/{id} - Should deactivate disponibilidad")
    void shouldDeactivateDisponibilidad() throws Exception {
        mockMvc.perform(delete("/api/v1/disponibilidades/admin/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/disponibilidades/{id}/activar - Should activate disponibilidad")
    void shouldActivateDisponibilidad() throws Exception {
        mockMvc.perform(patch("/api/v1/disponibilidades/admin/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad activado(a) correctamente"));
    }
}
