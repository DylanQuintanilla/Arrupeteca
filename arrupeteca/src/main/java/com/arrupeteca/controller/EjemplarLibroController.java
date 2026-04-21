package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.service.EjemplarLibroService;
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
@RequestMapping("/api/v1/ejemplares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EjemplarLibroController {

    private final EjemplarLibroService ejemplarLibroService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<EjemplarLibroResumen> pagina = ejemplarLibroService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de ejemplares activos", pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar encontrado", ejemplarLibroService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<EjemplarLibroResumen> pagina = ejemplarLibroService.busquedaGlobal(palabra, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", pagina));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> crear(@Valid @RequestBody EjemplarLibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Ejemplar creado exitosamente", ejemplarLibroService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EjemplarLibroRequest request) {
        return ResponseEntity.ok(
                ApiResponse.exito("Ejemplar actualizado correctamente", ejemplarLibroService.actualizar(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        ejemplarLibroService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        ejemplarLibroService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar activada correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de ejemplares",
                ejemplarLibroService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<EjemplarLibroResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idLibro,
            @RequestParam(required = false) Long idMueble,
            @RequestParam(required = false) String estadoFisico,
            @RequestParam(required = false) String disponibilidad,
            @RequestParam(required = false) String termino,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<EjemplarLibroResumen> pagina = ejemplarLibroService.busquedaMaestraAdmin(
                esBorrado, idLibro, idMueble, estadoFisico, disponibilidad, termino, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}
