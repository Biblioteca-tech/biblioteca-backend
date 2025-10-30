package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("SELECT c FROM Comentario c WHERE c.livro.id = :livroId AND c.autor.statusCliente = 'ATIVO'")
    List<Comentario> findActiveByLivroId(Long livroId);
}
