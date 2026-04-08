package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.*;
import com.arrupeteca.persistence.projection.AutorResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorServiceImpl implements AutorService {

    private final AutorRepository autorRepository;
    private final AutorMapper autorMapper;

    private final PaisRepository paisRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final GeneroRepository generoRepository;
    private final ObraRepository obraRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodosActivos(Pageable pageable) {
        return autorRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorResumen obtenerPorIdActivo(Long id) {
        return autorRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> obtenerTodos(Pageable pageable) {
        return autorRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public AutorResumen obtenerPorId(Long id) {
        return autorRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaGlobalAvanzada(String palabra, Integer inicio, Integer fin, Pageable pageable) {
        return autorRepository.busquedaGlobalAvanzada(palabra, inicio, fin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idNacionalidad, Long idPais,
                                                   Long idGenero, Long idObra, String terminoBusqueda,
                                                   Integer anioInicio, Integer anioFin,
                                                   Pageable pageable) {
        return autorRepository.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero, idObra,
                terminoBusqueda, anioInicio, anioFin, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutorResumen> buscarAutoresVivosEnRango(Integer anioInicio, Integer anioFin, Pageable pageable) {
        return autorRepository.buscarAutoresVivosEnRango(anioInicio, anioFin, pageable);
    }

    @Override
    @Transactional
    public AutorResumen crear(AutorRequest request) {
        List<String> errores = new ArrayList<>();

        if (request.getSeudonimo() == null || request.getSeudonimo().isBlank()) {
            request.setSeudonimo("Seudonimo Desconocido");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            request.setNombre("Nombre Desconocido");
        }

        Autor autorNuevo = autorMapper.toEntity(request);
        validarYAsignarRelaciones(autorNuevo, request, errores);

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }

        Autor autorGuardado = autorRepository.save(autorNuevo);
        return obtenerPorId(autorGuardado.getId());
    }

    @Override
    @Transactional
    public AutorResumen actualizar(Long id, AutorRequest request) {
        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Autor con ID: " + id));

        List<String> errores = new ArrayList<>();

        if (request.getSeudonimo() == null || request.getSeudonimo().isBlank()) {
            request.setSeudonimo("Seudonimo Desconocido");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            request.setNombre("Nombre Desconocido");
        }

        autorMapper.updateEntity(request, autorExistente);
        validarYAsignarRelaciones(autorExistente, request, errores);

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }

        autorRepository.save(autorExistente);
        return obtenerPorId(autorExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!autorRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Autor con ID: " + id);
        }
        if (estado) autorRepository.activarAutor(id);
        else autorRepository.desactivarAutor(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> verificarDuplicados(String termino, Long idExcluir) {
        if (termino == null || termino.isBlank()
                || termino.trim().equalsIgnoreCase("Seudonimo Desconocido")
                || termino.trim().equalsIgnoreCase("Nombre Desconocido")) {
            return List.of();
        }

        return autorRepository.buscarPosiblesDuplicados(termino.trim(), idExcluir);
    }

    private void validarYAsignarRelaciones(Autor autor, AutorRequest request, List<String> errores) {
        if (request.getIdPaisNacimiento() != null) {
            paisRepository.findById(request.getIdPaisNacimiento())
                    .ifPresentOrElse(
                            autor::setPaisNacimiento,
                            () -> errores.add("El País de nacimiento indicado (ID: " + request.getIdPaisNacimiento() + ") no existe.")
                    );
        } else {
            autor.setPaisNacimiento(null);
        }

        if (request.getIdNacionalidad() != null) {
            nacionalidadRepository.findById(request.getIdNacionalidad())
                    .ifPresentOrElse(
                            autor::setNacionalidad,
                            () -> errores.add("La Nacionalidad indicada (ID: " + request.getIdNacionalidad() + ") no existe.")
                    );
        } else {
            autor.setNacionalidad(null);
        }

        if (request.getIdGeneroPrincipal() != null) {
            generoRepository.findById(request.getIdGeneroPrincipal())
                    .ifPresentOrElse(
                            autor::setGeneroPrincipal,
                            () -> errores.add("El Género principal indicado (ID: " + request.getIdGeneroPrincipal() + ") no existe.")
                    );
        } else {
            autor.setGeneroPrincipal(null);
        }

        if (request.getIdObraCumbre() != null) {
            obraRepository.findById(request.getIdObraCumbre())
                    .ifPresentOrElse(
                            autor::setObraCumbre,
                            () -> errores.add("La Obra Cumbre indicada (ID: " + request.getIdObraCumbre() + ") no existe.")
                    );
        } else {
            autor.setObraCumbre(null);
        }
    }
}
