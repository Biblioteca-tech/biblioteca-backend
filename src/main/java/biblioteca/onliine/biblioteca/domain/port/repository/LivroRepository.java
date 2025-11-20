package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.EstadoRegistro;
import biblioteca.onliine.biblioteca.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    Livro findLivroById(Long livroId);
    List<Livro> findByEstadoRegistroLivro(EstadoRegistro estadoRegistroLivro);
    long countByEstadoRegistroLivro(EstadoRegistro estadoRegistro);

}
