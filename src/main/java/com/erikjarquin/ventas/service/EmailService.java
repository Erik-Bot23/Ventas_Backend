package com.erikjarquin.ventas.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//Service de email
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }

    public void sendPasswordRecoveryEmail(String recipient, String link){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("soportetiav@gmail.com");
        message.setTo(recipient);
        message.setSubject("Recuperación de contraseña");
        message.setText("Da clic en el siguiente enlace:\n\n" + link);
        mailSender.send(message);
    }
}
