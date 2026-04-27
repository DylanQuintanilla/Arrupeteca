package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.service.LibroService;
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
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de libros activos", libroService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado", libroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaGlobal(
            @RequestParam String palabra, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", libroService.busquedaGlobal(palabra, pageable)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de libros", libroService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado (admin)", libroService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Long idFormato,
            @RequestParam(required = false) Object grado,
            @RequestParam(required = false) Object ciclo,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                libroService.busquedaMaestraAdmin(esBorrado, idObra, idEditorial, idFormato,
                        grado, ciclo, termino, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/obra/{idObra}")
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> buscarPorObraId(@PathVariable Long idObra, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Libros de la obra", libroService.buscarPorObraId(idObra, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> crear(@Valid @RequestBody LibroRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Libro creado exitosamente", libroService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
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
}
