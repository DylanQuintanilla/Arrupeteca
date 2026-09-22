package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.service.LibroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
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

@WebMvcTest(LibroController.class)
class LibroControllerTest extends BaseControllerTest {

    @MockitoBean
    private LibroService libroService;

    @Test
    @DisplayName("GET /api/v1/libros - Should return active books")
    void shouldReturnActive() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.busquedaGlobal(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} - Should return book by ID")
    void shouldReturnById() throws Exception {
        LibroDetalleResponse detail = LibroDetalleResponse.builder()
                .id(1L).isbn("1234567890").anioPublicacion(2021).precio(BigDecimal.TEN).urlImagen("url").mora(BigDecimal.ONE)
                .version(0L).borradoLogico(false)
                .grado(GradoResponse.builder().id(1L).nombre("Grado").build())
                .ciclo(CicloResponse.builder().id(1L).nombre("Ciclo").build())
                .creadoPor("admin").modificadoPor("admin")
                .idiomas(Collections.emptySet()).build();
        when(libroService.busquedaGlobalPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.isbn").value("1234567890"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/obra/{idObra} - Should return books by work ID")
    void shouldReturnByObraId() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.buscarPorObraId(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros/obra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin/all - Should return all books for admin")
    void shouldReturnAllForAdmin() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.busquedaAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin/{id} - Should return book by ID for admin")
    void shouldReturnByIdForAdmin() throws Exception {
        LibroDetalleResponse detail = LibroDetalleResponse.builder()
                .id(1L).isbn("123").anioPublicacion(2021).precio(BigDecimal.TEN).urlImagen("U").mora(BigDecimal.ONE)
                .version(0L).borradoLogico(false)
                .grado(GradoResponse.builder().id(1L).nombre("G").build())
                .ciclo(CicloResponse.builder().id(1L).nombre("C").build())
                .creadoPor("A").modificadoPor("A")
                .idiomas(Collections.emptySet()).build();
        when(libroService.busquedaAdminPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/libros/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/libros/admin - Should create book")
    void shouldCreate() throws Exception {
        LibroRequest request = LibroRequest.builder()
                .isbn(org.openapitools.jackson.nullable.JsonNullable.of("9780596520687"))
                .anioPublicacion(org.openapitools.jackson.nullable.JsonNullable.of(2021))
                .idObra(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idFormato(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idEditorial(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idIdiomas(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .build();

        LibroDetalleResponse detail = LibroDetalleResponse.builder()
                .id(1L).isbn("9780596520687").anioPublicacion(2021).precio(BigDecimal.TEN).urlImagen("url").mora(BigDecimal.ONE)
                .version(0L).borradoLogico(false)
                .grado(GradoResponse.builder().id(1L).nombre("Grado").build())
                .ciclo(CicloResponse.builder().id(1L).nombre("Ciclo").build())
                .creadoPor("admin").modificadoPor("admin")
                .idiomas(Collections.emptySet()).build();
        when(libroService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/libros/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/libros/admin/{id} - Should update book")
    void shouldUpdate() throws Exception {
        LibroRequest request = LibroRequest.builder()
                .isbn(org.openapitools.jackson.nullable.JsonNullable.of("9780596520687"))
                .anioPublicacion(org.openapitools.jackson.nullable.JsonNullable.of(2021))
                .idObra(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idFormato(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idEditorial(org.openapitools.jackson.nullable.JsonNullable.of(1L))
                .idIdiomas(org.openapitools.jackson.nullable.JsonNullable.of(Collections.singleton(1L)))
                .version(0L)
                .build();

        LibroDetalleResponse detail = LibroDetalleResponse.builder()
                .id(1L).isbn("9780596520687").anioPublicacion(2021).precio(BigDecimal.TEN).urlImagen("url").mora(BigDecimal.ONE)
                .version(0L).borradoLogico(false)
                .grado(GradoResponse.builder().id(1L).nombre("Grado").build())
                .ciclo(CicloResponse.builder().id(1L).nombre("Ciclo").build())
                .creadoPor("admin").modificadoPor("admin")
                .idiomas(Collections.emptySet()).build();
        when(libroService.actualizar(any(Long.class), any(LibroRequest.class))).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/libros/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/libros/admin/{id} - Should deactivate book")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/libros/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/libros/admin/{id}/activar - Should activate book")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/libros/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

