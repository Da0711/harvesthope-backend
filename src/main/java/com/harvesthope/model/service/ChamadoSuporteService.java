
package com.harvesthope.model.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.dto.*;
import com.harvesthope.model.entity.*;
import com.harvesthope.model.repository.*;

@Service
public class ChamadoSuporteService {

    private final ChamadoSuporteRepository chamados;
    private final UsuarioRepository usuarios;
    private final EmailSuporteService emailService;

    public ChamadoSuporteService(
            ChamadoSuporteRepository chamados,
            UsuarioRepository usuarios,
            EmailSuporteService emailService
    ) {
        this.chamados = chamados;
        this.usuarios = usuarios;
        this.emailService = emailService;
    }

    @Transactional
    public ChamadoSuporteResponse criar(
            Long usuarioId,
            CriarChamadoRequest req
    ) {

        if (req == null
                || req.assunto() == null
                || req.assunto().isBlank()
                || req.mensagem() == null
                || req.mensagem().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o assunto e a mensagem."
            );
        }

        if (req.assunto().length() > 120
                || req.mensagem().length() > 3000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Assunto ou mensagem excede o limite permitido."
            );
        }

        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado."
                ));

        ChamadoSuporte chamado = new ChamadoSuporte();

        chamado.setAssunto(req.assunto().trim());
        chamado.setMensagem(req.mensagem().trim());
        chamado.setUsuario(usuario);

        ChamadoSuporte salvo = chamados.saveAndFlush(chamado);

        try {
            emailService.notificarNovoChamado(salvo);
        } catch (Exception e) {
            System.err.println(
                    "ERRO AO ENVIAR E-MAIL DO CHAMADO #"
                    + salvo.getId()
            );

            System.err.println("Motivo: " + e.getMessage());
            e.printStackTrace();
        }

        return ChamadoSuporteResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ChamadoSuporteResponse> meusChamados(Long usuarioId) {

        return chamados
                .findAllByUsuario_IdOrderByDataCriacaoDesc(usuarioId)
                .stream()
                .map(ChamadoSuporteResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChamadoSuporteResponse> todos() {

        return chamados
                .findAllByOrderByDataCriacaoDesc()
                .stream()
                .map(ChamadoSuporteResponse::de)
                .toList();
    }

    @Transactional
    public ChamadoSuporteResponse atualizarStatus(
            Long id,
            StatusChamado status
    ) {

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um status válido."
            );
        }

        ChamadoSuporte chamado = chamados.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chamado não encontrado."
                ));

        chamado.setStatus(status);

        return ChamadoSuporteResponse.de(
                chamados.save(chamado)
        );
    }

    @Transactional
    public void excluir(Long id) {

        ChamadoSuporte chamado = chamados.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Chamado não encontrado."
                ));

        chamados.delete(chamado);
    }
}
