package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.service.EjemplarLibroService;
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
@RequestMapping("/api/v1/ejemplares")
@RequiredArgsConstructor
@Tag(name = "Ejemplares de Libros", description = "Endpoints para la gestión de ejemplares físicos de libros")
public class EjemplarLibroController {

    private final EjemplarLibroService ejemplarLibroService;

    @GetMapping
    @Operation(
        summary = "Obtener ejemplares activos",
        description = "Retorna una página de ejemplares que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de ejemplares activos", ejemplarLibroService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de ejemplar activo por ID",
        description = "Retorna el detalle completo de un ejemplar de libro activo."
    )
    public ResponseEntity<ApiResponse<EjemplarLibroDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar encontrado", ejemplarLibroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    @Operation(
        summary = "Búsqueda global de ejemplares",
        description = "Busca ejemplares por coincidencias en el ISBN del libro, el título de la obra o el comentario del ejemplar, filtrando opcionalmente por género y categoría."
    )
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", ejemplarLibroService.busquedaGlobal(palabra, idGenero, idCategoria, pageable)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los ejemplares (Admin)",
        description = "Retorna una lista paginada de todos los ejemplares sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de ejemplares", ejemplarLibroService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de ejemplar por ID (Admin)",
        description = "Retorna el detalle completo de cualquier ejemplar registrado en la base de datos (solo admin)."
    )
    public ResponseEntity<ApiResponse<EjemplarLibroDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar encontrado (admin)", ejemplarLibroService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda avanzada multi-filtro (Admin)",
        description = "Búsqueda avanzada de ejemplares usando filtros por estado, libro, género, categoría, mueble, estado físico y disponibilidad (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idLibro,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idMueble,
            @RequestParam(required = false) Object estadoFisico,
            @RequestParam(required = false) Object disponibilidad,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                ejemplarLibroService.busquedaMaestraAdmin(esBorrado, idLibro, idGenero, idCategoria, idMueble,
                        estadoFisico, disponibilidad, termino, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo ejemplar de libro",
        description = "Vincula y registra un nuevo ejemplar físico a un libro y un mueble de ubicación."
    )
    public ResponseEntity<ApiResponse<EjemplarLibroDetalleResponse>> crear(@Valid @RequestBody EjemplarLibroRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Ejemplar creado exitosamente",
                ejemplarLibroService.crear(request)), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar ejemplar de libro",
        description = "Actualiza los atributos de un ejemplar físico (incluidos libro, mueble, estado físico y disponibilidad) controlando bloqueo optimista."
    )
    public ResponseEntity<ApiResponse<EjemplarLibroDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EjemplarLibroRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar actualizado correctamente",
                ejemplarLibroService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar ejemplar (Borrado lógico)",
        description = "Desactiva lógicamente un ejemplar físico en el catálogo público."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        ejemplarLibroService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar ejemplar",
        description = "Reactiva un ejemplar que había sido desactivado lógicamente."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        ejemplarLibroService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar activado correctamente"));
    }
}
