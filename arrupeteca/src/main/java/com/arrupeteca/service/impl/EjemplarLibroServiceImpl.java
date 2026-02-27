package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.EjemplarLibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjemplarLibroServiceImpl implements EjemplarLibroService {

    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EjemplarLibroMapper ejemplarLibroMapper;

    private final LibroRepository libroRepository;
    private final MuebleRepository muebleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarLibroResumen> obtenerTodosActivos() {
        return ejemplarLibroRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroResumen obtenerPorIdActivo(Long id) {
        return ejemplarLibroRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No está activo el ejemplar con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarLibroResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return ejemplarLibroRepository.busquedaGlobalAvanzada(palabra, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarLibroResumen> obtenerTodos() {
        return ejemplarLibroRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroResumen obtenerPorId(Long id) {
        return ejemplarLibroRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el ejemplar con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarLibroResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idLibro, Long idMueble, EstadoFisico estadoFisico,
                                                           Disponibilidad disponibilidad, String termino, String ordenarPor,
                                                           String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return ejemplarLibroRepository.busquedaMaestraAdmin(esBorrado, idLibro, idMueble, estadoFisico, disponibilidad, termino, sort);
    }

    @Override
    @Transactional
    public EjemplarLibroResumen crear(EjemplarLibroRequest request) {
        EjemplarLibro ejemplarLibroCreado = ejemplarLibroMapper.toEntity(request);
        asignarRelaciones(ejemplarLibroCreado, request);

        EjemplarLibro ejemplarLibroGuardado = ejemplarLibroRepository.save(ejemplarLibroCreado);
        return obtenerPorId(ejemplarLibroGuardado.getId());
    }

    @Override
    @Transactional
    public EjemplarLibroResumen actualizar(Long id, EjemplarLibroRequest request) {
        EjemplarLibro ejemplarLibroEncontrado = ejemplarLibroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró un ejemplar con ID: " + id));

        ejemplarLibroMapper.updateEntity(request, ejemplarLibroEncontrado);

        asignarRelaciones(ejemplarLibroEncontrado, request);

        EjemplarLibro ejemplarLibroActualizado = ejemplarLibroRepository.save(ejemplarLibroEncontrado);
        return obtenerPorId(ejemplarLibroActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarBorradoLogico(Long id, Boolean estado) {
        if (!ejemplarLibroRepository.existsById(id)){
            throw new RuntimeException("No se encontró un ejemplar con ID: " + id);
        }

        if(estado){
            ejemplarLibroRepository.activar(id);
        } else {
            ejemplarLibroRepository.desactivar(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long cantidadEjemplaresActivosPorIdLibroYDisponibilidad(Long idLibro, Disponibilidad disponibilidad) {
        return ejemplarLibroRepository.countByLibro_IdAndDisponibilidadAndBorradoLogicoFalse(idLibro, disponibilidad);
    }

    //---------------

    private Sort construirSort(String ordenarPor, String direccion) {
        String campoOrden = "libro.obra.titulo";

        if (ordenarPor != null && !ordenarPor.trim().isEmpty()) {
            if (ordenarPor.equalsIgnoreCase("titulo")) {
                campoOrden = "libro.obra.titulo";
            } else if (ordenarPor.equalsIgnoreCase("editorial")) {
                campoOrden = "libro.editorial.nombre";
            } else if (ordenarPor.equalsIgnoreCase("ubicacion")) {
                campoOrden = "mueble.nombre";
            } else {
                campoOrden = ordenarPor;
            }
        }

        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, campoOrden);
    }

    private void asignarRelaciones(EjemplarLibro ejemplarLibro, EjemplarLibroRequest request){
        if (request.getIdLibro() != null){
            ejemplarLibro.setLibro(libroRepository.findById(request.getIdLibro())
                    .orElseThrow(() -> new RuntimeException("No se encontró un Libro con ID: " + request.getIdLibro())));
        }

        if (request.getIdMueble() != null){
            ejemplarLibro.setMueble(muebleRepository.findById(request.getIdMueble())
                    .orElseThrow(() -> new RuntimeException("No se encontró un Mueble con ID: " + request.getIdMueble())));
        }
    }
}