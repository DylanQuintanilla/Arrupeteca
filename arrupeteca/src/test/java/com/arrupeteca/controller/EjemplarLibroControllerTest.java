package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.DisponibilidadResponse;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.service.EjemplarLibroService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EjemplarLibroController.class)
class EjemplarLibroControllerTest extends BaseControllerTest {

    @MockitoBean
    private EjemplarLibroService ejemplarLibroService;

    @Test
    @DisplayName("GET /api/v1/ejemplares/admin/all - Should return all for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<EjemplarLibroResumen> page = new PageImpl<>(List.of());
        when(ejemplarLibroService.busquedaAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/ejemplares/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/ejemplares/admin/{id} - Should return by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        EjemplarLibroDetalleResponse detail = EjemplarLibroDetalleResponse.builder()
                .id(1L).comentario("Test").version(0L).borradoLogico(false)
                .estadoFisico(EstadoFisicoResponse.builder().id(1L).nombre("BUENO").build())
                .disponibilidad(DisponibilidadResponse.builder().id(1L).nombre("DISPONIBLE").build())
                .creadoPor("admin").modificadoPor("admin").ubicacion("Ubicacion").build();
        when(ejemplarLibroService.busquedaAdminPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/ejemplares/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/ejemplares/admin - Should create ejemplar")
    void shouldCreate() throws Exception {
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario(org.openapitools.jackson.nullable.JsonNullable.of("Nuevo"))
                .idLibro(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idMueble(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idEstadoFisico(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idDisponibilidad(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .build();

        EjemplarLibroDetalleResponse detail = EjemplarLibroDetalleResponse.builder()
                .id(1L).comentario("Nuevo").version(0L).borradoLogico(false)
                .estadoFisico(EstadoFisicoResponse.builder().id(1L).nombre("BUENO").build())
                .disponibilidad(DisponibilidadResponse.builder().id(1L).nombre("DISPONIBLE").build())
                .creadoPor("admin").modificadoPor("admin").ubicacion("Estante 1").build();
        when(ejemplarLibroService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/ejemplares/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.comentario").value("Nuevo"));
    }

    @Test
    @DisplayName("PATCH /api/v1/ejemplares/admin/{id} - Should update ejemplar")
    void shouldUpdate() throws Exception {
        EjemplarLibroRequest request = EjemplarLibroRequest.builder()
                .comentario(org.openapitools.jackson.nullable.JsonNullable.of("Actualizado"))
                .idLibro(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idMueble(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idEstadoFisico(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idDisponibilidad(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .version(0L)
                .build();

        EjemplarLibroDetalleResponse detail = EjemplarLibroDetalleResponse.builder()
                .id(1L).comentario("Actualizado").version(0L).borradoLogico(false)
                .estadoFisico(EstadoFisicoResponse.builder().id(1L).nombre("BUENO").build())
                .disponibilidad(DisponibilidadResponse.builder().id(1L).nombre("DISPONIBLE").build())
                .creadoPor("admin").modificadoPor("admin").ubicacion("Estante 1").build();
        when(ejemplarLibroService.actualizar(any(Long.class), any(EjemplarLibroRequest.class))).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/ejemplares/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.comentario").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/ejemplares/admin/{id} - Should deactivate ejemplar")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/ejemplares/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/ejemplares/admin/{id}/activar - Should activate ejemplar")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/ejemplares/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar activado correctamente"));
    }
}

