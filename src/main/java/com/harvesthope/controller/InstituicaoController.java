package com.harvesthope.controller;

import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.service.InstituicaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;

    public InstituicaoController(InstituicaoService instituicaoService) {
        this.instituicaoService = instituicaoService;
    }

    @PostMapping
    public ResponseEntity<Instituicao> salvar(@RequestBody Instituicao instituicao) {
        return ResponseEntity.ok(instituicaoService.salvar(instituicao));
    }

    @GetMapping
    public ResponseEntity<List<Instituicao>> listar() {
        return ResponseEntity.ok(instituicaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(@PathVariable Long id) {
        return instituicaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Instituicao> atualizar(
            @PathVariable Long id,
            @RequestBody Instituicao instituicao) {

        instituicao.setId(id);

        return ResponseEntity.ok(instituicaoService.atualizar(instituicao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        instituicaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}