package biblioteca.onliine.biblioteca.domain.port.repository;

import biblioteca.onliine.biblioteca.domain.entity.Cliente;
import biblioteca.onliine.biblioteca.domain.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AdmRepository extends JpaRepository<Funcionario, Long> {
    Cliente findByEmail(String email);
}
