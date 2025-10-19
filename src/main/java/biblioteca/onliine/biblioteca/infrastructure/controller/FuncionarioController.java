package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final LivroService livroService;

    public FuncionarioController(LivroService livroService) {
        this.livroService = livroService;
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id , @RequestBody Livro livro) {
        Optional<Livro> livroAtualizado = livroService.findById(id);
        if (livroAtualizado.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        livro.setId(id);
        return ResponseEntity.ok(livroService.update(livro));
    }

    @GetMapping("/livros")
    public List<Livro> livros() {
        return livroService.findAll();
    }

}
