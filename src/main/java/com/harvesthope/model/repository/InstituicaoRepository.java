package com.harvesthope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harvesthope.model.entity.Instituicao;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
}