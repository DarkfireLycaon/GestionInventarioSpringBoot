package com.galvan.inventarios.servicio;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Async // ¡IMPORTANTE! Para no bloquear la petición
    public void enviarCorreoConfirmacion(String destinatario, String codigo) {
        log.info("📧 Iniciando envío de correo de confirmación a: {}", destinatario);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Activación de Cuenta - Inventarios");

            // Configurar remitente explícitamente
            helper.setFrom("rafatorretta@gmail.com");

            // Verificar que el código no sea nulo
            if (codigo == null || codigo.isEmpty()) {
                log.error("❌ Código de confirmación nulo o vacío para: {}", destinatario);
                return;
            }

            String link = "https://gestioninventariospringboot.onrender.com/auth/confirmar?token=" + codigo;
            log.debug("🔗 Link generado: {}", link);

            String contenidoHtml = generarHtmlConfirmacion(link);
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            log.info("✅ Correo enviado exitosamente a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("❌ Error al enviar correo a: {}. Error: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el correo de activación", e);
        } catch (Exception e) {
            log.error("❌ Error inesperado al enviar correo a: {}", destinatario, e);
            throw new RuntimeException("Error inesperado al enviar correo", e);
        }
    }

    @Async
    public void enviarCorreoRecuperacion(String destinatario, String token) {
        log.info("📧 Iniciando envío de correo de recuperación a: {}", destinatario);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Restablecer Contraseña - Inventarios");
            helper.setFrom("rafatorretta@gmail.com");

            if (token == null || token.isEmpty()) {
                log.error("❌ Token de recuperación nulo o vacío para: {}", destinatario);
                return;
            }

            String link = "https://inventario-l0pjr3yfh-darkfirelycaons-projects.vercel.app/reset-password?token=" + token;
            log.debug("🔗 Link de recuperación: {}", link);

            String contenidoHtml = generarHtmlRecuperacion(link);
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            log.info("✅ Correo de recuperación enviado a: {}", destinatario);

        } catch (MessagingException e) {
            log.error("❌ Error al enviar correo de recuperación a: {}. Error: {}", destinatario, e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el correo de recuperación", e);
        }
    }

    private String generarHtmlConfirmacion(String link) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; border: 1px solid #ddd; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>" +
                "<h1 style='color: #8b5cf6;'>¡Bienvenido a Inventarios!</h1>" +
                "<p style='font-size: 16px; color: #333;'>Gracias por registrarte. Para activar tu cuenta, haz clic en el botón:</p>" +
                "<div style='margin: 30px 0;'>" +
                "<a href='" + link + "' style='background-color: #8b5cf6; color: white; padding: 14px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "✅ Activar mi cuenta" +
                "</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #666;'>O copia este enlace en tu navegador:</p>" +
                "<p style='font-size: 12px; color: #8b5cf6; word-break: break-all; background-color: #f3f4f6; padding: 10px; border-radius: 5px;'>" + link + "</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999;'>Si no creaste esta cuenta, ignora este mensaje.</p>" +
                "</div>";
    }

    private String generarHtmlRecuperacion(String link) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; border: 1px solid #ddd; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>" +
                "<h1 style='color: #8b5cf6;'>Restablecer Contraseña</h1>" +
                "<p style='font-size: 16px; color: #333;'>Has solicitado restablecer tu contraseña. Haz clic en el botón para continuar:</p>" +
                "<div style='margin: 30px 0;'>" +
                "<a href='" + link + "' style='background-color: #8b5cf6; color: white; padding: 14px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "🔑 Restablecer contraseña" +
                "</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #e53e3e; font-weight: bold;'>⚠️ Este enlace expirará en 15 minutos</p>" +
                "<p style='font-size: 14px; color: #666;'>O copia este enlace:</p>" +
                "<p style='font-size: 12px; color: #8b5cf6; word-break: break-all; background-color: #f3f4f6; padding: 10px; border-radius: 5px;'>" + link + "</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                "<p style='font-size: 12px; color: #999;'>Si no solicitaste este cambio, ignora este correo.</p>" +
                "</div>";
    }
}