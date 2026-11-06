package com.harvesthope.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.harvesthope.model.entity.ChamadoSuporte;

@Service
public class EmailSuporteService {

    private final JavaMailSender mailSender;
    private final String destinatario;
    private final String remetente;

    public EmailSuporteService(
            JavaMailSender mailSender,
            @Value("${app.suporte.email}") String destinatario,
            @Value("${spring.mail.username}") String remetente
    ) {
        this.mailSender = mailSender;
        this.destinatario = destinatario;
        this.remetente = remetente;
    }

    public void notificarNovoChamado(ChamadoSuporte chamado) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setFrom(remetente);
        email.setTo(destinatario);
        email.setSubject(
                "[Harvest Hope] Novo chamado #" + chamado.getId()
        );

        email.setText(
                "Um novo chamado foi registrado.\n\n"
                + "Código: #" + chamado.getId() + "\n"
                + "Usuário: " + chamado.getUsuario().getNome() + "\n"
                + "E-mail: " + chamado.getUsuario().getEmail() + "\n"
                + "Assunto: " + chamado.getAssunto() + "\n\n"
                + chamado.getMensagem()
        );

        mailSender.send(email);
    }
}