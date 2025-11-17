package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.ClienteLivro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClienteLivroRepository extends JpaRepository<ClienteLivro, Long> {
    @Transactional
    @Modifying
    List<ClienteLivro> findByClienteId(Long clienteId);
    void deleteByClienteAndLivro(Cliente cliente, Livro livro);
    List<ClienteLivro> findByClienteIdAndLivroId(Long clienteId, Long livroId);


}
