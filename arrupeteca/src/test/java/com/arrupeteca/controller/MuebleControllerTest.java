package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.service.MuebleService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MuebleController.class)
class MuebleControllerTest extends BaseControllerTest {

    @MockitoBean
    private MuebleService muebleService;

    @Test
    @DisplayName("GET /api/v1/muebles - Should return active muebles")
    void shouldReturnActive() throws Exception {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(muebleService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/muebles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/{id} - Should return mueble by ID")
    void shouldReturnById() throws Exception {
        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Mueble 1", false, "admin", null, "admin", null, null);
        when(muebleService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/muebles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Mueble 1"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/global - Should return global search results")
    void shouldReturnGlobalSearch() throws Exception {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(muebleService.busquedaGlobal(eq("palabra"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/muebles/global")
                        .param("palabra", "palabra"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin - Should return all muebles")
    void shouldReturnAllAdmin() throws Exception {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(muebleService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/muebles/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin/{id} - Should return any mueble by ID")
    void shouldReturnByIdAdmin() throws Exception {
        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Mueble 1", true, "admin", null, "admin", null, null);
        when(muebleService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/muebles/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Mueble 1"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin/busqueda-maestra - Should return master search results")
    void shouldReturnMasterSearch() throws Exception {
        Page<MuebleResumen> page = new PageImpl<>(List.of());
        when(muebleService.busquedaMaestraAdmin(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/muebles/admin/busqueda-maestra")
                        .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/muebles - Should create mueble")
    void shouldCreate() throws Exception {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Nuevo");
        request.setIdSalon(1L);

        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Nuevo", false, "admin", null, "admin", null, null);
        when(muebleService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/muebles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/muebles/{id} - Should update mueble")
    void shouldUpdate() throws Exception {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Actualizado");
        request.setIdSalon(1L);

        MuebleDetalleResponse detail = new MuebleDetalleResponse(1L, "Actualizado", false, "admin", null, "admin", null, null);
        when(muebleService.actualizar(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(put("/api/v1/muebles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/muebles/{id} - Should deactivate mueble")
    void shouldDeactivate() throws Exception {
        doNothing().when(muebleService).cambiarEstado(1L, false);

        mockMvc.perform(delete("/api/v1/muebles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/muebles/{id}/activar - Should activate mueble")
    void shouldActivate() throws Exception {
        doNothing().when(muebleService).cambiarEstado(1L, true);

        mockMvc.perform(patch("/api/v1/muebles/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
