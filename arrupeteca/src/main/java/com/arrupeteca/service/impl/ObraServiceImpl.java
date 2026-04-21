package com.arrupeteca.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.entity.Categoria;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.ObraAutor;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.AutorRepository;
import com.arrupeteca.persistence.repository.CategoriaRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.service.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
    private final GeneroRepository generoRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final TipoAutoriaRepository tipoAutoriaRepository;

    private final ObraMapper obraMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> obtenerTodosActivos(Pageable pageable) {
        return obraRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorIdActivo(Long id) {
        return obraRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return obraRepository.busquedaGlobal(palabra, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> obtenerTodos(Pageable pageable) {
        return obraRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorId(Long id) {
        return obraRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino,
                                                  Integer anioInicio, Integer anioFin, Pageable pageable) {
        return obraRepository.busquedaMaestraAdmin(esBorrado, termino, anioInicio, anioFin, pageable);
    }

    @Override
    @Transactional
    public ObraResumen crear(ObraRequest request) {
        if (obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new ValidacionNegocioException("Ya existe una obra con el título: " + request.getTitulo());
        }

        Obra nuevaObra = obraMapper.toEntity(request);

        asignarGeneros(nuevaObra, request.getIdGeneros());
        asignarCategorias(nuevaObra, request.getIdCategorias());
        asignarAutores(nuevaObra, request.getAutores());

        Obra guardada = obraRepository.save(nuevaObra);
        return obtenerPorId(guardada.getId());
    }

    @Override
    @Transactional
    public ObraResumen actualizar(Long id, ObraRequest request) {
        Obra existente = obraRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Obra con ID: " + id));

        if (!existente.getTitulo().equalsIgnoreCase(request.getTitulo())
                && obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new ValidacionNegocioException("Ya existe una obra con el título: " + request.getTitulo());
        }

        obraMapper.updateEntity(request, existente);

        asignarGeneros(existente, request.getIdGeneros());
        asignarCategorias(existente, request.getIdCategorias());

        existente.getObraAutores().clear();
        obraRepository.saveAndFlush(existente);

        asignarAutores(existente, request.getAutores());

        obraRepository.save(existente);
        return obtenerPorId(existente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!obraRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Obra con ID: " + id);
        }
        if (estado) {
            obraRepository.activar(id);
        } else {
            obraRepository.desactivar(id);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void asignarGeneros(Obra obra, Set<Long> idGeneros) {
        if (obra.getGeneros() == null) {
            obra.setGeneros(new HashSet<>());
        }
        obra.getGeneros().clear();

        if (idGeneros != null && !idGeneros.isEmpty()) {
            List<Genero> generosEncontrados = generoRepository.findAllById(idGeneros);
            if (generosEncontrados.size() != idGeneros.size()) {
                throw new ValidacionNegocioException("Uno o más Géneros indicados no existen en la base de datos");
            }
            obra.getGeneros().addAll(generosEncontrados);
        }
    }

    private void asignarCategorias(Obra obra, Set<Long> idCategorias) {
        if (obra.getCategorias() == null) {
            obra.setCategorias(new HashSet<>());
        }
        obra.getCategorias().clear();

        if (idCategorias != null && !idCategorias.isEmpty()) {
            List<Categoria> categoriasEncontradas = categoriaRepository.findAllById(idCategorias);
            if (categoriasEncontradas.size() != idCategorias.size()) {
                throw new ValidacionNegocioException("Una o más Categorías indicadas no existen");
            }
            obra.getCategorias().addAll(categoriasEncontradas);
        }
    }

    private void asignarAutores(Obra obra, Set<ObraAutorRequest> autoresRequest) {
        if (autoresRequest != null && !autoresRequest.isEmpty()) {
            for (ObraAutorRequest req : autoresRequest) {
                Autor autor = autorRepository.findById(req.getIdAutor())
                        .orElseThrow(() -> new ValidacionNegocioException("Autor con ID "
                                + req.getIdAutor() + " no encontrado"));

                TipoAutoria tipo = tipoAutoriaRepository.findById(req.getIdTipoAutoria())
                        .orElseThrow(() -> new ValidacionNegocioException("Tipo de Autoría con ID "
                                + req.getIdTipoAutoria() + " no encontrado"));

                ObraAutor nuevoEnlace = ObraAutor.builder()
                        .autor(autor)
                        .tipoAutoria(tipo)
                        .obra(obra)
                        .borradoLogico(false)
                        .build();

                obra.getObraAutores().add(nuevoEnlace);
            }
        }
    }
}
