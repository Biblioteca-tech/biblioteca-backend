package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.AluguelDTO;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;
    private final ClienteService clienteService;
    private final LivroService livroService;
    private final AluguelRepository aluguelRepository;

    public AluguelController(AluguelService aluguelService, ClienteService clienteService,  LivroService livroService, AluguelRepository aluguelRepository) {
        this.aluguelService = aluguelService;
        this.clienteService = clienteService;
        this.livroService = livroService;
        this.aluguelRepository = aluguelRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarTodosAtivos() {
        return ResponseEntity.ok(aluguelService.findAllAtivosDTO());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarAlugueisPorCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        Cliente cliente = clienteOpt.get();
        List<Aluguel> alugueis = aluguelService.findByCliente(cliente);
        return ResponseEntity.ok(alugueis);
    }


    @PostMapping
    public ResponseEntity<?> criarAluguel(@RequestBody Aluguel aluguel) {
        Aluguel salvo = aluguelService.save(aluguel);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PostMapping("/alugar")
    public ResponseEntity<?> alugarLivro(@RequestParam String email, @RequestParam Long livroId, @RequestParam(defaultValue = "7") int dias
    ) {
        Optional<Cliente> clienteOpt = clienteService.findByEmail(email);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        Cliente cliente = clienteOpt.get();

        try {
            Aluguel aluguel = aluguelService.alugarLivro(cliente.getId(), livroId, dias);
            return ResponseEntity.status(HttpStatus.CREATED).body(aluguelService.toDTO(aluguel));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletar-historico/{id}")
    public void deletarAluguel(@PathVariable("id") Long aluguelId) {
        if (!aluguelRepository.existsById(aluguelId)) {
            throw new RuntimeException("Aluguel não encontrado para exclusão");
        }
        aluguelRepository.deleteById(aluguelId);
    }

    @GetMapping("/historico-aluguel")
    public ResponseEntity<List<AluguelDTO>> listarHistoricoAluguel() {
        List<AluguelDTO> historico =
                aluguelService.listarHistorico()
                        .stream()
                        .map(aluguelService::toDTO)
                        .toList();

        return ResponseEntity.ok(historico);
    }
    @GetMapping("/relatorio-aluguel")
    public ResponseEntity<?> relatorioAluguel() {
        Map<String, Object> relatorio = aluguelService.gerarRelatorioAluguel();
        return ResponseEntity.ok(relatorio);
    }
}
