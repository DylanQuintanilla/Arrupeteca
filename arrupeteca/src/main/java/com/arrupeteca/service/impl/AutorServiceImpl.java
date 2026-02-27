package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.mapper.AutorMapper;
import com.arrupeteca.persistence.entity.Autor;
import com.arrupeteca.persistence.projection.AutorResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.AutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<AutorResumen> obtenerTodosActivos() {
        return autorRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public AutorResumen obtenerPorIdActivo(Long id) {
        return autorRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Autor activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> obtenerTodos() {
        return autorRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public AutorResumen obtenerPorId(Long id) {
        return autorRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Autor con ID: " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> busquedaGlobalAvanzada(String palabra, Integer inicio, Integer fin, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return autorRepository.busquedaGlobalAvanzada(palabra, inicio, fin, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idNacionalidad, Long idPais, Long idGenero, Long idObra, String terminoBusqueda, Integer anioInicio, Integer anioFin, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return autorRepository.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero, idObra, terminoBusqueda, anioInicio, anioFin, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AutorResumen> buscarAutoresVivosEnRango(Integer anioInicio, Integer anioFin) {
        return autorRepository.buscarAutoresVivosEnRango(anioInicio, anioFin);
    }

    @Override
    @Transactional
    public AutorResumen crear(AutorRequest request) {
        if (request.getSeudonimo() != null && autorRepository.existsBySeudonimoIgnoreCase(request.getSeudonimo())) {
            throw new RuntimeException("El seudónimo " + request.getSeudonimo() + " ya está en uso.");
        }

        Autor autorNuevo = autorMapper.toEntity(request);
        asignarRelaciones(autorNuevo, request);

        Autor autorGuardado = autorRepository.save(autorNuevo);
        return obtenerPorId(autorGuardado.getId());
    }

    @Override
    @Transactional
    public AutorResumen actualizar(Long id, AutorRequest request) {
        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Autor con ID: " + id));

        // Validación de seudónimo (solo si le cambia el nombre y no es nulo)
        if (request.getSeudonimo() != null &&
                (autorExistente.getSeudonimo() == null || !autorExistente.getSeudonimo().equalsIgnoreCase(request.getSeudonimo())) &&
                autorRepository.existsBySeudonimoIgnoreCase(request.getSeudonimo())) {
            throw new RuntimeException("El seudónimo " + request.getSeudonimo() + " ya está en uso por otro autor.");
        }

        autorMapper.updateEntity(request, autorExistente);
        asignarRelaciones(autorExistente, request);

        Autor autorActualizado = autorRepository.save(autorExistente);
        return obtenerPorId(autorActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstadoLogico(Long id, boolean estado) {
        if (!autorRepository.existsById(id)) {
            throw new RuntimeException("No se encontró Autor con ID: " + id);
        }
        if (estado) autorRepository.activarAutor(id);
        else autorRepository.desactivarAutor(id);
    }

    // -----------------------------

    private void asignarRelaciones(Autor autor, AutorRequest request) {
        if (request.getIdPaisNacimiento() != null) {
            autor.setPaisNacimiento(paisRepository.findById(request.getIdPaisNacimiento())
                    .orElseThrow(() -> new RuntimeException("El País indicado no existe")));
        } else {
            autor.setPaisNacimiento(null);
        }

        if (request.getIdNacionalidad() != null) {
            autor.setNacionalidad(nacionalidadRepository.findById(request.getIdNacionalidad())
                    .orElseThrow(() -> new RuntimeException("La Nacionalidad indicada no existe")));
        } else {
            autor.setNacionalidad(null);
        }

        if (request.getIdGeneroPrincipal() != null) {
            autor.setGeneroPrincipal(generoRepository.findById(request.getIdGeneroPrincipal())
                    .orElseThrow(() -> new RuntimeException("El Género indicado no existe")));
        } else {
            autor.setGeneroPrincipal(null);
        }

        if (request.getIdObraCumbre() != null) {
            autor.setObraCumbre(obraRepository.findById(request.getIdObraCumbre())
                    .orElseThrow(() -> new RuntimeException("La Obra Cumbre indicada no existe")));
        } else {
            autor.setObraCumbre(null);
        }
    }

    private Sort construirSort(String ordenarPor, String direccion) {

        String campoOrden = (ordenarPor == null || ordenarPor.trim().isEmpty()) ? "nombre" : ordenarPor;

        Sort.Direction direction = (direccion != null
                && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, campoOrden);
    }
}