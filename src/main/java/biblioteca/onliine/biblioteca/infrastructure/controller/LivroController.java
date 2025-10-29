package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.VendaRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final LivroRepository livroRepository;
    private final LivroService livroService;
    private final ObjectMapper objectMapper;
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;

    public LivroController(LivroRepository livroRepository, ObjectMapper objectMapper, LivroService livroService, VendaRepository vendaRepository, ClienteRepository clienteRepository) {
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

        String uploadDir = "/home/iarley/Downloads/biblioteca/uploads/";
        Files.createDirectories(Paths.get(uploadDir));

        String capaFileName = System.currentTimeMillis() + "_" + capa.getOriginalFilename().replaceAll("", "_");
        String pdfFileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename().replaceAll("", "_");

        String capaPath = uploadDir + capaFileName;
        String pdfPath = uploadDir + pdfFileName;

        capa.transferTo(new File(capaPath));
        pdf.transferTo(new File(pdfPath));

        livro.setCapaPath(capaFileName);
        livro.setPdfPath(pdfFileName);
        livroRepository.save(livro);
        return ResponseEntity.ok("Livro cadastrado com sucesso!");
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

        Cliente usuario = clienteRepository.findByEmail(userDetails.getUsername());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isEmpty()) return ResponseEntity.notFound().build();

        Livro livro = livroOpt.get();
        boolean comprou = vendaRepository.existsByClienteIdAndLivroId(usuario.getId(), livro.getId());
        if (!comprou) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        File file = new File("/home/iarley/Downloads/biblioteca/uploads/" + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    @GetMapping("/capa/{fileName}")
    public ResponseEntity<Resource> getCapa(@PathVariable String fileName) throws IOException {
        File file = new File("/home/iarley/Downloads/biblioteca/uploads/" + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        UrlResource resource = new UrlResource(file.toURI());
        String contentType = Files.probeContentType(file.toPath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }
}
