package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public Optional<Livro> findById(Long id) {
        return livroRepository.findById(id);
    }

    public List<Livro> findAll() {
        return livroRepository.findAll();
    }

    public Livro save(Livro livro) {
        Optional<Livro> livroOptional = livroRepository.findByTituloAndAutorAndEditora(livro.getTitulo(), livro.getAutor(), livro.getEditora());
        if (livroOptional.isPresent()) {
            throw new RuntimeException("Livro ja existente");
        }
        return livroRepository.save(livro);
    }
    public ResponseEntity<String> delete(Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro nao encontrado");
        }
        livroRepository.deleteById(id);
        return ResponseEntity.ok("Livro removido com sucesso");
    }

    public Livro update(Livro livro) {
        return livroRepository.save(livro);
    }

}
