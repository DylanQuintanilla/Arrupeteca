package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class LibroSpecification {

    private LibroSpecification() {
    }

    public static Specification<Libro> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> {
            if (esBorrado == null) {
                return null;
            }
            return cb.equal(root.get("borradoLogico"), esBorrado);
        };
    }

    public static Specification<Libro> tieneObra(Long idObra) {
        return (root, query, cb) -> {
            if (idObra == null) {
                return null;
            }
            Join<Libro, Obra> joinObra = root.join("obra", JoinType.LEFT);
            return cb.equal(joinObra.get("id"), idObra);
        };
    }

    public static Specification<Libro> tieneEditorial(Long idEditorial) {
        return (root, query, cb) -> {
            if (idEditorial == null) {
                return null;
            }
            Join<Libro, Editorial> joinEditorial = root.join("editorial", JoinType.LEFT);
            return cb.equal(joinEditorial.get("id"), idEditorial);
        };
    }

    public static Specification<Libro> tieneFormato(Long idFormato) {
        return (root, query, cb) -> {
            if (idFormato == null) {
                return null;
            }
            Join<Libro, Formato> joinFormato = root.join("formato", JoinType.LEFT);
            return cb.equal(joinFormato.get("id"), idFormato);
        };
    }

    public static Specification<Libro> tieneGenero(Long idGenero) {
        return (root, query, cb) -> {
            if (idGenero == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<Libro, Obra> joinObra = root.join("obra", JoinType.LEFT);
            Join<Obra, Genero> joinGeneros = joinObra.join("generos", JoinType.LEFT);
            return cb.equal(joinGeneros.get("id"), idGenero);
        };
    }

    public static Specification<Libro> tieneCategoria(Long idCategoria) {
        return (root, query, cb) -> {
            if (idCategoria == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<Libro, Obra> joinObra = root.join("obra", JoinType.LEFT);
            Join<Obra, Categoria> joinCategorias = joinObra.join("categorias", JoinType.LEFT);
            return cb.equal(joinCategorias.get("id"), idCategoria);
        };
    }

    public static Specification<Libro> tieneGrado(Grado grado) {
        return (root, query, cb) -> {
            if (grado == null) {
                return null;
            }
            return cb.equal(root.get("grado"), grado);
        };
    }

    public static Specification<Libro> tieneCiclo(Ciclo ciclo) {
        return (root, query, cb) -> {
            if (ciclo == null) {
                return null;
            }
            return cb.equal(root.get("ciclo"), ciclo);
        };
    }

    public static Specification<Libro> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            String pattern = "%" + termino.trim().toLowerCase() + "%";
            Join<Libro, Obra> joinObra = root.join("obra", JoinType.LEFT);
            Join<Libro, Editorial> joinEditorial = root.join("editorial", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("isbn")), pattern),
                    cb.like(cb.lower(joinObra.get("titulo")), pattern),
                    cb.like(cb.lower(joinObra.get("resumen")), pattern),
                    cb.like(cb.lower(joinEditorial.get("nombre")), pattern)
            );
        };
    }

    public static Specification<Libro> publicadoEnRango(Integer anioInicio, Integer anioFin) {
        return (root, query, cb) -> {
            if (anioInicio == null && anioFin == null) {
                return null;
            }
            if (anioInicio != null && anioFin != null) {
                return cb.between(root.get("anioPublicacion"), anioInicio, anioFin);
            } else if (anioInicio != null) {
                return cb.greaterThanOrEqualTo(root.get("anioPublicacion"), anioInicio);
            } else {
                return cb.lessThanOrEqualTo(root.get("anioPublicacion"), anioFin);
            }
        };
    }

    public static Specification<Libro> busquedaGlobal(String palabra, Long idGenero, Long idCategoria) {
        return Specification.where(esBorrado(false))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(contieneTermino(palabra));
    }

    public static Specification<Libro> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idGenero, Long idCategoria,
            Long idEditorial, Long idFormato, Grado grado, Ciclo ciclo,
            String termino, Integer anioInicio, Integer anioFin) {

        return Specification.where(esBorrado(esBorrado))
                .and(tieneObra(idObra))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(tieneEditorial(idEditorial))
                .and(tieneFormato(idFormato))
                .and(tieneGrado(grado))
                .and(tieneCiclo(ciclo))
                .and(contieneTermino(termino))
                .and(publicadoEnRango(anioInicio, anioFin));
    }
}
