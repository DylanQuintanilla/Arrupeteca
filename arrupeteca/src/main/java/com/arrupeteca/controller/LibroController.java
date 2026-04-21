package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "isbn") Pageable pageable
    ) {
        Page<LibroResumen> pagina = libroService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de libros activos", pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado", libroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @PageableDefault(size = 10, sort = "isbn") Pageable pageable
    ) {
        Page<LibroResumen> pagina = libroService.busquedaGlobal(palabra, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", pagina));
    }

    @GetMapping("/por-obra/{idObra}")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerPorObra(
            @PathVariable Long idObra,
            @PageableDefault(size = 10, sort = "isbn") Pageable pageable
    ) {
        Page<LibroResumen> libros = libroService.obtenerPorObra(idObra, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Libros de la obra", libros));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LibroResumen>> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Libro creado exitosamente", libroService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroResumen>> actualizar(
            @PathVariable Long id,
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
            @PageableDefault(size = 10, sort = "isbn") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de libros",
                libroService.obtenerTodos(pageable)));
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
            @PageableDefault(size = 10, sort = "isbn") Pageable pageable
    ) {
        Page<LibroResumen> pagina = libroService.busquedaMaestraAdmin(
                esBorrado, idObra, idEditorial, idFormato, grado, ciclo,
                termino, anioInicio, anioFin, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}
