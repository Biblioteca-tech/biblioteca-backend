package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "aluguel")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @CreationTimestamp
    @Column(name = "data_aluguel", nullable = false, updatable = false)
    private LocalDateTime dataAluguel;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @Column(name = "valor_aluguel", nullable = false)
    private Double valorAluguel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluguel status; // ATIVO, DEVOLVIDO, ATRASADO, CANCELADO
}
