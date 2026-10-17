package com.harvesthope.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.harvesthope.model.entity.Notificacao;
import com.harvesthope.model.service.NotificacaoService;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {
    private final NotificacaoService service;
    public NotificacaoController(NotificacaoService service) { this.service = service; }
    private Long usuarioId(Authentication auth) {
        try { if (auth == null || auth.getName() == null) throw new NumberFormatException(); return Long.valueOf(auth.getName()); }
        catch (NumberFormatException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado."); }
    }
    @GetMapping public List<Notificacao> listar(Authentication auth) { return service.listar(usuarioId(auth)); }
    @PatchMapping("/{id}/lida") public Notificacao marcarLida(@PathVariable Long id, Authentication auth) { return service.marcarLida(id, usuarioId(auth)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) { service.excluir(id, usuarioId(auth)); return ResponseEntity.noContent().build(); }
    @DeleteMapping public ResponseEntity<Void> excluirTodas(Authentication auth) { service.excluirTodas(usuarioId(auth)); return ResponseEntity.noContent().build(); }
}
