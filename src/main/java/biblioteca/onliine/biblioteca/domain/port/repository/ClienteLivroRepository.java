package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface ClienteLivroRepository extends JpaRepository<ClienteLivro, Long> {
    @Transactional
    @Modifying
    void deleteByClienteIdAndLivroId(Long clienteId, Long livroId);
    boolean existsByClienteIdAndLivroId(Long clienteId, Long livroId);
    List<ClienteLivro> findByClienteId(Long clienteId);
}
