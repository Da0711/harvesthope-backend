
package com.harvesthope.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "doacao_id", nullable = false)
    private Doacao doacao;

    @Column(nullable = false)
    private String status = "PENDENTE";

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_retirada")
    private LocalDateTime dataRetirada;

    @Column(name = "local_retirada", length = 300)
    private String localRetirada;

    @Column(name = "data_confirmacao_recebimento")
    private LocalDateTime dataConfirmacaoRecebimento;

    @PrePersist
    protected void aoCriar() {
        if (dataSolicitacao == null) {
            dataSolicitacao = LocalDateTime.now();
        }

        if (status == null || status.isBlank()) {
            status = "PENDENTE";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public void setDoacao(Doacao doacao) {
        this.doacao = doacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public String getLocalRetirada() {
        return localRetirada;
    }

    public void setLocalRetirada(String localRetirada) {
        this.localRetirada = localRetirada;
    }

    public LocalDateTime getDataConfirmacaoRecebimento() {
        return dataConfirmacaoRecebimento;
    }

    public void setDataConfirmacaoRecebimento(
            LocalDateTime dataConfirmacaoRecebimento) {
        this.dataConfirmacaoRecebimento = dataConfirmacaoRecebimento;
    }
}
