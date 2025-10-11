package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.CadastroResponse;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import biblioteca.onliine.biblioteca.usecase.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
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

        cliente.setSenha(null);
        emailService.enviarEmailLogin(cliente.getEmail(), cliente.getNome());
        return ResponseEntity.status(HttpStatus.OK).body("Login realizado com sucesso");

    }
}
