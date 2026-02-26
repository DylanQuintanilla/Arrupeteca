package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.dto.request.ObraAutorRequest;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.*;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
    private final ObraMapper obraMapper;

    private final GeneroRepository generoRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final TipoAutoriaRepository tipoAutoriaRepository;


    @Override
    @Transactional(readOnly = true)
    public List<ObraResumen> obtenerTodasActivas() {

        return obraRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorIdActivo(Long id) {
        return obraRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Obra activa con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObraResumen> obtenerTodas() {

        return obraRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public ObraResumen obtenerPorId(Long id) {
        return obraRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Obra con ID: " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ObraResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return obraRepository.busquedaGlobalAvanzada(palabra, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ObraResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idGenero, Long idCategoria, Long idAutor,
                                                  String termino, Integer anioInicio, Integer anioFin,
                                                  String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return obraRepository.busquedaMaestraAdmin(esBorrado, idGenero, idCategoria, idAutor, termino, anioInicio, anioFin, sort);
    }

    @Override
    @Transactional
    public ObraResumen crear(ObraRequest request) {

        if (obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new RuntimeException("Ya existe una obra registrada con el título: " + request.getTitulo());
        }

        Obra obraNueva = obraMapper.toEntity(request);

        asignarGeneros(obraNueva, request.getIdGeneros());
        asignarCategorias(obraNueva, request.getIdCategorias());
        asignarAutores(obraNueva, request.getAutores());

        Obra obraGuardada = obraRepository.save(obraNueva);
        return obtenerPorId(obraGuardada.getId());
    }

    @Override
    @Transactional
    public ObraResumen actualizar(Long id, ObraRequest request) {
        Obra obraExistente = obraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Obra con ID: " + id));

        if (!obraExistente.getTitulo().equalsIgnoreCase(request.getTitulo()) &&
                obraRepository.existsByTituloIgnoreCase(request.getTitulo())) {
            throw new RuntimeException("Ya existe otra obra registrada con el título: " + request.getTitulo());
        }

        obraMapper.updateEntity(request, obraExistente);

        asignarGeneros(obraExistente, request.getIdGeneros());
        asignarCategorias(obraExistente, request.getIdCategorias());
        asignarAutores(obraExistente, request.getAutores());

        Obra obraActualizada = obraRepository.save(obraExistente);
        return obtenerPorId(obraActualizada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstadoLogico(Long id, boolean estado) {
        if (!obraRepository.existsById(id)) {
            throw new RuntimeException("No se encontró Obra con ID: " + id);
        }
        if (estado) {
            obraRepository.activarObra(id);
        } else {
            obraRepository.desactivarObra(id);
        }
    }

    //--------------------

    private void asignarGeneros(Obra obra, Set<Long> idGeneros) {
        obra.getGeneros().clear();
        if (idGeneros != null && !idGeneros.isEmpty()) {
            List<Genero> generosEncontrados = generoRepository.findAllById(idGeneros);
            if (generosEncontrados.size() != idGeneros.size()) {
                throw new RuntimeException("Uno o más Géneros indicados no existen");
            }
            obra.getGeneros().addAll(generosEncontrados);
        }
    }

    private void asignarCategorias(Obra obra, Set<Long> idCategorias) {
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
        // Obligatorio para orphanRemoval = true
        obra.getObraAutores().clear();

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

    private Sort construirSort(String ordenarPor, String direccion) {
        String campoOrden = (ordenarPor == null || ordenarPor.trim().isEmpty()) ? "titulo" : ordenarPor;
        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, campoOrden);
    }
}