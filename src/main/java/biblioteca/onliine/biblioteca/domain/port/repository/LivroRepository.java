package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
