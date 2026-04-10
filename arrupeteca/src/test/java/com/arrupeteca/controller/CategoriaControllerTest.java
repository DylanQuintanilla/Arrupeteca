package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest extends BaseControllerTest {

    @MockitoBean
    private CategoriaService categoriaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/v1/categorias - Should return active categories")
    void shouldReturnActiveCategories() throws Exception {
        Page<CategoriaResumen> page = new PageImpl<>(List.of());
        when(categoriaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de categorías activas"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias/{id} - Should return category by ID")
    void shouldReturnCategoryById() throws Exception {
        CategoriaResumen resumen = new TestCategoriaResumen(1L, "Existente", false);
        when(categoriaService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría encontrada"))
                .andExpect(jsonPath("$.data.nombre").value("Existente"));
    }

    @Test
    @DisplayName("POST /api/v1/categorias - Should create category")
    void shouldCreateCategory() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Nueva");

        CategoriaResumen resumen = new TestCategoriaResumen(1L, "Nueva", false);
        when(categoriaService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }

    @Test
    @DisplayName("PUT /api/v1/categorias/{id} - Should update category")
    void shouldUpdateCategory() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Actualizada");

        CategoriaResumen resumen = new TestCategoriaResumen(1L, "Actualizada", false);
        when(categoriaService.actualizar(any(Long.class), any(CategoriaRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría actualizada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    @DisplayName("DELETE /api/v1/categorias/{id} - Should deactivate category")
    void shouldDeactivateCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/categorias/{id}/activar - Should activate category")
    void shouldActivateCategory() throws Exception {
        mockMvc.perform(patch("/api/v1/categorias/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría activada correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias/admin - Should return all categories for admin")
    void shouldReturnAllCategoriesAdmin() throws Exception {
        Page<CategoriaResumen> page = new PageImpl<>(List.of());
        when(categoriaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/categorias/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de categorías"));
    }

    // Helper implementation for Projections
    private record TestCategoriaResumen(Long id, String nombre, boolean borradoLogico) implements CategoriaResumen {

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        @Override
        public boolean getBorradoLogico() {
            return borradoLogico;
        }
    }
}
