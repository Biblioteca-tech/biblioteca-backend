package biblioteca.onliine.biblioteca.controller;

import biblioteca.onliine.biblioteca.dto.CadastroResponse;
import biblioteca.onliine.biblioteca.model.Cliente;
import biblioteca.onliine.biblioteca.repositories.UserRepository;
import biblioteca.onliine.biblioteca.service.ConfigUser;
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
    public ClienteController(UserRepository userRepository,  ConfigUser configUser) {
        this.userRepository = userRepository;
        this.configUser = configUser;
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
        return ResponseEntity.status(HttpStatus.OK).body("Login realizado com sucesso");

    }
}
