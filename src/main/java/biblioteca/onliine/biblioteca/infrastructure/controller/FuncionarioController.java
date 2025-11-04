package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.FuncionarioInputDTO;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Optional;

import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.port.repository.AluguelRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.FuncionarioRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.usecase.service.FuncionarioService; // Importe o FuncionarioService
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Value("spring.diretorio.iarley")
    private String diretorio;

    private final LivroService livroService;
    private final ClienteRepository clienteRepository;
    private final AluguelRepository aluguelRepository;
    private final LivroRepository livroRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FuncionarioService funcionarioService; // Declare o service

    // Construtor completo com todas as injeções
    public FuncionarioController(LivroService livroService, ClienteRepository clienteRepository, AluguelRepository aluguelRepository, LivroRepository livroRepository, FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder, FuncionarioService funcionarioService) {
        this.livroService = livroService;
        this.clienteRepository = clienteRepository;
        this.aluguelRepository = aluguelRepository;
        this.livroRepository = livroRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.funcionarioService = funcionarioService; // Inicialize o service
    }


    @PutMapping("/atualizarDados/{id}")
    public ResponseEntity<String> atualizarDadosFuncionario(@PathVariable Long id, @RequestBody FuncionarioInputDTO dadosAtualizados) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(id);

        if (funcionarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado.");
        }

        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(dadosAtualizados.getSenha());
            dadosAtualizados.setSenha(senhaCriptografada);
        }

        Funcionario funcionarioExistente = funcionarioOpt.get();
        funcionarioExistente.atualizarDados(dadosAtualizados);
        funcionarioRepository.save(funcionarioExistente);

        return ResponseEntity.ok("Dados do funcionário atualizados com sucesso!");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarFuncionario(@PathVariable Long id) {
        try {
            funcionarioService.deletarFuncionario(id);
            return ResponseEntity.ok("Funcionário deletado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id , @RequestBody Livro livro) {
        Optional<Livro> livroAtualizado = livroService.findById(id);
        if (livroAtualizado.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        livro.setId(id);
        return ResponseEntity.ok(livroService.update(livro));
    }

    @GetMapping("/alugueis/{clienteId}")
    public ResponseEntity<?> listarAlugueis(@PathVariable Long clienteId) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        List<Aluguel> alugueis = aluguelRepository.findByCliente(cliente.get());
        return ResponseEntity.ok(alugueis);
    }

    @GetMapping("/livros")
    public ResponseEntity<List<Livro>> listarLivros() {
        List<Livro> livros = livroService.findAll();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/livros/pdf/{livroId}")
    public ResponseEntity<Resource> visualizarPdf(@PathVariable Long livroId) throws MalformedURLException {
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isEmpty()) return ResponseEntity.notFound().build();

        Livro livro = livroOpt.get();
        File file = new File(diretorio + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
