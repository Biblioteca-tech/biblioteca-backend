package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adicionar")
public class FuncionarioController {

    LivroRepository livroRepository;
    public FuncionarioController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }
    @PostMapping("/livro")
    public Livro adicionarLivro(@RequestBody Livro livro) {
        return livroRepository.save(livro);
    }
}
