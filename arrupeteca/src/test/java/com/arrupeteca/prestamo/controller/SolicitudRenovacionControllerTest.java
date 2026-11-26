package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.service.SolicitudRenovacionService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitudRenovacionController.class)
class SolicitudRenovacionControllerTest extends BaseControllerTest {

    @MockitoBean
    private SolicitudRenovacionService renovacionService;

    @Test
    @DisplayName("GET /api/v1/renovaciones/admin/all - Should return page")
    void shouldReturnPageForAdmin() throws Exception {
        SolicitudRenovacionResumen resumen = SolicitudRenovacionResumen.builder()
                .id(1L)
                .idEjemplarPrestamo(10L)
                .idUsuario(5L)
                .nombreUsuario("María López")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .diasSolicitados(7)
                .fechaSolicitud(LocalDateTime.now())
                .borradoLogico(false)
                .build();
        Page<SolicitudRenovacionResumen> page = new PageImpl<>(List.of(resumen));

        when(renovacionService.obtenerTodos(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/renovaciones/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/renovaciones/admin/{id} - Should return detail")
    void shouldReturnDetail() throws Exception {
        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder()
                .id(1L)
                .idUsuario(5L)
                .nombreUsuario("María López")
                .idEstadoSolicitud(1L)
                .estadoSolicitud("PENDIENTE")
                .build();

        when(renovacionService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/renovaciones/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombreUsuario").value("María López"));
    }

    @Test
    @DisplayName("POST /api/v1/renovaciones/solicitar - Should create")
    void shouldCreate() throws Exception {
        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .idEjemplarPrestamo(JsonNullable.of(10L))
                .idUsuario(JsonNullable.of(5L))
                .diasSolicitados(JsonNullable.of(7))
                .build();

        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder()
                .id(1L)
                .idUsuario(5L)
                .build();

        when(renovacionService.solicitarRenovacion(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/renovaciones/solicitar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/v1/renovaciones/admin/{id}/responder - Should respond")
    void shouldRespond() throws Exception {
        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .idEstadoSolicitud(JsonNullable.of(2L))
                .motivoRespuesta(JsonNullable.of("Aprobado"))
                .build();

        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder()
                .id(1L)
                .idEstadoSolicitud(2L)
                .motivoRespuesta("Aprobado")
                .build();

        when(renovacionService.responderRenovacion(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/renovaciones/admin/1/responder")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.idEstadoSolicitud").value(2));
    }

    @Test
    @DisplayName("PATCH /api/v1/renovaciones/admin/{id} - Should update partially")
    void shouldUpdatePartially() throws Exception {
        SolicitudRenovacionRequest request = SolicitudRenovacionRequest.builder()
                .diasSolicitados(JsonNullable.of(10))
                .motivoSolicitud(JsonNullable.of("Actualizado"))
                .build();

        SolicitudRenovacionDetalleResponse response = SolicitudRenovacionDetalleResponse.builder()
                .id(1L)
                .diasSolicitados(10)
                .motivoSolicitud("Actualizado")
                .build();

        when(renovacionService.actualizar(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/renovaciones/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.diasSolicitados").value(10));
    }

    @Test
    @DisplayName("DELETE /api/v1/renovaciones/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/renovaciones/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/renovaciones/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/renovaciones/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
