
package com.harvesthope.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.service.DoacaoService;

@RestController
@RequestMapping("/api/v1/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping
    public ResponseEntity<Doacao> salvar(
            @RequestBody Doacao doacao) {

        return ResponseEntity.ok(
                doacaoService.salvar(doacao)
        );
    }

    @GetMapping
    public ResponseEntity<List<Doacao>> listar() {
        return ResponseEntity.ok(
                doacaoService.listar()
        );
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
            @RequestHeader("X-Usuario-Id") Long usuarioId,
            @RequestBody Doacao doacao) {

        return ResponseEntity.ok(
                doacaoService.atualizar(id, usuarioId, doacao)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long usuarioId) {

        doacaoService.excluir(id, usuarioId);

        return ResponseEntity.noContent().build();
    }
}
