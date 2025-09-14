package biblioteca.onliine.biblioteca.controller;

import biblioteca.onliine.biblioteca.model.Cliente;
import biblioteca.onliine.biblioteca.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/adm")
public class AdmController {

    UserRepository userRepository;

    public AdmController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/cliente")
    public List<Cliente> buscarClientes() {
        return userRepository.findAll();
    }
    @DeleteMapping("/del/{id}")
    public String deletarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = userRepository.findById(id);
        if (clienteOptional.isPresent()) {
            userRepository.delete(clienteOptional.get());
            return "{deleted: " + id + "}";
        } else {
            return "{deleted: Resource not found}";
        }
    }
}
