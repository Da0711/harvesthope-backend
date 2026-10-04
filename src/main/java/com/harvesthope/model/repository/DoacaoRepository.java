package com.harvesthope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harvesthope.model.entity.Doacao;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

}