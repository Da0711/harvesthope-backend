package com.harvesthope.controller;

import com.harvesthope.model.dto.SolicitarDoacaoRequest;
import com.harvesthope.model.entity.Solicitacao;
import com.harvesthope.model.service.SolicitacaoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(
            SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public ResponseEntity<Solicitacao> solicitar(
            @RequestBody SolicitarDoacaoRequest dados) {

        Solicitacao solicitacao =
                solicitacaoService.solicitar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(solicitacao);
    }

    @GetMapping("/instituicao/{instituicaoId}")
    public ResponseEntity<List<Solicitacao>> listarPorInstituicao(
            @PathVariable Long instituicaoId) {

        return ResponseEntity.ok(
                solicitacaoService.listarPorInstituicao(instituicaoId)
        );
    }
}