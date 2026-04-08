package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.projection.CategoriaResumen;
import com.arrupeteca.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @DisplayName("POST /api/v1/categorias - Should create category")
    void shouldCreateCategory() throws Exception {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Nueva");

        CategoriaResumen resumen = new CategoriaResumen() {
            @Override public Long getId() { return 1L; }
            @Override public String getNombre() { return "Nueva"; }
            @Override public boolean getBorradoLogico() { return false; }
        };
        when(categoriaService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }
}
