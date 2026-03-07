package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UsuarioServicio {

    @Autowired // 👈 ESTO FALTABA
    private SendGridApiService sendGridApiService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // EmailService ya no es necesario, lo reemplazamos con SendGridApiService
    // @Autowired
    // private EmailService emailService; // 👈 ELIMINADO

    public Usuario registrar(Usuario usuario) {
        // Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Generar código de 6 dígitos
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoConfirmacion(codigo);
        usuario.setEnabled(false);

        Usuario guardado = usuarioRepository.save(usuario);

        // ✅ Enviar correo de confirmación con SendGrid
        sendGridApiService.enviarCorreoConfirmacion(guardado.getEmail(), codigo);

        return guardado;
    }

    public void generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // ✅ Enviar correo de recuperación TAMBIÉN con SendGrid
        sendGridApiService.enviarCorreoRecuperacion(usuario.getEmail(), token); // Necesitas crear este método
    }

    public boolean confirmarCuenta(String email, String codigo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (codigo.equals(usuario.getCodigoConfirmacion())) {
                usuario.setEnabled(true);
                usuario.setCodigoConfirmacion(null);
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }

    public boolean confirmarToken(String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCodigoConfirmacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setEnabled(true);
            usuario.setCodigoConfirmacion(null);
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    public void actualizarPasswordConToken(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado. Solicita uno nuevo.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null);
        usuario.setTokenExpiration(null);
        usuarioRepository.save(usuario);
    }

    // Los métodos generarHtmlConfirmacion y generarHtmlRecuperacion
    // ahora están en SendGridApiService, así que los eliminamos de aquí
}