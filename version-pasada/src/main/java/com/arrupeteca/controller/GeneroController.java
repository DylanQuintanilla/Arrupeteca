package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.GeneroDetalleResponse;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.service.GeneroService;
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
@RequestMapping("/api/v1/generos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GeneroController {

    private final GeneroService generoService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GeneroResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de géneros activos", generoService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Género encontrado", generoService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<GeneroResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de géneros", generoService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<GeneroDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Género encontrado (admin)", generoService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GeneroDetalleResponse>> crear(@Valid @RequestBody GeneroRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Género creado exitosamente", generoService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GeneroDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody GeneroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Género actualizado correctamente", generoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        generoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Género desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        generoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Género activado correctamente"));
    }
}
