package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.persistence.entity.Nacionalidad;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NacionalidadMapperTest {

    private final NacionalidadMapper mapper = Mappers.getMapper(NacionalidadMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        NacionalidadRequest request = NacionalidadRequest.builder()
                .nombre("Salvadoreña")
                .build();

        Nacionalidad entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Nacionalidad entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        NacionalidadRequest request = NacionalidadRequest.builder()
                .nombre("Guatemalteca")
                .build();
        Nacionalidad entity = new Nacionalidad();
        entity.setNombre("Salvadoreña");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Nacionalidad entity = new Nacionalidad();
        entity.setNombre("Salvadoreña");

        mapper.updateEntity(null, entity);

        assertEquals("Salvadoreña", entity.getNombre());
    }
}
