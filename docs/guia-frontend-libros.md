# Guía de Integración Frontend: Módulo de Libros Físicos (Arrupeteca API)

Esta guía documenta detalladamente cómo consumir los endpoints de la API de **Libros** de Arrupeteca desde un agente o desarrollador Frontend. Cubre autenticación, endpoints públicos y administrativos, reglas de validación, manejo de parches parciales (`JsonNullable`), control de concurrencia optimista y gestión del estado lógico.

---

## 1. Autenticación y Seguridad

Todos los endpoints del módulo de libros requieren autenticación HTTP mediante Token JWT en el encabezado `Authorization`:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Roles y Control de Acceso
* **Público Anónimo (Sin Token)**: La búsqueda global de catálogo (`busquedaGlobal`: `GET /api/v1/libros`, `GET /api/v1/autores`), la consulta de detalle activo por ID (`busquedaGlobalPorId`: `GET /api/v1/libros/{id}`, `GET /api/v1/autores/{id}`) y las listas para dropdowns de catálogos (`GET /api/v1/editoriales`, `GET /api/v1/generos`, `GET /api/v1/categorias`, `GET /api/v1/formatos`, `GET /api/v1/idiomas`) son **100% públicas** y no exigen iniciar sesión ni enviar token JWT.
* **Rol `ADMIN`**: Requerido exclusivamente para todas las operaciones administrativas bajo la ruta `/admin` (`/api/v1/*/admin/*`). Intentar acceder a estas rutas sin rol `ADMIN` retornará `403 Forbidden`.

---

## 2. Directorio de Endpoints y Utilidad

Base Path: `/api/v1/libros`

| Método | Endpoint | Rol Requerido | Propósito / Utilidad Frontend |
|---|---|---|---|
| `GET` | `/api/v1/libros` | **Público (Sin Token)** | **Búsqueda pública en el catálogo (`busquedaGlobal`)**. Permite a cualquier visitante sin iniciar sesión explorar libros activos (`borradoLogico = false`) filtrando por término (ISBN/título), autor, género, categoría, editorial, idioma y años. |
| `GET` | `/api/v1/libros/{id}` | **Público (Sin Token)** | **Ficha técnica / Detalle activo por ID (`busquedaGlobalPorId`)**. Retorna la información extendida de un libro activo a cualquier visitante sin requerir token JWT. |
| `GET` | `/api/v1/libros/obra/{idObra}` | **Público (Sin Token)** | **Filtrar por Obra**. Obtiene todos los libros físicos/ediciones asociados a una obra literaria específica. |
| `GET` | `/api/v1/libros/admin/all` | `ADMIN` | **Búsqueda de administración**. Permite auditar libros activos e inactivos mediante múltiples filtros (incluyendo filtro por `borradoLogico`, etiqueta, formato, grado, ciclo). |
| `GET` | `/api/v1/libros/admin/{id}` | `ADMIN` | **Detalle administrativo**. Retorna el detalle completo de un libro sin importar su estado de borrado lógico. |
| `POST` | `/api/v1/libros/admin` | `ADMIN` | **Crear libro**. Registra una nueva edición de libro físico en el catálogo. Valida unicidad de ISBN y existencia de entidades relacionadas. |
| `PATCH` | `/api/v1/libros/admin/{id}` | `ADMIN` | **Actualizar libro**. Permite edición parcial o total de los atributos y relaciones de un libro. Implementa `JsonNullable` y control de concurrencia optimista (`version`). |
| `DELETE` | `/api/v1/libros/admin/{id}` | `ADMIN` | **Desactivar libro (Borrado Lógico)**. Oculta el libro del catálogo público pasando `borradoLogico = true`. No elimina físicamente el registro de la BD. |
| `PATCH` | `/api/v1/libros/admin/{id}/activar` | `ADMIN` | **Activar libro**. Reactiva un libro previamente desactivado, volviéndolo a hacer visible en el catálogo público (`borradoLogico = false`). |

---

