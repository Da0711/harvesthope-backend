package com.harvesthope.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harvesthope.model.entity.ChamadoSuporte;

public interface ChamadoSuporteRepository
        extends JpaRepository<ChamadoSuporte, Long> {

    List<ChamadoSuporte> findAllByUsuario_IdOrderByDataCriacaoDesc(
            Long usuarioId
    );

    List<ChamadoSuporte> findAllByOrderByDataCriacaoDesc();
}