package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.dto.LivroDTO;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final PasswordEncoder passwordEncoder;
    private final AluguelRepository aluguelRepository;
    private final ClienteService clienteService;

    public ClienteController(AluguelRepository aluguelRepository, ClienteRepository clienteRepository, ConfigUser configUser, EmailService emailService, VendaRepository vendaRepository, AluguelService aluguelService, PasswordEncoder passwordEncoder, ClienteService clienteService) {
        this.clienteRepository = clienteRepository;
        this.configUser = configUser;
        this.emailService = emailService;
        this.vendaRepository = vendaRepository;
        this.aluguelService = aluguelService;
        this.passwordEncoder = passwordEncoder;
        this.aluguelRepository = aluguelRepository;
        this.clienteService = clienteService;
    }

    @PutMapping("/trocar-senha/{id}")
    public ResponseEntity<String> trocarSenha(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String senhaAtual = body.get("senhaAtual");
        String senhaNova = body.get("senhaNova");

        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        if (!configUser.loginUsuario(cliente, senhaAtual)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }
        cliente.atualizarSenha(senhaNova);
        clienteRepository.save(cliente);
        emailService.enviarEmailTrocaSenha(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }

    @GetMapping(value = "/meus-livros")
    public ResponseEntity<List<LivroDTO>> getLivrosDoCliente(@AuthenticationPrincipal UserDetails userDetails) {
        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        List<LivroDTO> livrosComprados = vendas.stream()
                .map(venda -> new LivroDTO(venda.getLivro(), true))
                .toList();

        List<Aluguel> alugueis = aluguelRepository.findByClienteAndStatus(cliente, StatusAluguel.ATIVO);

        List<LivroDTO> livrosAlugados = alugueis.stream()
                .map(aluguel -> new LivroDTO(aluguel.getLivro(), false)) // false = não é comprado
                .collect(Collectors.toList());

        List<LivroDTO> todosLivros = new ArrayList<>();
        todosLivros.addAll(livrosComprados);
        todosLivros.addAll(livrosAlugados);

        return ResponseEntity.ok(todosLivros);
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
    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }

        if (body.containsKey("nome")) {
            cliente.setNome((String) body.get("nome"));
        }
        if (body.containsKey("email")) {
            cliente.setEmail((String) body.get("email"));
        }
        if (body.containsKey("cpf")) {
            cliente.setCpf((String) body.get("cpf"));
        }
        if (body.containsKey("data_nascimento")) {
            try {
                String dataStr = (String) body.get("data_nascimento");
                LocalDate data = LocalDate.parse(dataStr); // parse direto para LocalDate
                cliente.setData_nascimento(data);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Formato de data inválido. Use yyyy-MM-dd.");
            }
        }
        if (body.containsKey("senha")) {
            String novaSenha = (String) body.get("senha");
            if (novaSenha != null && !novaSenha.isBlank()) {
                cliente.atualizarSenha(passwordEncoder.encode(novaSenha));
            }
        }
        clienteRepository.save(cliente);
        return ResponseEntity.ok("Perfil atualizado com sucesso!");
    }
    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/ativos")
    public ResponseEntity<?> clientesAtivos() {
        List<Cliente> clientes = clienteRepository.findByStatusCliente(Status.ATIVO);
        return ResponseEntity.ok(clientes);
    }
}
