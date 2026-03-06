package com.galvan.inventarios.servicio;

import com.galvan.inventarios.modelo.Usuario;
import com.galvan.inventarios.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.mail.SimpleMailMessage;
import java.util.Optional;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // Necesitas este Bean en SecurityConfig
    @Autowired
    private JavaMailSender mailSender;

    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 2. Generar código de 6 dígitos aleatorio
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoConfirmacion(codigo);
        usuario.setEnabled(false); // No puede entrar hasta confirmar

        Usuario guardado = usuarioRepository.save(usuario);

        // 3. Enviar el mail
        emailService.enviarCorreoConfirmacion(guardado.getEmail(), codigo);

        return guardado;
    }
    public void generarTokenRecuperacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Generar token único
        String token = UUID.randomUUID().toString();

        // 2. Guardar token con expiración (ej. 15 minutos)
        usuario.setResetToken(token);
        usuario.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // 3. Enviar el correo
        enviarCorreoRecuperacion(usuario.getEmail(), token);
    }
    private void enviarCorreoRecuperacion(String email, String token) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // El 'true' indica que el mensaje es multipart (soporta HTML)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("rafatorretta@gmail.com");
            helper.setTo(email);
            helper.setSubject("Restablecer Contraseña - StockMaster");

            String urlRecuperacion = "https://inventario-l0pjr3yfh-darkfirelycaons-projects.vercel.app/reset-password?token=" + token;

            // Aquí definimos el diseño con estilos CSS en línea (inline)
            String contenidoHtml =
                    "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 500px; margin: auto;'>" +
                            "<h2 style='color: #6f42c1;'>StockMaster</h2>" +
                            "<p>Has solicitado restablecer tu contraseña. Haz clic en el botón de abajo para continuar:</p>" +
                            "<div style='margin: 30px 0;'>" +
                            "<a href='" + urlRecuperacion + "' style='background-color: #6f42c1; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>" +
                            "Restablecer Contraseña" +
                            "</a>" +
                            "</div>" +
                            "<p style='color: #888; font-size: 12px;'>Este enlace expirará en 15 minutos.</p>" +
                            "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                            "<p style='color: #aaa; font-size: 10px;'>Si no solicitaste este cambio, puedes ignorar este correo.</p>" +
                            "</div>";

            helper.setText(contenidoHtml, true); // El 'true' activa el renderizado HTML

            mailSender.send(mimeMessage);
            System.out.println("¡Correo HTML enviado exitosamente!");

        } catch (Exception e) {
            System.err.println("Error al enviar correo HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public boolean confirmarCuenta(String email, String codigo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getCodigoConfirmacion().equals(codigo)) {
                usuario.setEnabled(true);
                usuario.setCodigoConfirmacion(null); // Borramos el código por seguridad
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }
    public boolean confirmarToken(String token) {
        // 1. Buscamos al usuario que tenga ese código exacto
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCodigoConfirmacion(token);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // 2. Cambiamos el estado a activo (true)
            usuario.setEnabled(true);

            // 3. Opcional: Borramos el código para que no se pueda usar de nuevo
            usuario.setCodigoConfirmacion(null);

            // 4. Guardamos los cambios en la DB
            usuarioRepository.save(usuario);

            return true; // Éxito: cuenta activada
        }

        return false; // Error: el token no existe o es inválido
    }
    public void actualizarPasswordConToken(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // Verificar si el token ya expiró
        if (usuario.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace ha expirado. Solicita uno nuevo.");
        }

        // Encriptar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));

        // Limpiar el token para que no se use dos veces
        usuario.setResetToken(null);
        usuario.setTokenExpiration(null);

        usuarioRepository.save(usuario);
    }


}