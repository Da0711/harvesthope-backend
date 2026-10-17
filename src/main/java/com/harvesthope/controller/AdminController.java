package com.harvesthope.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.harvesthope.model.dto.UsuarioResponse;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UsuarioRepository usuarios; private final InstituicaoRepository instituicoes; private final DoacaoRepository doacoes; private final SolicitacaoRepository solicitacoes;
    public AdminController(UsuarioRepository usuarios, InstituicaoRepository instituicoes, DoacaoRepository doacoes, SolicitacaoRepository solicitacoes) { this.usuarios=usuarios; this.instituicoes=instituicoes; this.doacoes=doacoes; this.solicitacoes=solicitacoes; }
    private void exigirAdmin(Authentication auth) {
        Long id; try { if (auth == null) throw new NumberFormatException(); id=Long.valueOf(auth.getName()); } catch (Exception e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado."); }
        Usuario u=usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado."));
        if (u.getTipoUsuario()==null || !"ADMIN".equalsIgnoreCase(u.getTipoUsuario())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a administradores.");
    }
    @GetMapping("/usuarios") public List<UsuarioResponse> listarUsuarios(Authentication auth) { exigirAdmin(auth); return usuarios.findAll().stream().map(u -> new UsuarioResponse(u.getId(),u.getNome(),u.getEmail(),u.getTipoUsuario())).toList(); }
    @GetMapping("/indicadores") public Map<String,Long> indicadores(Authentication auth) { exigirAdmin(auth); Map<String,Long> m=new LinkedHashMap<>(); m.put("usuarios",usuarios.count()); m.put("instituicoes",instituicoes.count()); m.put("doacoes",doacoes.count()); m.put("solicitacoes",solicitacoes.count()); return m; }
}
