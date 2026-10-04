package com.harvesthope.model.service;

import com.harvesthope.model.entity.Instituicao;
import com.harvesthope.repository.InstituicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituicaoService {

    private final InstituicaoRepository instituicaoRepository;

    public InstituicaoService(InstituicaoRepository instituicaoRepository) {
        this.instituicaoRepository = instituicaoRepository;
    }

    public Instituicao salvar(Instituicao instituicao) {
        return instituicaoRepository.save(instituicao);
    }

    public List<Instituicao> listar() {
        return instituicaoRepository.findAll();
    }

    public Optional<Instituicao> buscarPorId(Long id) {
        return instituicaoRepository.findById(id);
    }

    public Instituicao atualizar(Instituicao instituicao) {
        return instituicaoRepository.save(instituicao);
    }

    public void excluir(Long id) {
        instituicaoRepository.deleteById(id);
    }
}