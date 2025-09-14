package biblioteca.onliine.biblioteca.repositories;

import biblioteca.onliine.biblioteca.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    Cliente findByEmail(String email);
}
