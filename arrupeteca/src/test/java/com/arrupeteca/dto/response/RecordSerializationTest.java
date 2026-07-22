package com.arrupeteca.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.dto.response.GeneroResponse;
import com.arrupeteca.dto.response.IdiomaResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.dto.response.PaisResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.detail.ObraAutorDetalleResponse;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCoverAllSummaryRecords() throws Exception {
        AutorResumenResponse autor = AutorResumenResponse.builder()
                .id(1L)
                .nombre("Nombre")
                .seudonimo("Seudonimo")
                .anioNacimiento(Year.of(1900))
                .anioFallecimiento(Year.of(1980))
                .biografiaResumida("Bio")
                .borradoLogico(false)
                .nombrePais("Pais")
                .nombreNacionalidad("Nacionalidad")
                .build();
        CategoriaResponse cat = new CategoriaResponse(1L, "Cat", false);
        EdificioResponse edif = new EdificioResponse(1L, "Edif", false);
        EditorialResponse edit = new EditorialResponse(1L, "Edit", "Dir", "Email", "Tel", "Contacto", false);
        EjemplarLibroResumen ejem = new EjemplarLibroResumen(1L, "Comentario", false, "BUENO", "DISPONIBLE", "Libro", "Mueble");
        FormatoResponse form = new FormatoResponse(1L, "Form", false);
        GeneroResponse gen = new GeneroResponse(1L, "Gen", false);
        IdiomaResponse idio = new IdiomaResponse(1L, "Idio", false);
        LibroResumen libro = new LibroResumen(1L, "123", 2021, BigDecimal.TEN, "URL", BigDecimal.ONE, false, "G", "C",
                "Obra", "Edit", "Form");
        NacionalidadResponse nac = new NacionalidadResponse(1L, "Nac", false);
        ObraResumen obra = new ObraResumen(1L, "Obra", 2020, "Desc", false);
        PaisResponse pais = new PaisResponse(1L, "Pais", false);
        PlantaResponse plan = new PlantaResponse(1L, "Plan", false, edif);
        SalonResponse sal = new SalonResponse(1L, "Sal", false, plan);
        MuebleResponse mueb = new MuebleResponse(1L, "Mueb", false, sal);
        TipoAutoriaResponse tipo = new TipoAutoriaResponse(1L, "Tipo", false);

        verifySerialization(autor);
        verifySerialization(cat);
        verifySerialization(edif);
        verifySerialization(edit);
        verifySerialization(ejem);
        verifySerialization(form);
        verifySerialization(gen);
        verifySerialization(idio);
        verifySerialization(libro);
        verifySerialization(mueb);
        verifySerialization(nac);
        verifySerialization(obra);
        verifySerialization(pais);
        verifySerialization(plan);
        verifySerialization(sal);
        verifySerialization(tipo);
    }

    @Test
    void shouldCoverAllDetailRecords() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        
        AutorDetalleResponse autor = AutorDetalleResponse.builder()
                .id(1L)
                .nombre("N")
                .seudonimo("S")
                .anioNacimiento(Year.of(1900))
                .anioFallecimiento(Year.of(1980))
                .biografia("B")
                .borradoLogico(true)
                .creadoPor("U")
                .fechaCreacion(now)
                .actualizadoPor("U")
                .fechaActualizacion(now)
                .build();
        EjemplarLibroDetalleResponse ejem = EjemplarLibroDetalleResponse.builder()
                .id(1L).comentario("C").version(0L).borradoLogico(true).estadoFisico("U").disponibilidad("U")
                .creadoPor("U").fechaCreacion(now).modificadoPor("U").fechaModificacion(now).ubicacion("S").build();
        LibroDetalleResponse libro = LibroDetalleResponse.builder()
                .id(1L).isbn("I").anioPublicacion(2021).precio(BigDecimal.TEN).urlImagen("U").mora(BigDecimal.ONE)
                .version(0L).borradoLogico(true).grado("G").ciclo("C").creadoPor("U").fechaCreacion(now).modificadoPor("U").fechaModificacion(now)
                .idiomas(Collections.emptySet()).build();
        ObraAutorDetalleResponse obraAut = new ObraAutorDetalleResponse(1L, null, null);
        ObraDetalleResponse obra = ObraDetalleResponse.builder()
                .id(1L).titulo("T").anioCreacion(2020).resumen("D").version(0L).borradoLogico(true)
                .creadoPor("U").fechaCreacion(now).modificadoPor("U").fechaModificacion(now)
                .generos(Collections.emptySet()).categorias(Collections.emptySet()).autores(Collections.emptySet()).build();
        verifySerialization(autor);
        verifySerialization(ejem);
        verifySerialization(libro);
        verifySerialization(obraAut);
        verifySerialization(obra);
    }

    private void verifySerialization(Object record) throws Exception {
        String json = objectMapper.writeValueAsString(record);
        assertThat(json).isNotBlank();
        Object deserialized = objectMapper.readValue(json, record.getClass());
        assertThat(deserialized).isEqualTo(record);
    }
}
