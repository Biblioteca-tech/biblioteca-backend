package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.ComentarioRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final LivroRepository livroRepository;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository, LivroRepository livroRepository) {
        this.comentarioRepository = comentarioRepository;
        this.livroRepository = livroRepository;
    }

    public List<Comentario> buscarComentariosAtivosPorLivro(Long livroId) {
        return comentarioRepository.findActiveByLivroId(livroId);
    }

    // ... (Métodos adicionarComentario e deletarComentario são os mesmos)
    @Transactional
    public Comentario adicionarComentario(Long livroId, String texto, Usuario autor) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Comentario novoComentario = new Comentario();
        novoComentario.setTexto(texto);
        novoComentario.setAutor(autor);
        novoComentario.setLivro(livro);
        novoComentario.setDataCriacao(new Date());

        return comentarioRepository.save(novoComentario);
    }

    public void deletarComentario(Long comentarioId) {
        if (!comentarioRepository.existsById(comentarioId)) {
            throw new RuntimeException("Comentário não encontrado");
        }
        comentarioRepository.deleteById(comentarioId);
    }
}
