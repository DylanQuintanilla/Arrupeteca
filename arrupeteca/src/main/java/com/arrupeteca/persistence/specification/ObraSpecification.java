package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Etiqueta;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ObraSpecification {

    private ObraSpecification() {
    }

    public static Specification<Obra> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> {
            if (esBorrado == null) {
                return null;
            }
            return cb.equal(root.get("borradoLogico"), esBorrado);
        };
    }

    public static Specification<Obra> tieneGenero(Long idGenero) {
        return (root, query, cb) -> {
            if (idGenero == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<Obra, Genero> join = root.join("generos", JoinType.LEFT);
            return cb.equal(join.get("id"), idGenero);
        };
    }

    public static Specification<Obra> tieneCategoria(Long idCategoria) {
        return (root, query, cb) -> {
            if (idCategoria == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<Obra, Categoria> join = root.join("categorias", JoinType.LEFT);
            return cb.equal(join.get("id"), idCategoria);
        };
    }

    public static Specification<Obra> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) {
                return null;
            }
            String pattern = "%" + termino.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("titulo")), pattern),
                    cb.like(cb.lower(root.get("resumen")), pattern)
            );
        };
    }

    public static Specification<Obra> creadoEnRango(Integer anioInicio, Integer anioFin) {
        return (root, query, cb) -> {
            if (anioInicio == null && anioFin == null) {
                return null;
            }
            if (anioInicio != null && anioFin != null) {
                return cb.between(root.get("anioCreacion"), anioInicio, anioFin);
            } else if (anioInicio != null) {
                return cb.greaterThanOrEqualTo(root.get("anioCreacion"), anioInicio);
            } else {
                return cb.lessThanOrEqualTo(root.get("anioCreacion"), anioFin);
            }
        };
    }

    public static Specification<Obra> tieneEtiqueta(Long idEtiqueta) {
        return (root, query, cb) -> {
            if (idEtiqueta == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<Obra, Etiqueta> join = root.join("etiquetas", JoinType.LEFT);
            return cb.equal(join.get("id"), idEtiqueta);
        };
    }

    public static Specification<Obra> busquedaGlobal(String palabra) {
        return Specification.where(esBorrado(false))
                .and(contieneTermino(palabra));
    }

    public static Specification<Obra> busquedaMaestraAdmin(
            Boolean esBorrado, Long idGenero, Long idCategoria, Long idEtiqueta, String termino,
            Integer anioInicio, Integer anioFin) {
        return Specification.where(esBorrado(esBorrado))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(tieneEtiqueta(idEtiqueta))
                .and(contieneTermino(termino))
                .and(creadoEnRango(anioInicio, anioFin));
    }
}
