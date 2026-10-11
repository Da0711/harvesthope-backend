
package com.harvesthope.model.service;

import com.harvesthope.model.dto.LoginResponse;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponse autenticar(String email, String senha) {

        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null || usuario.getSenha() == null) {
            return null;
        }

        String senhaArmazenada = usuario.getSenha();

        // Compatibilidade temporária com senhas antigas em texto puro.
        if (!senhaArmazenada.startsWith("$2a$")
                && !senhaArmazenada.startsWith("$2b$")
                && !senhaArmazenada.startsWith("$2y$")) {

            if (!senhaArmazenada.equals(senha)) {
                return null;
            }

            // Converte a senha antiga para BCrypt após login válido.
            usuario.setSenha(passwordEncoder.encode(senha));
            usuarioRepository.save(usuario);

        } else if (!passwordEncoder.matches(senha, senhaArmazenada)) {
            return null;
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
