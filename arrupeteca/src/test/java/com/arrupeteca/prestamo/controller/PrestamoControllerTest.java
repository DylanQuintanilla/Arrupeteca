package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.internal.service.PrestamoService;
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

@WebMvcTest(PrestamoController.class)
class PrestamoControllerTest extends BaseControllerTest {

    @MockitoBean
    private PrestamoService prestamoService;

    @Test
    @DisplayName("GET /api/v1/prestamos/admin/all - Should return page")
    void shouldReturnPageForAdmin() throws Exception {
        PrestamoResumen resumen = PrestamoResumen.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Carlos Gómez")
                .idEstadoPrestamo(1L)
                .estadoPrestamo("ACTIVO")
                .fechaPrestamo(LocalDateTime.now())
                .borradoLogico(false)
                .build();
        Page<PrestamoResumen> page = new PageImpl<>(List.of(resumen));

        when(prestamoService.obtenerTodos(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/prestamos/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/prestamos/admin/{id} - Should return detail")
    void shouldReturnDetail() throws Exception {
        PrestamoDetalleResponse response = PrestamoDetalleResponse.builder()
                .id(1L)
                .idUsuario(10L)
                .nombreUsuario("Carlos Gómez")
                .idEstadoPrestamo(1L)
                .estadoPrestamo("ACTIVO")
                .build();

        when(prestamoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/prestamos/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombreUsuario").value("Carlos Gómez"));
    }

    @Test
    @DisplayName("POST /api/v1/prestamos/admin - Should create")
    void shouldCreate() throws Exception {
        PrestamoRequest request = PrestamoRequest.builder()
                .idUsuario(JsonNullable.of(10L))
                .entregadoPor(JsonNullable.of(2L))
                .build();

        PrestamoDetalleResponse response = PrestamoDetalleResponse.builder()
                .id(1L)
                .idUsuario(10L)
                .build();

        when(prestamoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/prestamos/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/prestamos/admin/{id} - Should update")
    void shouldUpdate() throws Exception {
        PrestamoRequest request = PrestamoRequest.builder()
                .idEstadoPrestamo(JsonNullable.of(2L))
                .build();

        PrestamoDetalleResponse response = PrestamoDetalleResponse.builder()
                .id(1L)
                .idEstadoPrestamo(2L)
                .build();

        when(prestamoService.actualizar(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/prestamos/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.idEstadoPrestamo").value(2));
    }

    @Test
    @DisplayName("DELETE /api/v1/prestamos/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/prestamos/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/prestamos/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/prestamos/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
