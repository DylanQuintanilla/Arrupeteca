# Est├índar de Desarrollo: Controladores Spring Boot para Cat├ílogos (Arrupeteca)
---

## 1. Configuraci├│n del Controlador REST

Todo controlador de cat├ílogo debe seguir las siguientes reglas de anotaci├│n y configuraci├│n:

*   **`@RestController`**: Registra la clase como un controlador de tipo REST.
*   **`@RequestMapping("/api/v1/[recurso]")`**: Ruta plural en min├║sculas (ej. `/api/v1/categorias`).
*   **`@RequiredArgsConstructor`**: Inyecci├│n de colaboradores de manera autom├ítica por constructor (los atributos inyectados deben ser `private final`).
*   **`@CrossOrigin(origins = "*")`**: Habilitar CORS para desarrollo.
*   **`@Tag(name = "[Recurso]", description = "...")`**: Agrupaci├│n e identificaci├│n obligatoria para OpenAPI Swagger.

---

## 2. Sem├íntica HTTP de Endpoints de Cat├ílogo

Los endpoints deben respetar la sem├íntica HTTP de manera estricta y retornar la misma estructura unificada de salida `[Entidad]Response`:

| M├⌐todo HTTP | Ruta relativa | Acci├│n | Retorno exitoso |
| :--- | :--- | :--- | :--- |
| **GET** | `/` | Obtener listado activo para dropdowns y autocompletado (p├║blico / soporte a filtro opcional por `nombre`). | `200 OK` $\rightarrow$ `ApiResponse<List<[Entidad]Response>>` |
| **GET** | `/admin/all` | Obtener tabla de administraci├│n (paginado con filtros `nombre` y `borradoLogico`). | `200 OK` $\rightarrow$ `ApiResponse<Page<[Entidad]Response>>` |
| **GET** | `/admin/{id}` | Obtener registro por ID (activo o inactivo). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **POST** | `/admin` | Crear nuevo registro. | `201 Created` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **PUT** | `/admin/{id}` | Actualizar completo (Full update). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **DELETE** | `/admin/{id}` | Desactivar (Borrado l├│gico). | `200 OK` $\rightarrow$ `ApiResponse<Void>` |
| **PATCH** | `/admin/{id}/activar` | Activar registro desactivado. | `200 OK` $\rightarrow$ `ApiResponse<Void>` |

*Para la respuesta exitosa del POST, utilizar la API fluida:*
```java
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("...", resultado));
```

---

## 3. Autodocumentaci├│n OpenAPI y Paginaci├│n

*   **Anotar con `@Operation(summary = "...", description = "...")`**: Obligatorio en todos los m├⌐todos para documentar la acci├│n.
*   **Formatear anotaciones**: Mantener las anotaciones OpenAPI multil├¡nea para cumplir estrictamente con el l├¡mite de caracteres.
*   **Documentaci├│n de paginaci├│n**: En `/admin/all`, el par├ímetro `Pageable` debe ir decorado con `@ParameterObject` y `@PageableDefault(size = 20, sort = "nombre")`.

---

## 4. Plantilla Maestra de Referencia (CategoriaController)

```java
package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categor├¡as", description = "Endpoints para la gesti├│n de categor├¡as literarias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener categor├¡as activas para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de categor├¡as activas, con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de categor├¡as activas", categoriaService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administraci├│n de categor├¡as",
        description = "Retorna una p├ígina de categor├¡as con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de categor├¡as", categoriaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener categor├¡a por ID",
        description = "Busca la informaci├│n de una categor├¡a por su ID sin importar su estado l├│gico."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categor├¡a encontrada", categoriaService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nueva categor├¡a",
        description = "Crea una nueva categor├¡a en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categor├¡a creada exitosamente", categoriaService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar categor├¡a",
        description = "Actualiza los campos de una categor├¡a existente mediante una edici├│n completa."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Categor├¡a actualizada correctamente",
                categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar categor├¡a (Borrado l├│gico)",
        description = "Oculta la categor├¡a en el cat├ílogo p├║blico cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categor├¡a desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar categor├¡a",
        description = "Restaura la visibilidad p├║blica de una categor├¡a previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categor├¡a activada correctamente"));
    }
}
