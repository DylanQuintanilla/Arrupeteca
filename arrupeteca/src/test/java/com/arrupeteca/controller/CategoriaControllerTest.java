package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.CategoriaResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    @DisplayName("GET /api/v1/categorias - Should return active categories list")
    void shouldReturnActiveCategories() throws Exception {
        when(categoriaService.obtenerTodosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de categorías activas"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias/all - Should return categories table")
    void shouldReturnAllCategories() throws Exception {
        Page<CategoriaResponse> page = new PageImpl<>(List.of());
        when(categoriaService.obtenerTodos(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/categorias/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de categorías"));
    }

    @Test
    @DisplayName("GET /api/v1/categorias/{id} - Should return category by ID")
    void shouldReturnCategoryById() throws Exception {
        CategoriaResponse response = new CategoriaResponse(1L, "Existente", false);
        when(categoriaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría encontrada"))
                .andExpect(jsonPath("$.data.nombre").value("Existente"));
    }

    @Test
    @DisplayName("POST /api/v1/categorias - Should create category")
    void shouldCreateCategory() throws Exception {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Nueva").build();

        CategoriaResponse response = new CategoriaResponse(1L, "Nueva", false);
        when(categoriaService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/categorias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }

    @Test
    @DisplayName("PUT /api/v1/categorias/{id} - Should update category")
    void shouldUpdateCategory() throws Exception {
        CategoriaRequest request = CategoriaRequest.builder().nombre("Actualizada").build();

        CategoriaResponse response = new CategoriaResponse(1L, "Actualizada", false);
        when(categoriaService.actualizar(any(Long.class), any(CategoriaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/categorias/1")
                        .with(csrf())
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
        mockMvc.perform(delete("/api/v1/categorias/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/categorias/{id}/activar - Should activate category")
    void shouldActivateCategory() throws Exception {
        mockMvc.perform(patch("/api/v1/categorias/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría activada correctamente"));
    }
}
