package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.dto.ComentarioDTO;
import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import biblioteca.onliine.biblioteca.usecase.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ComentarioController(ComentarioService comentarioService, UsuarioRepository usuarioRepository) {
        this.comentarioService = comentarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // ENDPOINT: Listar APENAS comentários de clientes ATIVOS de um livro
    @GetMapping("/livro/{livroId}")
    public ResponseEntity<List<ComentarioDTO>> getComentariosPorLivro(@PathVariable Long livroId) {
        List<Comentario> comentarios = comentarioService.buscarComentariosAtivosPorLivro(livroId);

        List<ComentarioDTO> comentariosDTO = comentarios.stream()
                .map(ComentarioDTO::new)
                .collect(Collectors.toList());

        if (comentariosDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comentariosDTO);
    }

    // ... (Métodos adicionarComentario e deletarComentario são os mesmos)
    @PostMapping("/adicionarComentario/{id}")
    public ResponseEntity<ComentarioDTO> adicionarComentario(@PathVariable("id") Long livroId,
                                                             @RequestBody Map<String, String> body,
                                                             @AuthenticationPrincipal UserDetails userDetails) {

        String textoComentario = body.get("texto");
        if (textoComentario == null || textoComentario.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Usuario autor = usuarioRepository.findByEmail(userDetails.getUsername());
        Comentario novoComentario = comentarioService.adicionarComentario(livroId, textoComentario, autor);
        return ResponseEntity.ok(new ComentarioDTO(novoComentario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @DeleteMapping("/deletarComentario/{id}")
    public ResponseEntity<String> deletarComentario(@PathVariable("id") Long comentarioId) {
        try {
            comentarioService.deletarComentario(comentarioId);
            return ResponseEntity.ok("Comentário deletado com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
