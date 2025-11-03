package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import biblioteca.onliine.biblioteca.domain.entity.Aluguel;
import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    // Retorna todos os aluguéis de um cliente específico
    List<Aluguel> findByCliente(Cliente cliente);

    // Retorna todos os aluguéis por status (ATIVO, DEVOLVIDO, etc.)
    List<Aluguel> findByStatus(StatusAluguel statusAluguel);

    // Retorna todos os aluguéis de um cliente com status específico
    List<Aluguel> findByClienteAndStatus(Cliente cliente, StatusAluguel statusAluguel);
}
