# Estándar de Desarrollo: Entidades JPA (Arrupeteca)

Este documento establece las reglas y convenciones para la capa de persistencia, asegurando la consistencia entre el código Java y el esquema de base de datos, y protegiendo la integridad de los datos mediante el bloqueo optimista.

## 1. Convenciones de Nomenclatura
*   **Java**: camelCase (ej. `nombreAutor`).
*   **SQL**: snake_case (ej. `id_autor`).
*   **Explícito**: Siempre definir el nombre en `@Table` y `@Column`.

## 2. Estructura Base, Herencia y Lombok
*   **Herencia de Auditoría**: Las entidades de negocio deben extender de `Auditable`.
*   **Bloqueo Optimista (Obligatorio)**:
    *   Toda entidad editable debe incluir un campo `Long version` anotado con `@Version`.
    *   **Propósito**: Evitar que dos usuarios sobrescriban sus cambios accidentalmente (Lost Update).
*   **Anotaciones Lombok**: 
    *   Usar `@Getter`, `@Setter`, `@Builder`.
    *   **Prohibido `@Data`**.
    *   `@ToString(onlyExplicitlyIncluded = true)`: Solo PK e identidad natural. **NUNCA incluir la versión ni relaciones**.
    *   `@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)`: Solo PK e identidad natural.

## 3. Configuración de Persistencia
*   **Identificadores (`@Id`)**: Usar `GenerationType.IDENTITY`. Marcar como `updatable = false`.
*   **Tipos**: Usar `java.time.Year` para años, `@Lob` para textos largos.

## 4. Gestión de Relaciones
*   **Lazy Loading**: Usar `FetchType.LAZY` por defecto.
*   **Colecciones**:
    *   **Obligatorio usar `Set`** para evitar `MultipleBagFetchException`.
    *   Inicializar siempre: `private Set<T> items = new HashSet<>();`.

## 5. Automatización de Comportamientos
*   **Soft Delete**: Usar `@SQLDelete` y el atributo `boolean borradoLogico` con `@Builder.Default`.

## 6. Ejemplo de Plantilla Completa

```java
package com.arrupeteca.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Year;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "entidad")
@SQLDelete(sql = "UPDATE entidad SET borrado_logico = 1 WHERE id_entidad = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Entidad extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entidad", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Version
    @Column(name = "version")
    private Long version; // Bloqueo Optimista

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", length = 100, nullable = false)
    @ToString.Include
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_relacion")
    private Relacion relacion;
}
```

---
*Este estándar garantiza la integridad de los datos en entornos multi-usuario.*
