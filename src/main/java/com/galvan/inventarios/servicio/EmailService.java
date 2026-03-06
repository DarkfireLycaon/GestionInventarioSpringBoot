package com.galvan.inventarios.servicio;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoConfirmacion(String destinatario, String codigo) {
        try {
            // 1. Usamos MimeMessage en lugar de SimpleMailMessage para soportar HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Activación de Cuenta - Inventarios");

            String link = "https://gestioninventariospringboot.onrender.com/auth/confirmar?token=" + codigo;

            // 2. Definimos el HTML
            String contenidoHtml =
                    "<div style='font-family: Arial, sans-serif; text-align: center; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                            "   <h1 style='color: #28a745;'>¡Bienvenido a Inventarios!</h1>" +
                            "   <p>Gracias por registrarte. Para activar tu cuenta, simplemente haz clic en el botón de abajo:</p>" +
                            "   <br>" +
                            "   <a href='" + link + "' style='background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Activar mi cuenta</a>" +
                            "   <br><br>" +
                            "   <p style='font-size: 12px; color: #777;'>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>" +
                            "   <p style='font-size: 12px; color: #007bff;'>" + link + "</p>" +
                            "</div>";

            // 3. El 'true' al final indica que es contenido HTML
            helper.setText(contenidoHtml, true);

            mailSender.send(message);
            System.out.println("Correo enviado con éxito a: " + destinatario);

        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de activación");
        }
    }
}