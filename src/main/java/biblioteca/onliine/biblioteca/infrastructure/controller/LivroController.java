package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/livros")
public class LivroController {

    LivroRepository livroRepository;
    LivroService livroService;
    ObjectMapper objectMapper;

    public LivroController(LivroRepository livroRepository,  ObjectMapper objectMapper,  LivroService livroService) {
        this.livroRepository = livroRepository;
        this.objectMapper = objectMapper;
        this.livroService = livroService;
    }

    @GetMapping(value = "/todos")
    public List<Livro> getLivros() {
        return livroRepository.findAll();
    }

    @PostMapping(value = "/cadastrar", consumes = {"multipart/form-data"})
    public ResponseEntity<String> cadastrarLivro(@RequestPart("livro") String livrojson,
                                                 @RequestPart("capa") MultipartFile capa,
                                                 @RequestPart("pdf") MultipartFile pdf) throws IOException {
        Livro livro = objectMapper.readValue(livrojson, Livro.class);
        String uploadDir = "/home/iarley/Downloads/biblioteca/uploads/";
        Files.createDirectories(Paths.get(uploadDir));

        String capaFileName = System.currentTimeMillis() + "_" + capa.getOriginalFilename();
        String pdfFileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename();

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
}
