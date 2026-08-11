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
        summary = "Búsqueda global de libros activos",
        description = "Permite buscar libros activos por término (ISBN o título de obra), autor, género, categoría, editorial y rango de años de publicación."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Long idAutor,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @ParameterObject @PageableDefault(size = 20, sort = "obra.titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda pública de libros activos",
                libroService.busquedaGlobal(termino, idAutor, idGenero, idCategoria, idEditorial, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de libro activo por ID",
        description = "Busca y retorna el detalle completo de un libro activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> busquedaGlobalPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro activo encontrado", libroService.busquedaGlobalPorId(id)));
    }

    @GetMapping("/obra/{idObra}")
    @Operation(
        summary = "Obtener libros por obra",
        description = "Retorna todos los libros físicos vinculados a un identificador de obra."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> buscarPorObraId(
            @PathVariable Long idObra,
            @ParameterObject @PageableDefault(size = 20, sort = "obra.titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Libros de la obra", libroService.buscarPorObraId(idObra, pageable)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Búsqueda de administración de libros",
        description = "Búsqueda avanzada de libros mediante múltiples filtros opcionales (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<LibroResumen>>> busquedaAdmin(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) Long idAutor,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idEtiqueta,
            @RequestParam(required = false) Long idEditorial,
            @RequestParam(required = false) Long idFormato,
            @RequestParam(required = false) Long idGrado,
            @RequestParam(required = false) Long idCiclo,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @ParameterObject @PageableDefault(size = 20, sort = "obra.titulo") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda de administración de libros",
                libroService.busquedaAdmin(borradoLogico, idObra, idAutor, idGenero, idCategoria, idEtiqueta, idEditorial, idFormato,
                        idGrado, idCiclo, termino, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de libro por ID (Admin)",
        description = "Busca el detalle de cualquier libro en el sistema sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Libro encontrado (admin)", libroService.busquedaAdminPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo libro (Admin)",
        description = "Crea un libro en el sistema validando unicidad de ISBN y relaciones asociadas (solo admin)."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> crear(@Valid @RequestBody LibroRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Libro creado exitosamente", libroService.crear(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar libro (Admin)",
        description = "Actualiza los atributos y relaciones de un libro existente controlando concurrencia (solo admin)."
    )
    public ResponseEntity<ApiResponse<LibroDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Libro actualizado correctamente", libroService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar libro (Admin)",
        description = "Desactiva lógicamente un libro en el catálogo público (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        libroService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Libro desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar libro (Admin)",
        description = "Reactiva un libro desactivado lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        libroService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Libro activado correctamente"));
    }
}

