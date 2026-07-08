package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.entity.Genero;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class AutorSpecification {

    private AutorSpecification() {

    }

    /**
     * Filtra por ID.
     */
    public static Specification<Autor> tieneId(Long id) {
        return (root, query, cb) -> {
            if (id == null) {
                return null;
            }
            return cb.equal(root.get("id"), id);
        };
    }

    /**
     * Filtra por estado de borrado lógico.
     * Si es null, por defecto filtra solo los no borrados (comportamiento estándar).
     */
    public static Specification<Autor> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> cb.equal(root.get("borradoLogico"), esBorrado != null ? esBorrado : false);
    }

    /**
     * Filtra por ID de Nacionalidad.
     */
    public static Specification<Autor> tieneNacionalidad(Long idNacionalidad) {
        return (root, query, cb) -> {
            if (idNacionalidad == null) {
                return null;
            }
            Join<Autor, Nacionalidad> join = root.join("nacionalidad", JoinType.LEFT);
            return cb.equal(join.get("id"), idNacionalidad);
        };
    }

    /**
     * Filtra por ID de País.
     */
    public static Specification<Autor> tienePais(Long idPais) {
        return (root, query, cb) -> {
            if (idPais == null) {
                return null;
            }
            Join<Autor, Pais> join = root.join("paisNacimiento", JoinType.LEFT);
            return cb.equal(join.get("id"), idPais);
        };
    }

    /**
     * Filtra por ID de Género.
     */
    public static Specification<Autor> tieneGenero(Long idGenero) {
        return (root, query, cb) -> {
            if (idGenero == null) {
                return null;
            }
            Join<Autor, Genero> join = root.join("generoPrincipal", JoinType.LEFT);
            return cb.equal(join.get("id"), idGenero);
        };
    }

    /**
     * Búsqueda por término (nombre o seudónimo).
     */
    public static Specification<Autor> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) {
                return null;
            }
            String pattern = "%" + termino.trim() + "%";
            return cb.or(
                    cb.like(root.get("nombre"), pattern),
                    cb.like(root.get("seudonimo"), pattern)
            );
        };
    }

    /**
     * Búsqueda avanzada por palabra clave en múltiples campos.
     * Importante: Se utiliza query.distinct(true) para evitar duplicados y JoinType.LEFT
     * para no excluir autores que tengan campos (como país o nacionalidad) nulos.
     */
    public static Specification<Autor> busquedaGlobal(String palabra) {
        return (root, query, cb) -> {
            if (palabra == null || palabra.isBlank()) {
                return null;
            }
            String pattern = "%" + palabra.trim() + "%";

            // Aseguramos que no haya duplicados si la consulta se vuelve compleja
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            Join<Autor, Pais> joinPais = root.join("paisNacimiento", JoinType.LEFT);
            Join<Autor, Nacionalidad> joinNacionalidad = root.join("nacionalidad", JoinType.LEFT);

            return cb.or(
                    cb.like(root.get("nombre"), pattern),
                    cb.like(root.get("seudonimo"), pattern),
                    cb.like(joinPais.get("nombre"), pattern),
                    cb.like(joinNacionalidad.get("nombre"), pattern)
            );
        };
    }

    /**
     * Filtra por rango de años de nacimiento.
     */
    public static Specification<Autor> anioNacimientoEntre(Year inicio, Year fin) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("anioNacimiento"), inicio));
            }
            if (fin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("anioNacimiento"), fin));
            }
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Orquestador para Búsqueda Maestra de Admin.
     */
    public static Specification<Autor> filtroMaestroAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero, String termino) {
        return Specification.where(esBorrado(esBorrado))
                .and(tieneNacionalidad(idNacionalidad))
                .and(tienePais(idPais))
                .and(tieneGenero(idGenero))
                .and(contieneTermino(termino));
    }

    /**
     * Orquestador para Búsqueda Global Avanzada de Usuario.
     */
    public static Specification<Autor> filtroBusquedaGlobal(String palabra, Year inicio, Year fin) {
        return Specification.where(esBorrado(false)) // Usuarios solo ven lo no borrado
                .and(busquedaGlobal(palabra))
                .and(anioNacimientoEntre(inicio, fin));
    }
}
