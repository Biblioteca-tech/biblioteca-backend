package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.usecase.service.ClienteLivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meus")
public class ClienteLivroController {

    private final ClienteLivroService clienteLivroService;

    public ClienteLivroController(ClienteLivroService clienteLivroService) {
        this.clienteLivroService = clienteLivroService;
    }

    @DeleteMapping("/remover/{clienteLivroId}")
    public ResponseEntity<?> removerLivro(@PathVariable Long clienteLivroId) {
        clienteLivroService.removerLivroDaBiblioteca(clienteLivroId);
        return ResponseEntity.ok("Livro removido da sua biblioteca.");
    }

    @GetMapping
    public ResponseEntity<?> listarMeusLivros() {
        return ResponseEntity.ok(clienteLivroService.listarMeusLivros());
    }


}
