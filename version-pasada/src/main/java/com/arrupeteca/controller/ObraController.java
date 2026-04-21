package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.service.ObraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<ObraResumen> lista = obraService.obtenerTodosActivos(page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Lista de obras activas", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada", obraService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<ObraResumen> lista = obraService.busquedaGlobal(palabra, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ObraResumen>> crear(@Valid @RequestBody ObraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Obra creada exitosamente", obraService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody ObraRequest request) {
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

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de obras",
                obraService.obtenerTodos(page, size, ordenarPor, direccion)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<ObraResumen> lista = obraService.busquedaMaestraAdmin(
                esBorrado, termino, anioInicio, anioFin, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", lista));
    }
}