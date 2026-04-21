package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import com.arrupeteca.service.NacionalidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/nacionalidades")
public class NacionalidadController {

    private final NacionalidadService nacionalidadService;

    // GET: Obtener todas las activas
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NacionalidadResumen>>> obtenerTodasActivas(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<NacionalidadResumen> pagina = nacionalidadService.obtenerTodasActivas(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de nacionalidades activas", pagina));
    }

    // GET: Obtener una por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> obtenerPorId(@PathVariable Long id) {
        NacionalidadResumen nacionalidad = nacionalidadService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada", nacionalidad));
    }

    // POST: Crear nueva
    @PostMapping
    public ResponseEntity<ApiResponse<NacionalidadResumen>> crear(@Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResumen nuevo = nacionalidadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Nacionalidad creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResumen actualizado = nacionalidadService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODAS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<NacionalidadResumen>>> obtenerTodasAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de nacionalidades", nacionalidadService.obtenerTodas(pageable))
        );
    }
}
