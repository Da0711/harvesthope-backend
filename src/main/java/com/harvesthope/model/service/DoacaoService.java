
package com.harvesthope.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.DoacaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public DoacaoService(
            DoacaoRepository doacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.doacaoRepository = doacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Doacao salvar(Doacao doacao) {
        if (doacao.getDoador() == null
                || doacao.getDoador().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do doador é obrigatório."
            );
        }

        Long usuarioId = doacao.getDoador().getId();

        Usuario doador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doador não encontrado."
                ));

        doacao.setDoador(doador);

        return doacaoRepository.save(doacao);
    }

    public List<Doacao> listar() {
        return doacaoRepository.findAll();
    }

    public Optional<Doacao> buscarPorId(Long id) {
        return doacaoRepository.findById(id);
    }

    @Transactional
    public Doacao atualizar(
            Long id,
            Long usuarioId,
            Doacao dados) {

        Doacao existente = doacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."
                ));

        verificarProprietario(existente, usuarioId);

        existente.setNomeAlimento(dados.getNomeAlimento());
        existente.setQuantidade(dados.getQuantidade());
        existente.setUnidade(dados.getUnidade());
        existente.setDataValidade(dados.getDataValidade());
        existente.setDescricao(dados.getDescricao());
        existente.setFoto(dados.getFoto());
        existente.setStatus(dados.getStatus());
        existente.setLocalRetirada(dados.getLocalRetirada());

        return doacaoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."
                ));

        verificarProprietario(doacao, usuarioId);

        doacaoRepository.delete(doacao);
    }

    private void verificarProprietario(
            Doacao doacao,
            Long usuarioId) {

        if (usuarioId == null
                || doacao.getDoador() == null
                || !doacao.getDoador().getId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para modificar esta doação."
            );
        }
    }
}
