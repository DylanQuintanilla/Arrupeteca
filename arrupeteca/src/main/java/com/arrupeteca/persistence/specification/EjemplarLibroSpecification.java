package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class EjemplarLibroSpecification {

    private EjemplarLibroSpecification() {
    }

    public static Specification<EjemplarLibro> esBorrado(Boolean esBorrado) {
        return (root, query, cb) -> {
            if (esBorrado == null) {
                return null;
            }
            return cb.equal(root.get("borradoLogico"), esBorrado);
        };
    }

    public static Specification<EjemplarLibro> tieneLibro(Long idLibro) {
        return (root, query, cb) -> {
            if (idLibro == null) {
                return null;
            }
            Join<EjemplarLibro, Libro> joinLibro = root.join("libro", JoinType.LEFT);
            return cb.equal(joinLibro.get("id"), idLibro);
        };
    }

    public static Specification<EjemplarLibro> tieneMueble(Long idMueble) {
        return (root, query, cb) -> {
            if (idMueble == null) {
                return null;
            }
            Join<EjemplarLibro, Mueble> joinMueble = root.join("mueble", JoinType.LEFT);
            return cb.equal(joinMueble.get("id"), idMueble);
        };
    }

    public static Specification<EjemplarLibro> tieneEstadoFisico(EstadoFisico estadoFisico) {
        return (root, query, cb) -> {
            if (estadoFisico == null) {
                return null;
            }
            return cb.equal(root.get("estadoFisico"), estadoFisico);
        };
    }

    public static Specification<EjemplarLibro> tieneDisponibilidad(Disponibilidad disponibilidad) {
        return (root, query, cb) -> {
            if (disponibilidad == null) {
                return null;
            }
            return cb.equal(root.get("disponibilidad"), disponibilidad);
        };
    }

    public static Specification<EjemplarLibro> tieneGenero(Long idGenero) {
        return (root, query, cb) -> {
            if (idGenero == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<EjemplarLibro, Libro> joinLibro = root.join("libro", JoinType.LEFT);
            Join<Libro, Obra> joinObra = joinLibro.join("obra", JoinType.LEFT);
            Join<Obra, Genero> joinGeneros = joinObra.join("generos", JoinType.LEFT);
            return cb.equal(joinGeneros.get("id"), idGenero);
        };
    }

    public static Specification<EjemplarLibro> tieneCategoria(Long idCategoria) {
        return (root, query, cb) -> {
            if (idCategoria == null) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            Join<EjemplarLibro, Libro> joinLibro = root.join("libro", JoinType.LEFT);
            Join<Libro, Obra> joinObra = joinLibro.join("obra", JoinType.LEFT);
            Join<Obra, Categoria> joinCategorias = joinObra.join("categorias", JoinType.LEFT);
            return cb.equal(joinCategorias.get("id"), idCategoria);
        };
    }

    public static Specification<EjemplarLibro> contieneTermino(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) {
                return null;
            }
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            String pattern = "%" + termino.trim().toLowerCase() + "%";
            Join<EjemplarLibro, Libro> joinLibro = root.join("libro", JoinType.LEFT);
            Join<Libro, Obra> joinObra = joinLibro.join("obra", JoinType.LEFT);
            Join<Libro, Editorial> joinEditorial = joinLibro.join("editorial", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("comentario")), pattern),
                    cb.like(cb.lower(joinLibro.get("isbn")), pattern),
                    cb.like(cb.lower(joinObra.get("titulo")), pattern),
                    cb.like(cb.lower(joinObra.get("resumen")), pattern),
                    cb.like(cb.lower(joinEditorial.get("nombre")), pattern)
            );
        };
    }

    public static Specification<EjemplarLibro> busquedaGlobal(String palabra, Long idGenero, Long idCategoria) {
        return Specification.where(esBorrado(false))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(contieneTermino(palabra));
    }

    public static Specification<EjemplarLibro> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idGenero, Long idCategoria,
            Long idMueble, EstadoFisico estadoFisico, Disponibilidad disponibilidad,
            String termino) {

        return Specification.where(esBorrado(esBorrado))
                .and(tieneLibro(idLibro))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(tieneMueble(idMueble))
                .and(tieneEstadoFisico(estadoFisico))
                .and(tieneDisponibilidad(disponibilidad))
                .and(contieneTermino(termino));
    }
}