## 3. Especificación de Campos (`LibroRequest`)

Para operaciones `POST` (Creación) y `PATCH` (Actualización), el Frontend interactúa con la estructura `LibroRequest`.

### Tabla de Atributos y Reglas de Validación

| Campo | Tipo | ¿Obligatorio en POST? | ¿Obligatorio en PATCH? | Reglas de Validación y Formato |
|---|---|---|---|---|
| `anioPublicacion` | `Integer` | **SÍ** | Opcional (salvo si se envía `null`) | Año válido de 4 dígitos máximo ($\le 9999$). No puede ser nulo en creación. |
| `idObra` | `Long` | **SÍ** | Opcional (salvo si se envía `null`) | ID de la Obra existente a la cual pertenece este libro. |
| `idFormato` | `Long` | **SÍ** | Opcional (salvo si se envía `null`) | ID del Formato físico existente (ej. Pasta dura, rústica, etc.). |
| `idEditorial` | `Long` | **SÍ** | Opcional (salvo si se envía `null`) | ID de la Editorial existente. |
| `idIdiomas` | `Set<Long>` | **SÍ** | Opcional (salvo si se envía `null`/vacío) | Arreglo de IDs de Idiomas. Debe contener al menos 1 ID (ej. `[1]`). |
| `isbn` | `String` | NO (Opcional) | Opcional | Si se envía, debe tener 10 o 13 caracteres (números/X) y máximo 20 de longitud total. Debe ser único en el sistema. |
| `precio` | `BigDecimal` | NO (Opcional) | Opcional | Valor numérico mayor o igual a $0.00$. |
| `urlImagen` | `String` | NO (Opcional) | Opcional | URL pública de la portada del libro. |
| `mora` | `BigDecimal` | NO (Opcional) | Opcional | Tarifa diaria de mora por retraso. Mayor o igual a $0.00$. |
| `idGrado` | `Long` | NO (Opcional) | Opcional | ID del Grado académico escolar sugerido (puede ser desasociado enviando `null` en PATCH). |
| `idCiclo` | `Long` | NO (Opcional) | Opcional | ID del Ciclo escolar sugerido (puede ser desasociado enviando `null` en PATCH). |
| `version` | `Long` | NO | **REQUERIDO** | Número de versión para bloqueo optimista. Debe enviarse el valor devuelto por la API en la consulta GET. |

---

## 4. Mínimo Requerido para Crear un Libro (`POST /api/v1/libros/admin`)

Para registrar un nuevo libro con éxito, el cliente Frontend debe enviar obligatoriamente como mínimo las siguientes 5 propiedades:

```json
{
  "anioPublicacion": 2024,
  "idObra": 1,
  "idFormato": 2,
  "idEditorial": 5,
  "idIdiomas": [1]
}
```

### Ejemplo Completo de Creación (`POST`)

```json
{
  "isbn": "978-0-307-47472-8",
  "anioPublicacion": 2024,
  "precio": 25.50,
  "urlImagen": "https://ejemplo.com/portadas/cien-anios-soledad.jpg",
  "mora": 0.50,
  "idGrado": 3,
  "idCiclo": 1,
  "idObra": 1,
  "idFormato": 2,
  "idEditorial": 5,
  "idIdiomas": [1, 2]
}
```

---

## 5. Protocolo de Actualización Parcial (`PATCH`) con `JsonNullable`

En el backend de Spring Boot, los campos del DTO utilizan la envoltura `JsonNullable<T>`. Esto permite al Frontend realizar **actualizaciones parciales semánticas (PATCH)** distinguiendo entre 3 estados:

### Los 3 Estados de un Campo en `PATCH`:

1. **Campo Presente con Valor**: `"urlImagen": "https://nueva-imagen.png"`
   * **Efecto**: El backend actualiza la propiedad con la nueva cadena/valor.
