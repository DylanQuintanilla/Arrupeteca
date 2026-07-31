package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.service.EstadoFisicoService;
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

@WebMvcTest(EstadoFisicoController.class)
class EstadoFisicoControllerTest extends BaseControllerTest {

    @MockitoBean
    private EstadoFisicoService estadoFisicoService;

    @Test
    @DisplayName("GET /api/v1/estados-fisicos - Should return active estados fisicos list")
    void shouldReturnActiveEstadosFisicos() throws Exception {
        when(estadoFisicoService.obtenerTodosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/estados-fisicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de estados físicos activos"));
    }

    @Test
    @DisplayName("GET /api/v1/estados-fisicos/all - Should return estados fisicos table")
    void shouldReturnAllEstadosFisicos() throws Exception {
        Page<EstadoFisicoResponse> page = new PageImpl<>(List.of());
        when(estadoFisicoService.obtenerTodos(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/estados-fisicos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de estados físicos"));
    }

    @Test
    @DisplayName("GET /api/v1/estados-fisicos/{id} - Should return estado fisico by ID")
    void shouldReturnEstadoFisicoById() throws Exception {
        EstadoFisicoResponse response = new EstadoFisicoResponse(1L, "Nuevo", false);
        when(estadoFisicoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/estados-fisicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Estado físico encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("POST /api/v1/estados-fisicos - Should create estado fisico")
    void shouldCreateEstadoFisico() throws Exception {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Nuevo").build();

        EstadoFisicoResponse response = new EstadoFisicoResponse(1L, "Nuevo", false);
        when(estadoFisicoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/estados-fisicos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/estados-fisicos/{id} - Should update estado fisico")
    void shouldUpdateEstadoFisico() throws Exception {
        EstadoFisicoRequest request = EstadoFisicoRequest.builder().nombre("Actualizado").build();

        EstadoFisicoResponse response = new EstadoFisicoResponse(1L, "Actualizado", false);
        when(estadoFisicoService.actualizar(any(Long.class), any(EstadoFisicoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/estados-fisicos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Estado físico actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/estados-fisicos/{id} - Should deactivate estado fisico")
    void shouldDeactivateEstadoFisico() throws Exception {
        mockMvc.perform(delete("/api/v1/estados-fisicos/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Estado físico desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/estados-fisicos/{id}/activar - Should activate estado fisico")
    void shouldActivateEstadoFisico() throws Exception {
        mockMvc.perform(patch("/api/v1/estados-fisicos/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Estado físico activado correctamente"));
    }
}
