package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.ObraAutor;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.service.ObraService;
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
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
    private final GeneroRepository generoRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final TipoAutoriaRepository tipoAutoriaRepository;
    private final ObraMapper obraMapper;

    @Override
    public Page<ObraResumen> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo obras activas paginadas: {}", pageable);
        return obraRepository.findByBorradoLogicoFalse(pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Cacheable(value = "obras", key = "#id")
    public ObraDetalleResponse obtenerPorIdActivo(Long id) {
        log.debug("Obteniendo obra activa por ID: {}", id);
        return obraRepository.findByIdAndBorradoLogicoFalse(id)
                .map(obraMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra activa con ID: " + id));
    }

    @Override
    public Page<ObraResumen> busquedaGlobal(String palabra, Pageable pageable) {
        log.debug("Búsqueda global de obras por '{}' con paginación: {}", palabra, pageable);
        return obraRepository.busquedaGlobal(palabra, pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    public Page<ObraResumen> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todas las obras (Admin) paginadas: {}", pageable);
        return obraRepository.findAll(pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    public ObraDetalleResponse obtenerPorId(Long id) {
        log.debug("Obteniendo obra por ID (Admin): {}", id);
        return obraRepository.findById(id)
                .map(obraMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));
    }

    @Override
    public Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idGenero, Long idCategoria, String termino, Integer anioInicio, Integer anioFin,
            Pageable pageable) {
        log.debug("Búsqueda maestra de obras: {}, género: {}, categoría: {}, '{}', {} - {}", esBorrado, idGenero, idCategoria, termino, anioInicio, anioFin);
        return obraRepository.busquedaMaestraAdmin(esBorrado, idGenero, idCategoria, termino, anioInicio, anioFin, pageable)
                .map(obraMapper::toResumen);
    }

    @Override
    @Transactional
    public ObraDetalleResponse crear(ObraRequest request) {
        log.info("Creando obra: {}", request.titulo());
        validarCreacion(request);
        validarUnicidadTitulo(request.titulo().orElse(null), null);

        Obra obra = obraMapper.toEntity(request);
        asignarRelaciones(obra, request);

        Obra guardada = obraRepository.save(obra);
        log.info("Obra creada exitosamente con ID: {}", guardada.getId());
        return obraMapper.toDetalleResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "obras", key = "#id")
    public ObraDetalleResponse actualizar(Long id, ObraRequest request) {
        log.info("Actualizando obra con ID: {}", id);
        Obra obraExistente = obraRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));

        validarActualizacion(request, obraExistente);
        if (request.titulo() != null && request.titulo().isPresent()) {
            validarUnicidadTitulo(request.titulo().get(), obraExistente);
        }

        obraMapper.updateEntity(request, obraExistente);
        asignarRelaciones(obraExistente, request);

        Obra actualizada = obraRepository.save(obraExistente);
        log.info("Obra con ID: {} actualizada exitosamente", id);
        return obraMapper.toDetalleResponse(actualizada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "obras", key = "#id")
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de obra ID: {} a {}", id, !estado);
        if (obraRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Obra con ID: " + id);
        }
        if (estado) {
            obraRepository.activar(id);
        } else {
            obraRepository.desactivar(id);
        }
    }

    private void validarCreacion(ObraRequest request) {
        List<String> errores = new ArrayList<>();
        if (request.titulo() == null || !request.titulo().isPresent() || request.titulo().get() == null || request.titulo().get().isBlank()) {
            errores.add("El título no puede estar vacío");
        }
        if (request.resumen() == null || !request.resumen().isPresent() || request.resumen().get() == null || request.resumen().get().isBlank()) {
            errores.add("Se debe de agregar un resumen");
        }
        if (request.idGeneros() == null || !request.idGeneros().isPresent() || request.idGeneros().get() == null || request.idGeneros().get().isEmpty()) {
            errores.add("La obra debe tener al menos un género");
        }
        if (request.idCategorias() == null || !request.idCategorias().isPresent() || request.idCategorias().get() == null || request.idCategorias().get().isEmpty()) {
            errores.add("Favor de elegir al menos una categoría para la obra");
        }
        if (request.autores() == null || !request.autores().isPresent() || request.autores().get() == null || request.autores().get().isEmpty()) {
            errores.add("La obra debe tener al menos un autor asociado");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void validarActualizacion(ObraRequest request, Obra existente) {
        // Bloqueo optimista
        Long vExistente = existente.getVersion() != null ? existente.getVersion() : 0L;
        if (request.version() == null || !request.version().equals(vExistente)) {
            throw new ValidacionNegocioException("El registro fue modificado por otro usuario. Por favor recargue la página.");
        }

        List<String> errores = new ArrayList<>();
        if (request.titulo() != null && request.titulo().isPresent() && (request.titulo().get() == null || request.titulo().get().isBlank())) {
            errores.add("El título no puede estar vacío");
        }
        if (request.resumen() != null && request.resumen().isPresent() && (request.resumen().get() == null || request.resumen().get().isBlank())) {
            errores.add("Se debe de agregar un resumen");
        }
        if (request.idGeneros() != null && request.idGeneros().isPresent() && (request.idGeneros().get() == null || request.idGeneros().get().isEmpty())) {
            errores.add("La obra debe tener al menos un género");
        }
        if (request.idCategorias() != null && request.idCategorias().isPresent() && (request.idCategorias().get() == null || request.idCategorias().get().isEmpty())) {
            errores.add("Favor de elegir al menos una categoría para la obra");
        }
        if (request.autores() != null && request.autores().isPresent() && (request.autores().get() == null || request.autores().get().isEmpty())) {
            errores.add("La obra debe tener al menos un autor asociado");
        }
        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }

    private void validarUnicidadTitulo(String tituloNuevo, Obra existente) {
        if (tituloNuevo == null || tituloNuevo.isBlank()) {
            return;
        }
        String tituloVal = tituloNuevo.trim();
        boolean esNuevo = (existente == null);
        boolean tituloCambio = !esNuevo && !existente.getTitulo().equalsIgnoreCase(tituloVal);

        if ((esNuevo || tituloCambio) && obraRepository.existsByTituloIgnoreCase(tituloVal)) {
            throw new ValidacionNegocioException("Ya existe una obra con el título: " + tituloVal);
        }
    }

    private void asignarRelaciones(Obra obra, ObraRequest request) {
        List<String> errores = new ArrayList<>();

        // 1. Géneros
        if (request.idGeneros() != null && request.idGeneros().isPresent()) {
            Set<Long> ids = request.idGeneros().get();
            if (ids != null) {
                Set<Genero> generos = new HashSet<>();
                for (Long id : ids) {
                    generoRepository.findById(id)
                            .ifPresentOrElse(generos::add,
                                    () -> errores.add("El género con ID " + id + " no existe."));
                }
                obra.setGeneros(generos);
            }
        }

        // 2. Categorías
        if (request.idCategorias() != null && request.idCategorias().isPresent()) {
            Set<Long> ids = request.idCategorias().get();
            if (ids != null) {
                Set<Categoria> categorias = new HashSet<>();
                for (Long id : ids) {
                    categoriaRepository.findById(id)
                            .ifPresentOrElse(categorias::add,
                                    () -> errores.add("La categoría con ID " + id + " no existe."));
                }
                obra.setCategorias(categorias);
            }
        }

        // 3. Autores (ObraAutor)
        if (request.autores() != null && request.autores().isPresent()) {
            Set<ObraAutorRequest> autoresReq = request.autores().get();
            if (autoresReq != null) {
                obra.getObraAutores().clear();
                for (ObraAutorRequest autReq : autoresReq) {
                    if (autReq.idAutor() == null || autReq.idTipoAutoria() == null) {
                        errores.add("El ID del autor y el tipo de autoría son obligatorios.");
                        continue;
                    }
                    Autor autor = autorRepository.findById(autReq.idAutor()).orElse(null);
                    TipoAutoria tipo = tipoAutoriaRepository.findById(autReq.idTipoAutoria()).orElse(null);

                    if (autor == null) {
                        errores.add("El autor con ID " + autReq.idAutor() + " no existe.");
                    }
                    if (tipo == null) {
                        errores.add("El tipo de autoría con ID " + autReq.idTipoAutoria() + " no existe.");
                    }

                    if (autor != null && tipo != null) {
                        ObraAutor obraAutor = ObraAutor.builder()
                                .obra(obra)
                                .autor(autor)
                                .tipoAutoria(tipo)
                                .build();
                        obra.getObraAutores().add(obraAutor);
                    }
                }
            }
        }

        if (!errores.isEmpty()) {
            throw new ValidacionNegocioException(errores);
        }
    }
}
