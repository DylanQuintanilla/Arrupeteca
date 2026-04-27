package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final LibroMapper libroMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> obtenerTodosActivos(Pageable pageable) {
        return libroRepository.findByBorradoLogicoFalse(pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroDetalleResponse obtenerPorIdActivo(Long id) {
        return libroRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(libroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return libroRepository.busquedaGlobal(palabra, pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> obtenerTodos(Pageable pageable) {
        return libroRepository.findAllProjectedBy(pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroDetalleResponse obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .map(libroMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
            Object gradoStr, Object cicloStr, String termino,
            Integer anioInicio, Integer anioFin, Pageable pageable) {

        Grado grado = (gradoStr instanceof String s && !s.isEmpty())
                ? Grado.valueOf(s) : (gradoStr instanceof Grado g ? g : null);

        Ciclo ciclo = (cicloStr instanceof String s && !s.isEmpty())
                ? Ciclo.valueOf(s) : (cicloStr instanceof Ciclo c ? c : null);

        return libroRepository.busquedaMaestraAdmin(esBorrado, idObra, idEditorial, idFormato,
                        grado, ciclo, termino, anioInicio, anioFin, pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> buscarPorObraId(Long idObra, Pageable pageable) {
        return libroRepository.buscarPorObraId(idObra, pageable)
                .map(libroMapper::toResumen);
    }

    @Override
    @Transactional
    public LibroDetalleResponse crear(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.getIsbn())) {
            throw new ValidacionNegocioException("Ya existe un libro con el ISBN: " + request.getIsbn());
        }
        Libro libro = libroMapper.toEntity(request);
        return libroMapper.toDetalleResponse(libroRepository.save(libro));
    }

    @Override
    @Transactional
    public LibroDetalleResponse actualizar(Long id, LibroRequest request) {
        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));

        if (!libroExistente.getIsbn().equals(request.getIsbn())
                && libroRepository.existsByIsbn(request.getIsbn())) {
            throw new ValidacionNegocioException("Ya existe otro libro con el ISBN: " + request.getIsbn());
        }

        libroMapper.updateEntity(request, libroExistente);
        return libroMapper.toDetalleResponse(libroRepository.save(libroExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!libroRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Libro con ID: " + id);
        }
        if (estado) {
            libroRepository.activar(id);
        } else {
            libroRepository.desactivar(id);
        }
    }
}
