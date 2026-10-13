
package com.harvesthope.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.dto.SolicitarDoacaoRequest;
import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.entity.Solicitacao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.DoacaoRepository;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.SolicitacaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final DoacaoRepository doacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            InstituicaoRepository instituicaoRepository,
            DoacaoRepository doacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.solicitacaoRepository = solicitacaoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.doacaoRepository = doacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Solicitacao solicitar(
            SolicitarDoacaoRequest dados,
            Long usuarioId) {

        if (dados == null
                || dados.getInstituicaoId() == null
                || dados.getDoacaoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe a instituição e a doação.");
        }

        Usuario usuario = buscarUsuario(usuarioId);
        validarTipo(usuario, "INSTITUICAO");

        Instituicao instituicao = instituicaoRepository
                .findById(dados.getInstituicaoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada."));

        if (instituicao.getUsuario() == null
                || !usuarioId.equals(instituicao.getUsuario().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode solicitar doações em nome desta instituição.");
        }

        Doacao doacao = doacaoRepository
                .findById(dados.getDoacaoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."));

        if (doacao.getDoador() == null
                || doacao.getDoador().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta doação não possui um doador associado.");
        }

        if (!"Disponível".equalsIgnoreCase(
                doacao.getStatus() == null
                        ? ""
                        : doacao.getStatus().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta doação não está disponível.");
        }

        if (solicitacaoRepository.existsByInstituicao_IdAndDoacao_Id(
                instituicao.getId(), doacao.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A doação já foi solicitada por outra instituição.");
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setInstituicao(instituicao);
        solicitacao.setDoacao(doacao);
        solicitacao.setStatus("PENDENTE");

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao aceitar(Long solicitacaoId, Long usuarioId) {

        Solicitacao solicitacao =
                buscarSolicitacaoObrigatoria(solicitacaoId);

        Usuario usuario = validarDoador(usuarioId);
        validarProprietario(solicitacao, usuario);
        validarPendente(solicitacao);

        Long doacaoId = solicitacao.getDoacao().getId();

        Doacao doacao = doacaoRepository
                .buscarPorIdComBloqueio(doacaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."));

        if (!"Disponível".equalsIgnoreCase(
                doacao.getStatus() == null
                        ? ""
                        : doacao.getStatus().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta doação não está disponível para reserva.");
        }

        doacao.setStatus("Reservada");
        solicitacao.setStatus("ACEITA");

        doacaoRepository.save(doacao);
        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao recusar(Long solicitacaoId, Long usuarioId) {

        Solicitacao solicitacao =
                buscarSolicitacaoObrigatoria(solicitacaoId);

        Usuario usuario = validarDoador(usuarioId);
        validarProprietario(solicitacao, usuario);
        validarPendente(solicitacao);

        solicitacao.setStatus("RECUSADA");

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listar(Long usuarioId) {

        Usuario usuario = buscarUsuario(usuarioId);

        if (ehTipo(usuario, "DOADOR")) {
            return solicitacaoRepository.findByDoacao_Doador_Id(usuarioId);
        }

        if (ehTipo(usuario, "INSTITUICAO")) {
            Instituicao instituicao =
                    buscarInstituicaoDoUsuario(usuarioId);

            return solicitacaoRepository.findByInstituicao_Id(
                    instituicao.getId());
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Você não tem permissão para consultar solicitações.");
    }

    @Transactional(readOnly = true)
    public Optional<Solicitacao> buscarPorId(
            Long solicitacaoId,
            Long usuarioId) {

        if (solicitacaoId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID da solicitação.");
        }

        Usuario usuario = buscarUsuario(usuarioId);

        if (ehTipo(usuario, "DOADOR")) {
            return solicitacaoRepository.findByIdAndDoacao_Doador_Id(
                    solicitacaoId, usuarioId);
        }

        if (ehTipo(usuario, "INSTITUICAO")) {
            Instituicao instituicao =
                    buscarInstituicaoDoUsuario(usuarioId);

            return solicitacaoRepository.findByIdAndInstituicao_Id(
                    solicitacaoId, instituicao.getId());
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Você não tem permissão para consultar solicitações.");
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listarPorInstituicao(
            Long instituicaoId,
            Long usuarioId) {

        if (instituicaoId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID da instituição.");
        }

        Usuario usuario = buscarUsuario(usuarioId);
        validarTipo(usuario, "INSTITUICAO");

        Instituicao instituicao = instituicaoRepository
                .findById(instituicaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada."));

        if (instituicao.getUsuario() == null
                || !usuarioId.equals(instituicao.getUsuario().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode consultar solicitações desta instituição.");
        }

        return solicitacaoRepository.findByInstituicao_Id(instituicaoId);
    }

    private Usuario buscarUsuario(Long usuarioId) {

        if (usuarioId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado.");
        }

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado."));
    }

    private Instituicao buscarInstituicaoDoUsuario(Long usuarioId) {
        return instituicaoRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Nenhuma instituição está vinculada a este usuário."));
    }

    private Usuario validarDoador(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        validarTipo(usuario, "DOADOR");
        return usuario;
    }

    private void validarTipo(Usuario usuario, String tipoEsperado) {
        if (!ehTipo(usuario, tipoEsperado)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Seu tipo de usuário não permite esta operação.");
        }
    }

    private boolean ehTipo(Usuario usuario, String tipo) {
        return usuario.getTipoUsuario() != null
                && tipo.equalsIgnoreCase(
                        usuario.getTipoUsuario().trim());
    }

    private Solicitacao buscarSolicitacaoObrigatoria(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID da solicitação.");
        }

        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada."));
    }

    private void validarProprietario(
            Solicitacao solicitacao,
            Usuario usuario) {

        Doacao doacao = solicitacao.getDoacao();

        if (doacao == null
                || doacao.getDoador() == null
                || doacao.getDoador().getId() == null
                || !usuario.getId().equals(
                        doacao.getDoador().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não é o proprietário desta doação.");
        }
    }

    private void validarPendente(Solicitacao solicitacao) {
        if (solicitacao.getStatus() == null
                || !"PENDENTE".equalsIgnoreCase(
                        solicitacao.getStatus().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta solicitação já foi decidida.");
        }
    }
}
