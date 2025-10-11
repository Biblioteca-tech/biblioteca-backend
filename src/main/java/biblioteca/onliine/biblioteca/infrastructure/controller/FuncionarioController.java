package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private LivroService livroService;

    public FuncionarioController(LivroService livroService) {
        this.livroService = livroService;
    }

    // ================== LIVROS =====================
    @PostMapping("/add/livro")
    public ResponseEntity<Livro> criar(@RequestBody Livro livro) {
        try {
            Livro novoLivro = livroService.save(livro);
            return  ResponseEntity.status(HttpStatus.CREATED).body(novoLivro);
        }  catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/buscar/livros")
    public List<Livro> listar() {
        return livroService.findAll();
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

}
