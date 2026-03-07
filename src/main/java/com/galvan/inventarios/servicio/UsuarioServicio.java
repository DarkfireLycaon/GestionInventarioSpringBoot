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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService; // ✅ ÚNICO servicio de email

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario) {
        // Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Generar código de 6 dígitos
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoConfirmacion(codigo);
        usuario.setEnabled(false);

        Usuario guardado = usuarioRepository.save(usuario);

        // Enviar el mail - PERO sin bloquear si falla
        try {
            emailService.enviarCorreoConfirmacion(guardado.getEmail(), codigo);
        } catch (Exception e) {
            // Loggear el error pero no impedir el registro
            System.err.println("Error enviando email: " + e.getMessage());
        }

        return guardado;
    }

    public void generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Enviar correo USANDO EmailService
        String asunto = "Restablecer Contraseña - StockMaster";
        String contenidoHtml = generarHtmlRecuperacion(token);
        emailService.enviarCorreoRecuperacion(usuario.getEmail(), token);
    }

    private String generarHtmlConfirmacion(String email, String codigo) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 500px; margin: auto;'>" +
                "<h2 style='color: #6f42c1;'>StockMaster</h2>" +
                "<p>Gracias por registrarte. Tu código de confirmación es:</p>" +
                "<div style='margin: 30px 0;'>" +
                "<span style='background-color: #6f42c1; color: white; padding: 12px 25px; border-radius: 5px; font-weight: bold; font-size: 24px;'>" + codigo + "</span>" +
                "</div>" +
                "<p style='color: #888;'>Ingresa este código en la aplicación para activar tu cuenta.</p>" +
                "</div>";
    }

    private String generarHtmlRecuperacion(String token) {
        String urlRecuperacion = "https://inventario-l0pjr3yfh-darkfirelycaons-projects.vercel.app/reset-password?token=" + token;

        return "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 500px; margin: auto;'>" +
                "<h2 style='color: #6f42c1;'>StockMaster</h2>" +
                "<p>Has solicitado restablecer tu contraseña. Haz clic en el botón de abajo:</p>" +
                "<div style='margin: 30px 0;'>" +
                "<a href='" + urlRecuperacion + "' style='background-color: #6f42c1; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>" +
                "Restablecer Contraseña" +
                "</a>" +
                "</div>" +
                "<p style='color: #888; font-size: 12px;'>Este enlace expirará en 15 minutos.</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                "<p style='color: #aaa; font-size: 10px;'>Si no solicitaste este cambio, ignora este correo.</p>" +
                "</div>";
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

}