2. **Campo Presente con `null`**: `"idGrado": null` o `"urlImagen": null`
   * **Efecto**: El backend **remueve / limpia** el valor o desasocia la relación en la base de datos (lo asigna como `NULL`).
   * *Atención*: No enviar `null` en campos obligatorios (`idObra`, `idEditorial`, `idFormato`, `anioPublicacion`, `idIdiomas`), ya que provocará un error de validación `400 Bad Request`.
3. **Campo Omitido (Ausente en el JSON)**: Si se omite la clave `"precio"` en el payload.
   * **Efecto**: El backend **mantiene el valor existente** en la base de datos sin hacerle ningún cambio.

### Ejemplo de Actualización Parcial (`PATCH`): Cambiar precio y quitar el Grado asignado

```json
{
  "version": 0,
  "precio": 30.00,
  "idGrado": null
}
```
*En este ejemplo, `anioPublicacion`, `isbn`, `idObra`, `editorial`, etc., conservan exactamente sus valores actuales en la base de datos.*

---

## 6. Control de Concurrencia Optimista (`version`)

Para prevenir la sobrescritura accidental de cambios concurrentes (cuando dos administradores editan el mismo libro al mismo tiempo):

1. Al consultar un libro (`GET`), la respuesta incluye una clave `"version"` (ejemplo: `"version": 3`).
2. Al ejecutar un `PATCH /api/v1/libros/admin/{id}`, el Frontend **DEBE incluir** el atributo `"version"` recibido en el último GET:

```json
{
  "version": 3,
  "precio": 29.99
}
```

3. Si la versión enviada no coincide con la versión actual en la base de datos, la API responderá con `400 Bad Request` o `409 Conflict`:

```json
{
  "exito": false,
  "mensaje": "El registro fue modificado por otro usuario. Por favor recargue la página.",
  "datos": null
}
```

---

## 7. Ciclo de Vida: Desactivar y Activar Libro (Borrado Lógico)

Arrupeteca no elimina físicamente registros de libros de la base de datos para preservar la integridad referencial histórica con préstamos y ejemplares.

### Desactivar Libro (Ocultar del Catálogo Público)
* **Endpoint**: `DELETE /api/v1/libros/admin/{id}`
* **Efecto**: Cambia `borradoLogico = true`.
* **Consecuencia**: El libro deja de aparecer en `GET /api/v1/libros` y consultas por obra. Los usuarios normales que intenten consultar `GET /api/v1/libros/{id}` recibirán un error indicando que no se encontró el libro activo.

### Reactivar Libro (Volver a publicar)
* **Endpoint**: `PATCH /api/v1/libros/admin/{id}/activar`
* **Efecto**: Cambia `borradoLogico = false`.
* **Consecuencia**: El libro vuelve a ser visible en las búsquedas públicas de catálogo.

---

## 8. Formato Estándar de Respuesta (`ApiResponse<T>`)

Todas las respuestas de la API vienen dentro de una envoltura genérica `ApiResponse`:

### Respuesta Exitosa Simple (Detalle)
```json
{
  "exito": true,
  "mensaje": "Libro encontrado (admin)",
  "datos": {
    "id": 1,
    "isbn": "9780307474728",
    "anioPublicacion": 2020,
    "precio": 19.99,
    "urlImagen": "https://ejemplo.com/portada.jpg",
    "mora": 0.50,
    "version": 1,
    "borradoLogico": false,
    "grado": { "id": 3, "nombre": "Tercer Grado" },
    "ciclo": { "id": 1, "nombre": "I Ciclo" },
    "creadoPor": "admin@arrupeteca.com",
    "fechaCreacion": "2026-08-10T10:00:00",
    "modificadoPor": "admin@arrupeteca.com",
    "fechaModificacion": "2026-08-11T12:30:00",
    "obra": { "id": 1, "titulo": "Cien Años de Soledad" },
    "editorial": { "id": 5, "nombre": "Editorial Sudamericana" },
    "formato": { "id": 2, "nombre": "Pasta Dura" },
    "idiomas": [
      { "id": 1, "nombre": "Español" }
    ]
  }
}
```

