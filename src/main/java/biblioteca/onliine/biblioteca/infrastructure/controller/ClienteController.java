package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.LivroDTO;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    private final ConfigUser configUser;
    private final ClienteRepository clienteRepository;
    private final EmailService emailService;
    private final VendaRepository vendaRepository;
    private final AluguelService aluguelService;


    public ClienteController(ClienteRepository clienteRepository, ConfigUser configUser, EmailService emailService, VendaRepository vendaRepository, AluguelService aluguelService) {
        this.clienteRepository = clienteRepository;
        this.configUser = configUser;
        this.emailService = emailService;
        this.vendaRepository = vendaRepository;
        this.aluguelService = aluguelService;
    }

    //Ainda não finalizado
    @PutMapping("/trocar-senha/{id}")
    public ResponseEntity<String> trocarSenha(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String senhaAtual = body.get("senhaAtual");
        String senhaNova = body.get("senhaNova");

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente nao encontrado");
        }
        if (!configUser.loginUsuario(cliente, senhaAtual)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }
        cliente.atualizarSenha(senhaNova);
        clienteRepository.save(cliente);
        emailService.enviarEmailTrocaSenha(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok("Senha aterada com sucesso.");
    }

    // O método alterarStatusCliente foi movido para UsuarioController.java

    @GetMapping(value = "/meus-livros")
    public List<LivroDTO> getLivrosComprados(@AuthenticationPrincipal UserDetails userDetails) {
        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername());
        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        return vendas.stream().map(venda -> {
            Livro livro = venda.getLivro();
            return new LivroDTO(livro, true);
        }).collect(Collectors.toList());
    }

    @PostMapping("/alugar")
    public ResponseEntity<?> alugarLivro(@RequestParam Long clienteId, @RequestParam Long livroId, @RequestParam(defaultValue = "7") int dias) {
        try {
            Aluguel aluguel = aluguelService.alugarLivro(clienteId, livroId, dias);
            return ResponseEntity.ok(aluguel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/devolver")
    public ResponseEntity<?> devolverLivro(@RequestParam Long aluguelId) {
        try {
            Aluguel aluguel = aluguelService.devolverLivro(aluguelId);
            return ResponseEntity.ok(aluguel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
