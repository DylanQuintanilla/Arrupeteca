package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.service.ObraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObraController.class)
class ObraControllerTest extends BaseControllerTest {

    @MockitoBean
    private ObraService obraService;

    @Test
    @DisplayName("GET /api/v1/obras/admin/all - Should return works for admin")
    void shouldReturnAllForAdmin() throws Exception {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(obraService.busquedaAdmin(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/obras/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin/{id} - Should return work by ID for admin")
    void shouldReturnByIdForAdmin() throws Exception {
        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Test").anioCreacion(2021).resumen("Resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).etiquetas(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.busquedaAdminPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/obras/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin/verificar-duplicados - Should return duplicate works")
    void shouldReturnDuplicateWorks() throws Exception {
        ObraResumen resumen = new ObraResumen(1L, "Cien años de soledad", 1967, "Resumen", false);
        when(obraService.buscarDuplicados("Cien", null)).thenReturn(List.of(resumen));

        mockMvc.perform(get("/api/v1/obras/admin/verificar-duplicados").param("termino", "Cien"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data[0].titulo").value("Cien años de soledad"));
    }

    @Test
    @DisplayName("POST /api/v1/obras/admin - Should create work")
    void shouldCreate() throws Exception {
        ObraRequest request = ObraRequest.builder()
                .titulo(org.openapitools.jackson.nullable.JsonNullable.of("Nueva"))
                .resumen(org.openapitools.jackson.nullable.JsonNullable.of("Resumen de la obra"))
                .idGeneros(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .idCategorias(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .autores(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(new com.arrupeteca.dto.request.ObraAutorRequest(1L, 1L))))
                .build();

        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Nueva").anioCreacion(2021).resumen("Resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).etiquetas(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/obras/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/obras/admin/{id} - Should update work")
    void shouldUpdate() throws Exception {
        ObraRequest request = ObraRequest.builder()
                .titulo(org.openapitools.jackson.nullable.JsonNullable.of("Actualizada"))
                .resumen(org.openapitools.jackson.nullable.JsonNullable.of("Nuevo resumen"))
                .idGeneros(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .idCategorias(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .autores(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(new com.arrupeteca.dto.request.ObraAutorRequest(1L, 1L))))
                .version(0L)
                .build();

        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Actualizada").anioCreacion(2021).resumen("Nuevo resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).etiquetas(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.actualizar(any(Long.class), any(ObraRequest.class))).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/obras/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/obras/admin/{id} - Should deactivate work")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/obras/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/obras/admin/{id}/activar - Should activate work")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/obras/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

