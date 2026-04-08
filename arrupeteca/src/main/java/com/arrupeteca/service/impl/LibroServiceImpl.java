package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;
    private final LibroMapper libroMapper;
    private final ObraRepository obraRepository;
    private final EditorialRepository editorialRepository;
    private final FormatoRepository formatoRepository;
    private final IdiomaRepository idiomaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> obtenerTodosActivos(Pageable pageable) {
        return libroRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResumen obtenerPorIdActivo(Long id) {
        return libroRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return libroRepository.busquedaGlobal(palabra, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> obtenerTodos(Pageable pageable) {
        return libroRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResumen obtenerPorId(Long id) {
        return libroRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
            String gradoStr, String cicloStr, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable) {

        Grado grado = (gradoStr != null && !gradoStr.isEmpty()) ? Grado.valueOf(gradoStr) : null;
        Ciclo ciclo = (cicloStr != null && !cicloStr.isEmpty()) ? Ciclo.valueOf(cicloStr) : null;

        return libroRepository.busquedaMaestraAdmin(esBorrado, idObra, idEditorial, idFormato,
                grado, ciclo, termino, anioInicio, anioFin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LibroResumen> obtenerPorObra(Long idObra, Pageable pageable) {
        if (!obraRepository.existsById(idObra)) {
            throw new ValidacionNegocioException("No se encontró Obra con ID: " + idObra);
        }
        return libroRepository.findByObraId(pageable, idObra);
    }

    @Override
    @Transactional
    public LibroResumen crear(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.getIsbn())) {
            throw new ValidacionNegocioException("El ISBN " + request.getIsbn() + " ya está registrado.");
        }

        Libro nuevoLibro = libroMapper.toEntity(request);
        asignarRelaciones(nuevoLibro, request);

        Libro libroGuardado = libroRepository.save(nuevoLibro);
        return obtenerPorId(libroGuardado.getId());
    }

    @Override
    @Transactional
    public LibroResumen actualizar(Long id, LibroRequest request) {
        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Libro con ID: " + id));

        if (!libroExistente.getIsbn().equalsIgnoreCase(request.getIsbn()) &&
                libroRepository.existsByIsbn(request.getIsbn())) {
            throw new ValidacionNegocioException("El ISBN " + request.getIsbn() + " ya está en uso por otro libro.");
        }

        libroMapper.updateEntity(request, libroExistente);
        asignarRelaciones(libroExistente, request);

        libroRepository.save(libroExistente);
        return obtenerPorId(libroExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!libroRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Libro con ID: " + id);
        }
        if (estado) libroRepository.activar(id);
        else libroRepository.desactivar(id);
    }

    private void asignarRelaciones(Libro libro, LibroRequest request) {
        if (request.getIdObra() != null) {
            libro.setObra(obraRepository.findById(request.getIdObra())
                    .orElseThrow(() -> new ValidacionNegocioException("La Obra indicada no existe")));
        }
        if (request.getIdEditorial() != null) {
            libro.setEditorial(editorialRepository.findById(request.getIdEditorial())
                    .orElseThrow(() -> new ValidacionNegocioException("La Editorial indicada no existe")));
        }
        if (request.getIdFormato() != null) {
            libro.setFormato(formatoRepository.findById(request.getIdFormato())
                    .orElseThrow(() -> new ValidacionNegocioException("El Formato indicado no existe")));
        }
        if (request.getIdIdiomas() != null && !request.getIdIdiomas().isEmpty()) {
            List<Idioma> idiomas = idiomaRepository.findAllById(request.getIdIdiomas());
            if (idiomas.size() != request.getIdIdiomas().size()) {
                throw new ValidacionNegocioException("Uno o más idiomas indicados no existen");
            }
            libro.setIdiomas(new HashSet<>(idiomas));
        }
    }
}
