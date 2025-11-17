package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_acesso")
public class TipoLocacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long clienteId;
    private Long livroId;

    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;
}
