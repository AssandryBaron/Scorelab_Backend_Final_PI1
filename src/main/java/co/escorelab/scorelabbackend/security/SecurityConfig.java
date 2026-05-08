package co.escorelab.scorelabbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints Públicos
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Torneos: Reglas específicas para el Organizador primero
                        // Esto soluciona el error 403 al crear (POST)
                        .requestMatchers(HttpMethod.POST, "/api/torneos/**").hasAuthority("ORGANIZADOR")
                        .requestMatchers(HttpMethod.GET, "/api/torneos/mis-torneos").hasAuthority("ORGANIZADOR")

                        // Acceso general para torneos (Ver todos, etc.)
                        .requestMatchers(HttpMethod.GET, "/api/torneos/todos").authenticated()
                        .requestMatchers("/api/torneos/**").authenticated()

                        // 3. Equipos: Gestión exclusiva del Organizador
                        .requestMatchers(HttpMethod.GET, "/api/equipos/pendientes").hasAuthority("ORGANIZADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/equipos/*/aprobar").hasAuthority("ORGANIZADOR")

                        // Acciones generales de equipos para Delegados/Otros
                        .requestMatchers("/api/equipos/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}