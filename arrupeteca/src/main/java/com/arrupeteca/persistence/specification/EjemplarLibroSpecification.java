package com.arrupeteca.persistence.specification;

import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Obra;
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

    public static Specification<EjemplarLibro> tieneEstadoFisico(Long idEstadoFisico) {
        return (root, query, cb) -> {
            if (idEstadoFisico == null) {
                return null;
            }
            Join<EjemplarLibro, EstadoFisico> joinEF = root.join("estadoFisico", JoinType.LEFT);
            return cb.equal(joinEF.get("id"), idEstadoFisico);
        };
    }

    public static Specification<EjemplarLibro> tieneDisponibilidad(Long idDisponibilidad) {
        return (root, query, cb) -> {
            if (idDisponibilidad == null) {
                return null;
            }
            Join<EjemplarLibro, Disponibilidad> joinD = root.join("disponibilidad", JoinType.LEFT);
            return cb.equal(joinD.get("id"), idDisponibilidad);
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
            Long idMueble, Long idEstadoFisico, Long idDisponibilidad,
            String termino) {

        return Specification.where(esBorrado(esBorrado))
                .and(tieneLibro(idLibro))
                .and(tieneGenero(idGenero))
                .and(tieneCategoria(idCategoria))
                .and(tieneMueble(idMueble))
                .and(tieneEstadoFisico(idEstadoFisico))
                .and(tieneDisponibilidad(idDisponibilidad))
                .and(contieneTermino(termino));
    }
}
