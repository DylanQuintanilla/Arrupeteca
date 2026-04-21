package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.mapper.TipoAutoriaMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.TipoAutoria;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import com.arrupeteca.persistence.repository.TipoAutoriaRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.TipoAutoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoAutoriaServiceImpl implements TipoAutoriaService {

    private final TipoAutoriaRepository tipoAutoriaRepository;

    private final TipoAutoriaMapper tipoAutoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TipoAutoriaResumen> obtenerTodosActivos() {

        return tipoAutoriaRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorIdActivo(Long id) {
        return tipoAutoriaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro un tipo de autoria con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoAutoriaResumen> obtenerTodos() {
        return tipoAutoriaRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoAutoriaResumen obtenerPorId(Long id) {
        return tipoAutoriaRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro un tipo de autoria con ID: "+id));

    }

    @Override
    @Transactional
    public TipoAutoriaResumen crear(TipoAutoriaRequest request) {
        if(tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        TipoAutoria TipoAutoriaMapeada = tipoAutoriaMapper.toEntity(request);
        TipoAutoria TipoAutoriaGuarda = tipoAutoriaRepository.save(TipoAutoriaMapeada);
        return obtenerPorId(TipoAutoriaGuarda.getId());
    }

    @Override
    @Transactional
    public TipoAutoriaResumen actualizar(Long id, TipoAutoriaRequest request) {

        TipoAutoria TipoAutoriaEncontrada = tipoAutoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro un tipo de autoria con ID: "+id));

        if (!TipoAutoriaEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && tipoAutoriaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe una autoria con el nombre: "+request.getNombre());
        }

        tipoAutoriaMapper.updateEntity(request, TipoAutoriaEncontrada);
        TipoAutoria TipoAutoriaActualizada = tipoAutoriaRepository.save(TipoAutoriaEncontrada);

        return obtenerPorId(TipoAutoriaEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!tipoAutoriaRepository.existsById(id)){
            throw new RuntimeException("No se encontro un tipo de autoria con ID: "+id);
        }
        if (estado)
            tipoAutoriaRepository.activar(id);
        else
            tipoAutoriaRepository.desactivar(id);
    }
}
