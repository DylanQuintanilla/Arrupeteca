package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.ObraRequest;
import com.arrupeteca.mapper.ObraMapper;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.projection.ObraResumen;
import com.arrupeteca.persistence.repository.ObraRepository;
import com.arrupeteca.service.ObraService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObraServiceImpl implements ObraService {

    private final ObraRepository obraRepository;
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
    public Page<ObraResumen> busquedaMaestraAdmin(
            Boolean esBorrado, String termino, Integer anioInicio, Integer anioFin,
            int page, int size, String ordenarPor, String direccion) {

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

    private Pageable construirPageable(int page, int size, String ordenarPor, String direccion) {
        String campoOrden = (ordenarPor == null || ordenarPor.trim().isEmpty()) ? "titulo" : ordenarPor;

        if ("anio".equalsIgnoreCase(campoOrden)) campoOrden = "anioCreacion";

        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, campoOrden);
        return PageRequest.of(page, size, sort);
    }
}