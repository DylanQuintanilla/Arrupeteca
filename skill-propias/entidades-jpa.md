# Estándar de Desarrollo: Entidades JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de persistencia, asegurando la consistencia entre el código Java y el esquema de base de datos.

## 1. Convenciones de Nomenclatura
Para mantener la compatibilidad con los estándares de la industria y facilitar el mapeo:

*   **Java (Código)**: Usar **camelCase** para nombres de atributos y variables (ej. `nombreAutor`, `fechaNacimiento`).
*   **SQL (Base de Datos)**: Usar **snake_case** para nombres de tablas y columnas (ej. `id_autor`, `fecha_nacimiento`).
*   **Explícito**: Siempre definir el nombre en `@Table` y `@Column` para evitar depender de las estrategias de generación automática de Hibernate.

## 2. Estructura Base, Herencia y Lombok
Toda entidad debe incluir las siguientes anotaciones para reducir el código repetitivo y mejorar la encapsulación:

*   **Herencia de Auditoría**: Las entidades deben extender de `Auditable` para heredar campos de traza (creado_por, fecha_creacion, etc.).
*   **Anotaciones de Datos**: 
    *   `@Getter` y `@Setter`: Para el acceso estándar a datos.
    *   **Prohibido `@Data`**: No usar `@Data` en entidades JPA; genera problemas de rendimiento, ciclos infinitos en relaciones y rompe el contrato de Hibernate.
    *   `@ToString(onlyExplicitlyIncluded = true)`: Incluir explícitamente solo la PK y campos que identifiquen al objeto de forma natural (ej. `nombre`, `codigo`).
    *   `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`: Incluir explícitamente solo la PK y campos de identidad natural.
*   **Constructores**:
    *   `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: Requisito de JPA, restringido para evitar estados inconsistentes.
    *   `@AllArgsConstructor(access = AccessLevel.PRIVATE)`: Restringido para forzar el uso de `@Builder`.
    *   `@Builder`: Patrón preferido para la creación de instancias.

## 3. Configuración de Persistencia
*   **`@Entity` y `@Table`**: Obligatorios. El nombre de la tabla debe ser en minúsculas y snake_case.
*   **Identificadores (`@Id`)**:
    *   Usar `GenerationType.IDENTITY` para MySQL.
    *   Marcar como `updatable = false` para garantizar la inmutabilidad de la llave primaria.
    *   Anotar con `@EqualsAndHashCode.Include` y `@ToString.Include`.
*   **Tipos Semánticos**:
    *   Usar `java.time.Year` para años.
    *   Usar `@Lob` con `columnDefinition = "TEXT"` para campos de texto largo.
    *   Usar Enums para valores constantes.

## 4. Gestión de Relaciones
*   **Carga Diferida**: Usar `FetchType.LAZY` en todas las relaciones por defecto para optimizar el rendimiento.
*   **Nulabilidad**:
    *   Definir `nullable = false` en `@Column` o `@JoinColumn` solo si el campo es obligatorio por regla de negocio.
    *   Documentar opcionalidad con `nullable = true` explícito si ayuda a la claridad.
*   **Exclusión de Logs**: Nunca incluir relaciones en el `@ToString` a menos que sea estrictamente necesario y se use un campo identificador simple.
*   **Colecciones (`OneToMany`, `ManyToMany`)**:
    *   **Obligatorio usar `Set`**: Siempre usar `Set` en lugar de `List` para representar colecciones. Esto evita duplicados y previene el error `MultipleBagFetchException` al cargar múltiples colecciones ansiosamente.
    *   **Inicialización**: Siempre inicializar las colecciones en el sitio de declaración usando `HashSet` (ej. `private Set<Libro> libros = new HashSet<>();`) para evitar `NullPointerException`.
    *   No incluir colecciones en `@ToString` ni `@EqualsAndHashCode`.

## 5. Automatización de Comportamientos (Soft Delete)
Para entidades que requieran borrado lógico:
*   **Anotaciones**:
    *   `@SQLDelete`: Define el `UPDATE` que se ejecutará al llamar a `delete()`.
*   **Campo Técnico**:
    *   Definir el atributo `boolean borradoLogico`.
    *   Usar `@Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")`.
    *   Usar `@Builder.Default` para inicializarlo en `false`.

## 6. Validaciones de Dominio
*   Utilizar anotaciones de `jakarta.validation` (`@NotBlank`, `@Size`, `@Max`, `@Min`) directamente en la entidad.
*   **Mensajes**: Proporcionar siempre un atributo `message` descriptivo en español (ej. `message = "El nombre no puede estar vacío"`).
*   **Reglas Complejas**: Usar `@AssertTrue` para reglas de negocio internas que involucren múltiples campos o comparaciones lógicas (ej. validación de rangos de fechas).

## 7. Ejemplo de Plantilla Completa
Usa este código como base para crear nuevas entidades:

```java
package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "nombre_entidad")
@SQLDelete(sql = "UPDATE nombre_entidad SET borrado_logico = 1 WHERE id_entidad = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EntidadEjemplo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @NotBlank(message = "El campo no puede estar vacío")
    @Column(name = "nombre", length = 100, nullable = false)
    @ToString.Include
    private String nombre;

    @Column(name = "anio")
    private Year anio;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relacion")
    private Relacion relacion;

    @Builder.Default
    @OneToMany(mappedBy = "entidadEjemplo", cascade = CascadeType.ALL)
    private Set<Detalle> detalles = new HashSet<>();

    @AssertTrue(message = "Lógica de validación personalizada")
    public boolean isValido() {
        return true; 
    }
}
```

---
*Este estándar es de cumplimiento obligatorio para todas las clases dentro del paquete `.persistence.entity`.*
