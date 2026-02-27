package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LibroResumen>>> obtenerTodosActivos() {
        return ResponseEntity.ok(ApiResponse.exito("Lista de libros activos", libroService.obtenerTodosActivos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado", libroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<List<LibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false, defaultValue = "obra.titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<LibroResumen> resultados = libroService.busquedaGlobalAvanzada(palabra, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", resultados));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LibroResumen>> crear(@Valid @RequestBody LibroRequest request) {
        LibroResumen nuevo = libroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Libro creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> actualizar(@PathVariable Long id,
                                                                @Valid @RequestBody LibroRequest request) {
        LibroResumen actualizado = libroService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Libro actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        libroService.cambiarBorradoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Libro desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        libroService.cambiarBorradoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Libro activado correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<LibroResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de libros", libroService.obtenerTodos()));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de libro (Admin)", libroService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<List<LibroResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Long idFormato,
            @RequestParam(required = false) Grado grado,
            @RequestParam(required = false) Ciclo ciclo,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(required = false, defaultValue = "obra.titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<LibroResumen> resultados = libroService.busquedaMaestraAdmin(
                esBorrado, idObra, idEditorial, idFormato, grado, ciclo, termino, anioInicio, anioFin, ordenarPor, direccion);

        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda maestra", resultados));
    }
}