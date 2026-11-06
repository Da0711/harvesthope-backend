
package com.harvesthope.model.service;

import java.util.Locale;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.harvesthope.config.JwtService;
import com.harvesthope.model.dto.LoginResponse;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            InstituicaoRepository instituicaoRepository,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public LoginResponse autenticar(String email, String senha) {
        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {
            return null;
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado);

        if (usuario == null || usuario.getSenha() == null) {
            return null;
        }

        String senhaArmazenada = usuario.getSenha();

        if (!senhaArmazenada.startsWith("$2a$")
                && !senhaArmazenada.startsWith("$2b$")
                && !senhaArmazenada.startsWith("$2y$")) {

            if (!senhaArmazenada.equals(senha)) {
                return null;
            }

            usuario.setSenha(passwordEncoder.encode(senha));
            usuarioRepository.save(usuario);

        } else if (!passwordEncoder.matches(senha, senhaArmazenada)) {
            return null;
        }

        String token = jwtService.gerarToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getTipoUsuario()
        );

        Long instituicaoId = null;

        if ("INSTITUICAO".equalsIgnoreCase(usuario.getTipoUsuario())) {
            instituicaoId = instituicaoRepository
                    .findByUsuario_Id(usuario.getId())
                    .map(Instituicao::getId)
                    .orElse(null);
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                token,
                usuario.getTipoUsuario(),
                instituicaoId
        );
    }
}
