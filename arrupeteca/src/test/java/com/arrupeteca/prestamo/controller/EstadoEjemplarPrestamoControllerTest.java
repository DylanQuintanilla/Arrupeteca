package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.EstadoEjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.EstadoEjemplarPrestamoResponse;
import com.arrupeteca.prestamo.internal.service.EstadoEjemplarPrestamoService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstadoEjemplarPrestamoController.class)
class EstadoEjemplarPrestamoControllerTest extends BaseControllerTest {

    @MockitoBean
    private EstadoEjemplarPrestamoService estadoEjemplarPrestamoService;

    @Test
    @DisplayName("GET /api/v1/estados-ejemplar-prestamo - Should return active list")
    void shouldReturnActive() throws Exception {
        when(estadoEjemplarPrestamoService.obtenerTodosActivos(any())).thenReturn(List.of(new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false)));

        mockMvc.perform(get("/api/v1/estados-ejemplar-prestamo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].nombre").value("EN_PRESTAMO"));
    }

    @Test
    @DisplayName("GET /api/v1/estados-ejemplar-prestamo/admin/all - Should return page")
    void shouldReturnAllAdmin() throws Exception {
        Page<EstadoEjemplarPrestamoResponse> page = new PageImpl<>(List.of(new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false)));
        when(estadoEjemplarPrestamoService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/estados-ejemplar-prestamo/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/estados-ejemplar-prestamo/admin - Should create")
    void shouldCreate() throws Exception {
        EstadoEjemplarPrestamoRequest request = new EstadoEjemplarPrestamoRequest("EN_PRESTAMO");
        when(estadoEjemplarPrestamoService.crear(any())).thenReturn(new EstadoEjemplarPrestamoResponse(1L, "EN_PRESTAMO", false));

        mockMvc.perform(post("/api/v1/estados-ejemplar-prestamo/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/estados-ejemplar-prestamo/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/estados-ejemplar-prestamo/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/estados-ejemplar-prestamo/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/estados-ejemplar-prestamo/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
