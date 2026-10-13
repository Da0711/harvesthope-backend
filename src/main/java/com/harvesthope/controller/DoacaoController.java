
package com.harvesthope.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.service.DoacaoService;

@RestController
@RequestMapping("/api/v1/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado."
            );
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identificação do usuário inválida."
            );
        }
    }

    @PostMapping
    public ResponseEntity<Doacao> salvar(
            Authentication authentication,
            @RequestBody Doacao doacao) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doacaoService.salvar(usuarioId, doacao));
    }

    @GetMapping
    public ResponseEntity<List<Doacao>> listar() {
        return ResponseEntity.ok(doacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doacao> buscarPorId(
            @PathVariable Long id) {

        return doacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doacao> atualizar(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody Doacao doacao) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                doacaoService.atualizar(id, usuarioId, doacao)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        doacaoService.excluir(id, usuarioId);

        return ResponseEntity.noContent().build();
    }
}
