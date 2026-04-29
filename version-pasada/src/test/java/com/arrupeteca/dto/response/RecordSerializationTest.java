package com.arrupeteca.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.detail.CategoriaDetalleResponse;
import com.arrupeteca.dto.response.detail.EdificioDetalleResponse;
import com.arrupeteca.dto.response.detail.EditorialDetalleResponse;
import com.arrupeteca.dto.response.detail.EjemplarLibroDetalleResponse;
import com.arrupeteca.dto.response.detail.FormatoDetalleResponse;
import com.arrupeteca.dto.response.detail.GeneroDetalleResponse;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.detail.LibroDetalleResponse;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.detail.ObraAutorDetalleResponse;
import com.arrupeteca.dto.response.detail.ObraDetalleResponse;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.dto.response.summary.CategoriaResumen;
import com.arrupeteca.dto.response.summary.EdificioResumen;
import com.arrupeteca.dto.response.summary.EditorialResumen;
import com.arrupeteca.dto.response.summary.EjemplarLibroResumen;
import com.arrupeteca.dto.response.summary.FormatoResumen;
import com.arrupeteca.dto.response.summary.GeneroResumen;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.dto.response.summary.LibroResumen;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
import com.arrupeteca.dto.response.summary.ObraResumen;
import com.arrupeteca.dto.response.summary.PaisResumen;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.dto.response.summary.TipoAutoriaResumen;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        AutorResumen autor = new AutorResumen(1L, "Nombre", "Seudonimo", 1900, 1980, "Bio", false, "Pais", "Nacionalidad");
        CategoriaResumen cat = new CategoriaResumen(1L, "Cat", false);
        EdificioResumen edif = new EdificioResumen(1L, "Edif", false);
        EditorialResumen edit = new EditorialResumen(1L, "Edit", "Dir", "Email", "Tel", "Contacto", false);
        EjemplarLibroResumen ejem = new EjemplarLibroResumen(1L, "Comentario", false, "BUENO", "DISPONIBLE", "Libro", "Mueble");
        FormatoResumen form = new FormatoResumen(1L, "Form", false);
        GeneroResumen gen = new GeneroResumen(1L, "Gen", false);
        IdiomaResumen idio = new IdiomaResumen(1L, "Idio", false);
        LibroResumen libro = new LibroResumen(1L, "123", 2021, BigDecimal.TEN, "URL", BigDecimal.ONE, false, "G", "C",
                "Obra", "Edit", "Form");
        MuebleResumen mueb = new MuebleResumen(1L, "Mueb", false, "Salon");
        NacionalidadResumen nac = new NacionalidadResumen(1L, "Nac", false);
        ObraResumen obra = new ObraResumen(1L, "Obra", 2020, "Desc", false);
        PaisResumen pais = new PaisResumen(1L, "Pais", false);
        PlantaResumen plan = new PlantaResumen(1L, "Plan", false, "Edif");
        SalonResumen sal = new SalonResumen(1L, "Sal", false, "Plan");
        TipoAutoriaResumen tipo = new TipoAutoriaResumen(1L, "Tipo", false);

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
        
        AutorDetalleResponse autor = new AutorDetalleResponse(1L, "N", "S", 1900, 1980, "B", true, "U", now, "U", now,
                null, null, null, null);
        CategoriaDetalleResponse cat = new CategoriaDetalleResponse(1L, "C", true, "U", now, "U", now,
                Collections.emptySet());
        EdificioDetalleResponse edif = new EdificioDetalleResponse(1L, "E", true, "U", now, "U", now);
        EditorialDetalleResponse edit = new EditorialDetalleResponse(1L, "E", "D", "T", "D", "E", true, "U", now, "U", now);
        EjemplarLibroDetalleResponse ejem = new EjemplarLibroDetalleResponse(1L, "C", true, "U", "U", "U", now, "U", now,
                null, null, "S");
        FormatoDetalleResponse form = new FormatoDetalleResponse(1L, "F", true, "U", now, "U", now);
        GeneroDetalleResponse gen = new GeneroDetalleResponse(1L, "G", true, "U", now, "U", now, Collections.emptySet());
        IdiomaDetalleResponse idio = new IdiomaDetalleResponse(1L, "I", true, "U", now, "U", now, Collections.emptySet());
        LibroDetalleResponse libro = new LibroDetalleResponse(1L, "I", 2021, BigDecimal.TEN, "U", BigDecimal.ONE, true,
                "G", "C", "U", now, "U", now, null, null, null, Collections.emptySet());
        MuebleDetalleResponse mueb = new MuebleDetalleResponse(1L, "M", true, "U", now, "U", now, null);
        ObraAutorDetalleResponse obraAut = new ObraAutorDetalleResponse(1L, null, null);
        ObraDetalleResponse obra = new ObraDetalleResponse(1L, "T", 2020, "D", true, "U", now, "U", now,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        PlantaDetalleResponse plan = new PlantaDetalleResponse(1L, "P", true, "U", now, "U", now, null);
        SalonDetalleResponse sal = new SalonDetalleResponse(1L, "S", true, "U", now, "U", now, null);

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
        verifySerialization(obraAut);
        verifySerialization(obra);
        verifySerialization(plan);
        verifySerialization(sal);
    }

    private void verifySerialization(Object record) throws Exception {
        String json = objectMapper.writeValueAsString(record);
        assertThat(json).isNotBlank();
        Object deserialized = objectMapper.readValue(json, record.getClass());
        assertThat(deserialized).isEqualTo(record);
    }
}
