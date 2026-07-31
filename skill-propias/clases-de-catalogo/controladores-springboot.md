# Estándar de Desarrollo: Controladores Spring Boot para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de la capa de **Controladores REST** en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Ciclo`, `Disponibilidad`, `EstadoFisico`, `Etiqueta`, `Formato`, `Genero`, `Grado`, `Idioma`, `Nacionalidad`, `Pais`, `Parentesco`, `TipoAutoria`, `TipoUsuario`).

---

## 1. Configuración del Controlador REST

Todo controlador de catálogo debe seguir las siguientes reglas de anotación y configuración:

*   **`@RestController`**: Registra la clase como un controlador de tipo REST.
*   **`@RequestMapping("/api/v1/[recurso]")`**: Ruta plural en minúsculas (ej. `/api/v1/categorias`).
*   **`@RequiredArgsConstructor`**: Inyección de colaboradores de manera automática por constructor (los atributos inyectados deben ser `private final`).
*   **`@CrossOrigin(origins = "*")`**: Habilitar CORS para desarrollo.
*   **`@Tag(name = "[Recurso]", description = "...")`**: Agrupación e identificación obligatoria para OpenAPI Swagger.

---

## 2. Semántica HTTP de Endpoints de Catálogo

Los endpoints deben respetar la semántica HTTP de manera estricta y retornar la misma estructura unificada de salida `[Entidad]Response`:

| Método HTTP | Ruta relativa | Acción | Retorno exitoso |
| :--- | :--- | :--- | :--- |
| **GET** | `/` | Obtener listado activo para dropdowns/selects (sin paginación). | `200 OK` $\rightarrow$ `ApiResponse<List<[Entidad]Response>>` |
| **GET** | `/all` | Obtener tabla de administración (paginado con filtros `nombre` y `borradoLogico`). | `200 OK` $\rightarrow$ `ApiResponse<Page<[Entidad]Response>>` |
| **GET** | `/{id}` | Obtener registro por ID (activo o inactivo). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **POST** | `/` | Crear nuevo registro. | `201 Created` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **PUT** | `/{id}` | Actualizar completo (Full update). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **DELETE** | `/{id}` | Desactivar (Borrado lógico). | `200 OK` $\rightarrow$ `ApiResponse<Void>` |
| **PATCH** | `/{id}/activar` | Activar registro desactivado. | `200 OK` $\rightarrow$ `ApiResponse<Void>` |

*Para la respuesta exitosa del POST, utilizar la API fluida:*
```java
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("...", resultado));
```

---

## 3. Autodocumentación OpenAPI y Paginación

*   **Anotar con `@Operation(summary = "...", description = "...")`**: Obligatorio en todos los métodos para documentar la acción.
*   **Formatear anotaciones**: Mantener las anotaciones OpenAPI multilínea para cumplir estrictamente con el límite de caracteres.
*   **Documentación de paginación**: En `/all`, el parámetro `Pageable` debe ir decorado con `@ParameterObject` y `@PageableDefault(size = 20, sort = "nombre")`.

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
@Tag(name = "Categorías", description = "Endpoints para la gestión de categorías literarias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener categorías activas para dropdowns",
        description = "Retorna una lista simple no paginada de categorías activas para selects y filtros."
    )
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> obtenerTodosActivos() {
        return ResponseEntity.ok(ApiResponse.exito("Lista de categorías activas", categoriaService.obtenerTodosActivos()));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Obtener tabla de administración de categorías",
        description = "Retorna una página de categorías con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de categorías", categoriaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener categoría por ID",
        description = "Busca la información de una categoría por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada", categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva categoría",
        description = "Crea una nueva categoría en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categoría creada exitosamente", categoriaService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar categoría",
        description = "Actualiza los campos de una categoría existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría actualizada correctamente",
                categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar categoría (Borrado lógico)",
        description = "Oculta la categoría en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoría desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar categoría",
        description = "Restaura la visibilidad pública de una categoría previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoría activada correctamente"));
    }
}
