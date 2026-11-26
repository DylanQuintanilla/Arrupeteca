package com.arrupeteca.security;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.context.annotation.Profile;

import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(
                                "/api/v1/prestamos/admin",
                                "/api/v1/prestamos/admin/**",
                                "/api/v1/solicitudes-prestamo/admin",
                                "/api/v1/solicitudes-prestamo/admin/**",
                                "/api/v1/solicitudes-prestamo-detalle/admin",
                                "/api/v1/solicitudes-prestamo-detalle/admin/**",
                                "/api/v1/ejemplares-prestamo/admin",
                                "/api/v1/ejemplares-prestamo/admin/**",
                                "/api/v1/renovaciones/admin",
                                "/api/v1/renovaciones/admin/**",
                                "/api/v1/transferencias/admin",
                                "/api/v1/transferencias/admin/**",
                                "/api/v1/solicitudes-transferencia/admin",
                                "/api/v1/solicitudes-transferencia/admin/**"
                        ).hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/ejemplares/admin",
                                "/api/v1/ejemplares/admin/**",
                                "/api/v1/usuarios/admin",
                                "/api/v1/usuarios/admin/**",
                                "/api/v1/libros/admin",
                                "/api/v1/libros/admin/**",
                                "/api/v1/obras/admin",
                                "/api/v1/obras/admin/**",
                                "/api/v1/muebles/admin",
                                "/api/v1/muebles/admin/**",
                                "/api/v1/salones/admin",
                                "/api/v1/salones/admin/**",
                                "/api/v1/plantas/admin",
                                "/api/v1/plantas/admin/**",
                                "/api/v1/edificios/admin",
                                "/api/v1/edificios/admin/**",
                                "/api/v1/disponibilidades/admin",
                                "/api/v1/disponibilidades/admin/**",
                                "/api/v1/estados-fisicos/admin",
                                "/api/v1/estados-fisicos/admin/**",
                                "/api/v1/estados-solicitud/admin",
                                "/api/v1/estados-solicitud/admin/**",
                                "/api/v1/estados-prestamo/admin",
                                "/api/v1/estados-prestamo/admin/**",
                                "/api/v1/estados-ejemplar-prestamo/admin",
                                "/api/v1/estados-ejemplar-prestamo/admin/**",
                                "/api/v1/estados-transferencia/admin",
                                "/api/v1/estados-transferencia/admin/**",
                                "/api/v1/estados-solicitud-transferencia/admin",
                                "/api/v1/estados-solicitud-transferencia/admin/**"
                        ).hasAnyRole("ADMIN", "BIBLIOTECARIO")
                        .requestMatchers("/api/v1/*/admin", "/api/v1/*/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/libros/**",
                                "/api/v1/autores/**",
                                "/api/v1/generos",
                                "/api/v1/categorias",
                                "/api/v1/editoriales",
                                "/api/v1/idiomas"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
