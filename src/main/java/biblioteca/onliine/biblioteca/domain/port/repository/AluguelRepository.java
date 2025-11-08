package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    List<Aluguel> findByCliente(Cliente cliente);
    List<Aluguel> findByStatus(StatusAluguel statusAluguel);
    List<Aluguel> findByClienteAndStatus(Cliente cliente, StatusAluguel statusAluguel);
    List<Aluguel> findByClienteIdAndStatus(Long cliente_id, StatusAluguel status);
}
