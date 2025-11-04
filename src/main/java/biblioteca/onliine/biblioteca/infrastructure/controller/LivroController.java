package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Value("spring.diretorio.iarley")
    private String diretorio;

    private final LivroRepository livroRepository;
    private final LivroService livroService;
    private final ObjectMapper objectMapper;
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;

    public LivroController(LivroRepository livroRepository,
                           ObjectMapper objectMapper,
                           LivroService livroService,
                           VendaRepository vendaRepository,
                           ClienteRepository clienteRepository) {
        this.livroRepository = livroRepository;
        this.objectMapper = objectMapper;
        this.livroService = livroService;
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping(value = "/cadastrar", consumes = {"multipart/form-data"})
    public ResponseEntity<String> cadastrarLivro(@RequestPart("livro") String livrojson,
                                                 @RequestPart("capa") MultipartFile capa,
                                                 @RequestPart("pdf") MultipartFile pdf) throws IOException {
        Livro livro = objectMapper.readValue(livrojson, Livro.class);

        if (livro.getPreco() == null || livro.getPreco() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor nao pode ser negativo");
        }

        String uploadDir = this.diretorio;
        Files.createDirectories(Paths.get(uploadDir));

        String capaFileName = System.currentTimeMillis() + "_" + capa.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String pdfFileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        capa.transferTo(new File(uploadDir + capaFileName));
        pdf.transferTo(new File(uploadDir + pdfFileName));

        livro.setCapaPath(capaFileName);
        livro.setPdfPath(pdfFileName);
        livroRepository.save(livro);

        return ResponseEntity.ok("Livro cadastrado com sucesso!");
    }

    @PutMapping("/atualizarLivro/{id}")
    public ResponseEntity<String> atualizarLivro(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
        Optional<Livro> livroExistenteOpt = livroRepository.findById(id);

        if (livroExistenteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado para o ID: " + id);
        }

        Livro livroExistente = livroExistenteOpt.get();
        livroExistente.atualizarDadosCompletos(
                livroAtualizado.getTitulo(),
                livroAtualizado.getAutor(),
                livroAtualizado.getEditora(),
                livroAtualizado.getAno_publicacao(),
                livroAtualizado.getGenero(),
                livroAtualizado.getSinopse(),
                livroAtualizado.getIdioma(),
                livroAtualizado.getPreco(),
                livroAtualizado.getStatusLivro()
        );

        if (livroExistente.getPreco() == null || livroExistente.getPreco() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor nao pode ser negativo");
        }

        livroRepository.save(livroExistente);
        return ResponseEntity.ok("Livro atualizado com sucesso!");
    }

    @PostMapping(value = "/atualizarLivro/capaPdf/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> atualizarArquivosLivro(@PathVariable Long id,
                                                         @RequestPart(value = "capa", required = false) MultipartFile capa,
                                                         @RequestPart(value = "pdf", required = false) MultipartFile pdf) throws IOException {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado.");
        }

        Livro livroExistente = livroOpt.get();
        String uploadDir = this.diretorio;
        Files.createDirectories(Paths.get(uploadDir));

        if (capa != null && !capa.isEmpty()) {
            String capaFileName = System.currentTimeMillis() + "_" + capa.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            capa.transferTo(new File(uploadDir + capaFileName));
            livroExistente.setCapaPath(capaFileName);
        }

        if (pdf != null && !pdf.isEmpty()) {
            String pdfFileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            pdf.transferTo(new File(uploadDir + pdfFileName));
            livroExistente.setPdfPath(pdfFileName);
        }

        livroRepository.save(livroExistente);
        return ResponseEntity.ok("Arquivos (capa e/ou pdf) do livro atualizados com sucesso!");
    }

    @DeleteMapping(value = "/deletar/{id}")
    public ResponseEntity<String> deletarLivro(@PathVariable("id") Long id) {
        return livroService.delete(id);
    }

    @GetMapping("/ativos")
    public List<Livro> getLivrosAtivos() {
        return livroRepository.findByStatusLivro(Status.ATIVO);
    }

    @GetMapping(value = "/pdf/{livroId}")
    public ResponseEntity<Resource> abrirPdf(@PathVariable Long livroId,
                                             @AuthenticationPrincipal UserDetails userDetails) throws MalformedURLException {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // CORREÇÃO: Desembrulhando Optional corretamente
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(userDetails.getUsername());
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Cliente usuario = clienteOpt.get();

        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isEmpty()) return ResponseEntity.notFound().build();
        Livro livro = livroOpt.get();

        boolean comprou = vendaRepository.existsByClienteIdAndLivroId(usuario.getId(), livro.getId());
        if (!comprou) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        File file = new File(this.diretorio + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/capa/{fileName}")
    public ResponseEntity<Resource> getCapa(@PathVariable String fileName) throws IOException {
        String uploadDir = this.diretorio;
        File file = new File(uploadDir + fileName);

        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());
        String contentType = Files.probeContentType(file.toPath());

        if (contentType == null || contentType.equals("application/octet-stream")) {
            String lowerCaseFileName = fileName.toLowerCase();
            if (lowerCaseFileName.endsWith(".webp")) contentType = "image/webp";
            else if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) contentType = "image/jpeg";
            else if (lowerCaseFileName.endsWith(".png")) contentType = "image/png";
            else contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> getLivroById(@PathVariable Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) return ResponseEntity.notFound().build();

        Livro livro = livroOpt.get();
        if (livro.getStatusLivro() != Status.ATIVO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(livro);
    }
}
