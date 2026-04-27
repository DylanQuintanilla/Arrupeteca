package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;
import com.arrupeteca.service.TipoAutoriaService;
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
@RequestMapping("/api/v1/tipo-autorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoAutoriaController {

    private final TipoAutoriaService tipoAutoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de autoría activos", tipoAutoriaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría encontrado", tipoAutoriaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de tipos de autoría", tipoAutoriaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría encontrado (admin)", tipoAutoriaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> crear(@Valid @RequestBody TipoAutoriaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Tipo de autoría creado exitosamente",
                tipoAutoriaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TipoAutoriaResumen>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoAutoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría actualizado correctamente",
                tipoAutoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Tipo de autoría activado correctamente"));
    }
}
