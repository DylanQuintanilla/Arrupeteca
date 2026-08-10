package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.service.NacionalidadService;
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
@RequestMapping("/api/v1/nacionalidades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Nacionalidades", description = "Endpoints para la gestión de nacionalidades")
public class NacionalidadController {

    private final NacionalidadService nacionalidadService;

    @GetMapping
    @Operation(
        summary = "Obtener nacionalidades activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de nacionalidades activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<NacionalidadResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de nacionalidades activos(as)", nacionalidadService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de nacionalidades",
        description = "Retorna una página de nacionalidades con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<NacionalidadResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de nacionalidades", nacionalidadService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener nacionalidades por ID",
        description = "Busca la información de un(a) nacionalidad por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad encontrado(a)", nacionalidadService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) nacionalidad",
        description = "Crea un(a) nuevo(a) nacionalidad en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> crear(@Valid @RequestBody NacionalidadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Nacionalidad creado(a) exitosamente", nacionalidadService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar nacionalidad",
        description = "Actualiza los campos de un(a) nacionalidad existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<NacionalidadResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody NacionalidadRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad actualizado(a) correctamente",
                nacionalidadService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar nacionalidad (Borrado lógico)",
        description = "Oculta el/la nacionalidad en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar nacionalidad",
        description = "Restaura la visibilidad pública de un(a) nacionalidad previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        nacionalidadService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Nacionalidad activado(a) correctamente"));
    }}
