package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Libros", description = "Endpoints para la gestión de libros físicos en el catálogo")
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    @Operation(
        summary = "Obtener libros activos paginados",
        description = "Retorna una página de libros registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "isbn") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de libros activos", libroService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de libro activo por ID",
        description = "Busca y retorna el detalle completo de un libro activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado", libroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    @Operation(
        summary = "Búsqueda global de libros",
        description = "Permite buscar libros por término coincidente en ISBN, título de obra, editorial o formato, y filtrar por género y categoría de la obra."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @ParameterObject @PageableDefault(size = 20, sort = "isbn") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", libroService.busquedaGlobal(palabra, idGenero, idCategoria, pageable)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los libros (Admin)",
        description = "Retorna una página de todos los libros en el sistema, incluidos los desactivados (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "isbn") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de libros", libroService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de libro por ID (Admin)",
        description = "Busca el detalle de cualquier libro en el sistema sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado (admin)", libroService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda avanzada de libros (Admin)",
        description = "Búsqueda avanzada de libros mediante múltiples filtros opcionales incluidos género y categoría (sólo admin)."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Long idFormato,
            @RequestParam(required = false) Object grado,
            @RequestParam(required = false) Object ciclo,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @ParameterObject @PageableDefault(size = 20, sort = "isbn") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                libroService.busquedaMaestraAdmin(esBorrado, idObra, idGenero, idCategoria, idEditorial, idFormato,
                        grado, ciclo, termino, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/obra/{idObra}")
    @Operation(
        summary = "Obtener libros por obra",
        description = "Retorna todos los libros físicos vinculados a un identificador de obra."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> buscarPorObraId(
            @PathVariable Long idObra,
            @ParameterObject @PageableDefault(size = 20, sort = "isbn") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Libros de la obra", libroService.buscarPorObraId(idObra, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo libro",
        description = "Crea un libro en el sistema validando unicidad de ISBN y relaciones asociadas."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> crear(@Valid @RequestBody LibroRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Libro creado exitosamente", libroService.crear(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar libro",
        description = "Actualiza los atributos y relaciones de un libro existente controlando concurrencia."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Libro actualizado correctamente", libroService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar libro (Borrado lógico)",
        description = "Desactiva lógicamente un libro en el catálogo público."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        libroService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Libro desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar libro",
        description = "Reactiva un libro desactivado lógicamente."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        libroService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Libro activado correctamente"));
    }
}
