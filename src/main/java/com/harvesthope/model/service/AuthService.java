package com.harvesthope.model.service;

import com.harvesthope.model.dto.LoginResponse;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse autenticar(String email, String senha) {

        Usuario usuario = usuarioRepository.findByEmailAndSenha(email, senha);

        if (usuario == null) {
            return null;
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}