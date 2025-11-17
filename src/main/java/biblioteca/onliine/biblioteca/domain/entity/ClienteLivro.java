package biblioteca.onliine.biblioteca.domain.entity;


import biblioteca.onliine.biblioteca.domain.TipoAcesso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "cliente_livros")
public class ClienteLivro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(name = "data_adicionado")
    private LocalDateTime dataAdicionado = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;

}
