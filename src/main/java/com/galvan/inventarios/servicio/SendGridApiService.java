package com.galvan.inventarios.servicio;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridApiService {

    private static final Logger log = LoggerFactory.getLogger(SendGridApiService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Async
    public void enviarCorreoConfirmacion(String destinatario, String codigo) {
        log.info("📧 Enviando correo de confirmación vía API a: {}", destinatario);

        Email from = new Email("rafatorretta@gmail.com", "Inventarios App");
        String subject = "Activación de Cuenta - Inventarios";
        Email to = new Email(destinatario);

        String link = "https://gestioninventariospringboot.onrender.com/auth/confirmar?token=" + codigo;
        String contenidoHtml = generarHtmlConfirmacion(link);
        Content content = new Content("text/html", contenidoHtml);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            log.info("📬 Código de respuesta SendGrid: {}", response.getStatusCode());
            log.info("📬 Cuerpo de respuesta: {}", response.getBody());

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("✅ Correo enviado exitosamente a: {}", destinatario);
            } else {
                log.error("❌ Error al enviar correo. Código: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
            }

        } catch (IOException ex) {
            log.error("❌ Error de conexión con SendGrid: {}", ex.getMessage(), ex);
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
    @Async
    public void enviarCorreoRecuperacion(String destinatario, String token) {
        log.info("📧 Enviando correo de recuperación vía API a: {}", destinatario);

        Email from = new Email("rafatorretta@gmail.com", "Inventarios App");
        String subject = "Recuperación de Contraseña - Inventarios";
        Email to = new Email(destinatario);

        String link = "https://inventario-l0pjr3yfh-darkfirelycaons-projects.vercel.app/reset-password?token=" + token;
        String contenidoHtml = generarHtmlRecuperacion(link);
        Content content = new Content("text/html", contenidoHtml);

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            log.info("📬 Código de respuesta SendGrid: {}", response.getStatusCode());

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("✅ Correo de recuperación enviado a: {}", destinatario);
            } else {
                log.error("❌ Error al enviar correo de recuperación. Código: {}", response.getStatusCode());
            }

        } catch (IOException ex) {
            log.error("❌ Error de conexión con SendGrid: {}", ex.getMessage(), ex);
        }
    }

    private String generarHtmlRecuperacion(String link) {
        return "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 500px; margin: auto;'>" +
                "<h2 style='color: #6f42c1;'>Recuperación de Contraseña</h2>" +
                "<p>Has solicitado restablecer tu contraseña. Haz clic en el botón de abajo:</p>" +
                "<div style='margin: 30px 0;'>" +
                "<a href='" + link + "' style='background-color: #6f42c1; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>" +
                "Restablecer Contraseña" +
                "</a>" +
                "</div>" +
                "<p style='color: #888; font-size: 12px;'>Este enlace expirará en 15 minutos.</p>" +
                "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                "<p style='color: #aaa; font-size: 10px;'>Si no solicitaste este cambio, ignora este correo.</p>" +
                "</div>";
    }
}