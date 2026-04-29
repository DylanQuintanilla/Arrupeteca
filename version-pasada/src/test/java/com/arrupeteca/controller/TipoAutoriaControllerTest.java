package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;
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
import static org.mockito.Mockito.verify;
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
    @DisplayName("GET /api/v1/tipo-autorias - Should return active authorship types")
    void shouldReturnActive() throws Exception {
        Page<TipoAutoriaResumen> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipo-autorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/tipo-autorias/{id} - Should return authorship type by ID")
    void shouldReturnById() throws Exception {
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", false);
        when(tipoAutoriaService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/tipo-autorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("GET /api/v1/tipo-autorias/admin - Should return all authorship types")
    void shouldReturnAllAdmin() throws Exception {
        Page<TipoAutoriaResumen> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipo-autorias/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/tipo-autorias/admin/{id} - Should return any authorship type by ID")
    void shouldReturnByIdAdmin() throws Exception {
        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Test", true);
        when(tipoAutoriaService.obtenerPorId(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/tipo-autorias/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/tipo-autorias - Should create authorship type")
    void shouldCreate() throws Exception {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Nuevo");

        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Nuevo", false);
        when(tipoAutoriaService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/tipo-autorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/tipo-autorias/{id} - Should update authorship type")
    void shouldUpdate() throws Exception {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Actualizado");

        TipoAutoriaResumen resumen = new TipoAutoriaResumen(1L, "Actualizado", false);
        when(tipoAutoriaService.actualizar(any(), any())).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/tipo-autorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tipo-autorias/{id} - Should deactivate authorship type")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/tipo-autorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));

        verify(tipoAutoriaService).cambiarEstado(1L, false);
    }

    @Test
    @DisplayName("PATCH /api/v1/tipo-autorias/{id}/activar - Should activate authorship type")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/tipo-autorias/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));

        verify(tipoAutoriaService).cambiarEstado(1L, true);
    }
}
