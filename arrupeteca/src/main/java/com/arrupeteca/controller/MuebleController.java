package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.service.MuebleService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Muebles", description = "Endpoints para la gestión de muebles")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    @Operation(
        summary = "Obtener muebles activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de muebles activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<MuebleResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos(as)", muebleService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de muebles",
        description = "Retorna una página de muebles con soporte para filtros por idSalon, nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<MuebleResponse>>> obtenerTodos(
            @RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de muebles", muebleService.obtenerTodos(idSalon, nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener muebles por ID",
        description = "Busca la información de un(a) mueble por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado(a)", muebleService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) mueble",
        description = "Crea un(a) nuevo(a) mueble en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> crear(@Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Mueble creado(a) exitosamente", muebleService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar mueble",
        description = "Actualiza los campos de un(a) mueble existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<MuebleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado(a) correctamente",
                muebleService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar mueble (Borrado lógico)",
        description = "Oculta el/la mueble en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar mueble",
        description = "Restaura la visibilidad pública de un(a) mueble previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado(a) correctamente"));
    }
}
