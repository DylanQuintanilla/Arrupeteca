package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.entity.Editorial;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EditorialMapperTest {

    private final EditorialMapper mapper = Mappers.getMapper(EditorialMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Editorial Arrupe")
                .correo("contacto@arrupe.com")
                .telefono("2222-3333")
                .personaContacto("Juan Perez")
                .direccion("Calle 1")
                .build();

        Editorial entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
        assertEquals(request.getCorreo(), entity.getCorreo());
        assertEquals(request.getTelefono(), entity.getTelefono());
        assertEquals(request.getPersonaContacto(), entity.getPersonaContacto());
        assertEquals(request.getDireccion(), entity.getDireccion());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Editorial entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        EditorialRequest request = EditorialRequest.builder()
                .nombre("Editorial Updated")
                .correo("updated@arrupe.com")
                .build();
        Editorial entity = new Editorial();
        entity.setNombre("Editorial Old");
        entity.setCorreo("old@arrupe.com");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
        assertEquals(request.getCorreo(), entity.getCorreo());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Editorial entity = new Editorial();
        entity.setNombre("Editorial");

        mapper.updateEntity(null, entity);

        assertEquals("Editorial", entity.getNombre());
    }
}
