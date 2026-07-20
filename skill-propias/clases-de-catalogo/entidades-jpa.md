# Estándar de Desarrollo: Entidades JPA para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de las entidades JPA en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Clasificación e Integración

Las entidades de catálogo representan datos de referencia que son mayormente de lectura y rara vez cambian.

*   **Auditoría**: No extienden de `Auditable` (a menos que se especifique lo contrario). Su estructura debe ser lo más ligera posible.
*   **Bloqueo Optimista**: **No llevan campo `@Version`**. Debido a la baja frecuencia de edición de estos registros, la concurrencia es extremadamente baja y no justifica la complejidad de versionado.

---

## 2. Separación de Responsabilidades (Cero Validaciones Java)

*   **Prohibido**: Usar anotaciones de la especificación Jakarta Bean Validation (tales como `@NotBlank`, `@NotNull`, `@Size`, etc.) en los campos de la clase de Entidad JPA.
*   **Enfoque Correcto**: Las validaciones residen en los Request DTOs. La entidad solo modela propiedades de columnas físicas de base de datos (`@Column(nullable = false, length = 50)`).

---

## 3. Lombok y Constructores Seguros

*   **Getter y Setter**: Utilizar `@Getter` y `@Setter` a nivel de clase.
*   **Prohibición de `@Data`**: NUNCA usar `@Data` por romper comportamientos de carga diferida (lazy queries) y equals inapropiados.
*   **Constructores para Builder**:
    *   `@NoArgsConstructor(access = AccessLevel.PROTECTED)`: Permite a Hibernate instanciar por reflexión, pero restringe la instanciación vacía manual.
    *   `@AllArgsConstructor(access = AccessLevel.PRIVATE)`: Constructor privado para uso exclusivo de `@Builder`.
    *   `@Builder`: Habilita construcción fluida de objetos.
*   **Equals y ToString Seguros**:
    *   `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`: Incluye únicamente la clave primaria (`@Id`) marcada con `@EqualsAndHashCode.Include`.
    *   `@ToString(onlyExplicitlyIncluded = true)`: Incluye únicamente la clave primaria y el campo de nombre.

---

## 4. Mapeo de Borrado Lógico

Para asegurar que al ejecutar el método `delete` o `deleteById` de Spring Data JPA se realice una desactivación (borrado lógico) de forma automática:
*   Decorar la clase con `@SQLDelete(sql = "UPDATE [nombre_tabla] SET borrado_logico = 1 WHERE id_[nombre_tabla] = ?")`.
*   Definir el campo `borradoLogico` con `@Builder.Default` para asegurar que inicie en `false`.

---

## 5. Plantilla Base Recomendada (Ejemplo de Categoria)

```java
package com.arrupeteca.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "categoria")
@SQLDelete(sql = "UPDATE categoria SET borrado_logico = 1 WHERE id_categoria = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria", updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    @ToString.Include
    private String nombre;

    @Column(name = "borrado_logico", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    @Builder.Default
    private boolean borradoLogico = false;

    @Builder.Default
    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    private Set<Obra> obras = new HashSet<>();
}
```

---

## 6. Lista de Autoverificación (Checklist)

1. **¿Está libre de anotaciones de validación Java (`jakarta.validation`)?**
   * ¿Se removieron `@NotBlank` y `@Size` de los atributos?
2. **¿Los accesos de constructores de Lombok están restringidos?**
   * ¿`NoArgsConstructor` es `PROTECTED` y `AllArgsConstructor` es `PRIVATE`?
3. **¿Se configuró `@SQLDelete`?**
   * ¿Tiene la sentencia UPDATE nativa correcta según la base de datos?
4. **¿No tiene `@Version`?**
   * ¿Se omitió el campo de versión para mantener la estructura lo más ligera posible?
5. **¿Equals y HashCode solo consideran la clave primaria?**
   * ¿Se configuró `onlyExplicitlyIncluded = true` en `@EqualsAndHashCode`?
