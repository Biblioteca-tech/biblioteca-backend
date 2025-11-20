package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PutMapping("/alterarStatus/{usuarioId}")
    public ResponseEntity<String> alternarStatusUsuario(@PathVariable Long usuarioId, @RequestParam EstadoRegistro novoEstadoRegistro) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }
        usuario.setEstadoRegistroCliente(novoEstadoRegistro);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Status do usuário " + usuarioId + " alterado para " + novoEstadoRegistro);
    }
}
