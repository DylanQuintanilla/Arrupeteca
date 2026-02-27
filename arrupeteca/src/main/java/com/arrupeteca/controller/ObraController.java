package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.service.ObraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ObraResumen>>> obtenerTodasActivas() {
        return ResponseEntity.ok(ApiResponse.exito("Lista de obras activas", obraService.obtenerTodasActivas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada", obraService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<List<ObraResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false, defaultValue = "titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<ObraResumen> resultados = obraService.busquedaGlobalAvanzada(palabra, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", resultados));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ObraResumen>> crear(@Valid @RequestBody ObraRequest request) {
        ObraResumen nueva = obraService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Obra creada exitosamente", nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody ObraRequest request) {
        ObraResumen actualizada = obraService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Obra actualizada correctamente", actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        obraService.cambiarEstadoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Obra desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        obraService.cambiarEstadoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Obra activada correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<ObraResumen>>> obtenerTodasAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de obras", obraService.obtenerTodas()));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de obra (Admin)", obraService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<List<ObraResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idAutor,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(required = false, defaultValue = "titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<ObraResumen> resultados = obraService.busquedaMaestraAdmin(
                esBorrado, idGenero, idCategoria, idAutor, termino, anioInicio, anioFin, ordenarPor, direccion);

        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda maestra", resultados));
    }
}