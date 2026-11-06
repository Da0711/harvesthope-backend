
package com.harvesthope.model.dto;

public class LoginResponse {

    private Long id;
    private String nome;
    private String email;
    private String token;
    private String tipoUsuario;
    private Long instituicaoId;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String nome, String email) {
        this(id, nome, email, null, null, null);
    }

    public LoginResponse(
            Long id,
            String nome,
            String email,
            String token
    ) {
        this(id, nome, email, token, null, null);
    }

    public LoginResponse(
            Long id,
            String nome,
            String email,
            String token,
            String tipoUsuario
    ) {
        this(id, nome, email, token, tipoUsuario, null);
    }

    public LoginResponse(
            Long id,
            String nome,
            String email,
            String token,
            String tipoUsuario,
            Long instituicaoId
    ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.token = token;
        this.tipoUsuario = tipoUsuario;
        this.instituicaoId = instituicaoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getInstituicaoId() {
        return instituicaoId;
    }

    public void setInstituicaoId(Long instituicaoId) {
        this.instituicaoId = instituicaoId;
    }
}
