package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public Livro save(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Optional<Livro> findById(Long id) {
        return livroRepository.findById(id);
    }

    public Livro update(Livro livro) {
        return livroRepository.save(livro);
    }

    public ResponseEntity<String> delete(Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.delete(livroOpt.get());
        return ResponseEntity.ok("Livro deletado com sucesso!");
    }

    public List<Livro> findAtivos() {
        return livroRepository.findByStatusLivro(Status.ATIVO);
    }
}
