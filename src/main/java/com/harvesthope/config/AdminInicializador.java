
package com.harvesthope.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.UsuarioRepository;

@Component
@ConditionalOnProperty(
    name = "app.admin.bootstrap",
    havingValue = "true"
)
public class AdminInicializador implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Value("${app.admin.nome}")
    private String nome;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.senha}")
    private String senha;

    public AdminInicializador(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {

        String emailNormalizado = email.trim().toLowerCase();

        if (usuarioRepository.findByEmail(emailNormalizado) != null) {
            System.out.println("Administrador já cadastrado.");
            return;
        }

        Usuario admin = new Usuario();

        admin.setNome(nome);
        admin.setEmail(emailNormalizado);
        admin.setSenha(
            new BCryptPasswordEncoder().encode(senha)
        );
        admin.setTipoUsuario("ADMIN");

        usuarioRepository.save(admin);

        System.out.println("Administrador inicial criado com sucesso!");
    }
}
