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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObraController.class)
class ObraControllerTest extends BaseControllerTest {

    @MockitoBean
    private ObraService obraService;

    @Test
    @DisplayName("GET /api/v1/obras - Should return active works")
    void shouldReturnActive() throws Exception {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(obraService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/obras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/{id} - Should return work by ID")
    void shouldReturnById() throws Exception {
        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Obra Test").anioCreacion(2021).resumen("Resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/obras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.titulo").value("Obra Test"));
    }

    @Test
    @DisplayName("GET /api/v1/obras/global - Should perform global search")
    void shouldPerformGlobalSearch() throws Exception {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(obraService.busquedaGlobal(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/obras/global").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin - Should return all works for admin")
    void shouldReturnAllForAdmin() throws Exception {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(obraService.obtenerTodos(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/obras/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin/{id} - Should return work by ID for admin")
    void shouldReturnByIdForAdmin() throws Exception {
        ObraDetalleResponse detail = ObraDetalleResponse.builder()
                .id(1L).titulo("Test").anioCreacion(2021).resumen("Resumen").version(0L).borradoLogico(false)
                .creadoPor("admin").modificadoPor("admin")
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/obras/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin/busqueda-maestra - Should perform master search")
    void shouldPerformMasterSearch() throws Exception {
        Page<ObraResumen> page = new PageImpl<>(List.of());
        when(obraService.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/obras/admin/busqueda-maestra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/obras - Should create work")
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
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/obras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/obras/{id} - Should update work")
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
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).autores(Collections.emptySet()).build();
        when(obraService.actualizar(any(Long.class), any(ObraRequest.class))).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/obras/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/obras/{id} - Should deactivate work")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/obras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/obras/{id}/activar - Should activate work")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/obras/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
