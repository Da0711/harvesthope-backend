package com.harvesthope.controller;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.service.DoacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping
    public ResponseEntity<Doacao> salvar(@RequestBody Doacao doacao) {
        return ResponseEntity.ok(doacaoService.salvar(doacao));
    }

    @GetMapping
    public ResponseEntity<List<Doacao>> listar() {
        return ResponseEntity.ok(doacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doacao> buscarPorId(@PathVariable Long id) {
        return doacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doacao> atualizar(
            @PathVariable Long id,
            @RequestBody Doacao doacao) {

        doacao.setId(id);

        return ResponseEntity.ok(doacaoService.atualizar(doacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        doacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}