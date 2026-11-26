package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import com.arrupeteca.prestamo.service.EstadoSolicitudService;
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

@WebMvcTest(EstadoSolicitudController.class)
class EstadoSolicitudControllerTest extends BaseControllerTest {

    @MockitoBean
    private EstadoSolicitudService estadoSolicitudService;

    @Test
    @DisplayName("GET /api/v1/estados-solicitud - Should return active list")
    void shouldReturnActive() throws Exception {
        when(estadoSolicitudService.obtenerTodosActivos(any())).thenReturn(List.of(new EstadoSolicitudResponse(1L, "PENDIENTE", false)));

        mockMvc.perform(get("/api/v1/estados-solicitud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].nombre").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/v1/estados-solicitud/admin/all - Should return page")
    void shouldReturnAllAdmin() throws Exception {
        Page<EstadoSolicitudResponse> page = new PageImpl<>(List.of(new EstadoSolicitudResponse(1L, "PENDIENTE", false)));
        when(estadoSolicitudService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/estados-solicitud/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/estados-solicitud/admin - Should create")
    void shouldCreate() throws Exception {
        EstadoSolicitudRequest request = new EstadoSolicitudRequest("PENDIENTE");
        when(estadoSolicitudService.crear(any())).thenReturn(new EstadoSolicitudResponse(1L, "PENDIENTE", false));

        mockMvc.perform(post("/api/v1/estados-solicitud/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/estados-solicitud/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/estados-solicitud/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/estados-solicitud/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/estados-solicitud/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
