package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import biblioteca.onliine.biblioteca.domain.dto.BuscaLivrosIdDTO;
import biblioteca.onliine.biblioteca.domain.dto.ListarLivrosDTO;
import biblioteca.onliine.biblioteca.domain.dto.LivroResponseDTO;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteLivroRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.ClienteRepository;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/livros")
public class LivroController {

    @Value("${spring.diretorio.iarley}")
    private String diretorio;

    private final LivroRepository livroRepository;
    private final LivroService livroService;
    private final ObjectMapper objectMapper;
    private final ClienteRepository clienteRepository;
    private final ClienteLivroRepository clienteLivroRepository;

    public LivroController(LivroRepository livroRepository,
                           ObjectMapper objectMapper,
                           LivroService livroService,
                           ClienteRepository clienteRepository, ClienteLivroRepository clienteLivroRepository) {
        this.livroRepository = livroRepository;
        this.objectMapper = objectMapper;
        this.livroService = livroService;
        this.clienteRepository = clienteRepository;
        this.clienteLivroRepository = clienteLivroRepository;
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
                livroAtualizado.getEstadoRegistroLivro()
        );

        if (livroExistente.getPreco() == null || livroExistente.getPreco() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Valor nao pode ser negativo");
        }

        livroRepository.save(livroExistente);
        return ResponseEntity.ok("Livro atualizado com sucesso!");
    }

    @DeleteMapping(value = "/deletar/{id}")
    public ResponseEntity<String> deletarLivro(@PathVariable("id") Long id) {
        return livroService.delete(id);
    }

    @GetMapping("/ativos")
    public List<LivroResponseDTO> getLivrosAtivos() {
        return livroRepository.findByEstadoRegistroLivro(EstadoRegistro.ATIVO)
                .stream()
                .map(l -> new LivroResponseDTO(
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditora(),
                        l.getAno_publicacao(),
                        l.getGenero().name(),
                        l.getSinopse(),
                        l.getIdioma().name(),
                        l.getCapaPath(),
                        l.getPreco(),
                        l.getPdfPath()
                ))
                .toList();
    }
    

    @GetMapping("/pdf/{livroId}")
    public ResponseEntity<?> abrirPdf(
            @PathVariable Long livroId,
            @AuthenticationPrincipal UserDetails userDetails) throws MalformedURLException {

        // -------------------------------------------------
        // 1. Verifica se é funcionário ou admin
        // -------------------------------------------------
        boolean isFuncionario = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_FUNCIONARIO".equals(a.getAuthority()) ||
                        "ROLE_ADMIN".equals(a.getAuthority()));

        // -------------------------------------------------
        // 2. Busca o livro (sempre)
        // -------------------------------------------------
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Path pdfPath = Paths.get(diretorio, livro.getPdfPath());
        if (!Files.exists(pdfPath)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(pdfPath.toUri());

        // -------------------------------------------------
        // 3. Funcionário tem acesso total
        // -------------------------------------------------
        if (isFuncionario) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("X-Can-Download", "true")
                    .body(resource);
        }

        // -------------------------------------------------
        // 4. Cliente comum – verifica relação ClienteLivro
        // -------------------------------------------------
        Cliente cliente = clienteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        List<ClienteLivro> relacoes = clienteLivroRepository
                .findByClienteIdAndLivroId(cliente.getId(), livroId);

        if (relacoes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Você não tem acesso a este livro."));
        }

        boolean canDownload = relacoes.stream()
                .anyMatch(r -> r.getTipoAcesso() == TipoAcesso.COMPRADO);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("X-Can-Download", String.valueOf(canDownload))
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
    public ResponseEntity<BuscaLivrosIdDTO> getLivroById(@PathVariable Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Livro livro = livroOpt.get();
        BuscaLivrosIdDTO dto = new BuscaLivrosIdDTO(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getAno_publicacao(),
                livro.getGenero(),
                livro.getSinopse(),
                livro.getIdioma(),
                livro.getPreco(),
                livro.getEstadoRegistroLivro(),
                livro.getPdfPath(),
                livro.getCapaPath()
        );

        return ResponseEntity.ok(dto);
    }

    // STATUS LIVRO
    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        Optional<Livro> optionalLivro = livroRepository.findById(id);
        if (optionalLivro.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Livro livro = optionalLivro.get();
        if (EstadoRegistro.ATIVO.equals(livro.getEstadoRegistroLivro())) {
            livro.setEstadoRegistroLivro(EstadoRegistro.INATIVO);
        } else {
            livro.setEstadoRegistroLivro(EstadoRegistro.ATIVO);
        }
        livroRepository.save(livro);
        return ResponseEntity.ok(livro);
    }

    @GetMapping
    public List<ListarLivrosDTO> listar() {
        return livroRepository.findAll()
                .stream()
                .map(l -> new ListarLivrosDTO(
                        l.getId(),
                        l.getTitulo(),
                        l.getAutor(),
                        l.getEditora(),
                        l.getAno_publicacao(),
                        l.getGenero().name(),
                        l.getIdioma().name(),
                        l.getEstadoRegistroLivro(),
                        l.getPreco(),
                        l.getCapaPath(),
                        l.getPdfPath()
                )).toList();
    }
    @PutMapping(value = "/{id}/capa", consumes = {"multipart/form-data"})
    public ResponseEntity<String> atualizarCapa(
            @PathVariable Long id,
            @RequestPart("capa") MultipartFile capa
    ) throws IOException {

        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado.");
        }

        Livro livro = livroOpt.get();

        if (capa == null || capa.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhuma capa enviada.");
        }

        String uploadDir = this.diretorio;
        Files.createDirectories(Paths.get(uploadDir));

        if (livro.getCapaPath() != null) {
            Path caminhoAntigo = Paths.get(uploadDir, livro.getCapaPath());
            Files.deleteIfExists(caminhoAntigo);
        }
        String nomeCapa = System.currentTimeMillis() + "_" +
                capa.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        Path novoCaminho = Paths.get(uploadDir, nomeCapa);
        capa.transferTo(novoCaminho.toFile());

        livro.setCapaPath(nomeCapa);
        livroRepository.save(livro);

        return ResponseEntity.ok("Capa atualizada com sucesso!");
    }
    @GetMapping("/status")
    public Map<String, Long> getStatusLivros() {
        long ativos = livroRepository.countByEstadoRegistroLivro(EstadoRegistro.ATIVO);
        long inativos = livroRepository.countByEstadoRegistroLivro(EstadoRegistro.INATIVO);

        Map<String, Long> response = new HashMap<>();
        response.put("ativos", ativos);
        response.put("inativos", inativos);

        return response;
    }

}
