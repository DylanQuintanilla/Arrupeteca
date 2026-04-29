package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.summary.PaisResumen;
import com.arrupeteca.service.PaisService;
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
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaisResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de países activos", paisService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("País encontrado", paisService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<PaisResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de países", paisService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("País encontrado (admin)", paisService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaisResumen>> crear(@Valid @RequestBody PaisRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("País creado exitosamente", paisService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaisResumen>> actualizar(@PathVariable Long id, @Valid @RequestBody PaisRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("País actualizado correctamente", paisService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        paisService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("País desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        paisService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("País activado correctamente"));
    }
}
