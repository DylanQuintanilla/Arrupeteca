package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.service.GeneroService;
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

@WebMvcTest(GeneroController.class)
class GeneroControllerTest extends BaseControllerTest {

    @MockitoBean
    private GeneroService generoService;

    @Test
    @DisplayName("GET /api/v1/generos - Should return active genres")
    void shouldReturnActive() throws Exception {
        Page<GeneroResponse> page = new PageImpl<>(List.of());
        when(generoService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/generos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/generos/{id} - Should return genre by ID")
    void shouldReturnById() throws Exception {
        GeneroResponse response = new GeneroResponse(1L, "Genero Test", false);
        when(generoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/generos/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Genero Test"));
    }

    @Test
    @DisplayName("POST /api/v1/generos - Should create genre")
    void shouldCreate() throws Exception {
        GeneroRequest request = GeneroRequest.builder().nombre("Nuevo").build();
        GeneroResponse response = new GeneroResponse(1L, "Nuevo", false);

        when(generoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/generos/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/generos/{id} - Should update genre")
    void shouldUpdate() throws Exception {
        GeneroRequest request = GeneroRequest.builder().nombre("Actualizado").build();
        GeneroResponse response = new GeneroResponse(1L, "Actualizado", false);

        when(generoService.actualizar(any(Long.class), any(GeneroRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/generos/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/generos/admin - Should return all genres for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<GeneroResponse> page = new PageImpl<>(List.of());
        when(generoService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/generos/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de géneros literarios"));
    }

    @Test
    @DisplayName("GET /api/v1/generos/admin/{id} - Should return genre by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        GeneroResponse response = new GeneroResponse(1L, "Genero Test", false);
        when(generoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/generos/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Genero encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/generos/{id} - Should deactivate genre")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/generos/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Genero desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/generos/{id}/activar - Should activate genre")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/generos/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Genero activado(a) correctamente"));
    }
}
