package com.harvesthope.model.service;

import com.harvesthope.model.dto.SolicitarDoacaoRequest;
import com.harvesthope.model.entity.Doacao;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.entity.Solicitacao;
import com.harvesthope.model.repository.DoacaoRepository;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.SolicitacaoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final DoacaoRepository doacaoRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            InstituicaoRepository instituicaoRepository,
            DoacaoRepository doacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.doacaoRepository = doacaoRepository;
    }

    @Transactional
    public Solicitacao solicitar(SolicitarDoacaoRequest dados) {

        if (dados == null
                || dados.getInstituicaoId() == null
                || dados.getDoacaoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe a instituição e a doação."
            );
        }

        Instituicao instituicao = instituicaoRepository
                .findById(dados.getInstituicaoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instituição não encontrada."
                ));

        Doacao doacao = doacaoRepository
                .findById(dados.getDoacaoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doação não encontrada."
                ));

        if (doacao.getDoador() == null
                || doacao.getDoador().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta doação não possui um doador associado."
            );
        }

        if (doacao.getStatus() == null
                || !doacao.getStatus().trim()
                        .equalsIgnoreCase("Disponível")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta doação não está disponível."
            );
        }

        boolean jaSolicitou =
                solicitacaoRepository
                        .existsByInstituicao_IdAndDoacao_Id(
                                instituicao.getId(),
                                doacao.getId()
                        );

        if (jaSolicitou) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta instituição já solicitou esta doação."
            );
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setInstituicao(instituicao);
        solicitacao.setDoacao(doacao);
        solicitacao.setStatus("PENDENTE");

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional(readOnly = true)
    public List<Solicitacao> listarPorInstituicao(Long instituicaoId) {

        if (instituicaoId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o ID da instituição."
            );
        }

        if (!instituicaoRepository.existsById(instituicaoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Instituição não encontrada."
            );
        }

        return solicitacaoRepository
                .findByInstituicao_Id(instituicaoId);
    }
}