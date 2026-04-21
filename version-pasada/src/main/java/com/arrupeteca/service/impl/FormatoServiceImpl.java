package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.mapper.FormatoMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.projection.FormatoResumen;
import com.arrupeteca.persistence.repository.FormatoRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.FormatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormatoServiceImpl implements FormatoService {

    private final FormatoRepository formatoRepository;
    
    private final FormatoMapper formatoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FormatoResumen> obtenerTodosActivos() {

        return formatoRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoResumen obtenerPorIdActivo(Long id) {
        return formatoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Formato con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormatoResumen> obtenerTodos() {
        return formatoRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public FormatoResumen obtenerPorId(Long id) {
        return formatoRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Formato con ID: "+id));

    }

    @Override
    @Transactional
    public FormatoResumen crear(FormatoRequest request) {
        if(formatoRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        Formato FormatoMapeada = formatoMapper.toEntity(request);
        Formato FormatoGuarda = formatoRepository.save(FormatoMapeada);
        return obtenerPorId(FormatoGuarda.getId());
    }

    @Override
    @Transactional
    public FormatoResumen actualizar(Long id, FormatoRequest request) {

        Formato FormatoEncontrada = formatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro Formato con ID: "+id));

        if (!FormatoEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && formatoRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un Formato con el nombre: "+request.getNombre());
        }

        formatoMapper.updateEntity(request, FormatoEncontrada);
        Formato FormatoActualizada = formatoRepository.save(FormatoEncontrada);

        return obtenerPorId(FormatoEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!formatoRepository.existsById(id)){
            throw new RuntimeException("No se encontro Formato con ID: "+id);
        }
        if (estado)
            formatoRepository.activar(id);
        else
            formatoRepository.desactivar(id);
    }
}
