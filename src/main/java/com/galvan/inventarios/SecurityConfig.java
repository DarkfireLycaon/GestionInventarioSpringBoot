package com.galvan.inventarios;


import com.galvan.inventarios.config.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Autowired
    // private JwtFilter jwtFilter;

    public SecurityConfig() {
        System.out.println("¡¡¡LA CONFIGURACIÓN DE SEGURIDAD SE HA CARGADO CORRECTAMENTE!!!");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 👇 PERMITIR TODAS LAS PETICIONES OPTIONS (PREFLIGHT)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // 👇 ENDPOINTS PÚBLICOS
                        .requestMatchers("/auth/**").permitAll()
                        // 👇 TODO LO DEMÁS REQUIERE AUTENTICACIÓN
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🚀 Configuración que acepta TODAS las URLs de Vercel
        config.setAllowedOriginPatterns(Arrays.asList(
                "https://*.vercel.app",   // El comodín * funciona con patrones
                "http://localhost:4200"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*")); // O la lista específica que prefieras
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true); // Necesario para JWT
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}