package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest extends BaseControllerTest {

    @MockitoBean
    private CategoriaService categoriaService;

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
        CategoriaDetalleResponse detalle = new CategoriaDetalleResponse(1L, "Existente", false, null, null, null, null, null);
        when(categoriaService.obtenerPorIdActivo(1L)).thenReturn(detalle);

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

        CategoriaDetalleResponse detalle = new CategoriaDetalleResponse(1L, "Nueva", false, null, null, null, null, null);
        when(categoriaService.crear(any())).thenReturn(detalle);

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

        CategoriaDetalleResponse detalle = new CategoriaDetalleResponse(1L, "Actualizada", false, null, null, null, null, null);
        when(categoriaService.actualizar(any(Long.class), any(CategoriaRequest.class))).thenReturn(detalle);

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

    @Test
    @DisplayName("GET /api/v1/categorias/admin/{id} - Should return category by ID for admin")
    void shouldReturnCategoryByIdAdmin() throws Exception {
        CategoriaDetalleResponse detalle = new CategoriaDetalleResponse(1L, "Existente", false, null, null, null, null, null);
        when(categoriaService.obtenerPorId(1L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/categorias/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría encontrada (admin)"))
                .andExpect(jsonPath("$.data.nombre").value("Existente"));
    }
}
