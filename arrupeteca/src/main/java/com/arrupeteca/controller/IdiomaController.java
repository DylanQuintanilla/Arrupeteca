package com.arrupeteca.controller;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.service.IdiomaService;
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
@RequestMapping("/api/v1/idiomas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IdiomaController {

    private final IdiomaService idiomaService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<IdiomaResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de idiomas activos", idiomaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IdiomaDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado", idiomaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<IdiomaResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de idiomas", idiomaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<IdiomaDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado (admin)", idiomaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IdiomaDetalleResponse>> crear(@Valid @RequestBody IdiomaRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Idioma creado exitosamente", idiomaService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IdiomaDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody IdiomaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Idioma actualizado correctamente", idiomaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Idioma desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Idioma activado correctamente"));
    }
}
