package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.service.ObraService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de obras activas", obraService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada", obraService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaGlobal(
            @RequestParam String palabra, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", obraService.busquedaGlobal(palabra, pageable)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de obras", obraService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada (admin)", obraService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                obraService.busquedaMaestraAdmin(esBorrado, termino, anioInicio, anioFin, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> crear(@Valid @RequestBody ObraRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Obra creada exitosamente", obraService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody ObraRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Obra actualizada correctamente", obraService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        obraService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Obra desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        obraService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Obra activada correctamente"));
    }
}
