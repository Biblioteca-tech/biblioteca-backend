package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByNome(String nome);
    Optional<Cliente> findByCpf(String cpf);

    boolean existsByLivros_Id(Long livroId);

}
