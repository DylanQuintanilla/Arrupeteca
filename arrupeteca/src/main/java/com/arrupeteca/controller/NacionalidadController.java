package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.service.NacionalidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/nacionalidades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NacionalidadController {

    private final NacionalidadService nacionalidadService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NacionalidadResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de nacionalidades activas", nacionalidadService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada", nacionalidadService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<NacionalidadResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de nacionalidades", nacionalidadService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrada (admin)", nacionalidadService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NacionalidadResumen>> crear(@Valid @RequestBody NacionalidadRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Nacionalidad creada exitosamente",
                nacionalidadService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NacionalidadResumen>> actualizar(
            @PathVariable Long id, @Valid @RequestBody NacionalidadRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad actualizada correctamente", nacionalidadService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad activada correctamente"));
    }
}
