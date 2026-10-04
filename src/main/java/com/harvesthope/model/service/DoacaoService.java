package com.harvesthope.model.service;

import com.harvesthope.model.entity.Doacao;
import com.harvesthope.repository.DoacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;

    public DoacaoService(DoacaoRepository doacaoRepository) {
        this.doacaoRepository = doacaoRepository;
    }

    public Doacao salvar(Doacao doacao) {
        return doacaoRepository.save(doacao);
    }

    public List<Doacao> listar() {
        return doacaoRepository.findAll();
    }

    public Optional<Doacao> buscarPorId(Long id) {
        return doacaoRepository.findById(id);
    }

    public Doacao atualizar(Doacao doacao) {
        return doacaoRepository.save(doacao);
    }

    public void excluir(Long id) {
        doacaoRepository.deleteById(id);
    }
}