### Respuesta Paginada (Listado / Búsqueda)
```json
{
  "exito": true,
  "mensaje": "Resultados búsqueda pública de libros activos",
  "datos": {
    "content": [
      {
        "id": 1,
        "isbn": "9780307474728",
        "anioPublicacion": 2020,
        "precio": 19.99,
        "urlImagen": "https://ejemplo.com/portada.jpg",
        "mora": 0.50,
        "borradoLogico": false,
        "nombreGrado": "Tercer Grado",
        "nombreCiclo": "I Ciclo",
        "tituloObra": "Cien Años de Soledad",
        "nombreEditorial": "Editorial Sudamericana",
        "nombreFormato": "Pasta Dura"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
}
```

### Respuesta de Error por Validación
```json
{
  "exito": false,
  "mensaje": "No se pudo procesar la solicitud debido a errores en los datos.",
  "datos": [
    "El año de publicación no puede estar vacío",
    "Debe elegir una obra para el libro"
  ]
}
```

---

## 9. Parámetros de Búsqueda y Paginación

### Búsqueda Pública de Libros Activos (`GET /api/v1/libros`)
* `termino` (String): Busca coincidencias por ISBN o título de obra.
* `idAutor` (Long)
* `idGenero` (Long)
* `idCategoria` (Long)
* `idEditorial` (Long)
* `idIdioma` (Long): Filtra libros asociados al ID de un idioma específico.
* `idCiclo` (Long): Filtra libros asociados a un ciclo escolar específico.
* `idEtiqueta` (Long): Filtra libros asociados al ID de una etiqueta de obra específica.
* `anioInicio` (Integer)
* `anioFin` (Integer)
* Paginación: `page` (def 0), `size` (def 20), `sort` (def `obra.titulo`).

### Búsqueda por ID con `busquedaGlobalPorId` (`GET /api/v1/libros/{id}`)
* **Propósito**: Obtener la ficha técnica/detalle completo de un libro activo por su identificador único ID.
* **Autenticación**: Público (sin token JWT).
* **Respuesta**: Devuelve `ApiResponse<LibroDetalleResponse>` con toda la información anidada (obra, editorial, formato, idiomas, grado, ciclo, versión).
* **Errores**: Si el ID no existe o el libro está desactivado lógicamente (`borradoLogico = true`), la API retornará HTTP 400 Bad Request con `"exito": false` y el mensaje `"No se encontró Libro activo con ID: {id}"`.

### Búsqueda Admin (`GET /api/v1/libros/admin/all`)
Incluye todos los filtros anteriores más:
* `borradoLogico` (Boolean): `true` (solo inactivos), `false` (solo activos), omitido (todos).
* `idObra` (Long)
* `idEtiqueta` (Long)
* `idFormato` (Long)
* `idGrado` (Long)
* `idCiclo` (Long)
* `idIdioma` (Long)

---

## 10. Ejemplos de Implementación Frontend (TypeScript / Fetch)

