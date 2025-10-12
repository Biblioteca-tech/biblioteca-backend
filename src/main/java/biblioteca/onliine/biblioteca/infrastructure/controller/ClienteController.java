package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.CadastroResponse;
import biblioteca.onliine.biblioteca.domain.dto.ClienteResponse;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    ConfigUser configUser;
    UserRepository userRepository;
    EmailService emailService;

    public ClienteController(UserRepository userRepository, ConfigUser configUser,  EmailService emailService) {
        this.userRepository = userRepository;
        this.configUser = configUser;
        this.emailService = emailService;
    }

    @PostMapping("/cadastro")
    public CadastroResponse cadastroUsuario(@RequestBody Cliente cliente) {
        CadastroResponse cadastroResponse = new CadastroResponse();
        if (userRepository.existsByEmail(cliente.getEmail())) {
            cadastroResponse.setSucesso(false);
            cadastroResponse.setMensagem("Usuario já existe");
            return cadastroResponse;
        }
        Cliente clienteSalvo = userRepository.save(cliente);

        emailService.enviarEmailCadastro(clienteSalvo.getEmail(), clienteSalvo.getNome()); //enviar email.

        cadastroResponse.setSucesso(true);
        cadastroResponse.setMensagem("Cliente cadastrado com sucesso");
        cadastroResponse.setCliente(clienteSalvo);
        return cadastroResponse;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String senha = loginRequest.get("senha");

        Cliente cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario não encontrado");
        }
        if (!configUser.loginUsuario(cliente, senha)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Senha incorreta");
        }
        ClienteResponse clienteResponse = new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail());

        emailService.enviarEmailLogin(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.ok(clienteResponse);
    }
    @PutMapping("/trocar-senha/{id}")
    public ResponseEntity<String> trocarSenha(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        String senhaAtual =  body.get("senhaAtual");
        String senhaNova =  body.get("senhaNova");

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
}
