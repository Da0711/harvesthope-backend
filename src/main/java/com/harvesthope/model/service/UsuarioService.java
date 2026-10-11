
package com.harvesthope.model.service;

import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {

        if (usuario.getSenha() != null
                && !usuario.getSenha().isBlank()
                && !usuario.getSenha().startsWith("$2a$")
                && !usuario.getSenha().startsWith("$2b$")
                && !usuario.getSenha().startsWith("$2y$")) {

            usuario.setSenha(
                    passwordEncoder.encode(usuario.getSenha())
            );
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
