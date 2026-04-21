package com.arrupeteca.controller;

import java.util.List;
import java.util.Set;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.service.LibroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibroController.class)
class LibroControllerTest extends BaseControllerTest {

    @MockitoBean
    private LibroService libroService;

    @Test
    @DisplayName("GET /api/v1/libros - Should return active libros page")
    void obtenerTodosActivos() throws Exception {
        when(libroService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de libros activos"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/{id} - Should return libro when active")
    void obtenerPorId() throws Exception {
        when(libroService.obtenerPorIdActivo(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libro encontrado"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/busqueda - Should return search results")
    void busquedaGlobal() throws Exception {
        when(libroService.busquedaGlobal(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros/busqueda").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados de búsqueda"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/por-obra/{idObra} - Should return libros of obra")
    void obtenerPorObra() throws Exception {
        when(libroService.obtenerPorObra(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros/por-obra/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libros de la obra"));
    }

    @Test
    @DisplayName("POST /api/v1/libros - Should create libro")
    void crear() throws Exception {
        LibroRequest request = new LibroRequest();
        request.setIsbn("978-3-16-148410-0");
        request.setAnioPublicacion(2021);
        request.setIdObra(1L);
        request.setIdFormato(1L);
        request.setIdEditorial(1L);
        request.setIdIdiomas(Set.of(1L));

        when(libroService.crear(any(LibroRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libro creado exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/libros/{id} - Should update libro")
    void actualizar() throws Exception {
        LibroRequest request = new LibroRequest();
        request.setIsbn("978-3-16-148410-0");
        request.setAnioPublicacion(2021);
        request.setIdObra(1L);
        request.setIdFormato(1L);
        request.setIdEditorial(1L);
        request.setIdIdiomas(Set.of(1L));

        when(libroService.actualizar(eq(1L), any(LibroRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/libros/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libro actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/libros/{id} - Should deactivate libro")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libro desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/libros/{id}/activar - Should activate libro")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/libros/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Libro activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin - Should return all libros for admin")
    void obtenerTodosAdmin() throws Exception {
        when(libroService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de libros"));
    }

    @Test
    @DisplayName("GET /api/v1/libros/admin/busqueda-maestra - Should return master search results")
    void busquedaMaestra() throws Exception {
        when(libroService.busquedaMaestraAdmin(anyBoolean(), anyLong(), anyLong(), anyLong(), anyString(), anyString(),
                anyString(), anyInt(), anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/libros/admin/busqueda-maestra")
                .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"));
    }
}
