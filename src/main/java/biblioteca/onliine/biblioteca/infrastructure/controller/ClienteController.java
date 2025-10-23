package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.LivroDTO;
import biblioteca.onliine.biblioteca.domain.dto.LoginDTO;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtService;
import biblioteca.onliine.biblioteca.usecase.service.AluguelService;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    private final ConfigUser configUser;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService  jwtService;
    private final VendaRepository vendaRepository;
    private final AluguelService aluguelService;


    public ClienteController(UserRepository userRepository, ConfigUser configUser, EmailService emailService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,  JwtService jwtService,  VendaRepository vendaRepository,  AluguelService aluguelService) {
        this.userRepository = userRepository;
        this.configUser = configUser;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.vendaRepository = vendaRepository;
        this.aluguelService = aluguelService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastroUsuario(@RequestBody Cliente cliente) {
        if (userRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        Cliente clienteSalvo = userRepository.save(cliente);
        emailService.enviarEmailCadastro(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok(clienteSalvo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getEmail(), login.getSenha())
        );
        var userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Login efetuado com sucesso!",
                "Token", token
        ));
    }

    @PutMapping("/trocar-senha/{id}")
    public ResponseEntity<String> trocarSenha(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String senhaAtual = body.get("senhaAtual");
        String senhaNova = body.get("senhaNova");

        Cliente cliente = userRepository.findById(id).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente nao encontrado");
        }
        if (!configUser.loginUsuario(cliente, senhaAtual)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }
        cliente.atualizarSenha(senhaNova);
        userRepository.save(cliente);
        emailService.enviarEmailTrocaSenha(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok("Senha aterada com sucesso.");
    }
    @GetMapping(value = "/meus-livros")
    public List<LivroDTO> getLivrosComprados(@AuthenticationPrincipal UserDetails userDetails) {
        Cliente cliente = userRepository.findByEmail(userDetails.getUsername());
        List<Venda> vendas = vendaRepository.findByClienteId(cliente.getId());
        return vendas.stream()
                .map(venda -> {
                    Livro livro = venda.getLivro();
                    return new LivroDTO(livro, true);
                })
                .collect(Collectors.toList());
    }
    @PostMapping("/alugar")
    public ResponseEntity<?> alugarLivro(
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            @RequestParam(defaultValue = "7") int dias) {
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
