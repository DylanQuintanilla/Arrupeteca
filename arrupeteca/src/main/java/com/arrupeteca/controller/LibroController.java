package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "isbn") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<LibroResumen> lista = libroService.obtenerTodosActivos(page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Lista de libros activos", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado", libroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "isbn") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<LibroResumen> lista = libroService.busquedaGlobal(palabra, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LibroResumen>> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Libro creado exitosamente", libroService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> actualizar(@PathVariable Long id,
                                                                @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Libro actualizado correctamente", libroService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        libroService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Libro desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        libroService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Libro activado correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "isbn") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de libros",
                libroService.obtenerTodos(page, size, ordenarPor, direccion)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Long idFormato,
            @RequestParam(required = false) String grado,
            @RequestParam(required = false) String ciclo,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "isbn") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<LibroResumen> lista = libroService.busquedaMaestraAdmin(
                esBorrado, idObra, idEditorial, idFormato, grado, ciclo,
                termino, anioInicio, anioFin, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", lista));
    }
}