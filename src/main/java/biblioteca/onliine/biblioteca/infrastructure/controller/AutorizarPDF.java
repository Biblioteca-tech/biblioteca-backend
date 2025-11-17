package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.domain.port.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/pdf")
public class AutorizarPDF {

    @Value("${spring.diretorio.iarley}")
    private String diretorio;

    private final LivroRepository livroRepository;

    public AutorizarPDF(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> abrirPdf(@PathVariable Long id) throws MalformedURLException {

        // Busca o livro
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Livro livro = livroOpt.get();

        Path pdfPath = Paths.get(diretorio).resolve(livro.getPdfPath()).normalize();
        File file = pdfPath.toFile();

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Cria o resource
        Resource resource = new UrlResource(pdfPath.toUri());

        // Garante nome bonitinho do arquivo
        String nomeArquivo = file.getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
