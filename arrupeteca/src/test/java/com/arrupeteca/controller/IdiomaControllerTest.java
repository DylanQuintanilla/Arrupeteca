package com.arrupeteca.controller;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.service.IdiomaService;
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

@WebMvcTest(IdiomaController.class)
class IdiomaControllerTest extends BaseControllerTest {

    @MockitoBean
    private IdiomaService idiomaService;

    @Test
    @DisplayName("GET /api/v1/idiomas - Should return active languages")
    void shouldReturnActive() throws Exception {
        Page<IdiomaResponse> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/idiomas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/{id} - Should return language by ID")
    void shouldReturnById() throws Exception {
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);
        when(idiomaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/idiomas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/idiomas - Should create language")
    void shouldCreate() throws Exception {
        IdiomaRequest request = IdiomaRequest.builder().nombre("Nuevo").build();
        IdiomaResponse response = new IdiomaResponse(1L, "Nuevo", false);

        when(idiomaService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/idiomas/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/idiomas/{id} - Should update language")
    void shouldUpdate() throws Exception {
        IdiomaRequest request = IdiomaRequest.builder().nombre("Actualizado").build();
        IdiomaResponse response = new IdiomaResponse(1L, "Actualizado", false);

        when(idiomaService.actualizar(any(Long.class), any(IdiomaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/idiomas/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/admin - Should return all languages for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<IdiomaResponse> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/idiomas/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de idiomas"));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/admin/{id} - Should return language by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        IdiomaResponse response = new IdiomaResponse(1L, "Test", false);
        when(idiomaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/idiomas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/idiomas/{id} - Should deactivate language")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/idiomas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/idiomas/{id}/activar - Should activate language")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/idiomas/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma activado(a) correctamente"));
    }
}
