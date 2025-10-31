package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmmRepository extends JpaRepository<Administrador, Long> {
}
