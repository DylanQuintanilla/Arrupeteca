package com.arrupeteca.controller;

import com.arrupeteca.config.JacksonConfig;
import com.arrupeteca.persistence.repository.UsuarioRepository;
import com.arrupeteca.security.CustomUserDetailsService;
import com.arrupeteca.security.JwtAuthenticationEntryPoint;
import com.arrupeteca.security.JwtAuthenticationFilter;
import com.arrupeteca.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for controller unit/integration tests using MockMvc.
 * Disables security filters so controller unit tests test controller logic directly.
 */
@Import({JacksonConfig.class, BaseControllerTest.TestCsrfConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@WithMockUser(roles = {"ADMIN"})
public abstract class BaseControllerTest {

    @org.springframework.boot.test.context.TestConfiguration
    static class TestCsrfConfig {
        @org.springframework.context.annotation.Bean
        public org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer mockMvcCsrfCustomizer() {
            return builder -> builder.defaultRequest(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/")
                            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
            );
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    protected CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected UsuarioRepository usuarioRepository;

    @org.junit.jupiter.api.BeforeEach
    void setupMockJwtFilter() throws Exception {
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletRequest req = invocation.getArgument(0);
            jakarta.servlet.http.HttpServletResponse res = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(req, res);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }

}
