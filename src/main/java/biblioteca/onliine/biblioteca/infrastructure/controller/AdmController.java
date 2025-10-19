package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.CadastroFuncionario;
import biblioteca.onliine.biblioteca.domain.dto.CadastroResponse;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Venda;
import biblioteca.onliine.biblioteca.domain.port.repository.AdmRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.UserRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.usecase.service.ConfigUser;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/adm")
public class AdmController {

    private final UserRepository userRepository;
    private final AdmRepository admRepository;
    private final VendaRepository vendaRepository;
    private final LivroRepository livroRepository;

    public AdmController(UserRepository userRepository,  AdmRepository admRepository,   VendaRepository vendaRepository,  LivroRepository livroRepository) {
        this.userRepository = userRepository;
        this.admRepository = admRepository;
        this.vendaRepository = vendaRepository;
        this.livroRepository = livroRepository;
    }

    // BUSCAR TODOS OS CLIENTES //
    @GetMapping("/cliente")
    public List<Cliente> buscarClientes() {
        return userRepository.findAll();
    }


    @DeleteMapping("/delete/{id}")
    public String deletarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = userRepository.findById(id);
        if (clienteOptional.isPresent()) {
            userRepository.delete(clienteOptional.get());
            return "{deleted: " + id + "}";
        } else {
            return "{deleted: Resource not found}";
        }
    }

    // CADASTRO DE FUNCIONARIO //
    @PostMapping("/cadastro-funcionario")
    public CadastroFuncionario cadastroUsuario(@RequestBody Funcionario funcionario) {
        CadastroFuncionario cadastroFuncionario = new CadastroFuncionario();
        if (admRepository.existsByEmail(funcionario.getEmail())) {
            cadastroFuncionario.setSucesso(false);
            cadastroFuncionario.setMensagem("Usuario já existe");
            return cadastroFuncionario;
        }
        Funcionario funcionarioSalvo = admRepository.save(funcionario);

        cadastroFuncionario.setSucesso(true);
        cadastroFuncionario.setMensagem("Cliente cadastrado com sucesso");
        cadastroFuncionario.setFuncionario(funcionarioSalvo);
        return cadastroFuncionario;
    }

    // BUSCAR OS FUNCIONARIOS REGISTRADOS //
    @GetMapping("/buscar-funcionario")
    public List<Funcionario> buscarFuncionarios() {
        return admRepository.findAll();
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
