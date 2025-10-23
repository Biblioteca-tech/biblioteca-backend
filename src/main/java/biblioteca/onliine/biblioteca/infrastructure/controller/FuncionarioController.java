package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AluguelRepository aluguelRepository;

    public FuncionarioController(LivroService livroService,  UserRepository userRepository,  AluguelRepository aluguelRepository) {
        this.livroService = livroService;
        this.userRepository = userRepository;
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
        Optional<Cliente> cliente = userRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        List<Aluguel> alugueis = aluguelRepository.findByCliente(cliente.get());
        return ResponseEntity.ok(alugueis);
    }

}
