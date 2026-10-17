package com.harvesthope.model.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.harvesthope.model.entity.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuario_IdOrderByDataCriacaoDesc(Long usuarioId);
    Optional<Notificacao> findByIdAndUsuario_Id(Long id, Long usuarioId);
    void deleteByUsuario_Id(Long usuarioId);
}
