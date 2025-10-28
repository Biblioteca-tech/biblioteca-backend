package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final LivroService livroService;
    private final ClienteRepository clienteRepository;
    private final AluguelRepository aluguelRepository;

    public FuncionarioController(LivroService livroService, ClienteRepository clienteRepository, AluguelRepository aluguelRepository) {
        this.livroService = livroService;
        this.clienteRepository = clienteRepository;
        this.aluguelRepository = aluguelRepository;
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

    @GetMapping("/alugueis/{clienteId}")
    public ResponseEntity<?> listarAlugueis(@PathVariable Long clienteId) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        List<Aluguel> alugueis = aluguelRepository.findByCliente(cliente.get());
        return ResponseEntity.ok(alugueis);
    }
    @GetMapping("/livros")
    public ResponseEntity<List<Livro>> listarLivros() {
        List<Livro> livros = livroService.findAll();
        return ResponseEntity.ok(livros);
    }

}
