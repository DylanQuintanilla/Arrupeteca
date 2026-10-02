package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitudPrestamoController.class)
class SolicitudPrestamoControllerTest extends BaseControllerTest {

    @MockitoBean
    private SolicitudPrestamoService solicitudPrestamoService;

    @Test
    @DisplayName("GET /api/v1/solicitudes-prestamo/admin/all - Should return page")
    void shouldReturnPageForAdmin() throws Exception {
        SolicitudPrestamoResumen resumen = SolicitudPrestamoResumen.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Juan Pérez")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .fechaSolicitud(LocalDateTime.now())
                .creadoPorAdmin(false)
                .borradoLogico(false)
                .build();
        Page<SolicitudPrestamoResumen> page = new PageImpl<>(List.of(resumen));

        when(solicitudPrestamoService.obtenerTodos(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/solicitudes-prestamo/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/solicitudes-prestamo/admin/{id} - Should return detail")
    void shouldReturnDetail() throws Exception {
        SolicitudPrestamoDetalleResponse response = SolicitudPrestamoDetalleResponse.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Juan Pérez")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .build();

        when(solicitudPrestamoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/solicitudes-prestamo/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombreUsuario").value("Juan Pérez"));
    }

    @Test
    @DisplayName("POST /api/v1/solicitudes-prestamo/admin - Should create")
    void shouldCreate() throws Exception {
        SolicitudPrestamoRequest request = SolicitudPrestamoRequest.builder()
                .idUsuario(JsonNullable.of(10L))
                .idEstadoSolicitud(JsonNullable.of(1L))
                .build();

        SolicitudPrestamoDetalleResponse response = SolicitudPrestamoDetalleResponse.builder()
                .id(1L)
                .idUsuario(10L)
                .build();

        when(solicitudPrestamoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/solicitudes-prestamo/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/solicitudes-prestamo/admin/{id} - Should update")
    void shouldUpdate() throws Exception {
        SolicitudPrestamoRequest request = SolicitudPrestamoRequest.builder()
                .idEstadoSolicitud(JsonNullable.of(2L))
                .build();

        SolicitudPrestamoDetalleResponse response = SolicitudPrestamoDetalleResponse.builder()
                .id(1L)
                .idEstadoSolicitud(2L)
                .build();

        when(solicitudPrestamoService.actualizar(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/solicitudes-prestamo/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.idEstadoSolicitud").value(2));
    }

    @Test
    @DisplayName("DELETE /api/v1/solicitudes-prestamo/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/solicitudes-prestamo/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/solicitudes-prestamo/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/solicitudes-prestamo/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
