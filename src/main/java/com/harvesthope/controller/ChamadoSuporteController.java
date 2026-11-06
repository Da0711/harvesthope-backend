
package com.harvesthope.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.dto.AtualizarStatusChamadoRequest;
import com.harvesthope.model.dto.ChamadoSuporteResponse;
import com.harvesthope.model.dto.CriarChamadoRequest;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.UsuarioRepository;
import com.harvesthope.model.service.ChamadoSuporteService;

@RestController
@RequestMapping("/api/v1/suporte")
public class ChamadoSuporteController {

    private final ChamadoSuporteService service;
    private final UsuarioRepository usuarios;

    public ChamadoSuporteController(
            ChamadoSuporteService service,
            UsuarioRepository usuarios
    ) {
        this.service = service;
        this.usuarios = usuarios;
    }

    private Usuario usuarioAutenticado(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Long id;

        try {
            id = Long.valueOf(auth.getName());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identificação do usuário inválida."
            );
        }

        return usuarios.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado."
                ));
    }

    private void exigirAdmin(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso restrito a administradores."
            );
        }
    }

    @PostMapping("/chamados")
    @ResponseStatus(HttpStatus.CREATED)
    public ChamadoSuporteResponse criar(
            @RequestBody CriarChamadoRequest req,
            Authentication auth
    ) {
        Usuario usuario = usuarioAutenticado(auth);

        return service.criar(
                usuario.getId(),
                req
        );
    }

    @GetMapping("/meus-chamados")
    public List<ChamadoSuporteResponse> meusChamados(
            Authentication auth
    ) {
        Usuario usuario = usuarioAutenticado(auth);

        return service.meusChamados(
                usuario.getId()
        );
    }

    @GetMapping("/admin/chamados")
    public List<ChamadoSuporteResponse> todos(
            Authentication auth
    ) {
        exigirAdmin(auth);

        return service.todos();
    }

    @PatchMapping("/admin/chamados/{id}/status")
    public ChamadoSuporteResponse atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusChamadoRequest req,
            Authentication auth
    ) {
        exigirAdmin(auth);

        return service.atualizarStatus(
                id,
                req == null ? null : req.status()
        );
    }

    @DeleteMapping("/admin/chamados/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable Long id,
            Authentication auth
    ) {
        exigirAdmin(auth);
        service.excluir(id);
    }
}
