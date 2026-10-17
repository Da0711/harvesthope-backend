package com.harvesthope.model.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "notificacao")
public class Notificacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String titulo;
    @Column(nullable = false, length = 1000)
    private String mensagem;
    @Column(nullable = false, length = 40)
    private String tipo = "INFO";
    @Column(nullable = false)
    private boolean lida = false;
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    @Column(name = "solicitacao_id")
    private Long solicitacaoId;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist void aoCriar() { if (dataCriacao == null) dataCriacao = LocalDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Long getSolicitacaoId() { return solicitacaoId; }
    public void setSolicitacaoId(Long solicitacaoId) { this.solicitacaoId = solicitacaoId; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
