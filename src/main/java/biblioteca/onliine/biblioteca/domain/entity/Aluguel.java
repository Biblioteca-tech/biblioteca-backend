package biblioteca.onliine.biblioteca.domain.entity;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aluguel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "livro_id")
    private Livro livro;

    private LocalDateTime dataAluguel;
    private LocalDateTime dataDevolucao;
    private Double valorAluguel;

    @Enumerated(EnumType.STRING)
    private StatusAluguel status; // ATIVO, DEVOLVIDO, ATRASADO, CANCELADO
}
