package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
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
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    private final LivroService livroService;
    private final ClienteRepository clienteRepository;
    private final AluguelRepository aluguelRepository;
    private final LivroRepository livroRepository; // Declare o livroRepository

    public FuncionarioController(LivroService livroService, ClienteRepository clienteRepository, AluguelRepository aluguelRepository, LivroRepository livroRepository) {
        this.livroService = livroService;
        this.clienteRepository = clienteRepository;
        this.aluguelRepository = aluguelRepository;
        this.livroRepository = livroRepository; // Inicialize o livroRepository
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
        File file = new File("/home/iarley/Downloads/biblioteca/uploads/" + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
