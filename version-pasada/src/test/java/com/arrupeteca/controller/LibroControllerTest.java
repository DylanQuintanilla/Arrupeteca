package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        when(libroService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} - Should return book by ID")
    void shouldReturnById() throws Exception {
        LibroDetalleResponse detail = new LibroDetalleResponse(1L, "1234567890", 2021, BigDecimal.TEN, "url", BigDecimal.ONE,
                false, "Grado", "Ciclo", "admin", null, "admin", null, null, null, null, Collections.emptySet());
        when(libroService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.isbn").value("1234567890"));
    }

    @Test
    @DisplayName("POST /api/v1/libros - Should create book")
    void shouldCreate() throws Exception {
        LibroRequest request = new LibroRequest();
        request.setIsbn("9780596520687");
        request.setAnioPublicacion(2021);
        request.setIdObra(1L);
        request.setIdFormato(1L);
        request.setIdEditorial(1L);
        request.setIdIdiomas(Collections.singleton(1L));

        LibroDetalleResponse detail = new LibroDetalleResponse(1L, "9780596520687", 2021, BigDecimal.TEN, "url", BigDecimal.ONE,
                false, "Grado", "Ciclo", "admin", null, "admin", null, null, null, null, Collections.emptySet());
        when(libroService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/libros/{id} - Should update book")
    void shouldUpdate() throws Exception {
        LibroRequest request = new LibroRequest();
        request.setIsbn("9780596520687");
        request.setAnioPublicacion(2021);
        request.setIdObra(1L);
        request.setIdFormato(1L);
        request.setIdEditorial(1L);
        request.setIdIdiomas(Collections.singleton(1L));

        LibroDetalleResponse detail = new LibroDetalleResponse(1L, "9780596520687", 2021, BigDecimal.TEN, "url", BigDecimal.ONE,
                false, "Grado", "Ciclo", "admin", null, "admin", null, null, null, null, Collections.emptySet());
        when(libroService.actualizar(any(Long.class), any(LibroRequest.class))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/global - Should perform global search")
    void shouldPerformGlobalSearch() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.busquedaGlobal(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros/global").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin - Should return all books for admin")
    void shouldReturnAllForAdmin() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.obtenerTodos(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin/{id} - Should return book by ID for admin")
    void shouldReturnByIdForAdmin() throws Exception {
        LibroDetalleResponse detail = new LibroDetalleResponse(1L, "123", 2021, BigDecimal.TEN, "U", BigDecimal.ONE,
                false, "G", "C", "A", null, "A", null, null, null, null, Collections.emptySet());
        when(libroService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/libros/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin/busqueda-maestra - Should perform master search")
    void shouldPerformMasterSearch() throws Exception {
        Page<LibroResumen> page = new PageImpl<>(List.of());
        when(libroService.busquedaMaestraAdmin(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/libros/admin/busqueda-maestra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
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
    @DisplayName("DELETE /api/v1/libros/{id} - Should deactivate book")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/libros/{id}/activar - Should activate book")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/libros/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
