package com.arrupeteca.controller;

import com.arrupeteca.service.NacionalidadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NacionalidadController.class)
class NacionalidadControllerTest extends BaseControllerTest {
    @MockitoBean private NacionalidadService service;

    @Test
    @DisplayName("GET /api/v1/nacionalidades - Should return active nationalities")
    void shouldReturnActive() throws Exception {
        when(service.obtenerTodasActivas(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/v1/nacionalidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}
