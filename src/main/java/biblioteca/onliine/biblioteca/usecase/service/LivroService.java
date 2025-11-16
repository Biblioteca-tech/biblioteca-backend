package biblioteca.onliine.biblioteca.usecase.service;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
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
    private final ClienteRepository clienteRepository;

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
        boolean livroVinculado = clienteRepository.existsByLivros_Id(id);
        if (livroVinculado) {
            return ResponseEntity
                    .status(400)
                    .body("Não é possível deletar um livro que está conectado a um cliente.");
        }

        Optional<Livro> livro = livroRepository.findById(id);
        if (livro.isEmpty()) {
            return ResponseEntity
                    .status(404)
                    .body("Livro não encontrado.");
        }
        livroRepository.deleteById(id);

        return ResponseEntity
                .status(200)
                .body("Livro deletado com sucesso!");
    }

    public List<Livro> findAtivos() {
        return livroRepository.findByStatusLivro(Status.ATIVO);
    }
}
