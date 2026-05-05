package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.NacionalidadRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.persistence.specification.AutorSpecification;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;
    private final PaisRepository paisRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final GeneroRepository generoRepository;
    private final ObraRepository obraRepository;
    private final AutorMapper autorMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodosActivos(Pageable pageable) {
        Specification<Autor> spec = AutorSpecification.esBorrado(false);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorDetalleResponse obtenerPorIdActivo(Long id) {
        return autorRepository.findById(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodos(Pageable pageable) {
        return autorRepository.findAll((Specification<Autor>) null, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorDetalleResponse obtenerPorId(Long id) {
        return autorRepository.findById(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaGlobal(String palabra, Year anioInicio, Year anioFin, Pageable pageable) {
        Specification<Autor> spec = AutorSpecification.filtroBusquedaGlobal(palabra, anioInicio, anioFin);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero,
            String termino, Pageable pageable) {
        Specification<Autor> spec = AutorSpecification.filtroMaestroAdmin(esBorrado, idNacionalidad, idPais, idGenero, termino);
        return autorRepository.findAll(spec, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional
    public AutorDetalleResponse crear(AutorRequest request) {
        if (autorRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un autor con el nombre: " + request.getNombre());
        }

        Autor autor = autorMapper.toEntity(request);
        asignarRelaciones(autor, request);
        return autorMapper.toDetalleResponse(autorRepository.save(autor));
    }

    @Override
    @Transactional
    public AutorDetalleResponse actualizar(Long id, AutorRequest request) {
        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));

        if (!autorExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && autorRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe otro autor con el nombre: " + request.getNombre());
        }

        autorMapper.updateEntity(request, autorExistente);
        asignarRelaciones(autorExistente, request);
        return autorMapper.toDetalleResponse(autorRepository.save(autorExistente));
    }

    private void asignarRelaciones(Autor autor, AutorRequest request) {
        java.util.List<String> errores = new java.util.ArrayList<>();

        if (request.getIdPaisNacimiento() != null) {
            paisRepository.findById(request.getIdPaisNacimiento())
                    .ifPresentOrElse(autor::setPaisNacimiento,
                            () -> errores.add("El país con ID " + request.getIdPaisNacimiento() + " no existe."));
        } else {
            autor.setPaisNacimiento(null);
        }

        if (request.getIdNacionalidad() != null) {
            nacionalidadRepository.findById(request.getIdNacionalidad())
                    .ifPresentOrElse(autor::setNacionalidad,
                            () -> errores.add("La nacionalidad con ID " + request.getIdNacionalidad() + " no existe."));
        } else {
            autor.setNacionalidad(null);
        }

        if (request.getIdGeneroPrincipal() != null) {
            generoRepository.findById(request.getIdGeneroPrincipal())
                    .ifPresentOrElse(autor::setGeneroPrincipal,
                            () -> errores.add("El género con ID " + request.getIdGeneroPrincipal() + " no existe."));
        } else {
            autor.setGeneroPrincipal(null);
        }

        if (request.getIdObraCumbre() != null) {
            obraRepository.findById(request.getIdObraCumbre())
                    .ifPresentOrElse(autor::setObraCumbre,
                            () -> errores.add("La obra con ID " + request.getIdObraCumbre() + " no existe."));
        } else {
            autor.setObraCumbre(null);
        }

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!autorRepository.existsByIdIncludeDeleted(id)) {
            throw new ValidacionNegocioException("No se encontró Autor con ID: " + id);
        }
        if (estado) {
            autorRepository.activar(id);
        } else {
            autorRepository.desactivar(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> buscarDuplicados(String termino, Long idExcluir) {
        return autorRepository.buscarPosiblesDuplicados(termino.trim(), idExcluir)
                .stream()
                .map(autorMapper::toResumen)
                .toList();
    }
}
