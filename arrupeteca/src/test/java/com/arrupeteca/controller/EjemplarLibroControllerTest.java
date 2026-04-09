package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.service.EjemplarLibroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EjemplarLibroController.class)
class EjemplarLibroControllerTest extends BaseControllerTest {

    @MockitoBean
    private EjemplarLibroService ejemplarLibroService;

    @Test
    @DisplayName("GET /api/v1/ejemplares - Should return active ejemplares page")
    void obtenerTodosActivos() throws Exception {
        when(ejemplarLibroService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/ejemplares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de ejemplares activos"));
    }

    @Test
    @DisplayName("GET /api/v1/ejemplares/{id} - Should return ejemplar when active")
    void obtenerPorId() throws Exception {
        when(ejemplarLibroService.obtenerPorIdActivo(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/ejemplares/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar encontrado"));
    }

    @Test
    @DisplayName("GET /api/v1/ejemplares/busqueda - Should return search results")
    void busquedaGlobal() throws Exception {
        when(ejemplarLibroService.busquedaGlobal(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/ejemplares/busqueda").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados de búsqueda"));
    }

    @Test
    @DisplayName("POST /api/v1/ejemplares - Should create ejemplar")
    void crear() throws Exception {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdLibro(1L);
        request.setIdMueble(1L);

        when(ejemplarLibroService.crear(any(EjemplarLibroRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/ejemplares")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar creado exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/ejemplares/{id} - Should update ejemplar")
    void actualizar() throws Exception {
        EjemplarLibroRequest request = new EjemplarLibroRequest();
        request.setIdLibro(1L);
        request.setIdMueble(1L);

        when(ejemplarLibroService.actualizar(eq(1L), any(EjemplarLibroRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/ejemplares/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/ejemplares/{id} - Should deactivate ejemplar")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/ejemplares/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/ejemplares/{id}/activar - Should activate ejemplar")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/ejemplares/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ejemplar activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/ejemplares/admin - Should return all ejemplares for admin")
    void obtenerTodosAdmin() throws Exception {
        when(ejemplarLibroService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/ejemplares/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de ejemplares"));
    }

    @Test
    @DisplayName("GET /api/v1/ejemplares/admin/busqueda-maestra - Should return master search results")
    void busquedaMaestra() throws Exception {
        when(ejemplarLibroService.busquedaMaestraAdmin(anyBoolean(), anyLong(), anyLong(), anyString(), anyString(),
                anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/ejemplares/admin/busqueda-maestra")
                .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"));
    }
}
