
package com.harvesthope.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.dto.SolicitarDoacaoRequest;
import com.harvesthope.model.entity.Solicitacao;
import com.harvesthope.model.service.SolicitacaoService;

@RestController
@RequestMapping("/api/v1/solicitacoes")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(
            SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    private Long obterUsuarioId(Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado.");
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identidade do usuário inválida.");
        }
    }

    @PostMapping
    public ResponseEntity<Solicitacao> solicitar(
            Authentication authentication,
            @RequestBody SolicitarDoacaoRequest dados) {

        Long usuarioId = obterUsuarioId(authentication);

        Solicitacao solicitacao =
                solicitacaoService.solicitar(dados, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitacao);
    }

    @GetMapping
    public ResponseEntity<List<Solicitacao>> listar(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                solicitacaoService.listar(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitacao> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        Solicitacao solicitacao = solicitacaoService
                .buscarPorId(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada."));

        return ResponseEntity.ok(solicitacao);
    }

    @GetMapping("/instituicao/{instituicaoId}")
    public ResponseEntity<List<Solicitacao>> listarPorInstituicao(
            @PathVariable Long instituicaoId,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                solicitacaoService.listarPorInstituicao(
                        instituicaoId, usuarioId));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Solicitacao> concluir(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        return ResponseEntity.ok(solicitacaoService.concluir(id, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication authentication) {
        Long usuarioId = obterUsuarioId(authentication);
        solicitacaoService.cancelar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<Solicitacao> aceitar(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                solicitacaoService.aceitar(id, usuarioId));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Solicitacao> recusar(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                solicitacaoService.recusar(id, usuarioId));
    }
}
