package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.service.EditorialService;
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

@WebMvcTest(EditorialController.class)
class EditorialControllerTest extends BaseControllerTest {

    @MockitoBean
    private EditorialService editorialService;

    @Test
    @DisplayName("GET /api/v1/editoriales - Should return active publisher entries")
    void shouldReturnActive() throws Exception {
        when(editorialService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/editoriales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/{id} - Should return publisher by ID")
    void shouldReturnById() throws Exception {
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);
        when(editorialService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/editoriales/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/editoriales - Should create publisher")
    void shouldCreate() throws Exception {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Nuevo")
                .correo("nuevo@editorial.com")
                .telefono("1234-5678")
                .personaContacto("Contacto")
                .build();
        EditorialResponse response = new EditorialResponse(1L, "Nuevo", null, "nuevo@editorial.com", "1234-5678", "Contacto", false);

        when(editorialService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/editoriales/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/editoriales/{id} - Should update publisher")
    void shouldUpdate() throws Exception {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Actualizado")
                .correo("actualizado@editorial.com")
                .telefono("1234-5678")
                .personaContacto("Contacto")
                .build();
        EditorialResponse response = new EditorialResponse(1L, "Actualizado", null, "actualizado@editorial.com", "1234-5678", "Contacto", false);

        when(editorialService.actualizar(any(Long.class), any(EditorialRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/editoriales/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/admin - Should return all publishers for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<EditorialResponse> page = new PageImpl<>(List.of());
        when(editorialService.obtenerTodos(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/editoriales/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de editoriales"));
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/admin/{id} - Should return publisher by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        EditorialResponse response = new EditorialResponse(1L, "Test", "Dir", "correo@test.com", "1234-5678", "Juan", false);
        when(editorialService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/editoriales/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/editoriales/{id} - Should deactivate publisher")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/editoriales/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/editoriales/{id}/activar - Should activate publisher")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/editoriales/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial activado(a) correctamente"));
    }
}
