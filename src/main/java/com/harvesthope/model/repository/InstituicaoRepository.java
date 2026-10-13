
package com.harvesthope.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harvesthope.model.entity.Instituicao;

public interface InstituicaoRepository
        extends JpaRepository<Instituicao, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Instituicao> findByUsuario_Id(Long usuarioId);
}
