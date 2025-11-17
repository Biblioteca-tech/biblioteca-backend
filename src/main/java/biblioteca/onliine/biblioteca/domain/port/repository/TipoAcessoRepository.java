package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.TipoLocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoAcessoRepository extends JpaRepository<TipoLocacao, Long> {
    Optional<TipoLocacao> findByClienteIdAndLivroId(Long clienteId, Long livroId);
}
