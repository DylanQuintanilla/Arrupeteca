package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoDetalleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SolicitudPrestamoDetalleController.class)
class SolicitudPrestamoDetalleControllerTest extends BaseControllerTest {

    @MockitoBean
    private SolicitudPrestamoDetalleService detalleService;

    @Test
    @DisplayName("GET /api/v1/solicitudes-prestamo-detalle/solicitud/{idSolicitud} - Should return items list")
    void shouldReturnBySolicitud() throws Exception {
        SolicitudPrestamoDetalleItemResponse item = SolicitudPrestamoDetalleItemResponse.builder()
                .id(1L)
                .idSolicitud(100L)
                .idLibro(50L)
                .tituloObra("Don Quijote")
                .cantidadSolicitada(2)
                .build();

        when(detalleService.obtenerPorSolicitud(100L)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/solicitudes-prestamo-detalle/solicitud/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].tituloObra").value("Don Quijote"));
    }

    @Test
    @DisplayName("POST /api/v1/solicitudes-prestamo-detalle/admin - Should add detail")
    void shouldAddDetail() throws Exception {
        SolicitudPrestamoDetalleRequest request = SolicitudPrestamoDetalleRequest.builder()
                .idSolicitud(JsonNullable.of(100L))
                .idLibro(JsonNullable.of(50L))
                .cantidadSolicitada(JsonNullable.of(2))
                .build();

        SolicitudPrestamoDetalleItemResponse response = SolicitudPrestamoDetalleItemResponse.builder()
                .id(1L)
                .build();

        when(detalleService.agregarDetalle(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/solicitudes-prestamo-detalle/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/solicitudes-prestamo-detalle/admin/{id} - Should update detail")
    void shouldUpdateDetail() throws Exception {
        SolicitudPrestamoDetalleRequest request = SolicitudPrestamoDetalleRequest.builder()
                .cantidadSolicitada(JsonNullable.of(5))
                .build();

        SolicitudPrestamoDetalleItemResponse response = SolicitudPrestamoDetalleItemResponse.builder()
                .id(1L)
                .cantidadSolicitada(5)
                .build();

        when(detalleService.actualizarDetalle(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/solicitudes-prestamo-detalle/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.cantidadSolicitada").value(5));
    }

    @Test
    @DisplayName("DELETE /api/v1/solicitudes-prestamo-detalle/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/solicitudes-prestamo-detalle/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/solicitudes-prestamo-detalle/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/solicitudes-prestamo-detalle/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
