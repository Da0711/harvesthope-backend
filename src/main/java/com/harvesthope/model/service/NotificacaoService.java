package com.harvesthope.model.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.harvesthope.model.entity.Notificacao;
import com.harvesthope.model.entity.Usuario;
import com.harvesthope.model.repository.NotificacaoRepository;
import com.harvesthope.model.repository.UsuarioRepository;

@Service
public class NotificacaoService {
    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    public NotificacaoService(NotificacaoRepository notificacaoRepository, UsuarioRepository usuarioRepository) {
        this.notificacaoRepository = notificacaoRepository; this.usuarioRepository = usuarioRepository;
    }
    @Transactional(readOnly = true)
    public List<Notificacao> listar(Long usuarioId) { return notificacaoRepository.findByUsuario_IdOrderByDataCriacaoDesc(usuarioId); }
    @Transactional
    public Notificacao criar(Long usuarioId, String titulo, String mensagem, String tipo, Long solicitacaoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        Notificacao n = new Notificacao(); n.setUsuario(usuario); n.setTitulo(titulo); n.setMensagem(mensagem); n.setTipo(tipo == null ? "INFO" : tipo); n.setSolicitacaoId(solicitacaoId);
        return notificacaoRepository.save(n);
    }
    @Transactional
    public Notificacao marcarLida(Long id, Long usuarioId) {
        Notificacao n = notificacaoRepository.findByIdAndUsuario_Id(id, usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada."));
        n.setLida(true); return notificacaoRepository.save(n);
    }
    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Notificacao n = notificacaoRepository.findByIdAndUsuario_Id(id, usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada."));
        notificacaoRepository.delete(n);
    }
    @Transactional
    public void excluirTodas(Long usuarioId) { notificacaoRepository.deleteByUsuario_Id(usuarioId); }
}
