
package com.harvesthope.model.service;

import java.util.Locale;

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

        if (request == null) {
            throw new IllegalArgumentException(
                    "Informe os dados do cadastro."
            );
        }

        if (vazio(request.getNomeResponsavel())
                || vazio(request.getEmailResponsavel())
                || vazio(request.getSenha())
                || vazio(request.getNomeInstituicao())
                || vazio(request.getCnpj())
                || vazio(request.getEmailInstituicao())) {

            throw new IllegalArgumentException(
                    "Preencha os campos obrigatórios."
            );
        }

        String nomeResponsavel = request.getNomeResponsavel().trim();
        String emailResponsavel = normalizarEmail(
                request.getEmailResponsavel()
        );

        String nomeInstituicao = request.getNomeInstituicao().trim();
        String cnpj = request.getCnpj().trim();
        String emailInstituicao = normalizarEmail(
                request.getEmailInstituicao()
        );

        if (usuarioRepository.existsByEmail(emailResponsavel)) {
            throw new IllegalArgumentException(
                    "O e-mail do responsável já está cadastrado."
            );
        }

        if (instituicaoRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException(
                    "O CNPJ já está cadastrado."
            );
        }

        if (instituicaoRepository.existsByEmail(emailInstituicao)) {
            throw new IllegalArgumentException(
                    "O e-mail da instituição já está cadastrado."
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nomeResponsavel);
        usuario.setEmail(emailResponsavel);
        usuario.setSenha(request.getSenha());
        usuario.setTipoUsuario("INSTITUICAO");

        // O UsuarioService valida os dados e aplica BCrypt à senha.
        Usuario usuarioSalvo = usuarioService.save(usuario);

        Instituicao instituicao = new Instituicao();
        instituicao.setNome(nomeInstituicao);
        instituicao.setCnpj(cnpj);
        instituicao.setDescricao(request.getDescricao());
        instituicao.setLocalizacao(request.getLocalizacao());
        instituicao.setTelefone(request.getTelefone());
        instituicao.setEmail(emailInstituicao);
        instituicao.setSite(request.getSite());

        // Mantém o vínculo entre a instituição e seu responsável.
        instituicao.setUsuario(usuarioSalvo);

        return instituicaoRepository.save(instituicao);
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
