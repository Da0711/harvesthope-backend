
package com.harvesthope.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioRepository usuarioRepository;

    public InstituicaoService(
            InstituicaoRepository instituicaoRepository,
            UsuarioRepository usuarioRepository) {
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Instituicao salvar(Instituicao instituicao) {
        validarDados(instituicao);

        instituicao.setId(null);

        validarDuplicidade(instituicao, null);

        return instituicaoRepository.save(instituicao);
    }

    @Transactional(readOnly = true)
    public List<Instituicao> listar() {
        return instituicaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Instituicao> buscarPorId(Long id) {
        return instituicaoRepository.findById(id);
    }

    /**
     * Busca a instituição vinculada ao usuário autenticado.
     */
    @Transactional(readOnly = true)
    public Optional<Instituicao> buscarPorUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."
                ));

        if (usuario.getTipoUsuario() == null
                || !usuario.getTipoUsuario()
                        .equalsIgnoreCase("INSTITUICAO")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas usuários de instituições podem consultar esta informação."
            );
        }

        return instituicaoRepository.findByUsuario_Id(usuarioId);
    }

    @Transactional
    public Instituicao atualizar(
            Long id,
            Instituicao dados,
            Long usuarioId) {

        if (id == null || dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID e os dados da instituição."
            );
        }

        Instituicao existente = instituicaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada."
                ));

        validarResponsavel(existente, usuarioId);
        validarDados(dados);
        validarDuplicidade(dados, id);

        existente.setNome(dados.getNome().trim());
        existente.setCategoria(dados.getCategoria());
        existente.setCnpj(dados.getCnpj().trim());
        existente.setDescricao(dados.getDescricao());
        existente.setLocalizacao(dados.getLocalizacao());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail().trim());
        existente.setSite(dados.getSite());

        // Preserva o usuário já vinculado à instituição.
        return instituicaoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Instituicao existente = instituicaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada."
                ));

        validarResponsavel(existente, usuarioId);

        instituicaoRepository.delete(existente);
    }

    private void validarResponsavel(
            Instituicao instituicao,
            Long usuarioId) {

        if (usuarioId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."
                ));

        if (usuario.getTipoUsuario() == null
                || !usuario.getTipoUsuario()
                        .equalsIgnoreCase("INSTITUICAO")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas usuários de instituições podem executar esta operação."
            );
        }

        if (instituicao.getUsuario() == null
                || instituicao.getUsuario().getId() == null
                || !instituicao.getUsuario().getId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para modificar esta instituição."
            );
        }
    }

    private void validarDados(Instituicao instituicao) {
        if (instituicao == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe os dados da instituição."
            );
        }

        if (instituicao.getNome() == null
                || instituicao.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da instituição é obrigatório."
            );
        }

        if (instituicao.getCnpj() == null
                || instituicao.getCnpj().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CNPJ é obrigatório."
            );
        }

        if (instituicao.getEmail() == null
                || instituicao.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O e-mail da instituição é obrigatório."
            );
        }

        instituicao.setNome(instituicao.getNome().trim());
        instituicao.setCnpj(instituicao.getCnpj().trim());
        instituicao.setEmail(instituicao.getEmail().trim());
    }

    private void validarDuplicidade(
            Instituicao instituicao,
            Long idAtual) {

        boolean cnpjDuplicado =
                instituicaoRepository.existsByCnpj(instituicao.getCnpj());

        if (cnpjDuplicado
                && !mesmoRegistroPorCnpj(instituicao.getCnpj(), idAtual)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O CNPJ já está cadastrado."
            );
        }

        boolean emailDuplicado =
                instituicaoRepository.existsByEmail(instituicao.getEmail());

        if (emailDuplicado
                && !mesmoRegistroPorEmail(instituicao.getEmail(), idAtual)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O e-mail da instituição já está cadastrado."
            );
        }
    }

    private boolean mesmoRegistroPorCnpj(
            String cnpj,
            Long idAtual) {

        if (idAtual == null) {
            return false;
        }

        return instituicaoRepository.findById(idAtual)
                .map(i -> i.getCnpj() != null
                        && i.getCnpj().trim().equalsIgnoreCase(cnpj))
                .orElse(false);
    }

    private boolean mesmoRegistroPorEmail(
            String email,
            Long idAtual) {

        if (idAtual == null) {
            return false;
        }

        return instituicaoRepository.findById(idAtual)
                .map(i -> i.getEmail() != null
                        && i.getEmail().trim().equalsIgnoreCase(email))
                .orElse(false);
    }
}
