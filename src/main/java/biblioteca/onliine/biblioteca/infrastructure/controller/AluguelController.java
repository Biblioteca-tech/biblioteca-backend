package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;
    private final ClienteService clienteService;

    public AluguelController(AluguelService aluguelService, ClienteService clienteService) {
        this.aluguelService = aluguelService;
        this.clienteService = clienteService;
    }

    // Mostra todos os alugueis ativos - apenas ADM e FUNCIONARIO
    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<List<Aluguel>> listarTodosAtivos() {
        List<Aluguel> alugueis = aluguelService.findAllAtivos();
        return ResponseEntity.ok(alugueis);
    }

    // Mostra alugueis de um cliente específico (ADM, FUNCIONARIO e o próprio cliente)
    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarAlugueisPorCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        // 👇 Aqui corrigido: pegamos o Cliente antes de chamar o serviço
        Cliente cliente = clienteOpt.get();
        List<Aluguel> alugueis = aluguelService.findByCliente(cliente);
        return ResponseEntity.ok(alugueis);
    }

    // Mostra apenas alugueis ATIVOS de um cliente
    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','CLIENTE')")
    @GetMapping("/cliente/{clienteId}/status/ATIVO")
    public ResponseEntity<?> listarAlugueisAtivosPorCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteService.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        Cliente cliente = clienteOpt.get();
        List<Aluguel> alugueis = aluguelService.findByClienteAndStatus(cliente, StatusAluguel.ATIVO);
        return ResponseEntity.ok(alugueis);
    }

    // Cria um novo aluguel (ADM, FUNCIONARIO ou CLIENTE)
    @PreAuthorize("hasAnyRole('ADMIN','FUNCIONARIO','CLIENTE')")
    @PostMapping
    public ResponseEntity<?> criarAluguel(@RequestBody Aluguel aluguel) {
        Aluguel salvo = aluguelService.save(aluguel);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}
