package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.service.MuebleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<MuebleResumen> lista = muebleService.obtenerTodosActivos(page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado", muebleService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<MuebleResumen> lista = muebleService.busquedaGlobal(palabra, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MuebleResumen>> crear(@Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Mueble creado exitosamente", muebleService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado correctamente", muebleService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de muebles",
                muebleService.obtenerTodos(page, size, ordenarPor, direccion)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) Long idPlanta,
            @RequestParam(required = false) Long idEdificio,
            @RequestParam(required = false) String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<MuebleResumen> lista = muebleService.busquedaMaestraAdmin(
                esBorrado, idSalon, idPlanta, idEdificio, termino, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", lista));
    }
}