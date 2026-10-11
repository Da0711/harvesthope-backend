package com.harvesthope.model.repository;

import com.harvesthope.model.entity.Solicitacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository
        extends JpaRepository<Solicitacao, Long> {

    boolean existsByInstituicao_IdAndDoacao_Id(
            Long instituicaoId,
            Long doacaoId
    );

    List<Solicitacao> findByInstituicao_Id(Long instituicaoId);
}