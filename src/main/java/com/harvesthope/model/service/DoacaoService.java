
package com.harvesthope.model.service;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.DoacaoRepository;
import com.harvesthope.model.repository.SolicitacaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            UsuarioRepository usuarioRepository,
            SolicitacaoRepository solicitacaoRepository) {

        this.doacaoRepository = doacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public Doacao salvar(Long usuarioId, Doacao doacao) {
        if (doacao == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da doação são obrigatórios."
            );
        }

        Usuario doador = buscarDoador(usuarioId);

        validarDados(doacao);

        doacao.setId(null);
        doacao.setDoador(doador);
        doacao.setStatus("Disponível");

        return doacaoRepository.save(doacao);
    }

    public List<Doacao> listar() {
        return doacaoRepository.findAll();
    }

    public Optional<Doacao> buscarPorId(Long id) {
        return doacaoRepository.findById(id);
    }

    public Doacao atualizar(Long id, Long usuarioId, Doacao dados) {
        if (dados == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da doação são obrigatórios."
            );
        }

        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."
                ));

        verificarProprietario(doacao, usuarioId);

        if ("Reservada".equalsIgnoreCase(doacao.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível alterar uma doação reservada."
            );
        }

        validarDados(dados);

        String novoStatus = doacao.getStatus();

        if (dados.getStatus() != null
                && !dados.getStatus().isBlank()) {
            novoStatus = normalizarStatus(dados.getStatus());
        }

        doacao.setNomeAlimento(dados.getNomeAlimento());
        doacao.setQuantidade(dados.getQuantidade());
        doacao.setUnidade(dados.getUnidade());
        doacao.setDataValidade(dados.getDataValidade());
        doacao.setDescricao(dados.getDescricao());
        doacao.setFoto(dados.getFoto());
        doacao.setLocalRetirada(dados.getLocalRetirada());
        doacao.setStatus(novoStatus);

        return doacaoRepository.save(doacao);
    }

    public void excluir(Long id, Long usuarioId) {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."
                ));

        verificarProprietario(doacao, usuarioId);

        if ("Reservada".equalsIgnoreCase(doacao.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir uma doação reservada."
            );
        }

        if (solicitacaoRepository.existsByDoacao_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível excluir uma doação vinculada a solicitações."
            );
        }

        doacaoRepository.delete(doacao);
    }

    private Usuario buscarDoador(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        if (!"DOADOR".equalsIgnoreCase(usuario.getTipoUsuario())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Somente usuários do tipo DOADOR podem gerenciar doações."
            );
        }

        return usuario;
    }

    private void verificarProprietario(
            Doacao doacao,
            Long usuarioId) {

        buscarDoador(usuarioId);

        if (doacao.getDoador() == null
                || !usuarioId.equals(doacao.getDoador().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para alterar esta doação."
            );
        }
    }

    private void validarDados(Doacao doacao) {
        if (vazio(doacao.getNomeAlimento())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do alimento é obrigatório."
            );
        }

        if (doacao.getQuantidade() == null
                || !Double.isFinite(doacao.getQuantidade())
                || doacao.getQuantidade() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser um número positivo."
            );
        }

        if (vazio(doacao.getUnidade())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A unidade é obrigatória."
            );
        }

        if (vazio(doacao.getDataValidade())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de validade é obrigatória."
            );
        }

        try {
            LocalDate validade = LocalDate.parse(
                    doacao.getDataValidade().trim()
            );

            if (validade.isBefore(LocalDate.now())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A data de validade não pode estar no passado."
                );
            }
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data de validade deve estar no formato AAAA-MM-DD."
            );
        }

        doacao.setNomeAlimento(doacao.getNomeAlimento().trim());
        doacao.setUnidade(doacao.getUnidade().trim());
        doacao.setDataValidade(doacao.getDataValidade().trim());

        if (doacao.getDescricao() != null) {
            doacao.setDescricao(doacao.getDescricao().trim());
        }

        if (doacao.getFoto() != null) {
            doacao.setFoto(doacao.getFoto().trim());
        }

        if (doacao.getLocalRetirada() != null) {
            doacao.setLocalRetirada(
                    doacao.getLocalRetirada().trim()
            );
        }
    }

    private String normalizarStatus(String status) {
        String valor = status.trim().toLowerCase(Locale.ROOT);

        if ("disponível".equals(valor)
                || "disponivel".equals(valor)) {
            return "Disponível";
        }

        if ("indisponível".equals(valor)
                || "indisponivel".equals(valor)) {
            return "Indisponível";
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status inválido. Utilize Disponível ou Indisponível."
        );
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
