package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import com.arrupeteca.service.NacionalidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nacionalidades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NacionalidadController {

    private final NacionalidadService nacionalidadService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<List<NacionalidadResumen>>> obtenerTodosActivos() {
        List<NacionalidadResumen> lista = nacionalidadService.obtenerTodasActivas();
        return ResponseEntity.ok(ApiResponse.exito("Lista de nacinalidades activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> obtenerPorId(@PathVariable Long id) {
        NacionalidadResumen Nacionalidad = nacionalidadService.obtenerPorIdActiva(id);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada", Nacionalidad));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<NacionalidadResumen>> crear(@Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResumen nuevo = nacionalidadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Nacionalidad creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody NacionalidadRequest request) {
        NacionalidadResumen actualizado = nacionalidadService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        nacionalidadService.cambiarEstadoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        nacionalidadService.cambiarEstadoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<NacionalidadResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de nacionalidades", nacionalidadService.obtenerTodas()));
    }
}