package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.AutorResumen;
import com.arrupeteca.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AutorResumen>>> obtenerTodosActivos() {
        return ResponseEntity.ok(ApiResponse.exito("Autores activos", autorService.obtenerTodosActivos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado", autorService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<List<AutorResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Integer inicio,
            @RequestParam(required = false) Integer fin,
            @RequestParam(required = false, defaultValue = "nombre") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<AutorResumen> lista = autorService.busquedaGlobalAvanzada(palabra, inicio, fin, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", lista));
    }

    @GetMapping("/vivos-rango")
    public ResponseEntity<ApiResponse<List<AutorResumen>>> autoresVivos(
            @RequestParam Integer inicio,
            @RequestParam Integer fin) {
        return ResponseEntity.ok(ApiResponse.exito("Autores vivos en el rango", autorService.buscarAutoresVivosEnRango(inicio, fin)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AutorResumen>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado", autorService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResumen>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstadoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstadoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<AutorResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo", autorService.obtenerTodos()));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<List<AutorResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(required = false, defaultValue = "nombre") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<AutorResumen> lista = autorService.busquedaMaestraAdmin(
                esBorrado, idNacionalidad, idPais, idGenero, idObra, termino, anioInicio, anioFin, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", lista));
    }
}