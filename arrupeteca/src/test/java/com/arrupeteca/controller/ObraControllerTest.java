package com.arrupeteca.controller;

import java.util.List;
import java.util.Set;

import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.service.ObraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObraController.class)
class ObraControllerTest extends BaseControllerTest {

    @MockitoBean
    private ObraService obraService;

    @Test
    @DisplayName("GET /api/v1/obras - Should return active obras page")
    void obtenerTodosActivos() throws Exception {
        when(obraService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/obras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de obras activas"));
    }

    @Test
    @DisplayName("GET /api/v1/obras/{id} - Should return obra when active")
    void obtenerPorId() throws Exception {
        when(obraService.obtenerPorIdActivo(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/obras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Obra encontrada"));
    }

    @Test
    @DisplayName("GET /api/v1/obras/busqueda - Should return search results")
    void busquedaGlobal() throws Exception {
        when(obraService.busquedaGlobal(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/obras/busqueda").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados de búsqueda"));
    }

    @Test
    @DisplayName("POST /api/v1/obras - Should create obra")
    void crear() throws Exception {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setResumen("Resumen de la obra");
        request.setIdGeneros(Set.of(1L));
        request.setIdCategorias(Set.of(1L));
        request.setAutores(Set.of(new ObraAutorRequest(1L, 1L)));

        when(obraService.crear(any(ObraRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/obras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Obra creada exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/obras/{id} - Should update obra")
    void actualizar() throws Exception {
        ObraRequest request = new ObraRequest();
        request.setTitulo("Obra 1");
        request.setResumen("Resumen de la obra");
        request.setIdGeneros(Set.of(1L));
        request.setIdCategorias(Set.of(1L));
        request.setAutores(Set.of(new ObraAutorRequest(1L, 1L)));

        when(obraService.actualizar(eq(1L), any(ObraRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/obras/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Obra actualizada correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/obras/{id} - Should deactivate obra")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/obras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Obra desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/obras/{id}/activar - Should activate obra")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/obras/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Obra activada correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin - Should return all obras for admin")
    void obtenerTodosAdmin() throws Exception {
        when(obraService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/obras/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de obras"));
    }

    @Test
    @DisplayName("GET /api/v1/obras/admin/busqueda-maestra - Should return master search results")
    void busquedaMaestra() throws Exception {
        when(obraService.busquedaMaestraAdmin(anyBoolean(), anyString(), anyInt(), anyInt(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/obras/admin/busqueda-maestra")
                .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"));
    }
}