```typescript
// Interfaces principales
export interface LibroMinimoRequest {
  anioPublicacion: number;
  idObra: number;
  idFormato: number;
  idEditorial: number;
  idIdiomas: number[];
}

export interface LibroPatchRequest {
  version: number;
  isbn?: string;
  anioPublicacion?: number;
  precio?: number;
  urlImagen?: string;
  mora?: number;
  idGrado?: number | null; // null desasocia el grado
  idCiclo?: number | null; // null desasocia el ciclo
  idObra?: number;
  idFormato?: number;
  idEditorial?: number;
  idIdiomas?: number[];
}

// 1. Obtener detalle de un libro activo por ID (busquedaGlobalPorId) - PÚBLICO
export async function fetchLibroPorId(id: number) {
  const res = await fetch(`/api/v1/libros/${id}`);
  const json = await res.json();
  if (json.exito) {
    return json.datos; // Retorna LibroDetalleResponse completo con version, obra, etc.
  }
  throw new Error(json.mensaje || 'No se encontró el libro activo');
}

// 2. Búsqueda pública paginada con filtros (busquedaGlobal) - PÚBLICO
export async function buscarLibrosGlobal(params: {
  termino?: string;
  idAutor?: number;
  idGenero?: number;
  idCategoria?: number;
  idEditorial?: number;
  idIdioma?: number;
  idCiclo?: number;
  idEtiqueta?: number;
  page?: number;
  size?: number;
}) {
  const query = new URLSearchParams();
  if (params.termino) query.append('termino', params.termino);
  if (params.idAutor) query.append('idAutor', params.idAutor.toString());
  if (params.idGenero) query.append('idGenero', params.idGenero.toString());
  if (params.idCategoria) query.append('idCategoria', params.idCategoria.toString());
  if (params.idEditorial) query.append('idEditorial', params.idEditorial.toString());
  if (params.idIdioma) query.append('idIdioma', params.idIdioma.toString());
  if (params.idCiclo) query.append('idCiclo', params.idCiclo.toString());
  if (params.idEtiqueta) query.append('idEtiqueta', params.idEtiqueta.toString());
  if (params.page !== undefined) query.append('page', params.page.toString());
  if (params.size !== undefined) query.append('size', params.size.toString());

  const res = await fetch(`/api/v1/libros?${query.toString()}`);
  return await res.json();
}

// 3. Crear libro (Admin - Requiere Token JWT)
export async function crearLibro(data: LibroMinimoRequest, token: string) {
  const res = await fetch('/api/v1/libros/admin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });
  return await res.json();
}

// 4. Parchear libro con JsonNullable y Version Lock (Admin - Requiere Token JWT)
export async function actualizarLibro(id: number, patchData: LibroPatchRequest, token: string) {
  const res = await fetch(`/api/v1/libros/admin/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(patchData)
  });
  return await res.json();
}

