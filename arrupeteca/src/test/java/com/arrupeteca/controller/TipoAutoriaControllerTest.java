package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.service.TipoAutoriaService;
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

@WebMvcTest(TipoAutoriaController.class)
class TipoAutoriaControllerTest extends BaseControllerTest {

    @MockitoBean
    private TipoAutoriaService tipoAutoriaService;

    @Test
    @DisplayName("GET /api/v1/tipo-autorias - Should return active tipo-autorias")
    void shouldReturnActive() throws Exception {
        Page<TipoAutoriaResponse> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipos-autoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-autoria/{id} - Should return tipo-autoria by ID")
    void shouldReturnById() throws Exception {
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Test", false);
        when(tipoAutoriaService.obtenerPorIdActivo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tipos-autoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/tipos-autoria - Should create tipo-autoria")
    void shouldCreate() throws Exception {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Nuevo").build();
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Nuevo", false);

        when(tipoAutoriaService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/tipos-autoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/tipos-autoria/{id} - Should update tipo-autoria")
    void shouldUpdate() throws Exception {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder().nombre("Actualizado").build();
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Actualizado", false);

        when(tipoAutoriaService.actualizar(any(Long.class), any(TipoAutoriaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/tipos-autoria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-autoria/admin - Should return all tipo-autorias for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<TipoAutoriaResponse> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipos-autoria/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de tipos de autoría"));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-autoria/admin/{id} - Should return tipo-autoria by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        TipoAutoriaResponse response = new TipoAutoriaResponse(1L, "Test", false);
        when(tipoAutoriaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tipos-autoria/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría encontrado (admin)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tipos-autoria/{id} - Should deactivate tipo-autoria")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/tipos-autoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/tipos-autoria/{id}/activar - Should activate tipo-autoria")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/tipos-autoria/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría activado correctamente"));
    }
}
