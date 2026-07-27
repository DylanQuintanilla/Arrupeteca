package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.persistence.specification.LibroSpecification;
import com.arrupeteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final ObraRepository obraRepository;
    private final EditorialRepository editorialRepository;
    private final FormatoRepository formatoRepository;
    private final IdiomaRepository idiomaRepository;
    private final LibroMapper libroMapper;

    @Override
    public Page<LibroResumen> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo libros activos paginados: {}", pageable);
        return libroRepository.findByBorradoLogicoFalse(pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Cacheable(value = "libros", key = "#id")
    public LibroDetalleResponse obtenerPorIdActivo(Long id) {
        log.debug("Obteniendo libro activo por ID: {}", id);
        return libroRepository.findByIdAndBorradoLogicoFalse(id)
                .map(libroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro activo con ID: " + id));
    }

    @Override
    public Page<LibroResumen> busquedaGlobal(String palabra, Long idGenero, Long idCategoria, Pageable pageable) {
        log.debug("Búsqueda global de libros por '{}', género: {}, categoría: {} paginados: {}", palabra, idGenero, idCategoria, pageable);
        return libroRepository.findAll(LibroSpecification.busquedaGlobal(palabra, idGenero, idCategoria), pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    public Page<LibroResumen> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los libros (Admin) paginados: {}", pageable);
        return libroRepository.findAll(pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    public LibroDetalleResponse obtenerPorId(Long id) {
        log.debug("Obteniendo libro por ID (Admin): {}", id);
        return libroRepository.findById(id)
                .map(libroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));
    }

    @Override
    public Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idGenero, Long idCategoria, Long idEditorial, Long idFormato,
            Object gradoStr, Object cicloStr, String termino,
            Integer anioInicio, Integer anioFin, Pageable pageable) {

        log.debug("Búsqueda maestra de libros para admin");
        Grado grado = (gradoStr instanceof String s && !s.isEmpty())
                ? Grado.valueOf(s) : (gradoStr instanceof Grado g ? g : null);

        Ciclo ciclo = (cicloStr instanceof String s && !s.isEmpty())
                ? Ciclo.valueOf(s) : (cicloStr instanceof Ciclo c ? c : null);

        return libroRepository.findAll(
                        LibroSpecification.busquedaMaestraAdmin(esBorrado, idObra, idGenero, idCategoria, idEditorial, idFormato,
                                grado, ciclo, termino, anioInicio, anioFin),
                        pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    public Page<LibroResumen> buscarPorObraId(Long idObra, Pageable pageable) {
        log.debug("Obteniendo libros por ID Obra: {}", idObra);
        return libroRepository.buscarPorObraId(idObra, pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional
    public LibroDetalleResponse crear(LibroRequest request) {
        log.info("Creando nuevo libro");
        validarCreacion(request);
        if (request.isbn() != null && request.isbn().isPresent()) {
            validarUnicidadIsbn(request.isbn().get(), null);
        }

        Libro libro = libroMapper.toEntity(request);
        asignarRelaciones(libro, request);

        Libro guardado = libroRepository.save(libro);
        log.info("Libro creado exitosamente con ID: {}", guardado.getId());
        return libroMapper.toDetalleResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libros", key = "#id")
    public LibroDetalleResponse actualizar(Long id, LibroRequest request) {
        log.info("Actualizando libro con ID: {}", id);
        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));

        validarActualizacion(request, libroExistente);
        if (request.isbn() != null && request.isbn().isPresent()) {
            validarUnicidadIsbn(request.isbn().get(), libroExistente);
        }

        libroMapper.updateEntity(request, libroExistente);
        asignarRelaciones(libroExistente, request);

        Libro actualizado = libroRepository.save(libroExistente);
        log.info("Libro con ID: {} actualizado exitosamente", id);
        return libroMapper.toDetalleResponse(actualizado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "libros", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado del libro ID: {} a {}", id, !estado);
        if (libroRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Libro con ID: " + id);
        }
        if (estado) {
            libroRepository.activar(id);
        } else {
            libroRepository.desactivar(id);
        }
    }

    private void validarCreacion(LibroRequest request) {
        List<String> errores = new ArrayList<>();
        if (request.anioPublicacion() == null || !request.anioPublicacion().isPresent() || request.anioPublicacion().get() == null) {
            errores.add("El año de publicación no puede estar vacío");
        }
        if (request.idObra() == null || !request.idObra().isPresent() || request.idObra().get() == null) {
            errores.add("Debe elegir una obra para el libro");
        }
        if (request.idFormato() == null || !request.idFormato().isPresent() || request.idFormato().get() == null) {
            errores.add("El libro debe pertenecer a un formato");
        }
        if (request.idEditorial() == null || !request.idEditorial().isPresent() || request.idEditorial().get() == null) {
            errores.add("El libro debe pertenecer a una editorial");
        }
        if (request.idIdiomas() == null || !request.idIdiomas().isPresent() || request.idIdiomas().get() == null || request.idIdiomas().get().isEmpty()) {
            errores.add("Debe elegir al menos un idioma para el libro");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void validarActualizacion(LibroRequest request, Libro existente) {
        // Bloqueo optimista
        Long vExistente = existente.getVersion() != null ? existente.getVersion() : 0L;
        if (request.version() == null || !request.version().equals(vExistente)) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue la página.");
        }

        List<String> errores = new ArrayList<>();
        if (request.anioPublicacion() != null && request.anioPublicacion().isPresent() && request.anioPublicacion().get() == null) {
            errores.add("El año de publicación no puede estar vacío");
        }
        if (request.idObra() != null && request.idObra().isPresent() && request.idObra().get() == null) {
            errores.add("Debe elegir una obra para el libro");
        }
        if (request.idFormato() != null && request.idFormato().isPresent() && request.idFormato().get() == null) {
            errores.add("El libro debe pertenecer a un formato");
        }
        if (request.idEditorial() != null && request.idEditorial().isPresent() && request.idEditorial().get() == null) {
            errores.add("El libro debe pertenecer a una editorial");
        }
        if (request.idIdiomas() != null && request.idIdiomas().isPresent() && (request.idIdiomas().get() == null || request.idIdiomas().get().isEmpty())) {
            errores.add("Debe elegir al menos un idioma para el libro");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void validarUnicidadIsbn(String isbnNuevo, Libro existente) {
        if (isbnNuevo == null || isbnNuevo.isBlank()) {
            return;
        }
        String isbnVal = isbnNuevo.trim();
        boolean esNuevo = (existente == null);
        boolean isbnCambio = !esNuevo && !existente.getIsbn().equals(isbnVal);

        if ((esNuevo || isbnCambio) && libroRepository.existsByIsbn(isbnVal)) {
            throw new ValidacionNegocioException("Ya existe un libro con el ISBN: " + isbnVal);
        }
    }

    private void asignarRelaciones(Libro libro, LibroRequest request) {
        List<String> errores = new ArrayList<>();

        // 1. Obra
        if (request.idObra() != null && request.idObra().isPresent()) {
            Long idObra = request.idObra().get();
            if (idObra != null) {
                obraRepository.findById(idObra)
                        .ifPresentOrElse(libro::setObra,
                                () -> errores.add("La obra con ID " + idObra + " no existe."));
            }
        }

        // 2. Editorial
        if (request.idEditorial() != null && request.idEditorial().isPresent()) {
            Long idEditorial = request.idEditorial().get();
            if (idEditorial != null) {
                editorialRepository.findById(idEditorial)
                        .ifPresentOrElse(libro::setEditorial,
                                () -> errores.add("La editorial con ID " + idEditorial + " no existe."));
            }
        }

        // 3. Formato
        if (request.idFormato() != null && request.idFormato().isPresent()) {
            Long idFormato = request.idFormato().get();
            if (idFormato != null) {
                formatoRepository.findById(idFormato)
                        .ifPresentOrElse(libro::setFormato,
                                () -> errores.add("El formato con ID " + idFormato + " no existe."));
            }
        }

        // 4. Idiomas
        if (request.idIdiomas() != null && request.idIdiomas().isPresent()) {
            Set<Long> ids = request.idIdiomas().get();
            if (ids != null) {
                Set<Idioma> idiomas = new HashSet<>();
                for (Long id : ids) {
                    idiomaRepository.findById(id)
                            .ifPresentOrElse(idiomas::add,
                                    () -> errores.add("El idioma con ID " + id + " no existe."));
                }
                libro.setIdiomas(idiomas);
            }
        }

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }
}
