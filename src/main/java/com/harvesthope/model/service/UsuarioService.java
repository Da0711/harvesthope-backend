
package com.harvesthope.model.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );

    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            InstituicaoRepository instituicaoRepository) {

        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario save(Usuario usuario) {

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe os dados do usuário."
            );
        }

        if (usuario.getNome() == null
                || usuario.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome é obrigatório."
            );
        }

        String email = normalizarEmail(usuario.getEmail());
        validarEmail(email);

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(email);
        usuario.setTipoUsuario(
                validarTipoUsuario(usuario.getTipoUsuario())
        );

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        if (usuario.getSenha() == null
                || usuario.getSenha().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha é obrigatória."
            );
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario update(Long id, Usuario dados) {

        if (id == null || dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID e os dados do usuário."
            );
        }

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        if (dados.getNome() == null
                || dados.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome é obrigatório."
            );
        }

        String email = normalizarEmail(dados.getEmail());
        validarEmail(email);

        Usuario usuarioComEmail = usuarioRepository.findByEmail(email);

        if (usuarioComEmail != null
                && !usuarioComEmail.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        existente.setNome(dados.getNome().trim());
        existente.setEmail(email);

        // Preserva o tipo de usuário já cadastrado.

        if (dados.getSenha() != null
                && !dados.getSenha().isBlank()) {

            existente.setSenha(
                    passwordEncoder.encode(dados.getSenha())
            );
        }

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void deleteById(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        if (instituicaoRepository.existsByUsuarioId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir um usuário vinculado a uma instituição."
            );
        }

        usuarioRepository.delete(usuario);
    }

    private String normalizarEmail(String email) {
        return email == null
                ? null
                : email.trim().toLowerCase();
    }

    private void validarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O e-mail é obrigatório."
            );
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um e-mail válido."
            );
        }
    }

    private String validarTipoUsuario(String tipoUsuario) {

        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tipo de usuário é obrigatório."
            );
        }

        String tipo = tipoUsuario.trim().toUpperCase();

        if (!tipo.equals("DOADOR")
                && !tipo.equals("INSTITUICAO")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de usuário inválido. Use DOADOR ou INSTITUICAO."
            );
        }

        return tipo;
    }
}
