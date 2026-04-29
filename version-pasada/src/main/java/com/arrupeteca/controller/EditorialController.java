package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.EditorialDetalleResponse;
import com.arrupeteca.dto.response.summary.EditorialResumen;
import com.arrupeteca.service.EditorialService;
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
@RequestMapping("/api/v1/editoriales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EditorialController {

    private final EditorialService editorialService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de editoriales activas", editorialService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorialDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada", editorialService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de editoriales", editorialService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<EditorialDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada (admin)", editorialService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                editorialService.busquedaMaestraAdmin(esBorrado, termino, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EditorialDetalleResponse>> crear(@Valid @RequestBody EditorialRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Editorial creada exitosamente",
                editorialService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorialDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EditorialRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial actualizada correctamente",
                editorialService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Editorial desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Editorial activada correctamente"));
    }
}
