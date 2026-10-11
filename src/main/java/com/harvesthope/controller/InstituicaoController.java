
package com.harvesthope.controller;

import com.harvesthope.model.dto.CadastroInstituicaoRequest;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.service.CadastroInstituicaoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/instituicoes")
public class InstituicaoController {

    private final InstituicaoRepository instituicaoRepository;
    private final CadastroInstituicaoService cadastroService;

    public InstituicaoController(
            InstituicaoRepository instituicaoRepository,
            CadastroInstituicaoService cadastroService) {
        this.instituicaoRepository = instituicaoRepository;
        this.cadastroService = cadastroService;
    }

    @GetMapping
    public ResponseEntity<List<Instituicao>> listarTodas() {
        return ResponseEntity.ok(instituicaoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> buscarPorId(
            @PathVariable Long id) {
        return instituicaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
}
