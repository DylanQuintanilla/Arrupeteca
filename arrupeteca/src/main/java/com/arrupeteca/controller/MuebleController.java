package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.service.MuebleService;
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
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        Page<MuebleResumen> pagina = muebleService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos", pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado", muebleService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        Page<MuebleResumen> pagina = muebleService.busquedaGlobal(palabra, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", pagina));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MuebleResumen>> crear(@Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Mueble creado exitosamente", muebleService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado correctamente", muebleService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de muebles",
                muebleService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) Long idPlanta,
            @RequestParam(required = false) Long idEdificio,
            @RequestParam(required = false) String termino,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        Page<MuebleResumen> pagina = muebleService.busquedaMaestraAdmin(
                esBorrado, idSalon, idPlanta, idEdificio, termino, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}
