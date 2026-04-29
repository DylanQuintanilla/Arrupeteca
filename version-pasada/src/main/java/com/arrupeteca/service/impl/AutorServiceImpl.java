package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodosActivos(Pageable pageable) {
        return autorRepository.findByBorradoLogicoFalse(pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorDetalleResponse obtenerPorIdActivo(Long id) {
        return autorRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodos(Pageable pageable) {
        return autorRepository.findAllProjectedBy(pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorDetalleResponse obtenerPorId(Long id) {
        return autorRepository.findProjectedById(id)
                .map(autorMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaGlobal(String palabra, Integer anioInicio, Integer anioFin, Pageable pageable) {
        return autorRepository.busquedaGlobalAvanzada(palabra, anioInicio, anioFin, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero, Long idObra,
            String termino, Integer anioInicio, Integer anioFin, Pageable pageable) {
        return autorRepository.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero, idObra,
                        termino, anioInicio, anioFin, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> buscarVivosEnRango(Integer anioInicio, Integer anioFin, Pageable pageable) {
        return autorRepository.buscarAutoresVivosEnRango(anioInicio, anioFin, pageable)
                .map(autorMapper::toResumen);
    }

    @Override
    @Transactional
    public AutorDetalleResponse crear(AutorRequest request) {
        if (autorRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un autor con el nombre: " + request.getNombre());
        }
        Autor autor = autorMapper.toEntity(request);
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
        return autorMapper.toDetalleResponse(autorRepository.save(autorExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!autorRepository.existsById(id)) {
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