// 5. Desactivar libro (Borrado Lógico - Admin)
export async function desactivarLibro(id: number, token: string) {
  const res = await fetch(`/api/v1/libros/admin/${id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await res.json();
}

// 6. Activar libro (Admin)
export async function activarLibro(id: number, token: string) {
  const res = await fetch(`/api/v1/libros/admin/${id}/activar`, {
    method: 'PATCH',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return await res.json();
}
```

---

## 11. Consumo de Catálogos en UI (Comboboxes, Selects y Autocomplete)

El backend expone dos tipos de endpoints según la cantidad de datos que maneja cada entidad:

---

### **Tipo 1: Catálogos Pequeños / Fijos -> Combobox de Carga Única y Filtro Local**

Para entidades pequeñas (Editoriales, Formatos, Idiomas, Grados, Ciclos, Categorías, Géneros), la API retorna un arreglo simple **no paginado** `ApiResponse<List<T>>`.

* **Funcionamiento**: El Frontend realiza **una sola petición HTTP** al montar la pantalla/modal (`useEffect` / React Query) y almacena la lista en memoria local para filtrarla dentro de un **Shadcn UI Combobox** o **React-Select**.

#### Tabla de Catálogos de Carga Única

| Catálogo / Entidad | Endpoint `GET` Público | Estructura de Respuesta | Uso en Formulario de Libro |
|---|---|---|---|
| **Editoriales** | `GET /api/v1/editoriales` | `ApiResponse<List<EditorialResponse>>` | Selector de Editorial (`idEditorial`) |
| **Formatos** | `GET /api/v1/formatos` | `ApiResponse<List<FormatoResponse>>` | Selector de Formato (`idFormato`) |
| **Idiomas** | `GET /api/v1/idiomas` | `ApiResponse<List<IdiomaResponse>>` | Selector múltiple (`idIdiomas`) |
| **Grados** | `GET /api/v1/grados` | `ApiResponse<List<GradoResponse>>` | Selector de Grado (`idGrado`) |
| **Ciclos** | `GET /api/v1/ciclos` | `ApiResponse<List<CicloResponse>>` | Selector de Ciclo (`idCiclo`) |
| **Categorías** | `GET /api/v1/categorias` | `ApiResponse<List<CategoriaResponse>>` | Filtro por Categoría |
| **Géneros** | `GET /api/v1/generos` | `ApiResponse<List<GeneroResponse>>` | Filtro por Género |

#### Ejemplo de Respuesta (`GET /api/v1/editoriales`)

```json
{
  "exito": true,
  "mensaje": "Lista de editoriales activos(as)",
  "datos": [
    { "id": 1, "nombre": "Editorial Santillana", "borradoLogico": false },
    { "id": 2, "nombre": "Penguin Random House", "borradoLogico": false }
  ]
}
```

---

### **Tipo 2: Entidades Voluminosas (Autores, Obras, Libros) -> Combobox Asíncrono / Autocomplete (Peticiones al Escribir)**

Para el catálogo de **Autores** (y Obras/Libros), debido al volumen de datos, el backend responde con un objeto **Paginado** `ApiResponse<Page<AutorResumenResponse>>`.

* **Endpoint**: `GET /api/v1/autores` *(Público Anónimo, no exige token JWT)*.
* **Estrategia Frontend**: Usar un **Combobox Asíncrono / Autocomplete con Debounce** (retraso de 300ms al escribir) que consulta la API a medida que el usuario tipe el nombre o seudónimo del autor.

#### Parámetros Query para Búsqueda de Autores (`GET /api/v1/autores`)

| Parámetro | Tipo | Requerido | Descripción |
|---|---|---|---|
| `termino` | `String` | NO | Coincidencia parcial insensible a mayúsculas en el **nombre** o **seudónimo** del autor. Ejemplo: `?termino=garcia` |
| `page` | `Integer` | NO | Número de página (por defecto `0`). |
| `size` | `Integer` | NO | Tamaño de página (por defecto `20`, para Autocomplete se recomienda `size=10`). |
| `sort` | `String` | NO | Campo de ordenamiento (por defecto `nombre`). |

#### Ejemplo de Respuesta Paginada (`GET /api/v1/autores?termino=gabo`)

```json
{
  "exito": true,
  "mensaje": "Resultados de búsqueda pública de autores activos",
  "datos": {
    "content": [
      {
        "id": 1,
        "nombre": "Gabriel García Márquez",
        "seudonimo": "Gabo",
        "anioNacimiento": 1927,
        "anioFallecimiento": 2014,
        "borradoLogico": false,
        "nombreNacionalidad": "Colombiana",
        "nombrePais": "Colombia",
        "tituloObraCumbre": "Cien Años de Soledad"
      }
    ],
    "pageable": { "pageNumber": 0, "pageSize": 10 },
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### **Ejemplos de Implementación en React / TypeScript**

```typescript
export interface Option {
  label: string;
  value: number;
}

// 1. Carga Única en Memoria (Editoriales, Formatos, Idiomas, etc.)
export async function fetchEditorialesOptions(): Promise<Option[]> {
  const res = await fetch('/api/v1/editoriales');
  const json = await res.json();
  
  if (json.exito && Array.isArray(json.datos)) {
    return json.datos.map((item: { id: number; nombre: string }) => ({
      label: item.nombre,
      value: item.id
    }));
  }
  return [];
}

// 2. Autocomplete Asíncrono al Escribir (Autores por Nombre o Seudónimo)
export async function searchAutoresAsync(query: string, limit = 10): Promise<Option[]> {
  const params = new URLSearchParams({
    termino: query.trim(),
    page: '0',
    size: limit.toString()
  });

  const res = await fetch(`/api/v1/autores?${params.toString()}`);
  const json = await res.json();

  if (json.exito && json.datos?.content) {
    return json.datos.content.map((autor: { id: number; nombre: string; seudonimo?: string }) => ({
      label: autor.seudonimo ? `${autor.nombre} ("${autor.seudonimo}")` : autor.nombre,
      value: autor.id
    }));
  }
  return [];
}
```

