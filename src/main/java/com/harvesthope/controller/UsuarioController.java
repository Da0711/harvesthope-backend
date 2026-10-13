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

import com.harvesthope.model.dto.UsuarioResponse;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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

    private UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "A listagem pública de usuários não é permitida.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> findById(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioAutenticadoId = obterUsuarioId(authentication);

        if (!usuarioAutenticadoId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode consultar sua própria conta.");
        }

        return usuarioService.findById(id)
                .map(this::converterParaResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(
            @RequestBody Usuario usuario) {

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe os dados do usuário.");
        }

        usuario.setId(null);
        usuario.setTipoUsuario("DOADOR");

        Usuario criado = usuarioService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(converterParaResponse(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> update(
            @PathVariable Long id,
            @RequestBody Usuario usuario,
            Authentication authentication) {

        Long usuarioAutenticadoId = obterUsuarioId(authentication);

        if (!usuarioAutenticadoId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode atualizar sua própria conta.");
        }

        Usuario atualizado = usuarioService.update(id, usuario);

        return ResponseEntity.ok(
                converterParaResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioAutenticadoId = obterUsuarioId(authentication);

        if (!usuarioAutenticadoId.equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode excluir sua própria conta.");
        }

        if (usuarioService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        usuarioService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}