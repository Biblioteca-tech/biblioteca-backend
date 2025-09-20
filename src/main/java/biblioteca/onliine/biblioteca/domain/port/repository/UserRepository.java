package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    Cliente findByEmail(String email);
}
