package com.galvan.inventarios.controlador;

import com.galvan.inventarios.modelo.ResetPasswordDTO;
import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.servicio.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.galvan.inventarios.config.JwtUtils; // <--- ESTA LÍNEA ES LA CLAVE
import com.galvan.inventarios.repositorio.UsuarioRepository;
import java.net.URI; // <--- ESTE FALTA PARA EL URI.create

// IMPORTANTE: Estos imports faltaban en tu código
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioServicio usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "El correo ya está registrado"));
            }

            usuarioService.registrar(usuario);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Usuario registrado con éxito"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", "Error en el servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        return usuarioRepository.findByEmail(loginRequest.getEmail())
                .map(usuario -> {
                    if (!usuario.isEnabled()) {
                        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Debes confirmar tu cuenta primero."));
                    }
                    if (passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                        String token = jwtUtils.generarToken(usuario.getEmail());
                        return ResponseEntity.ok(Collections.singletonMap("token", token));
                    }
                    return ResponseEntity.status(401).body(Collections.singletonMap("error", "Credenciales inválidas."));
                })
                .orElse(ResponseEntity.status(401).body(Collections.singletonMap("error", "Usuario no encontrado.")));
    }

    @PostMapping("/olvide-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            usuarioService.generarTokenRecuperacion(email);
            return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, se ha enviado un enlace."));
        } catch (Exception e) {
            // No revelamos si el correo existe o no por seguridad, pero logueamos el error
            return ResponseEntity.ok(Map.of("mensaje", "Proceso de recuperación iniciado."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody ResetPasswordDTO data) {
        try {
            usuarioService.actualizarPasswordConToken(data.getToken(), data.getNuevaPassword());
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Este es el que usa el link del correo de registro (opcional si usas código)
    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmarCuenta(@RequestParam String token) {
        boolean activado = usuarioService.confirmarToken(token);
        if (activado) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:4200/login?activado=true"))
                    .build();
        }
        return ResponseEntity.badRequest().body("Token inválido o expirado");
    }
}