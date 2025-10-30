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

        String uploadDir = "C:/Users/estee/OneDrive/Documentos/biblioteca-backend/upload/";
        Files.createDirectories(Paths.get(uploadDir));
        String safeOriginalFilename = capa.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String safeOriginalPdfFilename = pdf.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String capaFileName = System.currentTimeMillis() + "_" + safeOriginalFilename;
        String pdfFileName = System.currentTimeMillis() + "_" + safeOriginalPdfFilename;

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

        File file = new File("C:/Users/estee/OneDrive/Documentos/biblioteca-backend/upload/" + livro.getPdfPath());
        if (!file.exists()) return ResponseEntity.notFound().build();

        UrlResource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + livro.getPdfPath() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    @GetMapping("/capa/{fileName}")
    public ResponseEntity<Resource> getCapa(@PathVariable String fileName) throws IOException {
        // 1. Caminho Físico (Confirme a barra no final do diretório)
        String uploadDir = "C:/Users/estee/OneDrive/Documentos/biblioteca-backend/upload/";
        File file = new File(uploadDir + fileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        UrlResource resource = new UrlResource(file.toURI());

        // 2. Tenta identificar o Content-Type nativamente
        String contentType = Files.probeContentType(file.toPath());

        // 3. Fallback manual se a identificação nativa falhar (retorna null ou binário genérico)
        if (contentType == null || contentType.equals("application/octet-stream")) {
            String lowerCaseFileName = fileName.toLowerCase();

            // Verifica as extensões mais comuns
            if (lowerCaseFileName.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerCaseFileName.endsWith(".png")) {
                contentType = "image/png";
            } else {
                // Último recurso: binário genérico
                contentType = "application/octet-stream";
            }
        }

        // 4. Retorna a resposta com o Content-Type correto
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Livro> getLivroById(@PathVariable Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);

        if (livroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Livro livro = livroOpt.get();
        // opcional: se quiser mostrar só livros ATIVOS
        if (livro.getStatusLivro() != Status.ATIVO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(livro);
    }

}
