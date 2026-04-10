package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.persistence.entity.TipoAutoria;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TipoAutoriaMapperTest {

    private final TipoAutoriaMapper mapper = Mappers.getMapper(TipoAutoriaMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder()
                .nombre("Autor Principal")
                .build();

        TipoAutoria entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        TipoAutoria entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        TipoAutoriaRequest request = TipoAutoriaRequest.builder()
                .nombre("Coautor")
                .build();
        TipoAutoria entity = new TipoAutoria();
        entity.setNombre("Autor Principal");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        TipoAutoria entity = new TipoAutoria();
        entity.setNombre("Autor Principal");

        mapper.updateEntity(null, entity);

        assertEquals("Autor Principal", entity.getNombre());
    }
}
