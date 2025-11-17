package biblioteca.onliine.biblioteca.domain.dto;

import biblioteca.onliine.biblioteca.domain.StatusAluguel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AluguelDTO {
    private Long id;
    private Long clienteId;
    private Long livroId;
    private String livroTitulo;
    private String dataAluguel;
    private String dataDevolucao;
    private StatusAluguel status;

}