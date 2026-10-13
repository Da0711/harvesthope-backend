
package com.harvesthope.model.repository;

import com.harvesthope.model.entity.Doacao;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doacao d WHERE d.id = :id")
    Optional<Doacao> buscarPorIdComBloqueio(@Param("id") Long id);
}
