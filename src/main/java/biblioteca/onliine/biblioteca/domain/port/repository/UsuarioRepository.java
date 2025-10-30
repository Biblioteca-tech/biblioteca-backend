package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}
