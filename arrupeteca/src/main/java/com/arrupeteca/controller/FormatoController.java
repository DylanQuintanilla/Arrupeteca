package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.FormatoDetalleResponse;
import com.arrupeteca.dto.response.summary.FormatoResumen;
import com.arrupeteca.service.FormatoService;
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
@RequestMapping("/api/v1/formatos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormatoController {

    private final FormatoService formatoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FormatoResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de formatos activos", formatoService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado", formatoService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<FormatoResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de formatos", formatoService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<FormatoDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado (admin)", formatoService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FormatoDetalleResponse>> crear(@Valid @RequestBody FormatoRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Formato creado exitosamente",
                formatoService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody FormatoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Formato actualizado correctamente",
                formatoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Formato desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Formato activado correctamente"));
    }
}
