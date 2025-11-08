package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.*;
import biblioteca.onliine.biblioteca.infrastructure.seguranca.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/adm")
public class AdmController {

    private final ClienteRepository clienteRepository;
    private final AdmRepository admRepository;
    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;

    public AdmController(ClienteRepository clienteRepository, AdmRepository admRepository, VendaRepository vendaRepository, LivroRepository livroRepository) {
        this.clienteRepository = clienteRepository;
        this.admRepository = admRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
    }

    // BUSCAR TODOS OS CLIENTES //
    @GetMapping("/cliente")
    public List<Cliente> buscarClientes() {
        return clienteRepository.findAll();
    }


    @DeleteMapping("/cliente/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            clienteRepository.delete(clienteOptional.get());
            return "{deleted: " + id + "}";
        } else {
            return "{deleted: Resource not found}";
        }
    }

    // BUSCAR OS FUNCIONARIOS REGISTRADOS //
    @GetMapping("/buscar-funcionario")
    public List<Funcionario> buscarFuncionarios() {
        return admRepository.findAll();
    }

    // DELETAR FUNCIONARIO
    @DeleteMapping("/deletar/{id}")
    public String deletarFuncionario(@PathVariable Long id) {
        Optional<Funcionario> funcionarioOptional = admRepository.findById(id);
        if (funcionarioOptional.isPresent()) {
            admRepository.delete(funcionarioOptional.get());
            return "{deleted: " + id + "}";
        }
        return "{deleted: Resource not found}";
    }

    // EXIBIR HISTORICO DE LIVROS VENDIDOS //
    @GetMapping("/historico-vendas")
    public List<Venda> buscarVendas() {
        return vendaRepository.findAll();
    }

    // DELETAR HISTORICO LIVRO //
    @DeleteMapping("/deletar-historico/{id}")
    public ResponseEntity<?> deletarVenda(@PathVariable Long id) {
        Optional<Venda> vendaOptional = vendaRepository.findById(id);
        vendaOptional.ifPresent(vendaRepository::delete);
        return ResponseEntity.ok().body("Livro deletado com sucesso");
    }

    // ATIVAR LIVRO //
    @PutMapping(value = "/ativar/{id}")
    public ResponseEntity<String> ativarLivro(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.ativar();
                    livroRepository.save(livro);
                    return ResponseEntity.ok("Livro reativado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado"));
    }

    // DESATIVAR LIVRO //
    @PutMapping(value = "/desativar/{id}")
    public ResponseEntity<String> desativarLivro(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.desativar();
                    livroRepository.save(livro);
                    return ResponseEntity.ok("Livro desativado com sucesso!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado"));
    }

}
