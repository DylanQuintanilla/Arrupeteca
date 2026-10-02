package com.arrupeteca.prestamo.controller;

import com.arrupeteca.controller.BaseControllerTest;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.internal.service.EjemplarPrestamoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EjemplarPrestamoController.class)
class EjemplarPrestamoControllerTest extends BaseControllerTest {

    @MockitoBean
    private EjemplarPrestamoService ejemplarPrestamoService;

    @Test
    @DisplayName("GET /api/v1/ejemplares-prestamo/prestamo/{idPrestamo} - Should return list")
    void shouldReturnByPrestamo() throws Exception {
        EjemplarPrestamoDetalleResponse response = EjemplarPrestamoDetalleResponse.builder()
                .id(1L)
                .idPrestamo(100L)
                .codigoEjemplar("EJ-001")
                .build();

        when(ejemplarPrestamoService.obtenerPorPrestamo(100L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/ejemplares-prestamo/prestamo/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].codigoEjemplar").value("EJ-001"));
    }

    @Test
    @DisplayName("POST /api/v1/ejemplares-prestamo/admin - Should create")
    void shouldCreate() throws Exception {
        EjemplarPrestamoRequest request = EjemplarPrestamoRequest.builder()
                .idPrestamo(JsonNullable.of(100L))
                .idEjemplar(JsonNullable.of(50L))
                .build();

        EjemplarPrestamoDetalleResponse response = EjemplarPrestamoDetalleResponse.builder()
                .id(1L)
                .build();

        when(ejemplarPrestamoService.agregarEjemplar(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ejemplares-prestamo/admin")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/v1/ejemplares-prestamo/admin/pagar-mora/{id} - Should pay mora")
    void shouldPayMora() throws Exception {
        mockMvc.perform(patch("/api/v1/ejemplares-prestamo/admin/pagar-mora/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));

        verify(ejemplarPrestamoService).pagarMora(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/ejemplares-prestamo/admin/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/ejemplares-prestamo/admin/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/ejemplares-prestamo/admin/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/ejemplares-prestamo/admin/1/activar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
