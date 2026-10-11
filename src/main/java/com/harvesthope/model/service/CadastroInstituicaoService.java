
package com.harvesthope.model.service;

import com.harvesthope.model.dto.CadastroInstituicaoRequest;
import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.InstituicaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroInstituicaoService {

    private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final UsuarioService usuarioService;

    public CadastroInstituicaoService(
            UsuarioRepository usuarioRepository,
            InstituicaoRepository instituicaoRepository,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Instituicao cadastrar(CadastroInstituicaoRequest request) {

        if (request.getNomeResponsavel() == null
                || request.getNomeResponsavel().isBlank()
                || request.getEmailResponsavel() == null
                || request.getEmailResponsavel().isBlank()
                || request.getSenha() == null
                || request.getSenha().isBlank()
                || request.getNomeInstituicao() == null
                || request.getNomeInstituicao().isBlank()
                || request.getCnpj() == null
                || request.getCnpj().isBlank()
                || request.getEmailInstituicao() == null
                || request.getEmailInstituicao().isBlank()) {
            throw new IllegalArgumentException(
                    "Preencha os campos obrigatórios."
            );
        }

        if (usuarioRepository.existsByEmail(
                request.getEmailResponsavel().trim())) {
            throw new IllegalArgumentException(
                    "O e-mail do responsável já está cadastrado."
            );
        }

        if (instituicaoRepository.existsByCnpj(
                request.getCnpj().trim())) {
            throw new IllegalArgumentException(
                    "O CNPJ já está cadastrado."
            );
        }

        if (instituicaoRepository.existsByEmail(
                request.getEmailInstituicao().trim())) {
            throw new IllegalArgumentException(
                    "O e-mail da instituição já está cadastrado."
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNomeResponsavel().trim());
        usuario.setEmail(request.getEmailResponsavel().trim());
        usuario.setSenha(request.getSenha());
        usuario.setTipoUsuario("INSTITUICAO");

        Usuario usuarioSalvo = usuarioService.save(usuario);

        Instituicao instituicao = new Instituicao();
        instituicao.setNome(request.getNomeInstituicao().trim());
        instituicao.setCnpj(request.getCnpj().trim());
        instituicao.setDescricao(request.getDescricao());
        instituicao.setLocalizacao(request.getLocalizacao());
        instituicao.setTelefone(request.getTelefone());
        instituicao.setEmail(request.getEmailInstituicao().trim());
        instituicao.setSite(request.getSite());
        instituicao.setUsuario(usuarioSalvo);

        return instituicaoRepository.save(instituicao);
    }
}
