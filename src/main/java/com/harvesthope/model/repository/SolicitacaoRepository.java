
package com.harvesthope.model.repository;

import com.harvesthope.model.entity.Solicitacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository
        extends JpaRepository<Solicitacao, Long> {

    boolean existsByInstituicao_IdAndDoacao_Id(
            Long instituicaoId,
            Long doacaoId
    );

    boolean existsByDoacao_Id(Long doacaoId);

    List<Solicitacao> findByInstituicao_Id(Long instituicaoId);

    List<Solicitacao> findByDoacao_Doador_Id(Long doadorId);

    Optional<Solicitacao> findByIdAndInstituicao_Id(
            Long solicitacaoId,
            Long instituicaoId
    );

    Optional<Solicitacao> findByIdAndDoacao_Doador_Id(
            Long solicitacaoId,
            Long doadorId
    );
}
