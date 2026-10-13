package com.harvesthope.model.dto;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String tipoUsuario;

    public UsuarioResponse(
            Long id,
            String nome,
            String email,
            String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
}