package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
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
    public void delete(Livro livro) {
        livroRepository.delete(livro);
    }

    public Livro update(Livro livro) {
        return livroRepository.save(livro);
    }

}
