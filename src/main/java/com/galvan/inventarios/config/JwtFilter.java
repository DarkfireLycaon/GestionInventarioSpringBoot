package com.galvan.inventarios.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils; // 👈 INYECTA JwtUtils

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            try {
                // 1. Extraer email del token usando JwtUtils
                username = jwtUtils.getEmailFromToken(jwt);
                System.out.println("📌 Email extraído del token: " + username);

                // 2. Validar el token
                boolean tokenValido = jwtUtils.validarToken(jwt);
                System.out.println("📌 Token válido: " + tokenValido);

            } catch (Exception e) {
                System.out.println("❌ Error procesando token: " + e.getMessage());
            }
        }

        // Si tenemos username y no hay autenticación previa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargar el usuario desde la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Crear el token de autenticación (con authorities del usuario)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("✅ Usuario autenticado: " + username);
        }

        chain.doFilter(request, response);
    }
}