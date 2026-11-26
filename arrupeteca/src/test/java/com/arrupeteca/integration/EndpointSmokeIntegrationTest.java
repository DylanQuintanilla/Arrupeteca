package com.arrupeteca.integration;

import com.arrupeteca.persistence.entity.Disponibilidad;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.entity.Editorial;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.entity.Formato;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.entity.Obra;
import com.arrupeteca.persistence.entity.Parentesco;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.entity.Responsable;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.entity.TipoUsuario;
import com.arrupeteca.persistence.entity.Usuario;
import com.arrupeteca.prestamo.persistence.entity.EstadoEjemplarPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoPrestamo;
import com.arrupeteca.prestamo.persistence.entity.EstadoSolicitud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración End-to-End reales (sin @MockitoBean) contra H2
 * para validar seguridad, parámetros, JPA/Hibernate y serialización JSON de punta a punta.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointSmokeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private record SeedContext(
            Usuario admin,
            Usuario alumno,
            Usuario bibliotecario,
            EstadoSolicitud estadoPendiente,
            EstadoSolicitud estadoAprobada,
            EstadoPrestamo estadoPrestamoActivo,
            EstadoPrestamo estadoPrestamoEntregado,
            EstadoEjemplarPrestamo estadoEjemplarPrestamo,
            EstadoEjemplarPrestamo estadoEjemplarDevuelto,
            Libro libro,
            EjemplarLibro ejemplar,
            EstadoFisico estadoFisico
    ) {}

    private SeedContext seedBaseData() {
        TipoUsuario tipoAdmin = TipoUsuario.builder().nombre("admin").build();
        TipoUsuario tipoAlumno = TipoUsuario.builder().nombre("alumno").build();
        TipoUsuario tipoBibliotecario = TipoUsuario.builder().nombre("bibliotecario").build();
        em.persist(tipoAdmin);
        em.persist(tipoAlumno);
        em.persist(tipoBibliotecario);

        Usuario admin = Usuario.builder()
                .nombres("Admin")
                .apellidos("Prueba")
                .correo("admin.e2e@arrupeteca.com")
                .contrasena(passwordEncoder.encode("Password123!"))
                .carnet("ADM-E2E")
                .tipoUsuario(tipoAdmin)
                .build();
        Usuario alumno = Usuario.builder()
                .nombres("Alumno")
                .apellidos("Prueba")
                .correo("alumno.e2e@arrupeteca.com")
                .contrasena(passwordEncoder.encode("Password123!"))
                .carnet("ALU-E2E")
                .tipoUsuario(tipoAlumno)
                .build();
        Usuario bibliotecario = Usuario.builder()
                .nombres("Diego")
                .apellidos("Bibliotecario")
                .correo("bibliotecario.e2e@arrupeteca.com")
                .contrasena(passwordEncoder.encode("Password123!"))
                .carnet("BIB-E2E")
                .tipoUsuario(tipoBibliotecario)
                .build();
        em.persist(admin);
        em.persist(alumno);
        em.persist(bibliotecario);

        Parentesco parentesco = Parentesco.builder().nombre("Padre E2E").build();
        em.persist(parentesco);

        Responsable responsable = Responsable.builder()
                .usuario(alumno)
                .nombres("Carlos")
                .apellidos("Ramírez")
                .correo("carlos.e2e@correo.com")
                .telefono("7000-1111")
                .parentesco(parentesco)
                .build();
        em.persist(responsable);

        EstadoSolicitud estadoPendiente = EstadoSolicitud.builder().nombre("PENDIENTE").build();
        EstadoSolicitud estadoAprobada = EstadoSolicitud.builder().nombre("APROBADA").build();
        EstadoSolicitud estadoEntregada = EstadoSolicitud.builder().nombre("ENTREGADA").build();
        em.persist(estadoPendiente);
        em.persist(estadoAprobada);
        em.persist(estadoEntregada);

        EstadoPrestamo estadoPrestamoActivo = EstadoPrestamo.builder().nombre("ACTIVO").build();
        EstadoPrestamo estadoPrestamoEntregado = EstadoPrestamo.builder().nombre("ENTREGADO").build();
        em.persist(estadoPrestamoActivo);
        em.persist(estadoPrestamoEntregado);

        EstadoEjemplarPrestamo estadoEjemplar = EstadoEjemplarPrestamo.builder().nombre("EN_PRESTAMO").build();
        EstadoEjemplarPrestamo estadoEjemplarDevuelto = EstadoEjemplarPrestamo.builder().nombre("DEVUELTO").build();
        em.persist(estadoEjemplar);
        em.persist(estadoEjemplarDevuelto);

        Obra obra = Obra.builder()
                .titulo("Obra E2E")
                .anioCreacion(2020)
                .resumen("Resumen de prueba E2E")
                .build();
        obra.setFechaCreacion(LocalDateTime.now());
        em.persist(obra);

        Editorial editorial = Editorial.builder()
                .nombre("Editorial E2E")
                .correo("editorial.e2e@arrupeteca.com")
                .telefono("2222-3333")
                .personaContacto("Contacto E2E")
                .build();
        em.persist(editorial);

        Formato formato = Formato.builder().nombre("Tapa blanda E2E").build();
        em.persist(formato);

        Libro libro = Libro.builder()
                .isbn("978-999-000-001")
                .anioPublicacion(2021)
                .precio(new BigDecimal("15.00"))
                .mora(new BigDecimal("1.50"))
                .obra(obra)
                .editorial(editorial)
                .formato(formato)
                .build();
        libro.setFechaCreacion(LocalDateTime.now());
        em.persist(libro);

        Edificio edificio = Edificio.builder().nombre("Edificio E2E").build();
        em.persist(edificio);
        Planta planta = Planta.builder().nombre("Planta 1").edificio(edificio).build();
        em.persist(planta);
        Salon salon = Salon.builder().nombre("Salon A").planta(planta).build();
        em.persist(salon);
        Mueble mueble = Mueble.builder().nombre("Estante 1").salon(salon).build();
        em.persist(mueble);

        EstadoFisico estadoFisico = EstadoFisico.builder().nombre("Buen estado E2E").build();
        em.persist(estadoFisico);
        Disponibilidad disponibilidadDisponible = Disponibilidad.builder().nombre("Disponible").build();
        Disponibilidad disponibilidadPrestado = Disponibilidad.builder().nombre("Prestado").build();
        em.persist(disponibilidadDisponible);
        em.persist(disponibilidadPrestado);

        EjemplarLibro ejemplar = EjemplarLibro.builder()
                .libro(libro)
                .mueble(mueble)
                .estadoFisico(estadoFisico)
                .disponibilidad(disponibilidadDisponible)
                .comentario("Ejemplar E2E")
                .build();
        ejemplar.setFechaCreacion(LocalDateTime.now());
        em.persist(ejemplar);

        em.flush();
        return new SeedContext(
                admin, alumno, bibliotecario,
                estadoPendiente, estadoAprobada,
                estadoPrestamoActivo, estadoPrestamoEntregado,
                estadoEjemplar, estadoEjemplarDevuelto,
                libro, ejemplar, estadoFisico
        );
    }

    private String login(String correo) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "correo", correo,
                "contrasena", "Password123!"
        ));
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito", is(true)))
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    private JsonNode performJsonRequest(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder,
            String token,
            Object payload,
            int expectedStatus
    ) throws Exception {
        builder.header("Authorization", "Bearer " + token);
        if (payload != null) {
            builder.contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload));
        }
        MvcResult res = mockMvc.perform(builder)
                .andExpect(status().is(expectedStatus))
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString());
    }

    @Test
    void endpointsPublicosYCorreccionesDeConsultaFuncionanSinMocks() throws Exception {
        SeedContext seed = seedBaseData();
        String adminToken = login(seed.admin().getCorreo());
        String bibToken = login(seed.bibliotecario().getCorreo());

        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());

        for (String publicGet : new String[]{
                "/api/v1/libros", "/api/v1/autores", "/api/v1/generos",
                "/api/v1/categorias", "/api/v1/editoriales", "/api/v1/idiomas"
        }) {
            mockMvc.perform(get(publicGet))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exito", is(true)));
        }

        // Verifica el fix en ResponsableController (/all ahora ordena por 'nombres')
        performJsonRequest(get("/api/v1/responsables/all"), adminToken, null, 200);

        // Verifica que sin ?termino= responda 400 Bad Request y con ?termino= responda 200
        performJsonRequest(get("/api/v1/autores/admin/verificar-duplicados"), adminToken, null, 400);
        performJsonRequest(get("/api/v1/obras/admin/verificar-duplicados"), adminToken, null, 400);
        performJsonRequest(get("/api/v1/autores/admin/verificar-duplicados").param("termino", "a"), adminToken, null, 200);
        performJsonRequest(get("/api/v1/obras/admin/verificar-duplicados").param("termino", "Obra"), adminToken, null, 200);

        // Verifica permisos de ROLE_BIBLIOTECARIO:
        // Permitido en préstamos y consultas admin de ejemplares/usuarios/libros
        performJsonRequest(get("/api/v1/prestamos/admin/all"), bibToken, null, 200);
        performJsonRequest(get("/api/v1/ejemplares/admin/all"), bibToken, null, 200);
        performJsonRequest(get("/api/v1/usuarios/admin/all"), bibToken, null, 200);
        // Denegado (403) en catálogos administrativos generales y escrituras fuera de préstamos/transferencias
        performJsonRequest(get("/api/v1/categorias/admin/all"), bibToken, null, 403);
        performJsonRequest(post("/api/v1/ejemplares/admin"), bibToken, Map.of("comentario", "No permitido"), 403);
    }

    @Test
    void flujoCompletoDePrestamosEndToEndSinMocks() throws Exception {
        SeedContext seed = seedBaseData();
        String bibToken = login(seed.bibliotecario().getCorreo());

        // 1. Solicitud de préstamo: POST -> GET -> PATCH -> DELETE -> PATCH activar
        JsonNode solCreada = performJsonRequest(
                post("/api/v1/solicitudes-prestamo/admin"),
                bibToken,
                Map.of(
                        "idUsuario", seed.alumno().getId(),
                        "idEstadoSolicitud", seed.estadoPendiente().getId(),
                        "fechaLimiteRetiro", LocalDateTime.now().plusDays(2).toString(),
                        "creadoPorAdmin", true
                ),
                201
        );
        long idSolicitud = solCreada.path("data").path("id").asLong();
        performJsonRequest(get("/api/v1/solicitudes-prestamo/admin/" + idSolicitud), bibToken, null, 200);
        performJsonRequest(
                patch("/api/v1/solicitudes-prestamo/admin/" + idSolicitud),
                bibToken,
                Map.of(
                        "idEstadoSolicitud", seed.estadoAprobada().getId(),
                        "comentarioRespuesta", "Aprobada en E2E"
                ),
                200
        );
        performJsonRequest(delete("/api/v1/solicitudes-prestamo/admin/" + idSolicitud), bibToken, null, 200);
        performJsonRequest(patch("/api/v1/solicitudes-prestamo/admin/" + idSolicitud + "/activar"), bibToken, null, 200);

        // 2. Detalle de solicitud
        JsonNode detCreado = performJsonRequest(
                post("/api/v1/solicitudes-prestamo-detalle/admin"),
                bibToken,
                Map.of(
                        "idSolicitud", idSolicitud,
                        "idLibro", seed.libro().getId(),
                        "cantidadSolicitada", 1
                ),
                201
        );
        long idDetalle = detCreado.path("data").path("id").asLong();
        performJsonRequest(get("/api/v1/solicitudes-prestamo-detalle/solicitud/" + idSolicitud), bibToken, null, 200);
        performJsonRequest(
                patch("/api/v1/solicitudes-prestamo-detalle/admin/" + idDetalle),
                bibToken,
                Map.of("cantidadSolicitada", 2),
                200
        );

        // 3. Préstamo usando idSolicitud -> la solicitud debe pasar automáticamente a ENTREGADA
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limiteInicial = ahora.plusDays(7);
        JsonNode prestamoCreado = performJsonRequest(
                post("/api/v1/prestamos/admin"),
                bibToken,
                Map.of(
                        "idSolicitud", idSolicitud,
                        "idUsuario", seed.alumno().getId(),
                        "idEstadoPrestamo", seed.estadoPrestamoEntregado().getId(),
                        "fechaPrestamo", ahora.toString(),
                        "fechaLimiteDevolucion", limiteInicial.toString(),
                        "entregadoPor", seed.admin().getId(),
                        "comentarios", "Préstamo E2E"
                ),
                201
        );
        long idPrestamo = prestamoCreado.path("data").path("id").asLong();

        JsonNode solDespuesDeEntrega = performJsonRequest(
                get("/api/v1/solicitudes-prestamo/admin/" + idSolicitud), bibToken, null, 200);
        assertThat(solDespuesDeEntrega.path("data").path("estadoSolicitud").asText()).isEqualTo("ENTREGADA");

        // Reutilizar la misma solicitud ya ENTREGADA debe devolver 400
        performJsonRequest(
                post("/api/v1/prestamos/admin"),
                bibToken,
                Map.of(
                        "idSolicitud", idSolicitud,
                        "idUsuario", seed.alumno().getId(),
                        "idEstadoPrestamo", seed.estadoPrestamoActivo().getId(),
                        "fechaPrestamo", ahora.toString(),
                        "fechaLimiteDevolucion", limiteInicial.toString(),
                        "entregadoPor", seed.admin().getId()
                ),
                400
        );

        // PATCH /api/v1/prestamos/admin/{id} persiste entregadoPor y puede desvincular/actualizar solicitud
        JsonNode prestamoEditado = performJsonRequest(
                patch("/api/v1/prestamos/admin/" + idPrestamo),
                bibToken,
                Map.of(
                        "entregadoPor", seed.bibliotecario().getId(),
                        "comentarios", "Entregado por bibliotecario E2E"
                ),
                200
        );
        assertThat(prestamoEditado.path("data").path("entregadoPorId").asLong())
                .isEqualTo(seed.bibliotecario().getId());

        // 4. Ejemplar en préstamo -> cambia disponibilidad a Prestado y rechaza duplicado/no disponible
        JsonNode epCreado = performJsonRequest(
                post("/api/v1/ejemplares-prestamo/admin"),
                bibToken,
                Map.of(
                        "idPrestamo", idPrestamo,
                        "idEjemplar", seed.ejemplar().getId(),
                        "idEstadoEjemplarPrestamo", seed.estadoEjemplarPrestamo().getId(),
                        "idEstadoFisicoEntrega", seed.estadoFisico().getId(),
                        "exentoMora", false
                ),
                201
        );
        long idEjemplarPrestamo = epCreado.path("data").path("id").asLong();

        JsonNode ejemplarPrestado = performJsonRequest(
                get("/api/v1/ejemplares/admin/" + seed.ejemplar().getId()), bibToken, null, 200);
        assertThat(ejemplarPrestado.path("data").path("disponibilidad").path("nombre").asText())
                .isEqualToIgnoringCase("Prestado");

        // Intentar prestar de nuevo el mismo ejemplar debe ser rechazado con 400
        performJsonRequest(
                post("/api/v1/ejemplares-prestamo/admin"),
                bibToken,
                Map.of(
                        "idPrestamo", idPrestamo,
                        "idEjemplar", seed.ejemplar().getId(),
                        "idEstadoEjemplarPrestamo", seed.estadoEjemplarPrestamo().getId()
                ),
                400
        );

        // pagar-mora y exentar-mora devuelven EjemplarPrestamoDetalleResponse completo
        performJsonRequest(
                patch("/api/v1/ejemplares-prestamo/admin/" + idEjemplarPrestamo),
                bibToken,
                Map.of("moraCalculada", new BigDecimal("3.00")),
                200
        );
        JsonNode moraPagadaResp = performJsonRequest(
                patch("/api/v1/ejemplares-prestamo/admin/pagar-mora/" + idEjemplarPrestamo),
                bibToken, null, 200);
        assertThat(moraPagadaResp.path("data").path("id").asLong()).isEqualTo(idEjemplarPrestamo);
        assertThat(moraPagadaResp.path("data").path("moraPagada").asBoolean()).isTrue();

        JsonNode exentoResp = performJsonRequest(
                patch("/api/v1/ejemplares-prestamo/admin/exentar-mora/" + idEjemplarPrestamo),
                bibToken, null, 200);
        assertThat(exentoResp.path("data").path("exentoMora").asBoolean()).isTrue();

        // 5. Solicitud de renovación: POST -> PATCH editar -> PATCH /responder APROBADA extiende Prestamo.fechaLimiteDevolucion
        JsonNode renCreada = performJsonRequest(
                post("/api/v1/renovaciones/solicitar"),
                bibToken,
                Map.of(
                        "idEjemplarPrestamo", idEjemplarPrestamo,
                        "idUsuario", seed.alumno().getId(),
                        "idEstadoSolicitud", seed.estadoPendiente().getId(),
                        "diasSolicitados", 5,
                        "motivoSolicitud", "Necesito unos días extra"
                ),
                201
        );
        long idRenovacion = renCreada.path("data").path("id").asLong();

        JsonNode renEditada = performJsonRequest(
                patch("/api/v1/renovaciones/admin/" + idRenovacion),
                bibToken,
                Map.of("diasSolicitados", 6, "motivoSolicitud", "Motivo editado E2E"),
                200
        );
        assertThat(renEditada.path("data").path("diasSolicitados").asInt()).isEqualTo(6);

        LocalDateTime fechaExtendida = limiteInicial.plusDays(6);
        performJsonRequest(
                patch("/api/v1/renovaciones/admin/" + idRenovacion + "/responder"),
                bibToken,
                Map.of(
                        "idEstadoSolicitud", seed.estadoAprobada().getId(),
                        "revisadoPor", seed.bibliotecario().getId(),
                        "motivoRespuesta", "Prórroga aprobada en E2E",
                        "nuevaFechaLimitePropuesta", fechaExtendida.toString()
                ),
                200
        );

        JsonNode prestamoExtendido = performJsonRequest(
                get("/api/v1/prestamos/admin/" + idPrestamo), bibToken, null, 200);
        LocalDateTime limiteActualizado = LocalDateTime.parse(
                prestamoExtendido.path("data").path("fechaLimiteDevolucion").asText());
        assertThat(limiteActualizado).isAfter(limiteInicial);

        // 6. Devolver ejemplar -> disponibilidad vuelve a Disponible
        performJsonRequest(
                patch("/api/v1/ejemplares-prestamo/admin/" + idEjemplarPrestamo),
                bibToken,
                Map.of(
                        "idEstadoEjemplarPrestamo", seed.estadoEjemplarDevuelto().getId(),
                        "idEstadoFisicoDevolucion", seed.estadoFisico().getId(),
                        "fechaDevolucionReal", LocalDateTime.now().toString(),
                        "recibidoPor", seed.bibliotecario().getId()
                ),
                200
        );

        JsonNode ejemplarDevuelto = performJsonRequest(
                get("/api/v1/ejemplares/admin/" + seed.ejemplar().getId()), bibToken, null, 200);
        assertThat(ejemplarDevuelto.path("data").path("disponibilidad").path("nombre").asText())
                .isEqualToIgnoringCase("Disponible");
    }
}
