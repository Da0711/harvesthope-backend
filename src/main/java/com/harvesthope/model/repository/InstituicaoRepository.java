
package com.harvesthope.model.repository;

import com.harvesthope.model.entity.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstituicaoRepository
        extends JpaRepository<Instituicao, Long> {

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}
