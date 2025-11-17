package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import biblioteca.onliine.biblioteca.usecase.service.ClienteService;
import biblioteca.onliine.biblioteca.usecase.service.LivroService;
import biblioteca.onliine.biblioteca.usecase.service.TipoLocacaoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/dow")
public class SecurityDownload {

    private final ClienteService clienteService;
    private final TipoLocacaoService tipoLocacaoService;
    private final LivroService livroService;

    public SecurityDownload(TipoLocacaoService tipoLocacaoService, LivroService livroService,  ClienteService clienteService) {
        this.tipoLocacaoService = tipoLocacaoService;
        this.livroService = livroService;
        this.clienteService = clienteService;

    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadPdf(
            @PathVariable Long id,
            Principal principal) {

        Long clienteId = clienteService.buscarPorEmail(principal.getName()).getId();

        boolean podeBaixar = tipoLocacaoService.usuarioPodeBaixar(id, clienteId);

        if (!podeBaixar) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Você não tem permissão para baixar este livro (alugado)");
        }

        Optional<Livro> livroOpt = livroService.findById(id);
        if (livroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }
        Livro livro = livroOpt.get();
        File pdf = livroService.buscarPdf(
                livro.getPdfPath()
        );
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + livro.getTitulo() + ".pdf\"")
                .body(new FileSystemResource(pdf));
    }

}
