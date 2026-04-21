package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.mapper.PaisMapper;
import com.arrupeteca.persistence.entity.Pais;
import com.arrupeteca.persistence.projection.PaisResumen;
import com.arrupeteca.persistence.repository.PaisRepository;
import com.arrupeteca.service.PaisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaisServiceImpl implements PaisService {

    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaisResumen> obtenerTodosActivos() {
        return paisRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorIdActivo(Long id) {
        return paisRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe un pais con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaisResumen> obtenerTodos() {
        return paisRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public PaisResumen obtenerPorId(Long id) {
        return paisRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No existe un pais con ID: " +id));
    }

    @Override
    @Transactional
    public PaisResumen crear(PaisRequest paisRequest) {
        if(paisRepository.existsByNombreIgnoreCase(paisRequest.getNombre())){
            throw new RuntimeException("Ya existe un pais con el nombre: "+paisRequest.getNombre());
        }

        Pais paisNuevo = paisMapper.toEntity(paisRequest);
        Pais paisGuardado = paisRepository.save(paisNuevo);
        return obtenerPorId(paisGuardado.getId());
    }

    @Override
    @Transactional
    public PaisResumen actualizar(Long id, PaisRequest request) {
        Pais paisExistente = paisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encuentra el pais con ID: " +id));

        if (!paisExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && paisRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe otro pais con el nombre: "+request.getNombre());
        }

        paisMapper.updateEntity(request, paisExistente);
        Pais paisActualizado = paisRepository.save(paisExistente);

        return obtenerPorId(paisActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstadoLogico(Long id, boolean estado) {
        if (!paisRepository.existsById(id)){
            throw new RuntimeException("No se encuentra el pais con ID: "+id);
        }

        if (estado){
            paisRepository.activar(id);
        }
        else {
            paisRepository.desactivar(id);
        }


    }
}
