package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EditorialMapper;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.repository.EditorialRepository;
import com.arrupeteca.service.EditorialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EditorialServiceImpl implements EditorialService {

    private final EditorialRepository editorialRepository;
    private final EditorialMapper editorialMapper;

    @Override
    @Cacheable(value = "editoriales-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<EditorialResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo editoriales activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return editorialRepository.buscarActivosPorNombre(filtro).stream()
                .map(editorialMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EditorialResponse> obtenerTodos(String nombre, String correo, String personaContacto, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de editoriales con filtros - nombre: {}, correo: {}, personaContacto: {}, borradoLogico: {}, pageable: {}",
                nombre, correo, personaContacto, borradoLogico, pageable);
        String nFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String cFiltro = (correo != null && !correo.trim().isEmpty()) ? correo.trim() : null;
        String pFiltro = (personaContacto != null && !personaContacto.trim().isEmpty()) ? personaContacto.trim() : null;
        return editorialRepository.busquedaAdmin(nFiltro, cFiltro, pFiltro, borradoLogico, pageable)
                .map(editorialMapper::toResponse);
    }

    @Override
    public EditorialResponse obtenerPorId(Long id) {
        log.debug("Buscando editoriales por ID: {}", id);
        return editorialRepository.findById(id)
                .map(editorialMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) editorial con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public EditorialResponse crear(EditorialRequest request) {
        log.info("Creando nueva editorial con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        String correoTrimmed = request.correo().trim();
        validarUnicidad(nombreTrimmed, correoTrimmed, null);

        Editorial editorial = editorialMapper.toEntity(request);
        editorial.setNombre(nombreTrimmed);
        editorial.setCorreo(correoTrimmed);

        Editorial guardada = editorialRepository.save(editorial);
        log.info("Editorial creada exitosamente con ID: {}", guardada.getId());
        return editorialMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public EditorialResponse actualizar(Long id, EditorialRequest request) {
        log.info("Actualizando editorial con ID: {}", id);
        Editorial editorialExistente = editorialRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró una editorial con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        String correoTrimmed = request.correo().trim();
        validarUnicidad(nombreTrimmed, correoTrimmed, editorialExistente);

        editorialMapper.updateEntity(request, editorialExistente);
        editorialExistente.setNombre(nombreTrimmed);
        editorialExistente.setCorreo(correoTrimmed);

        Editorial actualizada = editorialRepository.save(editorialExistente);
        log.info("Editorial con ID: {} actualizada exitosamente", id);
        return editorialMapper.toResponse(actualizada);
    }

    private void validarUnicidad(String nombreNuevo, String correoNuevo, Editorial editorialExistente) {
        boolean esNuevo = (editorialExistente == null);
        boolean nombreCambio = !esNuevo && !editorialExistente.getNombre().equalsIgnoreCase(nombreNuevo);
        boolean correoCambio = !esNuevo && !editorialExistente.getCorreo().equalsIgnoreCase(correoNuevo);

        if ((esNuevo || nombreCambio) && editorialRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar editorial por nombre: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe una editorial con el nombre: " + nombreNuevo);
        }
        if ((esNuevo || correoCambio) && editorialRepository.existsByCorreoIgnoreCase(correoNuevo)) {
            log.warn("Intento de duplicar editorial por correo: {}", correoNuevo);
            throw new ValidacionNegocioException("Ya existe una editorial con el correo: " + correoNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "editoriales-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de editoriales ID: {} a {}", id, !estado);
        if (!editorialRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) editorial con ID: " + id);
        }
        if (estado) {
            editorialRepository.activar(id);
        } else {
            editorialRepository.desactivar(id);
        }
    }
}
