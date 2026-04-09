package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.service.ObraService;
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
@RequestMapping("/api/v1/obras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ObraController {

    private final ObraService obraService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ) {
        Page<ObraResumen> pagina = obraService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de obras activas", pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Obra encontrada", obraService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ) {
        Page<ObraResumen> pagina = obraService.busquedaGlobal(palabra, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", pagina));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ObraResumen>> crear(@Valid @RequestBody ObraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Obra creada exitosamente", obraService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ObraResumen>> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody ObraRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Obra actualizada correctamente", obraService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        obraService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Obra desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        obraService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Obra activada correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de obras",
                obraService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<ObraResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable
    ) {
        Page<ObraResumen> pagina = obraService.busquedaMaestraAdmin(
                esBorrado, termino, anioInicio, anioFin, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}
