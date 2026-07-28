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
import static org.mockito.Mockito.when;
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
    @DisplayName("GET /api/v1/disponibilidades - Should return active disponibilidades")
    void shouldReturnActive() throws Exception {
        Page<DisponibilidadResponse> page = new PageImpl<>(List.of());
        when(disponibilidadService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/disponibilidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/disponibilidades/{id} - Should return disponibilidad by ID")
    void shouldReturnById() throws Exception {
        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Disponible", false);
        when(disponibilidadService.obtenerPorIdActivo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/disponibilidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Disponible"));
    }

    @Test
    @DisplayName("POST /api/v1/disponibilidades - Should create disponibilidad")
    void shouldCreate() throws Exception {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Nueva disponibilidad").build();
        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Nueva disponibilidad", false);
        when(disponibilidadService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/disponibilidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/disponibilidades/{id} - Should update disponibilidad")
    void shouldUpdate() throws Exception {
        DisponibilidadRequest request = DisponibilidadRequest.builder().nombre("Disp act").build();
        DisponibilidadResponse response = new DisponibilidadResponse(1L, "Disp act", false);
        when(disponibilidadService.actualizar(any(Long.class), any(DisponibilidadRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/disponibilidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/disponibilidades/{id} - Should deactivate disponibilidad")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/disponibilidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/disponibilidades/{id}/activar - Should activate disponibilidad")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/disponibilidades/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
