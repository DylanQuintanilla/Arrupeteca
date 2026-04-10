package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.persistence.entity.Pais;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaisMapperTest {

    private final PaisMapper mapper = Mappers.getMapper(PaisMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        PaisRequest request = PaisRequest.builder()
                .nombre("El Salvador")
                .build();

        Pais entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Pais entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        PaisRequest request = PaisRequest.builder()
                .nombre("Guatemala")
                .build();
        Pais entity = new Pais();
        entity.setNombre("El Salvador");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Pais entity = new Pais();
        entity.setNombre("El Salvador");

        mapper.updateEntity(null, entity);

        assertEquals("El Salvador", entity.getNombre());
    }
}
