package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.service.EditorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;

    private final EditorialMapper editorialMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EditorialResumen> obtenerTodosActivos() {

        return editorialRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResumen obtenerPorIdActivo(Long id) {
        return editorialRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Editorial con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EditorialResumen> obtenerTodos() {
        return editorialRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public EditorialResumen obtenerPorId(Long id) {
        return editorialRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Editorial con ID: "+id));

    }

    @Override
    @Transactional
    public EditorialResumen crear(EditorialRequest request) {
        if(editorialRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        if(editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())){
            throw new RuntimeException("El correo: " + request.getCorreo() + " ya está registrado en otra editorial");
        }
        Editorial EditorialMapeada = editorialMapper.toEntity(request);
        Editorial EditorialGuarda = editorialRepository.save(EditorialMapeada);
        return obtenerPorId(EditorialGuarda.getId());
    }

    @Override
    @Transactional
    public EditorialResumen actualizar(Long id, EditorialRequest request) {

        Editorial EditorialEncontrada = editorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro Editorial con ID: "+id));

        if (!EditorialEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && editorialRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe una editorial con el nombre: "+request.getNombre());
        }

        if (!EditorialEncontrada.getCorreo().equalsIgnoreCase(request.getCorreo())
                && editorialRepository.existsByCorreoIgnoreCase(request.getCorreo())){
            throw new RuntimeException("Ya existe una editorial con el correo: "+request.getCorreo());
        }

        editorialMapper.updateEntity(request, EditorialEncontrada);
        Editorial EditorialActualizada = editorialRepository.save(EditorialEncontrada);

        return obtenerPorId(EditorialEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!editorialRepository.existsById(id)){
            throw new RuntimeException("No se encontro Editorial con ID: "+id);
        }
        if (estado)
            editorialRepository.activar(id);
        else
            editorialRepository.desactivar(id);
    }
}
