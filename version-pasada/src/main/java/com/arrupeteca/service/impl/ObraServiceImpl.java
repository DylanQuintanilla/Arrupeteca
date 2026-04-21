package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.*;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Page<ObraResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return obraRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorIdActivo(Long id) {
        return obraRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Obra activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return obraRepository.busquedaGlobal(palabra, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return obraRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorId(Long id) {
        return obraRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Obra con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ObraResumen> busquedaMaestraAdmin(Boolean esBorrado, String termino, Integer anioInicio, Integer anioFin, int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return obraRepository.busquedaMaestraAdmin(esBorrado, termino, anioInicio, anioFin, pageable);
    }

    @Override
    @Transactional
    public ObraResumen crear(ObraRequest request) {
        if (obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new RuntimeException("Ya existe una obra con el título: " + request.getTitulo());
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
                .orElseThrow(() -> new RuntimeException("No se encontró Obra con ID: " + id));

        if (!existente.getTitulo().equalsIgnoreCase(request.getTitulo()) &&
                obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new RuntimeException("Ya existe una obra con el título: " + request.getTitulo());
        }

        obraMapper.updateEntity(request, existente);

        asignarGeneros(existente, request.getIdGeneros());
        asignarCategorias(existente, request.getIdCategorias());

        existente.getObraAutores().clear();
        obraRepository.saveAndFlush(existente);

        asignarAutores(existente, request.getAutores());

        Obra actualizada = obraRepository.save(existente);
        return obtenerPorId(actualizada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!obraRepository.existsById(id)) {
            throw new RuntimeException("No se encontró Obra con ID: " + id);
        }
        if (estado) obraRepository.activar(id);
        else obraRepository.desactivar(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Pageable construirPageable(int page, int size, String ordenarPor, String direccion) {
        String campoOrden = (ordenarPor == null || ordenarPor.trim().isEmpty()) ? "titulo" : ordenarPor;
        if ("anio".equalsIgnoreCase(campoOrden)) campoOrden = "anioCreacion";

        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, campoOrden));
    }

    private void asignarGeneros(Obra obra, Set<Long> idGeneros) {
        if (obra.getGeneros() == null) obra.setGeneros(new HashSet<>());
        obra.getGeneros().clear();

        if (idGeneros != null && !idGeneros.isEmpty()) {
            List<Genero> generosEncontrados = generoRepository.findAllById(idGeneros);
            if (generosEncontrados.size() != idGeneros.size()) {
                throw new RuntimeException("Uno o más Géneros indicados no existen en la base de datos");
            }
            obra.getGeneros().addAll(generosEncontrados);
        }
    }

    private void asignarCategorias(Obra obra, Set<Long> idCategorias) {
        if (obra.getCategorias() == null) obra.setCategorias(new HashSet<>());
        obra.getCategorias().clear();

        if (idCategorias != null && !idCategorias.isEmpty()) {
            List<Categoria> categoriasEncontradas = categoriaRepository.findAllById(idCategorias);
            if (categoriasEncontradas.size() != idCategorias.size()) {
                throw new RuntimeException("Una o más Categorías indicadas no existen");
            }
            obra.getCategorias().addAll(categoriasEncontradas);
        }
    }

    private void asignarAutores(Obra obra, Set<ObraAutorRequest> autoresRequest) {
        if (autoresRequest != null && !autoresRequest.isEmpty()) {
            for (ObraAutorRequest req : autoresRequest) {
                Autor autor = autorRepository.findById(req.getIdAutor())
                        .orElseThrow(() -> new RuntimeException("Autor con ID " + req.getIdAutor() + " no encontrado"));

                TipoAutoria tipo = tipoAutoriaRepository.findById(req.getIdTipoAutoria())
                        .orElseThrow(() -> new RuntimeException("Tipo de Autoría con ID " + req.getIdTipoAutoria() + " no encontrado"));

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