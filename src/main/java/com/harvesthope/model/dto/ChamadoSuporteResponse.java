package com.harvesthope.model.dto;

import java.time.LocalDateTime;

import com.harvesthope.model.entity.ChamadoSuporte;
import com.harvesthope.model.entity.StatusChamado;

public record ChamadoSuporteResponse(
        Long id,
        String assunto,
        String mensagem,
        StatusChamado status,
        LocalDateTime dataCriacao,
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario
) {

    public static ChamadoSuporteResponse de(ChamadoSuporte chamado) {
        return new ChamadoSuporteResponse(
                chamado.getId(),
                chamado.getAssunto(),
                chamado.getMensagem(),
                chamado.getStatus(),
                chamado.getDataCriacao(),
                chamado.getUsuario().getId(),
                chamado.getUsuario().getNome(),
                chamado.getUsuario().getEmail()
        );
    }
}