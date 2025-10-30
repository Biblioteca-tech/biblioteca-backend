package biblioteca.onliine.biblioteca.infrastructure.controller;

import biblioteca.onliine.biblioteca.domain.Status;
import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import biblioteca.onliine.biblioteca.domain.port.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios") // Nova URL base: /usuarios
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Endpoint: Alternar o status de QUALQUER usuário (ADM/Funcionário podem fazer isso)
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @PutMapping("/alterarStatus/{usuarioId}")
    public ResponseEntity<String> alternarStatusUsuario(@PathVariable Long usuarioId, @RequestParam Status novoStatus) {
        // Usa o repositório base para encontrar qualquer tipo de usuário
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        usuario.setStatusCliente(novoStatus);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Status do usuário " + usuarioId + " alterado para " + novoStatus);
    }
}
