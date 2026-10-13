
package com.harvesthope.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.harvesthope.model.dto.CadastroInstituicaoRequest;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.service.CadastroInstituicaoService;
import com.harvesthope.model.service.InstituicaoService;

@RestController
@RequestMapping("/api/v1/instituicoes")
public class InstituicaoController {

    private final InstituicaoService instituicaoService;
    private final CadastroInstituicaoService cadastroService;

    public InstituicaoController(
            InstituicaoService instituicaoService,
            CadastroInstituicaoService cadastroService) {
        this.instituicaoService = instituicaoService;
        this.cadastroService = cadastroService;
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null
                || authentication.getPrincipal() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identificação do usuário inválida."
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<Instituicao>> listarTodas() {
        return ResponseEntity.ok(instituicaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(
            @PathVariable Long id) {
        return instituicaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(
            @RequestBody CadastroInstituicaoRequest request) {
        try {
            Instituicao instituicao = cadastroService.cadastrar(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensagem", "Instituição cadastrada com sucesso.",
                            "instituicaoId", instituicao.getId(),
                            "nome", instituicao.getNome()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensagem", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Instituicao> atualizar(
            @PathVariable Long id,
            @RequestBody Instituicao instituicao,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                instituicaoService.atualizar(id, instituicao, usuarioId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        instituicaoService.excluir(id, usuarioId);

        return ResponseEntity.noContent().build();
    }
}
