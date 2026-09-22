package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.service.PaisService;
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
@RequestMapping("/api/v1/paises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Países", description = "Endpoints para la gestión de países")
public class PaisController {

    private final PaisService paisService;

    @GetMapping
    @Operation(
        summary = "Obtener países activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de países activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<PaisResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de países activos(as)", paisService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de países",
        description = "Retorna una página de países con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<PaisResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de países", paisService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener países por ID",
        description = "Busca la información de un(a) pais por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Pais encontrado(a)", paisService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) pais",
        description = "Crea un(a) nuevo(a) pais en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> crear(@Valid @RequestBody PaisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Pais creado(a) exitosamente", paisService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar pais",
        description = "Actualiza los campos de un(a) pais existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<PaisResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody PaisRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Pais actualizado(a) correctamente",
                paisService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar pais (Borrado lógico)",
        description = "Oculta el/la pais en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        paisService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Pais desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar pais",
        description = "Restaura la visibilidad pública de un(a) pais previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        paisService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Pais activado(a) correctamente"));
    }}
