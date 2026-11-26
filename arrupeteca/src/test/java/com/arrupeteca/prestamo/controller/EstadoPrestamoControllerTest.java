package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.EstadoPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoPrestamoResponse;
import com.arrupeteca.prestamo.service.EstadoPrestamoService;
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

@WebMvcTest(EstadoPrestamoController.class)
class EstadoPrestamoControllerTest extends BaseControllerTest {

    @MockitoBean
    private EstadoPrestamoService estadoPrestamoService;

    @Test
    @DisplayName("GET /api/v1/estados-prestamo - Should return active estados prestamo list")
    void shouldReturnActive() throws Exception {
        when(estadoPrestamoService.obtenerTodosActivos(any())).thenReturn(List.of(new EstadoPrestamoResponse(1L, "ACTIVO", false)));

        mockMvc.perform(get("/api/v1/estados-prestamo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].nombre").value("ACTIVO"));
    }

    @Test
    @DisplayName("GET /api/v1/estados-prestamo/admin/all - Should return page")
    void shouldReturnAllAdmin() throws Exception {
        Page<EstadoPrestamoResponse> page = new PageImpl<>(List.of(new EstadoPrestamoResponse(1L, "ACTIVO", false)));
        when(estadoPrestamoService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/estados-prestamo/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/estados-prestamo/admin/{id} - Should return by ID")
    void shouldReturnById() throws Exception {
        when(estadoPrestamoService.obtenerPorId(1L)).thenReturn(new EstadoPrestamoResponse(1L, "ACTIVO", false));

        mockMvc.perform(get("/api/v1/estados-prestamo/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("ACTIVO"));
    }

    @Test
    @DisplayName("POST /api/v1/estados-prestamo/admin - Should create")
    void shouldCreate() throws Exception {
        EstadoPrestamoRequest request = new EstadoPrestamoRequest("ACTIVO");
        when(estadoPrestamoService.crear(any())).thenReturn(new EstadoPrestamoResponse(1L, "ACTIVO", false));

        mockMvc.perform(post("/api/v1/estados-prestamo/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/estados-prestamo/admin/{id} - Should update")
    void shouldUpdate() throws Exception {
        EstadoPrestamoRequest request = new EstadoPrestamoRequest("FINALIZADO");
        when(estadoPrestamoService.actualizar(eq(1L), any())).thenReturn(new EstadoPrestamoResponse(1L, "FINALIZADO", false));

        mockMvc.perform(put("/api/v1/estados-prestamo/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/estados-prestamo/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/estados-prestamo/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/estados-prestamo/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/estados-prestamo/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
