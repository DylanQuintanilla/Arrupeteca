package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.persistence.entity.Formato;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FormatoMapperTest {

    private final FormatoMapper mapper = Mappers.getMapper(FormatoMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        FormatoRequest request = FormatoRequest.builder()
                .nombre("Tapa Dura")
                .build();

        Formato entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Formato entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        FormatoRequest request = FormatoRequest.builder()
                .nombre("Bolsillo")
                .build();
        Formato entity = new Formato();
        entity.setNombre("Tapa Dura");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Formato entity = new Formato();
        entity.setNombre("Tapa Dura");

        mapper.updateEntity(null, entity);

        assertEquals("Tapa Dura", entity.getNombre());
    }
}
